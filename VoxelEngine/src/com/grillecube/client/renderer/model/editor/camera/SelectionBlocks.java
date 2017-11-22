package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;

public class SelectionBlocks extends Selection {

	private int x1, x2, y1, y2, z1, z2;
	private Vector3i face;

	public SelectionBlocks(CameraController cameraController) {
		super(cameraController);
	}

	@Override
	public void update() {
		Vector3i first = ((CameraControllerDefault) super.cameraController).getFirstBlock();
		Vector3i second = ((CameraControllerDefault) super.cameraController).getSecondBlock();
		this.x1 = first.x;
		this.y1 = first.y;
		this.z1 = first.z;
		this.x2 = second.x;
		this.y2 = second.y;
		this.z2 = second.z;
		this.face = ((CameraControllerDefault) super.cameraController).getFace();
	}

	public final int getX() {
		return (Maths.min(this.x1, this.x2));
	}

	public final int getY() {
		return (Maths.min(this.y1, this.y2));
	}

	public final int getZ() {
		return (Maths.min(this.z1, this.z2));
	}

	public final int getWidth() {
		return (Maths.abs(this.x1 - this.x2) + 1);
	}

	public final int getHeight() {
		return (Maths.abs(this.y1 - this.y2) + 1);
	}

	public final int getDepth() {
		return (Maths.abs(this.z1 - this.z2) + 1);
	}

	@Override
	public Vector3i getFace() {
		return (this.face);
	}

}
