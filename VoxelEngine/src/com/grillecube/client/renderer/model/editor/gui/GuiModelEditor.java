package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolbox;

public class GuiModelEditor extends Gui {

	public GuiModelEditor() {
		super();

		// the toolbox
		GuiToolbox toolbox = new GuiToolbox();
		toolbox.setBox(0.0f, 0, 0.25f, 1.0f, 0);
		this.addChild(toolbox);

		// the model viewer
		GuiViewModel viewModel = new GuiViewModel();
		viewModel.setBox(0.25f, 0, 0.75f, 1.0f, 0);
		this.addChild(viewModel);
	}
}
