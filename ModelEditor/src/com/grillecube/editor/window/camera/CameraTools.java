package com.grillecube.editor.window.camera;

import java.util.ArrayList;

import com.grillecube.editor.ModelEditor;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.renderer.MainRenderer.GLTask;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;

public class CameraTools {

	public static CameraTool[] TOOLS = new CameraTool[] { new CameraToolSet(), new CameraToolColorize(),
			new CameraToolUnset(), new CameraToolExtrude(), new CameraToolNewAttachmentPointModel(),
			new CameraToolNewEquipmentPointModelPart() };
	private ArrayList<Action> _actions;
	private int _index;
	private CameraEditor _camera;

	public CameraTools(CameraEditor camera) {
		this._camera = camera;
		this._actions = new ArrayList<Action>();
		this._index = 0;
	}

	public void useNextTool() {
		this.useTool(this._index + 1);
	}

	public void useTool(int index) {
		this._index = index % TOOLS.length;
		this._camera.setBoxColor(this.getTool().getColor());
	}

	public void useTool(CameraTool tool) {
		int index;
		for (index = 0; index < TOOLS.length; index++) {
			if (TOOLS[index].equals(tool)) {
				this.useTool(index);
				break;
			}
		}
	}

	public void useTool(Class<? extends CameraTool> clazz) {
		int index;
		for (index = 0; index < TOOLS.length; index++) {
			if (TOOLS[index].getClass() == clazz) {
				this.useTool(index);
				break;
			}
		}
	}

	public void doAction() {
		Model model = this._camera.getModel();
		if (model == null) {
			return;
		}
		ModelPartBuilder part = this._camera.getModelPart();
		if (part == null) {
			return;
		}
		ModelPartSkinBuilder skin = this._camera.getModelPartSkin();
		if (skin == null) {
			return;
		}
		Action action = this.getTool().newAction(model, part, skin, this._camera);
		this._actions.add(0, action);
		if (action.doo()) {
			this.updateModelMesh(model, part);
		}
	}

	public void undoAction() {
		if (this._actions.size() == 0) {
			ModelEditor.instance().getEngine().getRenderer().getGuiRenderer().toast("Nothing to be undo-ed", 1, 0, 0,
					1);
			return;
		}
		Action action = this._actions.remove(0);
		if (action.undo()) {
			this.updateModelMesh(action.getModel(), action.getModelPart());
		}
	}

	private void updateModelMesh(Model model, ModelPartBuilder part) {

		ModelEditor.instance().playSound(this.getTool().getSound());

		ModelEditor.instance().getEngine().getRenderer().addGLTask(new GLTask() {
			@Override
			public void run() {
				model.refreshVertices();
			}
		});

		part.updateBox();
		model.updateBox();
	}

	public CameraTool getTool() {
		return (TOOLS[this._index]);
	}

	public void cleanHistory() {
		this._actions.clear();
	}

	public int adjustXCoordinate(int x, Vector3f face) {
		return (this.getTool().adjustXCoordinate(x, face));
	}

	public int adjustYCoordinate(int y, Vector3f face) {
		return (this.getTool().adjustYCoordinate(y, face));
	}

	public int adjustZCoordinate(int z, Vector3f face) {
		return (this.getTool().adjustZCoordinate(z, face));
	}
}
