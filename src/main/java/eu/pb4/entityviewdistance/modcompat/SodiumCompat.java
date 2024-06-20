package eu.pb4.entityviewdistance.modcompat;

import eu.pb4.entityviewdistance.screen.EvdSettingsScreen;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionFlag;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;

import static eu.pb4.entityviewdistance.EvdUtils.getText;


public class SodiumCompat {
    public static final Option<Void> BUTTON = new FakeOptionImpl();

    public static final OptionStorage<Void> VOID_STORAGE = new OptionStorage<>() {
        @Override
        public Void getData() {
            return Void.VOID;
        }

        @Override
        public void save() {

        }
    };

    public static class FakeOptionImpl implements Option<Void> {
        private static final Text TITLE = getText("button.options");
        private static final Text TOOLTIP = Text.translatable("sodium.options.entity_distance.tooltip");
        private final Control<Void> control =  new CyclingControl<>(this, SodiumCompat.Void.class, new Text[]{ Text.empty(), Text.empty() });

        @Override
        public Text getName() {
            return TITLE;
        }

        @Override
        public Text getTooltip() {
            return TOOLTIP;
        }

        @Override
        public OptionImpact getImpact() {
            return OptionImpact.MEDIUM;
        }

        @Override
        public Control<Void> getControl() {
            return control;
        }

        @Override
        public Void getValue() {
            return Void.VOID;
        }

        @Override
        public void setValue(Void aVoid) {
            var client = MinecraftClient.getInstance();
            MinecraftClient.getInstance().setScreen(new EvdSettingsScreen(client.currentScreen, client.options));
        }

        @Override
        public void reset() {}

        @Override
        public OptionStorage<?> getStorage() {
            return VOID_STORAGE;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void applyChanges() {}

        @Override
        public Collection<OptionFlag> getFlags() {
            return Collections.emptyList();
        }
    }


    public enum Void {
        VOID,
        ACTIVE;
    }
}
