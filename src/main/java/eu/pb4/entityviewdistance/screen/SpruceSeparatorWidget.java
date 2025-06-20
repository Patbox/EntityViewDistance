/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SpruceUI.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.pb4.entityviewdistance.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a separator element.
 *
 * @author LambdAurora
 */
public class SpruceSeparatorWidget extends AbstractTextWidget {
	public static final int TEXT_COLOR = 0xffe0e0e0;
	private Text title;
	private Text tooltip;
	private int tooltipTicks;
	private long lastTick;

	public SpruceSeparatorWidget(@Nullable Text title, TextRenderer renderer) {
		super(0, 0, 20, 9, title == null ? Text.empty() : title, renderer);
		this.height = 9;
		this.title = title;
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
			int titleWidth = this.getTextRenderer().getWidth(this.title);
			int titleX = this.getX() + (this.getWidth() / 2 - titleWidth / 2);
			if (this.width > titleWidth) {
				graphics.fill(this.getX(), this.getY() + 4, titleX - 5, this.getY() + 6, TEXT_COLOR);
				graphics.fill(titleX + titleWidth + 5, this.getY() + 4, this.getX() + this.getWidth(), this.getY() + 6, TEXT_COLOR);
			}
			graphics.drawTextWithShadow(this.getTextRenderer(), this.title, titleX, this.getY(), 0xFFFFFFFF);
		} else {
			graphics.fill(this.getX(), this.getY() + 4, this.getX() + this.getWidth(), this.getY() + 6, TEXT_COLOR);
		}

		//Tooltip.queueFor(this, mouseX, mouseY, this.tooltipTicks, i -> this.tooltipTicks = i, this.lastTick, i -> this.lastTick = i);
	}
}