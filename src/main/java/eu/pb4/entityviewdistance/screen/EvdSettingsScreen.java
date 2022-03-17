package eu.pb4.entityviewdistance.screen;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.*;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.config.EvdOverrideSide;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import static eu.pb4.entityviewdistance.EvdUtils.getKey;
import static eu.pb4.entityviewdistance.EvdUtils.getText;

public class EvdSettingsScreen extends SpruceScreen {
    private final Screen parent;
    private SpruceOptionListWidget list;

    public EvdSettingsScreen(@Nullable Screen parent) {
        super(new LiteralText("Entity View Distance Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.list = new SpruceOptionListWidget(Position.of(0, 32), this.width, this.height - 32 - 32);

        var toggleValue = new AtomicInteger(ConfigManager.getConfig().mode.ordinal());

        this.list.addOptionEntry(new SpruceCyclingOption(getKey("menu.option.toggle"),
                (i) -> ConfigManager.getConfig().mode = EvdOverrideSide.values()[toggleValue.incrementAndGet() % EvdOverrideSide.values().length],
                (option) -> getText("menu.option.toggle", EvdOverrideSide.values()[toggleValue.get() % EvdOverrideSide.values().length].displayName),
                EvdOverrideSide.TOOLTIP
        ), new SpruceDoubleOption(getKey("menu.option.vanilla_dist"),
                0.5d,
                5.0d,
                0.25f,
                () -> (double) MinecraftClient.getInstance().options.entityDistanceScaling,
                (value) -> MinecraftClient.getInstance().options.entityDistanceScaling = (float) (double) value,
                (value) -> new TranslatableText(getKey("menu.option.vanilla_dist"), (int) (value.get() * 100d)),
                getText("menu.option.vanilla_dist.description")
        ));

        this.list.addSingleOptionEntry(new SpruceSeparatorOption(getKey("menu.separator.entity_options"), true, null));
        this.list.addSingleOptionEntry(new SpruceOption("") {
            @Override
            public SpruceWidget createWidget(Position position, int width) {
                return new SpruceLabelWidget(position, getText("menu.separator.entity_options.description"), width, true);
            }
        });

        this.list.addSingleOptionEntry(new SpruceSeparatorOption("", false, null));

        var entries = new ArrayList<EvdValueModifierOption>();

        for (var entry : Registry.ENTITY_TYPE) {
            if (entry.getMaxTrackDistance() == 0) {
                continue;
            }

            entries.add(new EvdValueModifierOption(entry));
        }
        entries.sort(Comparator.comparing(e -> e.nameString));

        for (var entry : entries) {
            this.list.addSingleOptionEntry(entry);
        }


        this.addDrawableChild(list);

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 100, this.height - 27), 200, 20, SpruceTexts.GUI_DONE,
                btn -> {
                    ConfigManager.overrideConfig();
                    EvdUtils.updateAll();
                    if (this.client.getServer() != null) {
                        this.client.getServer().execute(() -> EvdUtils.updateServer(this.client.getServer()));
                    }

                    this.client.setScreen(this.parent);
                }).asVanilla());
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        this.renderBackgroundTexture(0);
    }

    @Override
    public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 16777215);
    }
}
