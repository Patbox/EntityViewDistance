package eu.pb4.entityviewdistance.modcompat;

import eu.pb4.entityviewdistance.screen.EvdSettingsScreen;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SodiumCompat implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
                .setNonTintedIcon(Identifier.fromNamespaceAndPath("entityviewdistance", "icon.png"))
                .setColorTheme(builder.createColorTheme()
                        .setBaseThemeRGB(0xff57ff92)
                )
                .addPage(builder.createExternalPage()
                        .setName(Component.translatable("entityviewdistance.button.options.sodium"))
                        .setScreenConsumer(screen -> Minecraft.getInstance().setScreenAndShow(new EvdSettingsScreen(screen, Minecraft.getInstance().options)))
                )

        ;
    }
}
