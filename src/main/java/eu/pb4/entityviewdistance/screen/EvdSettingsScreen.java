package eu.pb4.entityviewdistance.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.config.EvdOverrideSide;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static eu.pb4.entityviewdistance.EvdUtils.getText;

public class EvdSettingsScreen extends Screen {
    protected final Screen parent;
    protected final GameOptions gameOptions;
    @Nullable
    private EntryListWidget body;

    public EvdSettingsScreen(@Nullable Screen parent, GameOptions options) {
        super(Text.translatable("entityviewdistance.menu.title"));
        this.parent = parent;
        this.gameOptions = options;
    }

    protected void init() {
        var layout = new ThreePartsLayoutWidget(this);
        this.initHeader(layout);
        this.initBody(layout);
        this.initFooter(layout);
        this.initTabNavigation(layout);
        layout.forEachChild(this::addDrawableChild);
    }

    protected void initHeader(ThreePartsLayoutWidget layout) {
        layout.addHeader(this.title, this.textRenderer);
    }

    protected void initBody(ThreePartsLayoutWidget layout) {
        this.body = layout.addBody(new EntryListWidget(this.client, this.width, layout.getContentHeight(), layout.getHeaderHeight(), 25));
        this.addOptions();
    }

    protected void initFooter(ThreePartsLayoutWidget layout) {
        layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.close();
        }).width(200).build());
    }

    protected void initTabNavigation(ThreePartsLayoutWidget layout) {
        layout.refreshPositions();
        if (this.body != null) {
            this.body.position(this.width, layout);
        }
    }

    public void removed() {
        this.saveChanges();
    }

    public void close() {
        this.saveChanges();
        this.client.setScreen(this.parent);
    }

    protected void addOptions() {
        assert this.body != null;
        var scale = gameOptions.getEntityDistanceScaling();

        this.body.addEntry(OptionEntry.create(
                        CyclingButtonWidget.builder((EvdOverrideSide value) -> getText("menu.option.toggle", value.displayName))
                                .values(List.of(EvdOverrideSide.values()))
                                .tooltip(SimpleOption.constantTooltip(EvdOverrideSide.TOOLTIP))
                                .initially(ConfigManager.getConfig().mode)
                                .omitKeyText()
                                .build(0, 0, 150, 20, getText("menu.option.toggle"), (button, value) -> {
                                    ConfigManager.getConfig().mode = value;
                                }),
                        new SimpleOption<>("options.entityDistanceScaling", SimpleOption.constantTooltip(getText("menu.option.vanilla_dist.description")),
                                (text, value) -> getText("menu.option.vanilla_dist", (int) (value * 100.0)),
                                scale.getCallbacks(), Codec.doubleRange(0.5, 5.0), scale.getValue(), (value) -> {
                        }).createWidget(gameOptions, 0, 0, 150, (x) -> gameOptions.getEntityDistanceScaling().setValue(x)),
                        this
                )
        );

        this.body.addEntry(new Separator(getText("menu.separator.entity_options"), textRenderer));
        this.body.addEntry(new CenteredText(getText("menu.separator.entity_options.description").formatted(Formatting.GRAY), textRenderer));
        this.body.addEntry(new Separator(null, textRenderer));

        var entries = new ArrayList<EvdValueModifierOption>();

        for (var entry : Registries.ENTITY_TYPE) {
            if (entry.getMaxTrackDistance() == 0) {
                continue;
            }

            entries.add(new EvdValueModifierOption(entry, textRenderer));
        }
        entries.sort(Comparator.comparing(e -> e.nameString));

        for (var entry : entries) {
            this.body.addEntry(entry);
        }

    }

    private void saveChanges() {
        ConfigManager.overrideConfig();
        EvdUtils.updateAll();
        this.gameOptions.write();
        if (this.client.getServer() != null) {
            this.client.getServer().execute(() -> EvdUtils.updateServer(this.client.getServer()));
        }
    }

    private static class EntryListWidget extends ElementListWidget<Entry> {
        public EntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l) {
            super(minecraftClient, i, j, k, l);
        }

        @Override
        public int addEntry(EvdSettingsScreen.Entry entry) {
            return super.addEntry(entry);
        }

        public int getRowWidth() {
            return 310;
        }
    }

    public static abstract class Entry extends ElementListWidget.Entry<Entry> {
        protected void setPos(Widget label, int x, int y, int height) {
            label.setPosition(x, y + (height - label.getHeight()) / 2);
        }
    }

    public static class CenteredText extends Entry {
        private final MultilineTextWidget label;

        public CenteredText(final Text text, TextRenderer renderer) {
            this.label = new MultilineTextWidget(text, renderer);
        }

        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.label.setCentered(true);
            this.setPos(this.label, x + (entryWidth - this.label.getWidth()) / 2, y, entryHeight);
            this.label.render(context, mouseX, mouseY, tickDelta);
        }

        public List<? extends Element> children() {
            return List.of(label);
        }

        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }
    }

    public static class Separator extends Entry {
        private final SpruceSeparatorWidget separator;

        public Separator(@Nullable Text text, TextRenderer renderer) {
            this.separator = new SpruceSeparatorWidget(text, renderer);
        }

        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.setPos(this.separator, x, y, entryHeight);
            this.separator.setWidth(entryWidth);
            this.separator.render(context, mouseX, mouseY, tickDelta);
        }

        public List<? extends Element> children() {
            return List.of();
        }

        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }
    }

    private static class OptionEntry extends Entry {
        private final List<ClickableWidget> widgets;
        private final Screen screen;

        OptionEntry(List<ClickableWidget> widgets, Screen screen) {
            this.widgets = ImmutableList.copyOf(widgets);
            this.screen = screen;
        }

        public static OptionEntry create(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget, Screen screen) {
            return secondWidget == null
                    ? new OptionEntry(ImmutableList.of(firstWidget), screen)
                    : new OptionEntry(ImmutableList.of(firstWidget, secondWidget), screen);
        }

        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int i = 0;
            int j = this.screen.width / 2 - 155;

            for (var var13 = this.widgets.iterator(); var13.hasNext(); i += 160) {
                var widget = var13.next();
                this.setPos(widget, j + i, y, entryHeight);
                widget.render(context, mouseX, mouseY, tickDelta);
            }

        }

        public List<? extends Element> children() {
            return this.widgets;
        }

        public List<? extends Selectable> selectableChildren() {
            return this.widgets;
        }
    }
}
