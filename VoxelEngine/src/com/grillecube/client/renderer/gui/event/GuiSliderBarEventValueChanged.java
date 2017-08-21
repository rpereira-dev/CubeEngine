package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSliderBar;

public class GuiSliderBarEventValueChanged<T extends GuiSliderBar> extends GuiSliderBarEvent<T> {

	private final int prevIndex;
	private final Object prevObject;

	public GuiSliderBarEventValueChanged(T gui, int prevIndex, Object prevObject) {
		super(gui);
		this.prevIndex = prevIndex;
		this.prevObject = prevObject;
	}

	public final int getPreviouslySelectedIndex() {
		return (this.prevIndex);
	}

	public final Object getPreviouslySelectedObject() {
		return (this.prevObject);
	}
}
