package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.animations.GuiAnimation;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAlignLeft;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterYBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.common.maths.Vector4f;

public class GuiButton extends GuiLabel {

	private static final Vector4f HOVERED_COLOR = new Vector4f(0.6f, 0.6f, 1.0f, 1.0f);
	private static final Vector4f OUT_COLOR = new Vector4f(0.87f, 0.87f, 0.87f, 1.0f);
	private static final Vector4f PRESSED_COLOR = new Vector4f(0.5f, 0.5f, 0.9f, 1.0f);
	private static final Vector4f DISABLED_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);

	private final GuiColoredQuad bg;
	private final Vector4f hoveredColor;
	private final Vector4f outColor;
	private final Vector4f pressedColor;
	private final Vector4f disabledColor;
	private float transition;

	public GuiButton() {
		this(0.15f);
	}

	public GuiButton(float transition) {
		super();

		this.addListener(ON_PRESS_FOCUS_LISTENER);

		this.transition = transition;
		this.hoveredColor = new Vector4f();
		this.outColor = new Vector4f();
		this.pressedColor = new Vector4f();
		this.disabledColor = new Vector4f();

		this.bg = new GuiColoredQuad();
		this.addChild(0, this.bg);
		this.bg.setHoverable(false);

		this.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addTextParameter(new GuiTextParameterTextCenterYBox());
		this.addTextParameter(new GuiTextParameterTextAlignLeft(0.1f));

		this.setHoveredColor(HOVERED_COLOR);
		this.setOutColor(OUT_COLOR);
		this.setPressedColor(PRESSED_COLOR);
		this.setDisabledColor(DISABLED_COLOR);

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
				Vector4f color;
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
						color = Vector4f.mix(guiButton.getOutColor(), guiButton.getHoveredColor(), ratio, null);
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
						color = Vector4f.mix(guiButton.getOutColor(), guiButton.getHoveredColor(), ratio, null);
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

	public final void setBGColor(Vector4f color) {
		this.bg.setColor(color);
	}

	public final void setHoveredColor(Vector4f color) {
		this.setHoveredColor(color.x, color.y, color.z, color.w);
	}

	public final void setHoveredColor(float r, float g, float b, float a) {
		this.hoveredColor.set(r, g, b, a);
	}

	public final void setOutColor(Vector4f color) {
		this.setOutColor(color.x, color.y, color.z, color.w);
	}

	public final void setOutColor(float r, float g, float b, float a) {
		this.outColor.set(r, g, b, a);
	}

	public final void setPressedColor(Vector4f color) {
		this.setPressedColor(color.x, color.y, color.z, color.w);
	}

	public final void setPressedColor(float r, float g, float b, float a) {
		this.pressedColor.set(r, g, b, a);
	}

	public final void setDisabledColor(Vector4f color) {
		this.setDisabledColor(color.x, color.y, color.z, color.w);
	}

	public final void setDisabledColor(float r, float g, float b, float a) {
		this.disabledColor.set(r, g, b, a);
	}

	public final Vector4f getHoveredColor() {
		return (this.hoveredColor);
	}

	public final Vector4f getOutColor() {
		return (this.outColor);
	}

	public final Vector4f getPressedColor() {
		return (this.pressedColor);
	}

	public final Vector4f getDisabledColor() {
		return (this.disabledColor);
	}
}
