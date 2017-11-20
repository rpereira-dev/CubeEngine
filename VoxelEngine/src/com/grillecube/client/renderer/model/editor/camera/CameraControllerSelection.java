package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.entity.collision.Positioneable;
import com.grillecube.common.world.entity.collision.Sizeable;

public class CameraControllerSelection implements Positioneable, Sizeable {

	private final CameraController cameraController;
	private final Vector3i firstBlock;
	private final Vector3i secondBlock;

	public CameraControllerSelection(CameraController cameraController, Vector3i firstBlock, Vector3i secondBlock) {
		this.cameraController = cameraController;
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

	@Override
	public float getPositionX() {
		return (this.getX() * this.cameraController.getBlockSizeUnit());
	}

	@Override
	public float getPositionY() {
		return ((this.getY() + 0.05f) * this.cameraController.getBlockSizeUnit());
	}

	@Override
	public float getPositionZ() {
		return (this.getZ() * this.cameraController.getBlockSizeUnit());
	}

	@Override
	public float getPositionVelocityX() {
		return 0;
	}

	@Override
	public float getPositionVelocityY() {
		return 0;
	}

	@Override
	public float getPositionVelocityZ() {
		return 0;
	}

	@Override
	public float getPositionAccelerationX() {
		return 0;
	}

	@Override
	public float getPositionAccelerationY() {
		return 0;
	}

	@Override
	public float getPositionAccelerationZ() {
		return 0;
	}

	@Override
	public void setPositionX(float x) {
	}

	@Override
	public void setPositionY(float y) {
	}

	@Override
	public void setPositionZ(float z) {
	}

	@Override
	public void setPositionVelocityX(float vx) {
	}

	@Override
	public void setPositionVelocityY(float vy) {
	}

	@Override
	public void setPositionVelocityZ(float vz) {
	}

	@Override
	public void setPositionAccelerationX(float ax) {
	}

	@Override
	public void setPositionAccelerationY(float ay) {
	}

	@Override
	public void setPositionAccelerationZ(float az) {
	}

	@Override
	public float getSizeX() {
		return (this.getWidth() * this.cameraController.getBlockSizeUnit());
	}

	@Override
	public float getSizeY() {
		return (this.getHeight() * this.cameraController.getBlockSizeUnit());
	}

	@Override
	public float getSizeZ() {
		return (this.getDepth() * this.cameraController.getBlockSizeUnit());
	}

	@Override
	public float getSizeVelocityX() {
		return 0;
	}

	@Override
	public float getSizeVelocityY() {
		return 0;
	}

	@Override
	public float getSizeVelocityZ() {
		return 0;
	}

	@Override
	public float getSizeAccelerationX() {
		return 0;
	}

	@Override
	public float getSizeAccelerationY() {
		return 0;
	}

	@Override
	public float getSizeAccelerationZ() {
		return 0;
	}

	@Override
	public void setSizeX(float x) {
	}

	@Override
	public void setSizeY(float y) {
	}

	@Override
	public void setSizeZ(float z) {
	}

	@Override
	public void setSizeVelocityX(float vx) {
	}

	@Override
	public void setSizeVelocityY(float vy) {
	}

	@Override
	public void setSizeVelocityZ(float vz) {
	}

	@Override
	public void setSizeAccelerationX(float ax) {
	}

	@Override
	public void setSizeAccelerationY(float ay) {
	}

	@Override
	public void setSizeAccelerationZ(float az) {
	}

}
