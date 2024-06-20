package eu.pb4.entityviewdistance;

import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.interfaces.EvdEntityType;
import eu.pb4.entityviewdistance.mixin.EntityTrackerAccessor;
import eu.pb4.entityviewdistance.mixin.ServerChunkLoadingManagerAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EvdUtils {
    public static final String BUTTON_TEXT = getKey("button.options");
    public static final int MAX_DISTANCE = 32 * 16;

    public static MutableText getText(String type, Object... obj) {
        return Text.translatable("entityviewdistance" + "." + type, obj);
    }

    public static String getKey(String type) {
        return "entityviewdistance" + "." + type;
    }

    public static void initialize(EntityType type, Identifier identifier) {
        var map = ConfigManager.getConfig().entityViewDistances;
        if (map.containsKey(identifier)) {
            var value = map.getInt(identifier);
            ((EvdEntityType) type).evd_setTrackingDistance(MathHelper.clamp(value, -1, MAX_DISTANCE));
        }
    }

    public static void updateAll() {
        var map = ConfigManager.getConfig().entityViewDistances;

        for (var entry : Registries.ENTITY_TYPE) {
            var identifier = Registries.ENTITY_TYPE.getId(entry);
            var value = map.getOrDefault(identifier, -1);
            ((EvdEntityType) entry).evd_setTrackingDistance(MathHelper.clamp(value, -1, MAX_DISTANCE));
        }
    }

    public static void updateServer(MinecraftServer server) {
        boolean apply = ConfigManager.getConfig().mode.server;

        for (var world :server.getWorlds()) {
            for (var entry : ((ServerChunkLoadingManagerAccessor) world.getChunkManager().chunkLoadingManager).getEntityTrackers().int2ObjectEntrySet()) {
                var tracker = (EntityTrackerAccessor) entry.getValue();

                var entity = tracker.getEntity();

                var value = apply ? ((EvdEntityType) entity.getType()).evd_getTrackingDistance() : -1;

                tracker.setMaxDistance(value > -1 ? Math.max(1, value) : entity.getType().getMaxTrackDistance() * 16);
            }
        }
    }
}
