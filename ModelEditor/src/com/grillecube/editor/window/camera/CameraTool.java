package com.grillecube.editor.window.camera;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.engine.sound.ALSound;

public abstract class CameraTool {

	private Vector4f _color;

	public CameraTool(Vector4f color) {
		this._color = color;
	}

	public Vector4f getColor() {
		return (this._color);
	}

	protected abstract ALSound getSound();

	public int adjustXCoordinate(int x, Vector3f face) {
		return (x);
	}

	public int adjustYCoordinate(int y, Vector3f face) {
		return (y);
	}

	public int adjustZCoordinate(int z, Vector3f face) {
		return (z);
	}

	public abstract Action newAction(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin,
			CameraEditor camera);

	public abstract String getName();

	@Override
	public String toString() {
		return (this.getName());
	}
}
