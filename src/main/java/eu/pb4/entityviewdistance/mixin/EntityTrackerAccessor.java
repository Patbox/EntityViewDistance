package eu.pb4.entityviewdistance.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerChunkLoadingManager.EntityTracker.class)
public interface EntityTrackerAccessor {
    @Accessor
    Entity getEntity();

    @Mutable
    @Accessor
    void setMaxDistance(int maxDistance);
}
