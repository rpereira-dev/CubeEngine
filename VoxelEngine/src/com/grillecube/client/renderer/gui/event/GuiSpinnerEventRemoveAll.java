package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

public class GuiSpinnerEventRemoveAll<T extends GuiSpinner> extends GuiSpinnerEvent<T> {

	public GuiSpinnerEventRemoveAll(T gui) {
		super(gui);
	}
}
