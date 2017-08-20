package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolbox;

public class GuiModelEditor extends Gui {

	@Override
	protected void onInitialized(GuiRenderer renderer) {

		// the toolbox
		GuiToolbox toolbox = new GuiToolbox();
		toolbox.setBox(0.0f, 0, 0.25f, 1.0f, 0);
		this.addChild(toolbox);

		// the model viewer
		GuiViewModel viewModel = new GuiViewModel();
		viewModel.setBox(0.25f, 0, 0.75f, 1.0f, 0);
		this.addChild(viewModel);
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
