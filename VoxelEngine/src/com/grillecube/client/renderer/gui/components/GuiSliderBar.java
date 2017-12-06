package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.event.GuiSliderBarEventSelect;

/** a slider bar */
public class GuiSliderBar extends Gui {

	/** prefix and suffix to be displayed */
	private String prefix;
	private String suffix;

	private float percent;

	public GuiSliderBar() {
		super();
		this.percent = 0.5f;
		this.prefix = "";
		this.suffix = "";
		this.addListener(ON_PRESS_FOCUS_LISTENER);
	}

	/** select the value at given index */
	public Object select(float percent) {
		if (percent < 0.0f) {
			percent = 0.0f;
		} else if (percent > 1.0f) {
			percent = 1.0f;
		}
		this.percent = percent;
		super.stackEvent(new GuiSliderBarEventSelect<GuiSliderBar>(this));
		return (this.percent);
	}

	/** the prefix to be displayed */
	public final void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/** the suffix to be displayed */
	public final void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public final String getPrefix() {
		return (this.prefix);
	}

	public final String getSuffix() {
		return (this.suffix);
	}

	/** get the percent progression of the selected value */
	public float getPercent() {
		return (this.percent);
	}

	@Override
	protected void onInputUpdate() {
		if (super.isPressed() && super.hasFocus() && super.isEnabled()) {
			this.select(this.getMouseX());
		}
	}
}