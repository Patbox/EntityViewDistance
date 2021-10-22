package eu.pb4.entityviewdistance.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.Text;

import static eu.pb4.entityviewdistance.EvdUtils.getText;

public class EvdSettingsButton extends Option {
    private static final String KEY = "entityviewdistance.menu.title";
    private final Text text;

    public EvdSettingsButton() {
        super(KEY);
        this.text = getText("button.options");
    }

    @Override
    public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
        return new ButtonWidget(x, y, width, 20, this.text, btn -> MinecraftClient.getInstance().setScreen(new EvdSettingsScreen(MinecraftClient.getInstance().currentScreen)));
    }
}