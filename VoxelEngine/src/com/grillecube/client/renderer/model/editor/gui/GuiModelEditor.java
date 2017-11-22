package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolbox;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.instance.ModelInstance;

public class GuiModelEditor extends Gui {

	private final GuiToolbox toolbox;
	private final GuiModelView viewModel;

	public GuiModelEditor() {
		super();

		// the toolbox
		this.toolbox = new GuiToolbox();
		this.toolbox.setBox(0.0f, 0, 0.20f, 1.0f, 0);
		this.addChild(this.toolbox);

		// the model viewer
		this.viewModel = new GuiModelView();
		this.viewModel.setBox(0.20f, 0, 0.80f, 1.0f, 0);
		this.addChild(this.viewModel);
	}

	public final GuiToolbox getToolbox() {
		return (this.toolbox);
	}

	public final GuiModelView getModelView() {
		return (this.viewModel);
	}

	public final void onPickedModelChanged(ModelInstance prevModelInstance, ModelInstance newModelInstance) {
		// TODO Auto-generated method stub

	}

	public final void onModelInstanceAdded(ModelInstance modelInstance) {
		this.viewModel.addModelInstance(modelInstance);
	}

	public final void onModelInstanceRemoved(ModelInstance modelInstance) {
		this.viewModel.removeModelInstance(modelInstance);
	}

	public final ModelInstance getSelectedModelInstance() {
		return (this.toolbox.getSelectedModelInstance());
	}

	public final EditableModel getSelectedModel() {
		return (this.toolbox.getSelectedModel());
	}
}
