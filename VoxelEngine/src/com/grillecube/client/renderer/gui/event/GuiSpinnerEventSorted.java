package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

public class GuiSpinnerEventSorted<T extends GuiSpinner> extends GuiSpinnerEvent<T> {

	public GuiSpinnerEventSorted(T gui) {
		super(gui);
	}
}
