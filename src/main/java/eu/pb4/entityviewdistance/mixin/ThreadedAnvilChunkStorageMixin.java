package eu.pb4.entityviewdistance.mixin;

import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.interfaces.EvdEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
    @ModifyVariable(method = "loadEntity", at = @At("STORE"), ordinal = 0)
    private int evd_replaceDistance(int distance, Entity entity) {
        var x = ((EvdEntityType) entity.getType()).evd_getTrackingDistance();
        return !ConfigManager.getConfig().mode.server || x == -1 || distance == 0 ? distance : x;
    }
}
