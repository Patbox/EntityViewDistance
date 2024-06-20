package eu.pb4.entityviewdistance;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.entityviewdistance.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EvdCommands {
    public static final Predicate<ServerCommandSource> IS_HOST = source -> {
        var player = source.getPlayer();
        return player != null && source.getServer().isHost(player.getGameProfile());
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
                                    .then(argument("entity", IdentifierArgumentType.identifier())
                                            .suggests((ctx, builder) -> {
                                                var remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                                                CommandSource.forEachMatching(Registries.ENTITY_TYPE.getIds(), remaining, Function.identity(), id -> {
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

    private static int getEntity(CommandContext<ServerCommandSource> context) {
        var identifier = context.getArgument("entity", Identifier.class);
        var val = ConfigManager.getConfig().entityViewDistances.getOrDefault(identifier, -1);
        context.getSource().sendFeedback(() -> Text.literal("" + identifier + " = " + val + " (Default: " + Registries.ENTITY_TYPE.get(identifier).getMaxTrackDistance() * 16 + ")"), false);
        return val;
    }

    private static int setEntity(CommandContext<ServerCommandSource> context) {
        var identifier = context.getArgument("entity", Identifier.class);
        var val = MathHelper.clamp(context.getArgument("distance", Integer.class), -1, EvdUtils.MAX_DISTANCE);
        ConfigManager.getConfig().entityViewDistances.put(identifier, val);
        context.getSource().sendFeedback( () -> Text.literal("Changed " + identifier + " view distance to " + val + " blocks"), false);
        EvdUtils.updateAll();
        EvdUtils.updateServer(context.getSource().getServer());
        ConfigManager.overrideConfig();
        return val;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            EvdUtils.updateAll();
            EvdUtils.updateServer(context.getSource().getServer());
            context.getSource().sendFeedback(() -> Text.literal("Reloaded config!"), false);
        } else {
            context.getSource().sendError(Text.literal("Error occurred while reloading config!").formatted(Formatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Entity View Distance")
                .setStyle(Style.EMPTY.withColor(0xfc4103))
                .append(Text.literal(" - " + EVDMod.VERSION)
                        .formatted(Formatting.WHITE)
                ), false);

        return 1;
    }
}
