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

package com.grillecube.client.renderer.camera;

import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public class CameraPicker {

	private GuiViewWorld guiViewWorld;
	private final CameraProjective camera;
	private Vector3f ray; // world space ray

	public CameraPicker(CameraProjective camera) {
		this.camera = camera;
		this.ray = new Vector3f();
	}

	public final void setGuiViewWorldRelative(GuiViewWorld guiViewWorld) {
		this.guiViewWorld = guiViewWorld;
	}

	public void update() {

		float mouseX = (float) (this.getMouseX() / this.camera.getWindow().getWidth());
		float mouseY = (float) (1.0f - this.getMouseY() / this.camera.getWindow().getHeight());

		if (this.guiViewWorld != null) {
			Vector4f mouse = new Vector4f(mouseX, mouseY, 0.0f, 1.0f);
			Matrix4f windowToGuiChangeOfBasis = this.guiViewWorld.getWindowToGuiChangeOfBasis();
			if (windowToGuiChangeOfBasis != null) {
				Matrix4f.transform(windowToGuiChangeOfBasis, mouse, mouse);
				mouseX = mouse.x;
				mouseY = mouse.y;
			}
		}

		mouseX = 2.0f * mouseX - 1.0f;
		mouseY = 2.0f * mouseY - 1.0f;

		// float mouseX = this.camera.getWindow().getWidth() / 2;
		// float mouseY = this.camera.getWindow().getHeight() / 2;
		Vector4f clipcoords = new Vector4f(mouseX, mouseY, -1.0f, 1.0f);
		Matrix4f invertedprojection = Matrix4f.invert(this.camera.getProjectionMatrix(), null);
		Vector4f eyecoords = Matrix4f.transform(invertedprojection, clipcoords, null);
		eyecoords.setZ(-1);
		eyecoords.setW(0);
		Matrix4f invertedview = Matrix4f.invert(this.camera.getViewMatrix(), null);
		Vector4f rayworld = Matrix4f.transform(invertedview, eyecoords, null);
		this.ray.setX(rayworld.x);
		this.ray.setY(rayworld.y);
		this.ray.setZ(rayworld.z);
	}

	/** place the cursor relative to the 3d world */
	public void setMouseCursor(float posx, float posy, float posz) {
		float mouseX = (float) ((2 * this.getMouseX()) / this.camera.getWindow().getWidth() - 1);
		float mouseY = (float) (2 - (2 * this.getMouseY()) / this.camera.getWindow().getHeight() - 1);
		// float mouseX = this.camera.getWindow().getWidth() / 2;
		// float mouseY = this.camera.getWindow().getHeight() / 2;
		Vector4f clipcoords = new Vector4f(mouseX, mouseY, -1.0f, 1.0f);
		Matrix4f invertedprojection = Matrix4f.invert(this.camera.getProjectionMatrix(), null);
		Vector4f eyecoords = Matrix4f.transform(invertedprojection, clipcoords, null);
		eyecoords.setZ(-1);
		eyecoords.setW(0);
		Matrix4f invertedview = Matrix4f.invert(this.camera.getViewMatrix(), null);
		Vector4f rayworld = Matrix4f.transform(invertedview, eyecoords, null);
		this.ray.setX(rayworld.x);
		this.ray.setY(rayworld.y);
		this.ray.setZ(rayworld.z);
	}

	public double getMouseX() {
		return (this.camera.getWindow().getMouseX());
	}

	public double getMouseY() {
		return (this.camera.getWindow().getMouseY());
	}

	public Vector3f getRay() {
		return (this.ray);
	}
}
