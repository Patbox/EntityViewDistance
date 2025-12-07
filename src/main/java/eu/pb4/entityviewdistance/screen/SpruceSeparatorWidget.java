/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SpruceUI.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.pb4.entityviewdistance.screen;

import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a separator element.
 *
 * @author LambdAurora
 */
public class SpruceSeparatorWidget extends ClickableWidget {
	public static final int TEXT_COLOR = 0xffe0e0e0;
	private final TextRenderer renderer;
	private Text title;
	private Text tooltip;
	private int tooltipTicks;
	private long lastTick;

	public SpruceSeparatorWidget(@Nullable Text title, TextRenderer renderer) {
		super(0, 0, 20, 9, title == null ? Text.empty() : title);
		this.height = 9;
		this.title = title;
		this.renderer = renderer;
	}

	/**
	 * Gets the title of this separator widget.
	 *
	 * @return the title
	 */
	public Optional<Text> getTitle() {
		return Optional.ofNullable(this.title);
	}

	/**
	 * Sets the title of this separator widget.
	 *
	 * @param title the title
	 */
	public void setTitle(@Nullable Text title) {
		this.title = title;
	}

	/* Rendering */

	@Override
	protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
		if (this.title != null) {
			int titleWidth = this.renderer.getWidth(this.title);
			int titleX = this.getX() + (this.getWidth() / 2 - titleWidth / 2);
			if (this.width > titleWidth) {
				graphics.fill(this.getX(), this.getY() + 4, titleX - 5, this.getY() + 6, TEXT_COLOR);
				graphics.fill(titleX + titleWidth + 5, this.getY() + 4, this.getX() + this.getWidth(), this.getY() + 6, TEXT_COLOR);
			}
			graphics.drawTextWithShadow(this.renderer, this.title, titleX, this.getY(), 0xFFFFFFFF);
		} else {
			graphics.fill(this.getX(), this.getY() + 4, this.getX() + this.getWidth(), this.getY() + 6, TEXT_COLOR);
		}

		//Tooltip.queueFor(this, mouseX, mouseY, this.tooltipTicks, i -> this.tooltipTicks = i, this.lastTick, i -> this.lastTick = i);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.title);
	}
}