package eu.pb4.entityviewdistance.mixin;

import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.interfaces.EvdEntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract EntityType<?> getType();

    @Inject(method = "shouldRender(DDD)Z", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void evd_shouldRender(double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Boolean> cir, double deltaX, double deltaY, double deltaZ) {
        var config = ConfigManager.getConfig();
        if (config.mode.client) {
            var value = ((EvdEntityType) this.getType()).evd_getTrackingDistance();

            if (value != -1) {
                cir.setReturnValue(Math.abs(deltaX) < value && Math.abs(deltaY) < value && Math.abs(deltaZ) < value);
            }
        }
    }
}
