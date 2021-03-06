package eu.pb4.entityviewdistance;

import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.interfaces.EvdEntityType;
import eu.pb4.entityviewdistance.mixin.EntityTrackerAccessor;
import eu.pb4.entityviewdistance.mixin.ThreadedAnvilChunkStorageAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class EvdUtils {
    public static TranslatableText getText(String type, Object... obj) {
        return new TranslatableText("entityviewdistance" + "." + type, obj);
    }

    public static String getKey(String type) {
        return "entityviewdistance" + "." + type;
    }

    public static void initialize(EntityType type, Identifier identifier) {
        var map = ConfigManager.getConfig().entityViewDistances;
        if (map.containsKey(identifier)) {
            var value = map.getInt(identifier);
            ((EvdEntityType) type).evd_setTrackingDistance(MathHelper.clamp(value, -1, 32 * 16));
        }
    }

    public static void updateAll() {
        var map = ConfigManager.getConfig().entityViewDistances;

        for (var entry : Registry.ENTITY_TYPE) {
            var identifier = Registry.ENTITY_TYPE.getId(entry);
            var value = map.getOrDefault(identifier, -1);
            ((EvdEntityType) entry).evd_setTrackingDistance(MathHelper.clamp(value, -1, 32 * 16));
        }
    }

    public static void updateServer(MinecraftServer server) {
        boolean apply = ConfigManager.getConfig().mode.server;

        for (var world :server.getWorlds()) {
            for (var entry : ((ThreadedAnvilChunkStorageAccessor) world.getChunkManager().threadedAnvilChunkStorage).getEntityTrackers().int2ObjectEntrySet()) {
                var tracker = (EntityTrackerAccessor) entry.getValue();

                var entity = tracker.getEntity();

                var value = apply ? ((EvdEntityType) entity.getType()).evd_getTrackingDistance() : -1;

                tracker.setMaxDistance(value > -1 ? Math.max(1, value) : entity.getType().getMaxTrackDistance() * 16);
            }
        }
    }
}
