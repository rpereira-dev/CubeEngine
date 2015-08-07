package com.grillecube.client.mod.renderer.particles.cube;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class CubeParticle
{
	private Matrix4f _transf_matrix;
	
	private Vector3f _pos;
	private Vector3f _rot;
	private Vector3f _scale;
	
	private Vector3f _pos_vel;
	private Vector3f _rot_vel;
	private Vector3f _scale_vel;

	private Vector4f _color;
	
	public CubeParticle()
	{
		this._transf_matrix = new Matrix4f();
		this._pos = new Vector3f(0, 0, 0);
		this._rot = new Vector3f(0, 0, 0);
		this._scale = new Vector3f(1, 1, 1);
		
		this._pos_vel = new Vector3f(0, 0, 0);
		this._rot_vel = new Vector3f(0, 0, 0);
		this._scale_vel = new Vector3f(0, 0, 0);
		
		this._color = new Vector4f(0.8f, 0.5f, 0.3f, 1.0f);
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
		
		Matrix4f.createTransformationMatrix(this._transf_matrix, this._pos, this._rot, this._scale);
	}
	
	public Matrix4f getTransfMatrix()
	{
		return (this._transf_matrix);
	}

	/** return true if the partcle is dead */
	public boolean isDead()
	{
		return (false);
	}

	public Vector4f getColor()
	{
		return (this._color);
	}

	public Vector3f getPosition()
	{
		return (this._pos);
	}

}
