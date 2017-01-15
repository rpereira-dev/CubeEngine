package com.grillecube.editor.window.camera;

import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;

public abstract class Action {
	private Model _model;
	private ModelPartBuilder _part;
	protected CameraEditor _camera;

	public Action(Model model, ModelPartBuilder part, CameraEditor camera) {
		this._model = model;
		this._part = part;
		this._camera = camera;
	}

	public Model getModel() {
		return (this._model);
	}

	public ModelPartBuilder getModelPart() {
		return (this._part);
	}

	public CameraEditor getCamera() {
		return (this._camera);
	}

	protected abstract boolean doo();

	protected abstract boolean undo();
}
