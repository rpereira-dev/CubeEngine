/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.pot.client.renderer.gui;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldEntity;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector2f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.entity.Entity;

public class GuiViewDebug extends GuiView {

	private GuiLabel _label;

	public GuiViewDebug() {
		super();
	}

	@Override
	public void onAdded(GuiRenderer renderer) {
		this._label = new GuiLabel();
		this._label.setPosition(-1, 1);
		this._label.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		this._label.setFontSize(new Vector2f(0.6f, 0.6f));
		super.addGui(this._label);
		// GuiTexture t1 = new GuiTexture(
		// VoxelEngineClient.instance().getRenderer().getWorldRenderer().getShadowMap());
		// t1.setPosition(-0.5f, 1.0f);
		// t1.setSize(0.6f, 0.6f);
		// super.addGui(t1);
	}

	@Override
	public void onRemoved(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUpdate() {
		this.updateText();
	}

	private void updateText() {

		// if (true) {
		// StringBuilder builder = new StringBuilder();
		//
		// builder.append("STARTS\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("abcdefghijklmn\n");
		// builder.append("END\n");
		// this._label.setText(builder.toString());
		//
		// return ;
		// }

		CameraProjectiveWorld cam = VoxelEngineClient.instance().getRenderer().getCamera();

		Vector3f pos = cam.getPosition();
		Vector3f look = cam.getViewVector();

		StringBuilder builder = new StringBuilder();

		if (cam instanceof CameraPerspectiveWorldEntity) {
			Entity entity = ((CameraPerspectiveWorldEntity) cam).getEntity();
			builder.append("Entity: ");
			builder.append(Maths.approximatate(entity.getPosition().x, 10.0f));
			builder.append(":");
			builder.append(Maths.approximatate(entity.getPosition().y, 10.0f));
			builder.append(":");
			builder.append(Maths.approximatate(entity.getPosition().z, 10.0f));

			builder.append("\n");
			builder.append("Acceleration: ");
			builder.append(Maths.approximatate(entity.getPositionAcceleration().x, 1000.0f));
			builder.append(":");
			builder.append(Maths.approximatate(entity.getPositionAcceleration().y, 1000.0f));
			builder.append(":");
			builder.append(Maths.approximatate(entity.getPositionAcceleration().z, 1000.0f));

			builder.append("\n");
			builder.append("Velocity: ");
			builder.append(Maths.approximatate(entity.getPositionVelocity().x, 10.0f));
			builder.append(":");
			builder.append(Maths.approximatate(entity.getPositionVelocity().y, 10.0f));
			builder.append(":");
			builder.append(Maths.approximatate(entity.getPositionVelocity().z, 10.0f));
		} else {
			builder.append("Position: ");
			builder.append((int) pos.x);
			builder.append(":");
			builder.append((int) pos.y);
			builder.append(":");
			builder.append((int) pos.z);

			builder.append("\n");
			builder.append("Look: ");
			builder.append(Maths.approximatate(look.x, 10.0f));
			builder.append(":");
			builder.append(Maths.approximatate(look.y, 10.0f));
			builder.append(":");
			builder.append(Maths.approximatate(look.z, 10.0f));

			Vector3i windex = cam.getWorld().getTerrainIndex(cam.getLookCoords());
			builder.append("\n");
			builder.append("Look index: ");
			builder.append(windex.x);
			builder.append(":");
			builder.append(windex.y);
			builder.append(":");
			builder.append(windex.z);

			Vector3f vec = cam.getLookCoords();
			builder.append("\n");
			builder.append("Block: ");
			builder.append((int) (vec.x));
			builder.append(":");
			builder.append((int) (vec.y));
			builder.append(":");
			builder.append((int) (vec.z));
		}

		builder.append("\nFPS: ");
		builder.append(GLH.glhGetContext().getWindow().getFPS());

		builder.append("\nDraw calls: ");
		builder.append(GLH.glhGetContext().getDrawCalls());
		builder.append("\nVertex drawn: ");
		builder.append(GLH.glhGetContext().getVerticesDrawn());
		this._label.setText(builder.toString());
	}
}