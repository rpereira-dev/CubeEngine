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

package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldEntity;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.entity.Entity;

public class GuiViewDebug extends GuiView {

	private final GuiLabel label;
	private final CameraProjective camera;

	public GuiViewDebug(CameraProjective camera) {
		super();
		this.camera = camera;
		this.label = new GuiLabel();
		this.label.setFontSize(0.65f, 0.65f);
		this.label.setFontColor(Gui.COLOR_BLUE);
		this.addChild(this.label);
	}

	@Override
	protected void onUpdate() {
		this.updateText();
	}

	private void updateText() {

		CameraProjective cam = this.camera;

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

			if (cam instanceof CameraProjectiveWorld && ((CameraProjectiveWorld) cam).getWorld() != null) {
				Vector3i windex = ((CameraProjectiveWorld) cam).getWorld()
						.getTerrainIndex(((CameraProjectiveWorld) cam).getLookCoords());
				builder.append("\n");
				builder.append("Look index: ");
				builder.append(windex.x);
				builder.append(":");
				builder.append(windex.y);
				builder.append(":");
				builder.append(windex.z);

				Vector3f vec = ((CameraProjectiveWorld) cam).getLookCoords();
				builder.append("\n");
				builder.append("Block: ");
				builder.append((int) (vec.x));
				builder.append(":");
				builder.append((int) (vec.y));
				builder.append(":");
				builder.append((int) (vec.z));
			}
		}

		builder.append("\nFPS: ");
		builder.append(GLH.glhGetContext().getWindow().getFPS());

		builder.append("\nDraw calls: ");
		builder.append(GLH.glhGetContext().getDrawCalls());
		builder.append("\nVertex drawn: ");
		builder.append(GLH.glhGetContext().getVerticesDrawn());
		this.label.setText(builder.toString());
		this.label.setBoxPosition(0.0f, 1.0f - this.label.getTextHeight());
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

}