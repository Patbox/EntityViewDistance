package eu.pb4.entityviewdistance.screen;

import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import java.util.List;

import static eu.pb4.entityviewdistance.EvdUtils.getText;

public class EvdValueModifierOption extends EvdSettingsScreen.Entry {

    private final EntityType<?> type;
    private final Identifier identifier;
    private final Component name;
    public final String nameString;

    private final List<NarratableEntry> selectables;
    private final List<GuiEventListener> all;
    private final Button plus;
    private final EditBox text;
    private final Button minus;
    private final FittingMultiLineTextWidget label;

    public EvdValueModifierOption(EntityType<?> type, Font renderer) {
        this.type = type;
        this.identifier = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        var possibleName = this.type.getDescription();
        if (possibleName.getContents() instanceof TranslatableContents text && !Language.getInstance().has(text.getKey())) {
            possibleName = Component.literal(BuiltInRegistries.ENTITY_TYPE.getKey(this.type).toString());
        }
        this.name = possibleName;
        this.nameString = this.name.getString();

        this.label = null;//= new ScrollableTextWidget(0, 20, 100, 9*2, this.name.copy().formatted(Formatting.GRAY), renderer);

        this.text = new EditBox(renderer, 50, 20, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);
                if (!focused) {
                    if (this.getValue().isEmpty() || this.getValue().equals("-")) {
                        this.setValue("-1");
                    }
                } else {
                    if (this.getValue().equals("-1")) {
                        this.setValue("");
                    }
                }
            }
        };

        text.setResponder((input) -> {
            if (!input.isEmpty() && !input.equals("-")) {
                try {
                    var maxDistance = EvdUtils.MAX_DISTANCE;

                    if (Minecraft.getInstance().options.renderDistance().values() instanceof OptionInstance.IntRange callbacks) {
                        maxDistance = callbacks.maxInclusive() * 16;
                    }

                    var value = Integer.parseInt(input);
                    this.setValue(Mth.clamp(value, -1, maxDistance));
                    if (this.getValue() == -1) {
                        text.setTextColor(0xff777777);
                    } else if (value > maxDistance) {
                        text.setTextColor(0xffff2222);
                    } else {
                        text.setTextColor(0xffffffff);
                    }
                    //label.setText(getText("menu.options.type", this.type.getName().shallowCopy().formatted(Formatting.GRAY), this.getDefault()).formatted(Formatting.DARK_GRAY));
                } catch (Exception e) {
                    // Silence!
                }
            }
        });

        text.setFilter((input) -> {
            if (input.isEmpty() || input.equals("-")) {
                return true;
            }
            try {
                var i = Integer.parseInt(input);

                if (i >= -1) {
                    return true;
                }
            } catch (Exception e) {
                // Silence!
            }

            return false;
        });

        text.setValue("" + this.getValue());

        if (this.getValue() == -1) {
            text.setTextColor(0xff777777);
        }

        this.text.setTooltip(Tooltip.create(getText("menu.options.default", this.getDefault())));

        this.minus = Button.builder(Component.literal("-"), (x) -> {
            if (Minecraft.getInstance().hasShiftDown()) {
                text.setValue("" + (this.getValue() - 10));
            } else {
                text.setValue("" + (this.getValue() - 1));
            }
        }).width(20).build();

        this.plus = Button.builder (Component.literal("+"), (x) -> {
            if (Minecraft.getInstance().hasShiftDown()) {
                text.setValue("" + (Math.max(this.getValue(), 0) + 10));
            } else {
                text.setValue("" + (Math.max(this.getValue(), 0) + 1));
            }
        }).width(20).build();
        
        this.selectables = List.of(minus, text, plus);
        this.all = List.of(minus, text, plus);
    }

    private int getDefault() {
        return this.type.clientTrackingRange() * 16;
    }

    private int getValue() {
        return ConfigManager.getConfig().entityViewDistances.getOrDefault(this.identifier, -1);
    }

    private void setValue(int value) {
        if (value != -1) {
            ConfigManager.getConfig().entityViewDistances.put(this.identifier, value);
        } else {
            ConfigManager.getConfig().entityViewDistances.removeInt(this.identifier);
        }
    }

    private Component getActiveText() {
        return this.getValue() != 0 ? getText("menu.options.type.status.active").withStyle(ChatFormatting.GREEN) : getText("menu.options.type.status.default").withStyle(ChatFormatting.RED);
    }


    @Override
    public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawString(Minecraft.getInstance().font, this.name, this.getX(), this.getY() + this.getHeight() / 2 - 3, CommonColors.LIGHT_GRAY);

        this.setPos(this.plus,  this.getX() + this.getWidth() - 25, this.getY(), this.getHeight());
        this.plus.render(context, mouseX, mouseY, deltaTicks);

        this.setPos(this.text, this.getX() + this.getWidth() - 25 - 55, this.getY(), this.getHeight());
        this.text.render(context, mouseX, mouseY, deltaTicks);

        this.setPos(this.minus, this.getX() + this.getWidth() - 25 - 55 - 25, this.getY(), this.getHeight());
        this.minus.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return this.selectables;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.all;
    }
}
