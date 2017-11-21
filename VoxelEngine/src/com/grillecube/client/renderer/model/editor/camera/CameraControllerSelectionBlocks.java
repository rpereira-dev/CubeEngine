package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;

public class CameraControllerSelectionBlocks extends CameraControllerSelection {

	private final Vector3i firstBlock;
	private final Vector3i secondBlock;

	public CameraControllerSelectionBlocks(CameraController cameraController, Vector3i firstBlock,
			Vector3i secondBlock) {
		super(cameraController);
		this.firstBlock = firstBlock;
		this.secondBlock = secondBlock;
	}

	public final int getX() {
		return (Maths.min(this.firstBlock.x, this.secondBlock.x));
	}

	public final int getY() {
		return (Maths.min(this.firstBlock.y, this.secondBlock.y));
	}

	public final int getZ() {
		return (Maths.min(this.firstBlock.z, this.secondBlock.z));
	}

	public final int getWidth() {
		return (Maths.abs(this.firstBlock.x - this.secondBlock.x) + 1);
	}

	public final int getHeight() {
		return (Maths.abs(this.firstBlock.y - this.secondBlock.y) + 1);
	}

	public final int getDepth() {
		return (Maths.abs(this.firstBlock.z - this.secondBlock.z) + 1);
	}

}
