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

import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.World;

public class ParticleCube {
	protected Matrix4f transfMatrix;

	protected Vector3f pos;
	protected Vector3f rot;
	protected Vector3f scale;

	protected Vector3f posVel;
	protected Vector3f rotVel;
	protected Vector3f scaleVel;

	protected Vector4f color;

	protected int maxhealth;
	protected int health;
	protected float healthRatio;

	private double cameraSquareDistance;

	public ParticleCube(int health) {
		this.maxhealth = health;
		this.health = health;
		this.healthRatio = 1;
		this.transfMatrix = new Matrix4f();
		this.pos = new Vector3f(0, 0, 0);
		this.rot = new Vector3f(0, 0, 0);
		this.scale = new Vector3f(1, 1, 1);

		this.posVel = new Vector3f(0, 0, 0);
		this.rotVel = new Vector3f(0, 0, 0);
		this.scaleVel = new Vector3f(0, 0, 0);

		this.color = new Vector4f(0.8f, 0.5f, 0.3f, 1.0f);

		this.cameraSquareDistance = 0;
	}

	public ParticleCube() {
		this(1000);
	}

	/** set particle world location */
	public void setPosition(float x, float y, float z) {
		this.pos.set(x, y, z);
	}

	public void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public void setHealth(int health) {
		this.health = health;
		this.maxhealth = health;
	}

	/** set particle world location */
	public void setPositionVel(float x, float y, float z) {
		this.posVel.set(x, y, z);
	}

	/** set particle world location */
	public void setRotation(float x, float y, float z) {
		this.rot.set(x, y, z);
	}

	/** set particle world location */
	public void setRotationVel(float x, float y, float z) {
		this.rotVel.set(x, y, z);
	}

	/** set particle world location */
	public void setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
	}

	public void setScale(float f) {
		this.setScale(f, f, f);
	}

	/** set particle world location */
	public void setScaleVel(float x, float y, float z) {
		this.scaleVel.set(x, y, z);
	}

	public Vector3f getScale() {
		return (this.scale);
	}

	public void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

	/** update the particle (move it) */
	public void update(World world, CameraProjective camera) {
		this.pos.add(this.posVel);
		this.rot.add(this.rotVel);
		this.scale.add(this.scaleVel);
		this.health--;

		this.healthRatio = this.health / (float) this.maxhealth;

		this.calculateTransformationMatrix(camera);
		this.cameraSquareDistance = Vector3f.distanceSquare(camera.getPosition(), this.getPosition());
	}

	protected void calculateTransformationMatrix(Camera camera) {
		Matrix4f.createTransformationMatrix(this.transfMatrix, this.pos, this.rot, this.scale);
	}

	public double getCameraSquareDistance() {
		return (this.cameraSquareDistance);
	}

	public Matrix4f getTransfMatrix() {
		return (this.transfMatrix);
	}

	/** return true if the partcle is dead */
	public boolean isDead() {
		return (this.health <= 0);
	}

	public Vector4f getColor() {
		return (this.color);
	}

	public Vector3f getPosition() {
		return (this.pos);
	}

	/**
	 * a ratio in range of [0,1] which determine particle health statues (0 is
	 * dead, 1 is born)
	 */
	public float getHealthRatio() {
		return (this.healthRatio);
	}

	public void setRotation(Vector3f rot) {
		this.setRotation(rot.x, rot.y, rot.z);
	}

}
