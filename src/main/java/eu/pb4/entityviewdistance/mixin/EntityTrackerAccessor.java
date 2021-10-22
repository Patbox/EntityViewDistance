package eu.pb4.entityviewdistance.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadedAnvilChunkStorage.EntityTracker.class)
public interface EntityTrackerAccessor {
    @Accessor
    Entity getEntity();

    @Mutable
    @Accessor
    void setMaxDistance(int maxDistance);
}
