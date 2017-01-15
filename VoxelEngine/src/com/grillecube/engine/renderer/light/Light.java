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

package com.grillecube.engine.renderer.light;

import com.grillecube.engine.maths.Vector3f;

public abstract class Light {
	/** light world position */
	private Vector3f _position;

	/** light color */
	private Vector3f _color;

	/** intensed color */
	private Vector3f _intensed_color;

	/** light intensity */
	private float _intensity;

	public Light(Vector3f pos, Vector3f color, float intensity) {
		this._position = pos;
		this._color = color;
		this._intensity = intensity;
		this._intensed_color = new Vector3f(color.x * intensity, color.y * intensity, color.z * intensity);
	}

	public Light(Light light) {
		this._position = new Vector3f(light.getPosition());
		this._color = new Vector3f(light.getColor());
		this._intensity = light.getIntensity();
	}

	public float getIntensity() {
		return (this._intensity);
	}

	public Vector3f getPosition() {
		return (this._position);
	}

	public Vector3f getColor() {
		return (this._color);
	}

	public void setPosition(float x, float y, float z) {
		this._position.set(x, y, z);
	}

	public void setColor(float r, float g, float b) {
		this._color.set(r, g, b);
		this._intensed_color.set(r * this._intensity, g * this._intensity, b * this._intensity);
	}

	public void setColorAndIntensity(float intensity, float r, float g, float b) {
		this._intensity = intensity;
		this.setColor(r, g, b);
	}

	public void setIntensity(float intensity) {
		this._intensity = intensity;
		this._intensed_color.set(this._color.x * this._intensity, this._color.y * this._intensity,
				this._color.z * this._intensity);
	}

	public Vector3f getIntensedLight() {
		return (this._intensed_color);
	}
}
