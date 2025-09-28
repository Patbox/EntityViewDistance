package eu.pb4.entityviewdistance.screen;

import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static eu.pb4.entityviewdistance.EvdUtils.getText;

public class EvdValueModifierOption extends EvdSettingsScreen.Entry {

    private final EntityType<?> type;
    private final Identifier identifier;
    private final Text name;
    public final String nameString;

    private final List<Selectable> selectables;
    private final List<Element> all;
    private final ButtonWidget plus;
    private final TextFieldWidget text;
    private final ButtonWidget minus;
    private final ScrollableTextWidget label;

    public EvdValueModifierOption(EntityType<?> type, TextRenderer renderer) {
        this.type = type;
        this.identifier = Registries.ENTITY_TYPE.getId(type);
        var possibleName = this.type.getName();
        if (possibleName.getContent() instanceof TranslatableTextContent text && !Language.getInstance().hasTranslation(text.getKey())) {
            possibleName = Text.literal(Registries.ENTITY_TYPE.getId(this.type).toString());
        }
        this.name = possibleName;
        this.nameString = this.name.getString();

        this.label = null;//= new ScrollableTextWidget(0, 20, 100, 9*2, this.name.copy().formatted(Formatting.GRAY), renderer);

        this.text = new TextFieldWidget(renderer, 50, 20, Text.empty()) {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);
                if (!focused) {
                    if (this.getText().isEmpty() || this.getText().equals("-")) {
                        this.setText("-1");
                    }
                } else {
                    if (this.getText().equals("-1")) {
                        this.setText("");
                    }
                }
            }
        };

        text.setChangedListener((input) -> {
            if (!input.isEmpty() && !input.equals("-")) {
                try {
                    var maxDistance = EvdUtils.MAX_DISTANCE;

                    if (MinecraftClient.getInstance().options.getViewDistance().getCallbacks() instanceof SimpleOption.ValidatingIntSliderCallbacks callbacks) {
                        maxDistance = callbacks.maxInclusive() * 16;
                    }

                    var value = Integer.parseInt(input);
                    this.setValue(MathHelper.clamp(value, -1, maxDistance));
                    if (this.getValue() == -1) {
                        text.setEditableColor(0xff777777);
                    } else if (value > maxDistance) {
                        text.setEditableColor(0xffff2222);
                    } else {
                        text.setEditableColor(0xffffffff);
                    }
                    //label.setText(getText("menu.options.type", this.type.getName().shallowCopy().formatted(Formatting.GRAY), this.getDefault()).formatted(Formatting.DARK_GRAY));
                } catch (Exception e) {
                    // Silence!
                }
            }
        });

        text.setTextPredicate((input) -> {
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

        text.setText("" + this.getValue());

        if (this.getValue() == -1) {
            text.setEditableColor(0xff777777);
        }

        this.text.setTooltip(Tooltip.of(getText("menu.options.default", this.getDefault())));

        this.minus = ButtonWidget.builder(Text.literal("-"), (x) -> {
            if (MinecraftClient.getInstance().isShiftPressed()) {
                text.setText("" + (this.getValue() - 10));
            } else {
                text.setText("" + (this.getValue() - 1));
            }
        }).width(20).build();

        this.plus = ButtonWidget.builder (Text.literal("+"), (x) -> {
            if (MinecraftClient.getInstance().isShiftPressed()) {
                text.setText("" + (Math.max(this.getValue(), 0) + 10));
            } else {
                text.setText("" + (Math.max(this.getValue(), 0) + 1));
            }
        }).width(20).build();
        
        this.selectables = List.of(minus, text, plus);
        this.all = List.of(minus, text, plus);
    }

    private int getDefault() {
        return this.type.getMaxTrackDistance() * 16;
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

    private Text getActiveText() {
        return this.getValue() != 0 ? getText("menu.options.type.status.active").formatted(Formatting.GREEN) : getText("menu.options.type.status.default").formatted(Formatting.RED);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, this.name, this.getX(), this.getY() + this.getHeight() / 2 - 3, Colors.LIGHT_GRAY);

        this.setPos(this.plus,  this.getX() + this.getWidth() - 25, this.getY(), this.getHeight());
        this.plus.render(context, mouseX, mouseY, deltaTicks);

        this.setPos(this.text, this.getX() + this.getWidth() - 25 - 55, this.getY(), this.getHeight());
        this.text.render(context, mouseX, mouseY, deltaTicks);

        this.setPos(this.minus, this.getX() + this.getWidth() - 25 - 55 - 25, this.getY(), this.getHeight());
        this.minus.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.selectables;
    }

    @Override
    public List<? extends Element> children() {
        return this.all;
    }
}
