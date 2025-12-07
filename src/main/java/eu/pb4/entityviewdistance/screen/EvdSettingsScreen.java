package eu.pb4.entityviewdistance.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.config.EvdOverrideSide;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static eu.pb4.entityviewdistance.EvdUtils.getText;

public class EvdSettingsScreen extends Screen {
    protected final Screen parent;
    protected final Options gameOptions;
    @Nullable
    private EntryListWidget body;
    private EditBox searchStringWidget;

    public EvdSettingsScreen(@Nullable Screen parent, Options options) {
        super(Component.translatable("entityviewdistance.menu.title"));
        this.parent = parent;
        this.gameOptions = options;
    }

    protected void init() {
        var layout = new HeaderAndFooterLayout(this);
        this.initHeader(layout);
        this.initBody(layout);
        this.initFooter(layout);
        this.initTabNavigation(layout);
        layout.visitWidgets(this::addRenderableWidget);
    }

    protected void initHeader(HeaderAndFooterLayout layout) {
        layout.addTitleHeader(this.title, this.font);
        this.searchStringWidget = new EditBox(this.font, 0, 0, 310 / 3, 20, this.searchStringWidget, Component.translatable("debug.options.search").withStyle(EditBox.SEARCH_HINT_STYLE));
        this.searchStringWidget.setResponder(this::updateOptions);
        this.searchStringWidget.setHint(Component.translatable("debug.options.search").withStyle(EditBox.SEARCH_HINT_STYLE));
        layout.addToHeader(this.searchStringWidget, x -> x.alignHorizontallyRight().paddingRight(Math.max(10, ((this.width - 310) / 2) - 30)));
    }

    protected void initBody(HeaderAndFooterLayout layout) {
        this.body = layout.addToContents(new EntryListWidget(this.minecraft, this.width, layout.getContentHeight(), layout.getHeaderHeight(), 25));
        this.updateOptions(this.searchStringWidget.getValue());
    }

    protected void initFooter(HeaderAndFooterLayout layout) {
        layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.onClose();
        }).width(200).build());
    }

    protected void initTabNavigation(HeaderAndFooterLayout layout) {
        layout.arrangeElements();
        if (this.body != null) {
            this.body.updateSize(this.width, layout);
        }
    }

    public void removed() {
        this.saveChanges();
    }

    public void onClose() {
        this.saveChanges();
        this.minecraft.setScreen(this.parent);
    }

    protected void updateOptions(String search) {
        assert this.body != null;
        var scale = gameOptions.entityDistanceScaling();

        this.body.clearEntries();

        this.body.addEntry(OptionEntry.create(
                        CycleButton.builder((EvdOverrideSide value) -> getText("menu.option.toggle", value.displayName), ConfigManager.getConfig().mode)
                                .withValues(List.of(EvdOverrideSide.values()))
                                .withTooltip(OptionInstance.cachedConstantTooltip(EvdOverrideSide.TOOLTIP))
                                .displayOnlyValue()
                                .create(0, 0, 150, 20, getText("menu.option.toggle"), (button, value) -> {
                                    ConfigManager.getConfig().mode = value;
                                }),
                        new OptionInstance<>("options.entityDistanceScaling", OptionInstance.cachedConstantTooltip(getText("menu.option.vanilla_dist.description")),
                                (text, value) -> getText("menu.option.vanilla_dist", (int) (value * 100.0)),
                                scale.values(), Codec.doubleRange(0.5, 5.0), scale.get(), (value) -> {
                        }).createButton(gameOptions, 0, 0, 150, (x) -> gameOptions.entityDistanceScaling().set(x)),
                        this
                )
        );

        this.body.addEntry(new Separator(getText("menu.separator.entity_options"), font));
        this.body.addEntry(new CenteredText(getText("menu.separator.entity_options.description").withStyle(ChatFormatting.GRAY), font));
        this.body.addEntry(new Separator(null, font));

        var entries = new ArrayList<EvdValueModifierOption>();

        Predicate<EntityType<?>> predicate;

        search = search.toLowerCase(Locale.ROOT);
        if (search.isEmpty()) {
            predicate = entry -> true;
        } else if (search.startsWith("#")) {
            var id = Identifier.tryParse(search.substring(1));
            if (id != null) {
                var tag = TagKey.create(Registries.ENTITY_TYPE, id);
                predicate = entry -> entry.is(tag);
            } else {
                String finalSearch = search;
                predicate = entry -> entry.getDescription().getString().toLowerCase(Locale.ROOT).contains(finalSearch);
            }
        } else if (search.startsWith("@")) {
            var namespace = search.substring(1);
            predicate = entry -> entry.builtInRegistryHolder().unwrapKey().orElseThrow().identifier().getNamespace().contains(namespace);
        } else if (search.startsWith("$")) {
            var namespace = search.substring(1);
            predicate = entry -> entry.builtInRegistryHolder().unwrapKey().orElseThrow().identifier().getPath().contains(namespace);
        } else {
            String finalSearch = search;
            predicate = entry -> entry.getDescription().getString().toLowerCase(Locale.ROOT).contains(finalSearch);
        }

        for (var entry : BuiltInRegistries.ENTITY_TYPE) {
            if (entry.clientTrackingRange() == 0 || !predicate.test(entry)) {
                continue;
            }

            entries.add(new EvdValueModifierOption(entry, font));
        }
        entries.sort(Comparator.comparing(e -> e.nameString));

        for (var entry : entries) {
            this.body.addEntry(entry);
        }
    }

    private void saveChanges() {
        ConfigManager.overrideConfig();
        EvdUtils.updateAll();
        this.gameOptions.save();
        if (this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.getSingleplayerServer().execute(() -> EvdUtils.updateServer(this.minecraft.getSingleplayerServer()));
        }
    }

    private static class EntryListWidget extends ContainerObjectSelectionList<eu.pb4.entityviewdistance.screen.EvdSettingsScreen.Entry> {
        public EntryListWidget(Minecraft minecraftClient, int i, int j, int k, int l) {
            super(minecraftClient, i, j, k, l);
        }

        @Override
        public int addEntry(EvdSettingsScreen.Entry entry) {
            return super.addEntry(entry);
        }

        @Override
        public void clearEntries() {
            super.clearEntries();
        }

        public int getRowWidth() {
            return 310;
        }
    }

    public static abstract class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        protected void setPos(LayoutElement label, int x, int y, int height) {
            label.setPosition(x, y + (height - label.getHeight()) / 2);
        }
    }

    public static class CenteredText extends Entry {
        private final MultiLineTextWidget label;

        public CenteredText(final Component text, Font renderer) {
            this.label = new MultiLineTextWidget(text, renderer);
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            this.label.setCentered(true);
            this.setPos(this.label, this.getX() + (this.getWidth() - this.label.getWidth()) / 2, this.getY(), this.getHeight());
            this.label.render(context, mouseX, mouseY, deltaTicks);
        }

        public List<? extends GuiEventListener> children() {
            return List.of(label);
        }

        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    public static class Separator extends Entry {
        private final SpruceSeparatorWidget separator;

        public Separator(@Nullable Component text, Font renderer) {
            this.separator = new SpruceSeparatorWidget(text, renderer);
        }

        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.setPos(this.separator, x, y, entryHeight);
            this.separator.setWidth(entryWidth);
            this.separator.render(context, mouseX, mouseY, tickDelta);
        }

        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public int getHeight() {
            return this.separator.getHeight();
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            //this.setPos(this.separator, x, y, entryHeight);
            this.separator.setWidth(this.getWidth());
            this.separator.setPosition(this.getX(), this.getY());
            this.separator.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    private static class OptionEntry extends Entry {
        private final List<AbstractWidget> widgets;
        private final Screen screen;

        OptionEntry(List<AbstractWidget> widgets, Screen screen) {
            this.widgets = ImmutableList.copyOf(widgets);
            this.screen = screen;
        }

        public static OptionEntry create(AbstractWidget firstWidget, @Nullable AbstractWidget secondWidget, Screen screen) {
            return secondWidget == null
                    ? new OptionEntry(ImmutableList.of(firstWidget), screen)
                    : new OptionEntry(ImmutableList.of(firstWidget, secondWidget), screen);
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int i = 0;
            int j = this.screen.width / 2 - 155;

            for (var var13 = this.widgets.iterator(); var13.hasNext(); i += 160) {
                var widget = var13.next();
                this.setPos(widget, j + i, this.getY(), this.getHeight());
                widget.setY(this.getY());
                widget.render(context, mouseX, mouseY, deltaTicks);
            }

        }

        public List<? extends GuiEventListener> children() {
            return this.widgets;
        }

        public List<? extends NarratableEntry> narratables() {
            return this.widgets;
        }
    }
}
