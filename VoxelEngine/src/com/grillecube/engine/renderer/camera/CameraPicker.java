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

package com.grillecube.engine.renderer.camera;

import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector4f;

public class CameraPicker {
	
	private final CameraProjective _camera;

	private Vector3f _ray; // world space ray

	public CameraPicker(CameraProjective camera) {
		this._camera = camera;
		this._ray = new Vector3f();
	}

	public void update() {
		
		float mouseX = (float) ((2 * this.getMouseX()) / this._camera.getWindow().getWidth() - 1);
		float mouseY = (float) (2 - (2 * this.getMouseY()) / this._camera.getWindow().getHeight() - 1);
		// float mouseX = this._camera.getWindow().getWidth() / 2;
		// float mouseY = this._camera.getWindow().getHeight() / 2;
		Vector4f clipcoords = new Vector4f(mouseX, mouseY, -1.0f, 1.0f);
		Matrix4f invertedprojection = Matrix4f.invert(this._camera.getProjectionMatrix(), null);
		Vector4f eyecoords = Matrix4f.transform(invertedprojection, clipcoords, null);
		eyecoords.setZ(-1);
		eyecoords.setW(0);
		Matrix4f invertedview = Matrix4f.invert(this._camera.getViewMatrix(), null);
		Vector4f rayworld = Matrix4f.transform(invertedview, eyecoords, null);
		this._ray.setX(rayworld.x);
		this._ray.setY(rayworld.y);
		this._ray.setZ(rayworld.z);
	}

	/** place the cursor relative to the 3d world */
	public void setMouseCursor(float posx, float posy, float posz) {
		float mouseX = (float) ((2 * this.getMouseX()) / this._camera.getWindow().getWidth() - 1);
		float mouseY = (float) (2 - (2 * this.getMouseY()) / this._camera.getWindow().getHeight() - 1);
		// float mouseX = this._camera.getWindow().getWidth() / 2;
		// float mouseY = this._camera.getWindow().getHeight() / 2;
		Vector4f clipcoords = new Vector4f(mouseX, mouseY, -1.0f, 1.0f);
		Matrix4f invertedprojection = Matrix4f.invert(this._camera.getProjectionMatrix(), null);
		Vector4f eyecoords = Matrix4f.transform(invertedprojection, clipcoords, null);
		eyecoords.setZ(-1);
		eyecoords.setW(0);
		Matrix4f invertedview = Matrix4f.invert(this._camera.getViewMatrix(), null);
		Vector4f rayworld = Matrix4f.transform(invertedview, eyecoords, null);
		this._ray.setX(rayworld.x);
		this._ray.setY(rayworld.y);
		this._ray.setZ(rayworld.z);
	}

	public double getMouseX() {
		return (this._camera.getWindow().getMouseX());
	}

	public double getMouseY() {
		return (this._camera.getWindow().getMouseY());
	}

	public Vector3f getRay() {
		return (this._ray);
	}
}
