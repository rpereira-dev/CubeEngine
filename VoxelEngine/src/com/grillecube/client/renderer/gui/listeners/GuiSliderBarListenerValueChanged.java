package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.GuiSliderBar;

public interface GuiSliderBarListenerValueChanged {
	public void invokeSliderBarValueChanged(GuiSliderBar guiSliderBar, int selectedIndex, Object value);
}
