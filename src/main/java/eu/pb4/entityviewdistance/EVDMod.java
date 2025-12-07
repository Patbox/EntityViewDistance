package eu.pb4.entityviewdistance;

import eu.pb4.entityviewdistance.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EVDMod implements ModInitializer, DedicatedServerModInitializer, ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Entity View Distance");
    public static final String ID = "entity-view-distance";

    public static String VERSION = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion().getFriendlyString();

    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();
        EvdCommands.register();

        for (var entry : BuiltInRegistries.ENTITY_TYPE) {
            EvdUtils.initialize(entry, BuiltInRegistries.ENTITY_TYPE.getKey(entry));
        }

        RegistryEntryAddedCallback.event(BuiltInRegistries.ENTITY_TYPE).register(((rawId, id, entry) -> {
            EvdUtils.initialize(entry, id);
        }));
    }

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            ConfigManager.overrideConfig();
        });
    }

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register((client) -> {
            CardboardWarning.checkAndAnnounce();
            ConfigManager.overrideConfig();
        });
    }
}
