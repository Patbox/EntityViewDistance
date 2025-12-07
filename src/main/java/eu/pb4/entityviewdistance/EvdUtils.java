package eu.pb4.entityviewdistance;

import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.interfaces.EvdEntityType;
import eu.pb4.entityviewdistance.mixin.TrackedEntityAccessor;
import eu.pb4.entityviewdistance.mixin.ChunkMapAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;

public class EvdUtils {
    public static final String BUTTON_TEXT = getKey("button.options");
    public static final int MAX_DISTANCE = 32 * 16;

    public static MutableComponent getText(String type, Object... obj) {
        return Component.translatable("entityviewdistance" + "." + type, obj);
    }

    public static String getKey(String type) {
        return "entityviewdistance" + "." + type;
    }

    public static void initialize(EntityType type, Identifier identifier) {
        var map = ConfigManager.getConfig().entityViewDistances;
        if (map.containsKey(identifier)) {
            var value = map.getInt(identifier);
            ((EvdEntityType) type).evd_setTrackingDistance(Math.max(value, -1));
        }
    }

    public static void updateAll() {
        var map = ConfigManager.getConfig().entityViewDistances;

        for (var entry : BuiltInRegistries.ENTITY_TYPE) {
            var identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entry);
            var value = map.getOrDefault(identifier, -1);
            ((EvdEntityType) entry).evd_setTrackingDistance(Math.max(value, -1));
        }
    }

    public static void updateServer(MinecraftServer server) {
        boolean apply = ConfigManager.getConfig().mode.server;

        for (var world :server.getAllLevels()) {
            for (var entry : ((ChunkMapAccessor) world.getChunkSource().chunkMap).getEntityMap().int2ObjectEntrySet()) {
                var tracker = (TrackedEntityAccessor) entry.getValue();

                var entity = tracker.getEntity();

                var value = apply ? ((EvdEntityType) entity.getType()).evd_getTrackingDistance() : -1;

                tracker.setRange(value > -1 ? Math.max(1, value) : entity.getType().clientTrackingRange() * 16);
            }
        }
    }
}
