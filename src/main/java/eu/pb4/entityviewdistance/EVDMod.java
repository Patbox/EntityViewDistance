package eu.pb4.entityviewdistance;

import eu.pb4.entityviewdistance.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EVDMod implements ModInitializer, DedicatedServerModInitializer, ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Entity View Distance");
    public static final String ID = "entity-view-distance";

    public static String VERSION = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion().getFriendlyString();

    @Override
    public void onInitialize() {
        this.crabboardDetection();
        ConfigManager.loadConfig();
        EvdCommands.register();

        for (var entry : Registry.ENTITY_TYPE) {
            EvdUtils.initialize(entry, Registry.ENTITY_TYPE.getId(entry));
        }

        RegistryEntryAddedCallback.event(Registry.ENTITY_TYPE).register(((rawId, id, entry) -> {
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
            this.crabboardDetection();
            ConfigManager.overrideConfig();
        });
    }

    private void crabboardDetection() {
        if (FabricLoader.getInstance().isModLoaded("cardboard")) {
            LOGGER.error("");
            LOGGER.error("Cardboard detected! This mod doesn't work with it!");
            LOGGER.error("You won't get any support as long as it's present!");
            LOGGER.error("");
            LOGGER.error("Read more: https://gist.github.com/Patbox/e44844294c358b614d347d369b0fc3bf");
            LOGGER.error("");
        }
    }
}
