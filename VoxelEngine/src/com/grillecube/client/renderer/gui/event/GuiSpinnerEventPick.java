package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

public class GuiSpinnerEventPick<T extends GuiSpinner> extends GuiSpinnerEvent<T> {

	public GuiSpinnerEventPick(T gui) {
		super(gui);
	}
}
