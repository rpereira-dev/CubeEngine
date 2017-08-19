package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.GuiSpinner;

/**
 * a listener called when the element at given index is newly added to the
 * spinner
 */
public interface GuiSpinnerListenerAdd {
	public void invokeSpinnerAddObject(GuiSpinner guiSpinner, int index);
}
