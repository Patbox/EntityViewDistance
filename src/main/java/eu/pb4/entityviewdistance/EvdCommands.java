package eu.pb4.entityviewdistance;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.entityviewdistance.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class EvdCommands {
    public static final Predicate<CommandSourceStack> IS_HOST = source -> {
        var player = source.getPlayer();
        return player != null && source.getServer().isSingleplayerOwner(player.nameAndId());
    };

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            dispatcher.register(
                    literal("entityviewdistance")
                            .requires(Permissions.require("entityviewdistance.main", true))
                            .executes(EvdCommands::about)

                            .then(literal("reload")
                                    .requires(Permissions.require("entityviewdistance.reload", 3).or(IS_HOST))
                                    .executes(EvdCommands::reloadConfig)
                            )

                            .then(literal("values")
                                    .requires(Permissions.require("entityviewdistance.set", 3).or(IS_HOST))
                                    .then(argument("entity", IdentifierArgument.id())
                                            .suggests((ctx, builder) -> {
                                                var remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                                                SharedSuggestionProvider.filterResources(BuiltInRegistries.ENTITY_TYPE.keySet(), remaining, Function.identity(), id -> {
                                                    builder.suggest(id.toString(), null);
                                                });

                                                return builder.buildFuture();
                                            })
                                            .executes(EvdCommands::getEntity)
                                            .then(argument("distance", IntegerArgumentType.integer(-1, EvdUtils.MAX_DISTANCE))
                                                    .executes(EvdCommands::setEntity)
                                            )
                                    )
                            )
            );
        });
    }

    private static int getEntity(CommandContext<CommandSourceStack> context) {
        var identifier = context.getArgument("entity", Identifier.class);
        var val = ConfigManager.getConfig().entityViewDistances.getOrDefault(identifier, -1);
        context.getSource().sendSuccess(() -> Component.literal("" + identifier + " = " + val + " (Default: " + BuiltInRegistries.ENTITY_TYPE.getValue(identifier).clientTrackingRange() * 16 + ")"), false);
        return val;
    }

    private static int setEntity(CommandContext<CommandSourceStack> context) {
        var identifier = context.getArgument("entity", Identifier.class);
        var val = Mth.clamp(context.getArgument("distance", Integer.class), -1, EvdUtils.MAX_DISTANCE);
        ConfigManager.getConfig().entityViewDistances.put(identifier, val);
        context.getSource().sendSuccess( () -> Component.literal("Changed " + identifier + " view distance to " + val + " blocks"), false);
        EvdUtils.updateAll();
        EvdUtils.updateServer(context.getSource().getServer());
        ConfigManager.overrideConfig();
        return val;
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        if (ConfigManager.loadConfig()) {
            EvdUtils.updateAll();
            EvdUtils.updateServer(context.getSource().getServer());
            context.getSource().sendSuccess(() -> Component.literal("Reloaded config!"), false);
        } else {
            context.getSource().sendFailure(Component.literal("Error occurred while reloading config!").withStyle(ChatFormatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Entity View Distance")
                .setStyle(Style.EMPTY.withColor(0xfc4103))
                .append(Component.literal(" - " + EVDMod.VERSION)
                        .withStyle(ChatFormatting.WHITE)
                ), false);

        return 1;
    }
}
