package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.GuiSliderBar;

public class GuiSliderBarEventSelect<T extends GuiSliderBar> extends GuiSliderBarEvent<T> {

	public GuiSliderBarEventSelect(T gui) {
		super(gui);
	}

}
