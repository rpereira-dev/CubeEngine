package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.Gui;

public abstract class GuiParameter<T extends Gui> {
	
	/** run the param to the given gui */
	public abstract void run(T gui);

}
