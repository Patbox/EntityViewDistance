package eu.pb4.entityviewdistance.mixin.client;

import com.mojang.serialization.Codec;
import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.screen.EvdSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;

@Mixin(VideoSettingsScreen.class)
public class VideoSettingsScreenMixin {

    @Redirect(method = "qualityOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;entityDistanceScaling()Lnet/minecraft/client/OptionInstance;"))
    private static OptionInstance<?> entityViewDistance_addOptionButton(Options instance) {
        return new OptionInstance<>(EvdUtils.BUTTON_TEXT, OptionInstance.noTooltip(), (x, y) -> Component.empty(), new OptionInstance.ValueSet<>() {
            @Override
            public Function<OptionInstance<Boolean>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<Boolean> tooltipFactory, Options gameOptions, int x, int y, int width, Consumer<Boolean> changeCallback) {
                return (option) -> {
                    var client = Minecraft.getInstance();
                    return Button.builder(Component.translatable(EvdUtils.BUTTON_TEXT),
                            btn -> Minecraft.getInstance().setScreen(new EvdSettingsScreen(client.screen, client.options))).pos(x, y).width(width).build();
                };
            }

            @Override
            public Optional<Boolean> validateValue(Boolean value) {
                return Optional.of(value);
            }

            @Override
            public Codec<Boolean> codec() {
                return Codec.BOOL;
            }
        }, false, (x) -> {});
    }
}
