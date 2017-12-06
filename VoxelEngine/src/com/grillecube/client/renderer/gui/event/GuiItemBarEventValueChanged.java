package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiItemBar;

public class GuiItemBarEventValueChanged<T extends GuiItemBar> extends GuiSliderBarEvent<T> {

	private final Object selectedObject;
	private final int selectedIndex;

	private final int prevIndex;
	private final Object prevObject;

	public GuiItemBarEventValueChanged(T gui, int prevIndex, Object prevObject) {
		super(gui);
		this.selectedObject = gui.getSelectedValue();
		this.selectedIndex = gui.getSelectedIndex();
		this.prevIndex = prevIndex;
		this.prevObject = prevObject;
	}

	public final int getPreviouslySelectedIndex() {
		return (this.prevIndex);
	}

	public final Object getPreviouslySelectedObject() {
		return (this.prevObject);
	}

	public final Object getSelectedObject() {
		return (this.selectedObject);
	}

	public final int getSelectedIndex() {
		return (this.selectedIndex);
	}
}
