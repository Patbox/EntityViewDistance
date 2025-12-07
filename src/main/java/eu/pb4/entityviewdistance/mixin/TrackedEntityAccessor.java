package eu.pb4.entityviewdistance.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.TrackedEntity.class)
public interface TrackedEntityAccessor {
    @Accessor
    Entity getEntity();

    @Mutable
    @Accessor
    void setRange(int maxDistance);
}
