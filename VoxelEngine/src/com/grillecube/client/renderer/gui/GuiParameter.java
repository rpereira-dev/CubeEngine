package com.grillecube.client.renderer.gui;

public abstract class GuiParameter<T extends Gui> {
	
	/** run the param to the given gui */
	public abstract void run(T gui);

}
