package eu.pb4.entityviewdistance.mixin.client;

import eu.pb4.entityviewdistance.screen.EvdSettingsButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.Option;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin {
    @Shadow
    @Final
    private static Option[] OPTIONS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void entityViewDistance_addOptionButton(CallbackInfo ci) {
        for (int i = 0; i < OPTIONS.length; i++) {
            if (OPTIONS[i] == Option.ENTITY_DISTANCE_SCALING) {
                OPTIONS[i] = new EvdSettingsButton();
            }
        }
    }
}
