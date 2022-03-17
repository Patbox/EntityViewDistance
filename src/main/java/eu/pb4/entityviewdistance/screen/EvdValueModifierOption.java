package eu.pb4.entityviewdistance.screen;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.config.ConfigManager;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;


import static eu.pb4.entityviewdistance.EvdUtils.getText;

public class EvdValueModifierOption extends SpruceOption {

    private final EntityType type;
    private final Identifier identifier;
    private final Text name;
    public final String nameString;

    public EvdValueModifierOption(EntityType type) {
        super(type.getTranslationKey());
        this.type = type;
        this.identifier = Registry.ENTITY_TYPE.getId(type);
        var possibleName = this.type.getName();
        if (possibleName instanceof TranslatableText text && !Language.getInstance().hasTranslation(text.getKey())) {
            possibleName = new LiteralText(Registry.ENTITY_TYPE.getId(this.type).toString());
        }
        this.name = possibleName;
        this.nameString = this.name.getString();
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
    public SpruceWidget createWidget(Position position, int width) {
        var container = new SpruceContainerWidget(position, width, 20);
        container.addChildren((containerWidth, containerHeight, widgetAdder) -> {
            var label = new SpruceLabelWidget(Position.of(0, 0),
                    getText("menu.options.type", this.name.shallowCopy().formatted(Formatting.GRAY), this.getDefault()).formatted(Formatting.DARK_GRAY),
                    width - 80);
            widgetAdder.accept(label);

            var text = new SpruceTextFieldWidget(Position.of(containerWidth - 60, 0), 40, containerHeight, LiteralText.EMPTY) {
                @Override
                public void setFocused(boolean focused) {
                    super.setFocused(focused);

                    if (!focused) {
                        if (this.getText().isEmpty() || this.getText().equals("-")) {
                            this.setText("-1");
                        }
                        try {
                            var i = Integer.parseInt(this.getText());
                            if (i > 32 * 16) {
                                this.setText((32 * 16) + "");
                            }

                        } catch (Exception e) {}
                    } else {
                        if (this.getText().equals("-1")) {
                            this.setText("");
                        }
                    }
                }
            };

            widgetAdder.accept(new SpruceButtonWidget(Position.of(containerWidth - 80, 0), 20, containerHeight, new LiteralText("-"), (x) -> {
                if (Screen.hasShiftDown()) {
                    text.setText("" + (this.getValue() - 10));
                } else {
                    text.setText("" + (this.getValue() - 1));
                }
            }));
            widgetAdder.accept(new SpruceButtonWidget(Position.of(containerWidth - 20, 0), 20, containerHeight, new LiteralText("+"), (x) -> {
                if (Screen.hasShiftDown()) {
                    text.setText("" + (this.getValue() + 10));
                } else {
                    text.setText("" + (this.getValue() + 1));
                }
            }));

            text.setText("" + this.getValue());

            if (this.getValue() == -1) {
                text.setEditableColor(0x777777);
            }

            text.setChangedListener((input) -> {
                if (!input.isEmpty() && !input.equals("-")) {
                    try {
                        var value = Integer.parseInt(input);
                        this.setValue(MathHelper.clamp(value, -1, 32 * 16));
                        if (this.getValue() == -1) {
                            text.setEditableColor(0x777777);
                        } else if (value > 32 * 16) {
                            text.setEditableColor(0xff2222);
                        } else {
                            text.setEditableColor(0xffffff);
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

            widgetAdder.accept(text);
        });
        return container;
    }
}
