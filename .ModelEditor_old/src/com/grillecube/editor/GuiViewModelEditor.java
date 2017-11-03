package com.grillecube.editor;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.editor.window.camera.CameraEditor;

public class GuiViewModelEditor extends GuiView {
	private GuiLabel _label;
	private GuiTexture _shadow_map;

	public GuiViewModelEditor() {
		super();
	}

	@Override
	public void onAdded(GuiRenderer renderer) {
		this._label = new GuiLabel();
		this._label.setFontSize(0.4f, 0.4f);
		this._label.setPosition(-1, 1.0f);
		this.addGui(this._label);
	}

	@Override
	protected String getName() {
		return ("Gui Model Editor Scene");
	}

	@Override
	protected void onUpdate() {
		CameraEditor cam = (CameraEditor) ModelEditor.instance().getEngine().getRenderer().getCamera();
		Vector3f campos = cam.getPosition();
		Vector3f lookpos = cam.getLookCoords();
		Vector3f face = cam.getLookFace();

		StringBuilder builder = new StringBuilder();

		builder.append("Position: ");
		builder.append(Math.round(campos.x * 100.0f) / 100.0f);
		builder.append(":");
		builder.append(Math.round(campos.y * 100.0f) / 100.0f);
		builder.append(":");
		builder.append(Math.round(campos.z * 100.0f) / 100.0f);

		builder.append("\nPosition looked: ");
		builder.append(Math.round(lookpos.x * 100.0f) / 100.0f);
		builder.append(":");
		builder.append(Math.round(lookpos.y * 100.0f) / 100.0f);
		builder.append(":");
		builder.append(Math.round(lookpos.z * 100.0f) / 100.0f);

		builder.append("\nLook vec: ");
		builder.append(Math.round(face.x * 100.0f) / 100.0f);
		builder.append(":");
		builder.append(Math.round(face.y * 100.0f) / 100.0f);
		builder.append(":");
		builder.append(Math.round(face.z * 100.0f) / 100.0f);

		builder.append("\nFPS: ");
		builder.append(ModelEditor.instance().getEngine().getGLFWWindow().getFPS());

		builder.append("\nTool: ");
		builder.append(cam.getTool().getName());

		this._label.setText(builder.toString());
	}

	@Override
	public void onRemoved(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

}
