package com.grillecube.common.world.entity.collision;

import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.block.Block;

public class PhysicObjectBlock extends PhysicObject {

	private final Block block;
	private final float posx, posy, posz;

	public PhysicObjectBlock(Block block, float posx, float posy, float posz) {
		this.block = block;
		this.posx = posx;
		this.posy = posy;
		this.posz = posz;
	}

	@Override
	public float getPositionX() {
		return (this.posx);
	}

	@Override
	public float getPositionY() {
		return (this.posy);
	}

	@Override
	public float getPositionZ() {
		return (this.posz);
	}

	@Override
	public float getPositionVelocityX() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getPositionVelocityY() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getPositionVelocityZ() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getPositionAccelerationX() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getPositionAccelerationY() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getPositionAccelerationZ() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionX(float x) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionY(float y) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionZ(float z) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionVelocityX(float vx) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionVelocityY(float vy) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionVelocityZ(float vz) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionAccelerationX(float ax) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionAccelerationY(float ay) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPositionAccelerationZ(float az) {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getRotationX() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getRotationY() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getRotationZ() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getRotationVelocityX() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getRotationVelocityY() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getRotationVelocityZ() {
		throw new UnsupportedOperationException();

	}

	@Override
	public float getRotationAccelerationX() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getRotationAccelerationY() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getRotationAccelerationZ() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationX(float x) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationY(float y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationZ(float z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationVelocityX(float vx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationVelocityY(float vy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationVelocityZ(float vz) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationAccelerationX(float ax) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationAccelerationY(float ay) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationAccelerationZ(float az) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getSizeX() {
		return (Terrain.BLOCK_SIZE);
	}

	@Override
	public float getSizeY() {
		return (Terrain.BLOCK_SIZE);
	}

	@Override
	public float getSizeZ() {
		return (Terrain.BLOCK_SIZE);
	}

	@Override
	public float getSizeVelocityX() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getSizeVelocityY() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getSizeVelocityZ() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getSizeAccelerationX() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getSizeAccelerationY() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getSizeAccelerationZ() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeX(float x) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeY(float y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeZ(float z) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setSizeVelocityX(float vx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeVelocityY(float vy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeVelocityZ(float vz) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeAccelerationX(float ax) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeAccelerationY(float ay) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeAccelerationZ(float az) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getMass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMass(float mass) {
		throw new UnsupportedOperationException();

	}

	public final Block getBlock() {
		return (this.block);
	}
}
