package eu.pb4.entityviewdistance.mixin.client;

import com.mojang.serialization.Codec;
import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.screen.EvdSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin {

    @Redirect(method = "getOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getEntityDistanceScaling()Lnet/minecraft/client/option/SimpleOption;"))
    private static SimpleOption<?> entityViewDistance_addOptionButton(GameOptions instance) {
        return new SimpleOption<>(EvdUtils.BUTTON_TEXT, SimpleOption.emptyTooltip(), (x, y) -> Text.empty(), new SimpleOption.Callbacks<>() {
            @Override
            public Function<SimpleOption<Boolean>, ClickableWidget> getButtonCreator(SimpleOption.TooltipFactory<Boolean> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<Boolean> changeCallback) {
                return (option) -> {
                    return ButtonWidget.builder(Text.translatable(EvdUtils.BUTTON_TEXT), btn -> MinecraftClient.getInstance().setScreen(new EvdSettingsScreen(MinecraftClient.getInstance().currentScreen))).position(x, y).width(width).build();
                };
            }

            @Override
            public Optional<Boolean> validate(Boolean value) {
                return Optional.of(value);
            }

            @Override
            public Codec<Boolean> codec() {
                return Codec.BOOL;
            }
        }, false, (x) -> {});
    }
}
