package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

public class GuiSpinnerEventExpanded<T extends GuiSpinner> extends GuiSpinnerEvent<T> {

	public GuiSpinnerEventExpanded(T gui) {
		super(gui);
	}
}
