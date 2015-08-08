package com.grillecube.client.mod.renderer.particles;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public abstract class Particle
{
	protected Matrix4f _transf_matrix;
	
	protected Vector3f _pos;
	protected Vector3f _rot;
	protected Vector3f _scale;
	
	protected Vector3f _pos_vel;
	protected Vector3f _rot_vel;
	protected Vector3f _scale_vel;

	protected Vector4f _color;

	protected int _max_health;
	protected int _health;
	protected float _health_ratio;
	
	public Particle(int health)
	{
		this._max_health = health;
		this._health = health;
		this._health_ratio = 1;
		this._transf_matrix = new Matrix4f();
		this._pos = new Vector3f(0, 0, 0);
		this._rot = new Vector3f(0, 0, 0);
		this._scale = new Vector3f(1, 1, 1);
		
		this._pos_vel = new Vector3f(0, 0, 0);
		this._rot_vel = new Vector3f(0, 0, 0);
		this._scale_vel = new Vector3f(0, 0, 0);
		
		this._color = new Vector4f(0.8f, 0.5f, 0.3f, 1.0f);
	}
	
	public Particle()
	{
		this(500);
	}
	
	
	/** set particle world location */
	public void setPosition(float x, float y, float z)
	{
		this._pos.set(x, y, z);
	}
	
	/** set particle world location */
	public void setPositionVel(float x, float y, float z)
	{
		this._pos_vel.set(x, y, z);
	}
	
	/** set particle world location */
	public void setRotation(float x, float y, float z)
	{
		this._rot.set(x, y, z);
	}
	
	/** set particle world location */
	public void setRotationVel(float x, float y, float z)
	{
		this._rot_vel.set(x, y, z);
	}
	
	/** set particle world location */
	public void setScale(float x, float y, float z)
	{
		this._scale.set(x, y, z);
	}
	
	/** set particle world location */
	public void setScaleVel(float x, float y, float z)
	{
		this._scale_vel.set(x, y, z);
	}
	
	public void setColor(float r, float g, float b, float a)
	{
		this._color.set(r, g, b, a);
	}
	
	/** update the particle (move it) */
	public void update()
	{
		this._pos.add(this._pos_vel);
		this._rot.add(this._rot_vel);
		this._scale.add(this._scale_vel);
		this._health--;
		
		this._health_ratio = this._health / (float)this._max_health;
		Matrix4f.createTransformationMatrix(this._transf_matrix, this._pos, this._rot, this._scale);
	}
	
	public Matrix4f getTransfMatrix()
	{
		return (this._transf_matrix);
	}

	/** return true if the partcle is dead */
	public boolean isDead()
	{
		return (this._health == 0);
	}

	public Vector4f getColor()
	{
		return (this._color);
	}

	public Vector3f getPosition()
	{
		return (this._pos);
	}

	/** a ratio in range of [0,1] which determine particle health statues (0 is dead, 1 is born) */
	public float getHealthRatio()
	{
		return (this._health_ratio);
	}

}
