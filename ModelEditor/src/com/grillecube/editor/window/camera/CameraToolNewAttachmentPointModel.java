package com.grillecube.editor.window.camera;

import javax.swing.JOptionPane;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelAttachmentPoint;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.terrain.Terrain;
import com.grillecube.editor.ModelEditor;
import com.grillecube.editor.toolbox.Toolbox;
import com.grillecube.editor.toolbox.ToolboxPanelModel;

public class CameraToolNewAttachmentPointModel extends CameraTool {
	public CameraToolNewAttachmentPointModel() {
		super(new Vector4f(1.0f, 0.5f, 0.5f, 1.0f));
	}

	@Override
	protected ALSound getSound() {
		return (ALH.alhLoadSound(R.getResPath("sounds/pop.wav")));
	}

	@Override
	public String getName() {
		return ("Add attachment point (Model)");
	}

	@Override
	public Action newAction(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin, CameraEditor camera) {
		return (new Action(model, part, camera) {

			private ModelAttachmentPoint _point;

			@Override
			protected boolean doo() {

				Toolbox toolbox = ModelEditor.instance().getToolbox();
				ToolboxPanelModel panel = toolbox.getModelPanel();
				if (panel == null) {
					return (false);
				}

				// get the block
				Vector4f pos = new Vector4f(camera.getBlockOne().x, camera.getBlockOne().y, camera.getBlockOne().z,
						Terrain.BLOCK_SIZE);

				// get the name
				toolbox.toFront();
				String name = JOptionPane.showInputDialog(toolbox, "Enter an attachment point name: ");
				toolbox.getModelEditor().getEngine().getGLFWWindow().focus(true);
				if (name == null || name.length() == 0) {
					return (false);
				}

				// transform block coordinates to world coordinates
				Matrix4f matrix = Matrix4f.createTransformationMatrix(null, model.getOrigin(), model.getAxis(),
						part.getBlockScale());
				Matrix4f.transform(matrix, pos, pos);

				this._point = new ModelAttachmentPoint(name, pos.xyz());
				panel.addAttachmentPoint(this._point);

				return (true);
			}

			@Override
			protected boolean undo() {
				ToolboxPanelModel panel = ModelEditor.instance().getToolbox().getModelPanel();

				this.getModel().removeAttachmentPoint(this._point);
				panel.removeAttachmentPoint(this._point);
				return (false);
			}

		});
	}
}
