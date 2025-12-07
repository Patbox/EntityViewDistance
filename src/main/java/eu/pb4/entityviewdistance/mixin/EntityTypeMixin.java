package eu.pb4.entityviewdistance.mixin;

import eu.pb4.entityviewdistance.interfaces.EvdEntityType;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public class EntityTypeMixin implements EvdEntityType {
    @Unique
    private int evd_trackingDistance = -1;

    @Override
    public int evd_getTrackingDistance() {
        return this.evd_trackingDistance;
    }

    @Override
    public void evd_setTrackingDistance(int blocks) {
        this.evd_trackingDistance = blocks;
    }
}
