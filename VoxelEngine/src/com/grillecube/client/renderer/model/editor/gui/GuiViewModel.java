package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;

/** the gui which displays the model */
public class GuiViewModel extends Gui {

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		GuiViewWorld guiViewIngame = new GuiViewWorld();
		this.addChild(guiViewIngame);
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddedTo(Gui gui) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemovedFrom(Gui gui) {
		// TODO Auto-generated method stub

	}

}
