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

package com.grillecube.client.renderer.world.particles;

import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.World;

public class ParticleBillboarded {

	/** sprite to use */
	private TextureSprite _sprite;

	protected Vector3f _pos;
	protected Vector3f _scale;

	protected Vector3f _pos_vel;
	protected Vector3f _scale_vel;

	protected Vector4f _color;

	protected int _max_health;
	protected int _health;

	private double _camera_square_distance;

	/** true if the particle is blowing (additive blending) */
	private boolean _glows;

	/** constructor of a billboarded particles:
	 * 
	 * @param health : health for this particle
	 * @param sprite : the sprite to use
	 * @param glows : if the particle glows
	 */
	public ParticleBillboarded(int health, TextureSprite sprite, boolean glows) {
		this._max_health = health;
		this._health = health;
		this._pos = new Vector3f(0, 0, 0);
		this._scale = new Vector3f(1, 1, 1);

		this._pos_vel = new Vector3f(0, 0, 0);
		this._scale_vel = new Vector3f(0, 0, 0);

		this._color = new Vector4f(0.8f, 0.5f, 0.3f, 1.0f);

		this._camera_square_distance = 0;
		this._glows = glows;

		this._sprite = sprite;
	}

	public ParticleBillboarded(int health, TextureSprite sprite) {
		this(health, sprite, false);
	}

	public ParticleBillboarded(TextureSprite sprite) {
		this(500, sprite);
	}

	/** set particle world location */
	public void setPosition(float x, float y, float z) {
		this._pos.set(x, y, z);
	}

	public void setHealth(int health) {
		this._health = health;
		this._max_health = health;
	}

	/** set particle world location */
	public void setPositionVel(float x, float y, float z) {
		this._pos_vel.set(x, y, z);
	}

	/** set particle world location */
	public void setScale(float x, float y) {
		this._scale.set(x, y);
	}

	/** set particle world location */
	public void setScaleVel(float x, float y) {
		this._scale_vel.set(x, y);
	}

	public void setColor(float r, float g, float b, float a) {
		this._color.set(r, g, b, a);
	}

	/** update the particle (move it) */
	public void update(World world, CameraProjectiveWorld camera) {
		this._pos.add(this._pos_vel);
		this._scale.add(this._scale_vel);
		this._health--;
		this._camera_square_distance = Vector3f.distanceSquare(camera.getPosition(), this.getPosition());
	}

	public double getCameraSquareDistance() {
		return (this._camera_square_distance);
	}

	/** return true if the partcle is dead */
	public boolean isDead() {
		return (this._health == 0);
	}

	public Vector4f getColor() {
		return (this._color);
	}

	public Vector3f getPosition() {
		return (this._pos);
	}

	public TextureSprite getSprite() {
		return (this._sprite);
	}

	public Vector3f getScale() {
		return (this._scale);
	}

	public boolean isGlowing() {
		return (this._glows);
	}

	public int getHealth() {
		return (this._health);
	}

	public int getMaxHealth() {
		return (this._max_health);
	}
}
