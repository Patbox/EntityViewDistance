package eu.pb4.entityviewdistance.config;


import eu.pb4.entityviewdistance.EVDMod;
import eu.pb4.entityviewdistance.config.data.ConfigData;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import net.minecraft.resources.Identifier;


public final class Config {
    public final Object2IntMap<Identifier> entityViewDistances;
    public EvdOverrideSide mode;

    public Config(ConfigData data) {
        this.entityViewDistances = new Object2IntOpenHashMap<>();

        for (var entry : data.entityViewDistance.entrySet()) {
            var identifier = Identifier.tryParse(entry.getKey());

            if (identifier != null) {
                this.entityViewDistances.put(identifier, (int) entry.getValue());
            }
        }

        try {
            this.mode = EvdOverrideSide.valueOf(data.mode.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            EVDMod.LOGGER.warn("Invalid 'mode' config value! Defaulting to BOTH...");
            this.mode = EvdOverrideSide.BOTH;
        }
    }

    public ConfigData toConfigData() {
        var data = new ConfigData();
        data.mode = this.mode.toString().toLowerCase(Locale.ROOT);

        for (var entry : this.entityViewDistances.object2IntEntrySet()) {
            data.entityViewDistance.put(entry.getKey().toString(), entry.getIntValue());
        }

        return data;
    }

}
