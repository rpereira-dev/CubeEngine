package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.animations.GuiAnimation;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAlignLeft;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterYBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.common.utils.Color;

public class GuiButton extends GuiLabel {

	private static final Color HOVERED_COLOR = new Color(0.6f, 0.6f, 1.0f, 1.0f);
	private static final Color OUT_COLOR = new Color(0.87f, 0.87f, 0.87f, 1.0f);
	private static final Color PRESSED_COLOR = new Color(0.5f, 0.5f, 0.9f, 1.0f);
	private static final Color DISABLED_COLOR = new Color(0.5f, 0.5f, 0.5f, 1.0f);

	private final GuiColoredQuad bg;
	private final Color hoveredColor;
	private final Color outColor;
	private final Color pressedColor;
	private final Color disabledColor;
	private float transition;

	public GuiButton() {
		this(0.15f);
	}

	public GuiButton(float transition) {
		super();

		this.addListener(ON_PRESS_FOCUS_LISTENER);
		this.addListener(ON_UNPRESS_FOCUS_LISTENER);

		this.transition = transition;
		this.hoveredColor = new Color(0);
		this.outColor = new Color(0);
		this.pressedColor = new Color(0);
		this.disabledColor = new Color(0);

		this.bg = new GuiColoredQuad();
		this.addChild(0, this.bg);
		this.bg.setHoverable(false);

		this.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addTextParameter(new GuiTextParameterTextCenterYBox());
		this.addTextParameter(new GuiTextParameterTextAlignLeft(0.1f));

		this.setDefaultColors();

		super.startAnimation(new GuiAnimation<GuiButton>() {

			double lastUpdate;
			double transition;

			@Override
			public void onStart(GuiButton guiButton) {
				this.lastUpdate = super.getTimer().getTime();
				this.transition = 0.0d;
			}

			@Override
			public void onStop(GuiButton guiButton) {
			}

			@Override
			public boolean run(GuiButton guiButton) {
				Color color;
				if (!guiButton.isEnabled()) {
					color = guiButton.getDisabledColor();
				} else if (guiButton.isPressed() || guiButton.isSelected()) {
					color = guiButton.getPressedColor();
				} else if (guiButton.isHovered()) {
					if (guiButton.getTransition() <= 0) {
						color = guiButton.getHoveredColor();
					} else {
						this.transition += (this.getTimer().getTime() - this.lastUpdate);
						if (this.transition > guiButton.getTransition()) {
							this.transition = guiButton.getTransition();
						}
						float ratio = (float) (this.transition / guiButton.getTransition());
						color = Color.mix(guiButton.getOutColor(), guiButton.getHoveredColor(), ratio, null);
					}
				} else {
					if (guiButton.getTransition() <= 0) {
						color = guiButton.getOutColor();
					} else {
						this.transition -= (this.getTimer().getTime() - this.lastUpdate);
						if (this.transition < 0.0d) {
							this.transition = 0.0d;
						}
						float ratio = (float) (this.transition / guiButton.getTransition());
						color = Color.mix(guiButton.getOutColor(), guiButton.getHoveredColor(), ratio, null);
					}
				}
				guiButton.setBGColor(color);

				this.lastUpdate = super.getTimer().getTime();
				return (false);
			}
		});
	}

	public final void setTransition(float dt) {
		this.transition = dt;
	}

	public final float getTransition() {
		return (this.transition);
	}

	public final void setBGColor(Color color) {
		this.bg.setColor(color);
	}

	public final void setHoveredColor(Color color) {
		this.hoveredColor.set(color.getARGB());
	}

	public final void setHoveredColor(float r, float g, float b, float a) {
		this.hoveredColor.set(r, g, b, a);
	}

	public final void setOutColor(Color color) {
		this.setOutColor(color.getR(), color.getG(), color.getB(), color.getA());
	}

	public final void setOutColor(float r, float g, float b, float a) {
		this.outColor.set(r, g, b, a);
	}

	public final void setPressedColor(Color color) {
		this.setPressedColor(color.getR(), color.getG(), color.getB(), color.getA());
	}

	public final void setPressedColor(float r, float g, float b, float a) {
		this.pressedColor.set(r, g, b, a);
	}

	public final void setDisabledColor(Color color) {
		this.setDisabledColor(color.getR(), color.getG(), color.getB(), color.getA());
	}

	public final void setDisabledColor(float r, float g, float b, float a) {
		this.disabledColor.set(r, g, b, a);
	}

	public final Color getHoveredColor() {
		return (this.hoveredColor);
	}

	public final Color getOutColor() {
		return (this.outColor);
	}

	public final Color getPressedColor() {
		return (this.pressedColor);
	}

	public final Color getDisabledColor() {
		return (this.disabledColor);
	}

	public void setColors(GuiButton guiButton) {
		this.setHoveredColor(guiButton.getHoveredColor());
		this.setOutColor(guiButton.getOutColor());
		this.setPressedColor(guiButton.getPressedColor());
		this.setDisabledColor(guiButton.getDisabledColor());
	}

	public void setDefaultColors() {
		this.setHoveredColor(HOVERED_COLOR);
		this.setOutColor(OUT_COLOR);
		this.setPressedColor(PRESSED_COLOR);
		this.setDisabledColor(DISABLED_COLOR);
	}
}
