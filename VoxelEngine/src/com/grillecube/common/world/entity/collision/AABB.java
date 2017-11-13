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

package com.grillecube.common.world.entity.collision;

import com.grillecube.common.maths.Vector3f;

/**
 * Axis aligned bounding boxes
 * 
 * @author Romain
 *
 */
public class AABB extends BB {

	private final Vector3f velocity;
	private final Vector3f acceleration;

	public AABB() {
		super();
		this.velocity = new Vector3f();
		this.acceleration = new Vector3f();
	}

	public AABB(BB box) {
		super(box);
		this.velocity = new Vector3f();
		this.acceleration = new Vector3f();
	}

	@Override
	public float getPositionVelocityX() {
		return (this.velocity.x);
	}

	@Override
	public float getPositionVelocityY() {
		return (this.velocity.y);
	}

	@Override
	public float getPositionVelocityZ() {
		return (this.velocity.z);
	}

	@Override
	public float getPositionAccelerationX() {
		return (this.acceleration.x);
	}

	@Override
	public float getPositionAccelerationY() {
		return (this.acceleration.y);
	}

	@Override
	public float getPositionAccelerationZ() {
		return (this.acceleration.z);
	}

	@Override
	public void setPositionVelocityX(float vx) {
		this.velocity.x = vx;
	}

	@Override
	public void setPositionVelocityY(float vy) {
		this.velocity.y = vy;
	}

	@Override
	public void setPositionVelocityZ(float vz) {
		this.velocity.z = vz;
	}

	@Override
	public void setPositionAccelerationX(float ax) {
		this.acceleration.x = ax;
	}

	@Override
	public void setPositionAccelerationY(float ay) {
		this.acceleration.y = ay;
	}

	@Override
	public void setPositionAccelerationZ(float az) {
		this.acceleration.z = az;
	}
}
