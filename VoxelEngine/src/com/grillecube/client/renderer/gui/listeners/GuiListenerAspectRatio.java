package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.Gui;

/**
 * a listener called when the Gui box width/height changed, and so it aspect
 * ratio changes too
 */
public interface GuiListenerAspectRatio<T extends Gui> {
	public void invokeAspectRatioChanged(T gui, boolean runParameters);
}
