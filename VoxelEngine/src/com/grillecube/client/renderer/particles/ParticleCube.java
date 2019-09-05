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

package com.grillecube.client.renderer.particles;

import com.grillecube.common.maths.Matrix4f;

public class ParticleCube extends WorldObjectParticle {
	protected Matrix4f transfMatrix;
	private float ax, ay, az;
	public ParticleCube(int health) {
		super(health);
		this.transfMatrix = new Matrix4f();
		this.ax = 0;
		this.ay = 0;
		this.az = 0;
	}

	public ParticleCube() {
		this(1000);
	}

	public final Matrix4f getTransfMatrix() {
		return (this.transfMatrix);
	}

	@Override
	protected void onUpdate(double dt) {
		this.calculateTransformationMatrix();
	}

	protected void calculateTransformationMatrix() {
		Matrix4f.createTransformationMatrix(this.transfMatrix, this.getPosition(), this.getRotation(), this.getSize());
	}
	
	@Override
	public void setPositionAccelerationX(float ax) {
		this.ax = ax;
	}

	@Override
	public void setPositionAccelerationY(float ay) {
		this.ay = ay;
	}

	@Override
	public void setPositionAccelerationZ(float az) {
		this.az = az;
	}
	
	@Override
	public float getPositionAccelerationX() {
		return (this.ax);
	}

	@Override
	public float getPositionAccelerationY() {
		return (this.ay);
	}

	@Override
	public float getPositionAccelerationZ() {
		return (this.az);
	}
}
