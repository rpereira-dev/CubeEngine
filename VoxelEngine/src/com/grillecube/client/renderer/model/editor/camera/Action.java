package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.model.editor.mesher.EditableModel;

public abstract class Action {

	private final EditableModel model;
	private final Selection selection;

	public Action(EditableModel editableModel, Selection selection) {
		this.model = editableModel;
		this.selection = selection;
	}

	public final void actionDo() {
		this.onActionDo();
	}

	protected abstract void onActionDo();

	protected final EditableModel getEditableModel() {
		return (this.model);
	}

	protected final Selection getSelection() {
		return (this.selection);
	}

}
