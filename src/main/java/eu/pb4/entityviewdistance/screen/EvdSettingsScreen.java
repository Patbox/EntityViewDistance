package eu.pb4.entityviewdistance.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.*;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import eu.pb4.entityviewdistance.EvdUtils;
import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.config.EvdOverrideSide;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
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
        super(Text.literal("Entity View Distance Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.list = new SpruceOptionListWidget(Position.of(0, 32), this.width, this.height - 32 - 32) {
            @Override
            protected int getScrollbarPositionX() {
                return this.width / 2 + 124 + 32;
            }

            @Override
            protected void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                int scrollbarPositionX = this.getScrollbarPositionX();
                int scrollBarEnd = scrollbarPositionX + 6;
                int left = this.getX();
                int right = left + this.getWidth();
                int top = this.getY();
                int bottom = top + this.getHeight();

                ScissorManager.push(this.getX(), this.getY(), this.getWidth(), this.getHeight());
                for (var id = 0; id < this.getEntriesCount(); id++) {
                    this.getEntry(id).render(matrices, mouseX, mouseY, delta);
                }
                ScissorManager.pop();

                var tessellator = Tessellator.getInstance();
                var buffer = tessellator.getBuffer();
                // Render the transition thingy.
                if (this.shouldRenderTransition()) {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                            GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
                    );
                    RenderSystem.disableTexture();
                    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                    // TOP
                    buffer.vertex(left, top + 4, 0).color(0, 0, 0, 0).next();
                    buffer.vertex(right, top + 4, 0).color(0, 0, 0, 0).next();
                    buffer.vertex(right, top, 0).color(0, 0, 0, 255).next();
                    buffer.vertex(left, top, 0).color(0, 0, 0, 255).next();
                    // RIGHT
                    buffer.vertex(right - 4, bottom, 0).color(0, 0, 0, 0).next();
                    buffer.vertex(right, bottom, 0).color(0, 0, 0, 255).next();
                    buffer.vertex(right, top, 0).color(0, 0, 0, 255).next();
                    buffer.vertex(right - 4, top, 0).color(0, 0, 0, 0).next();
                    // BOTTOM
                    buffer.vertex(left, bottom, 0).color(0, 0, 0, 255).next();
                    buffer.vertex(right, bottom, 0).color(0, 0, 0, 255).next();
                    buffer.vertex(right, bottom - 4, 0).color(0, 0, 0, 0).next();
                    buffer.vertex(left, bottom - 4, 0).color(0, 0, 0, 0).next();
                    tessellator.draw();
                }

                // Scrollbar
                int maxScroll = this.getMaxScroll();
                if (maxScroll > 0) {
                    RenderSystem.disableTexture();
                    int scrollbarHeight = (int) ((float) ((this.getHeight()) * (this.getHeight())) / (float) this.getMaxPosition());
                    scrollbarHeight = MathHelper.clamp(scrollbarHeight, 32, this.getHeight() - 8);
                    int scrollbarY = (int) this.getScrollAmount() * (this.getHeight() - scrollbarHeight) / maxScroll + this.getY();
                    if (scrollbarY < this.getY()) {
                        scrollbarY = this.getY();
                    }

                    this.renderScrollbar(tessellator, buffer, scrollbarPositionX, scrollBarEnd, scrollbarY, scrollbarHeight);
                }

                this.getBorder().render(matrices, this, mouseX, mouseY, delta);

                RenderSystem.enableTexture();
                RenderSystem.disableBlend();
            }
        };

        var toggleValue = new AtomicInteger(ConfigManager.getConfig().mode.ordinal());

        this.list.addOptionEntry(new SpruceCyclingOption(getKey("menu.option.toggle"),
                (i) -> ConfigManager.getConfig().mode = EvdOverrideSide.values()[toggleValue.incrementAndGet() % EvdOverrideSide.values().length],
                (option) -> getText("menu.option.toggle", EvdOverrideSide.values()[toggleValue.get() % EvdOverrideSide.values().length].displayName),
                EvdOverrideSide.TOOLTIP
        ), new SpruceDoubleOption(getKey("menu.option.vanilla_dist"),
                0.5d,
                5.0d,
                0.25f,
                () -> MinecraftClient.getInstance().options.getEntityDistanceScaling().getValue(),
                (value) -> MinecraftClient.getInstance().options.getEntityDistanceScaling().setValue(value),
                (value) -> Text.translatable(getKey("menu.option.vanilla_dist"), (int) (value.get() * 100d)),
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

        for (var entry : Registries.ENTITY_TYPE) {
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
                    saveChanges();
                }).asVanilla());
    }

    private void saveChanges() {
        ConfigManager.overrideConfig();
        EvdUtils.updateAll();
        if (this.client.getServer() != null) {
            this.client.getServer().execute(() -> EvdUtils.updateServer(this.client.getServer()));
        }

        this.client.setScreen(this.parent);
    }

    @Override
    public void close() {
        this.saveChanges();
        super.close();
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        this.renderBackgroundTexture(0);
    }

    @Override
    public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
    }
}
