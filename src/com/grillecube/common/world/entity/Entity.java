package com.grillecube.common.world.entity;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.world.World;

public abstract class Entity
{
	/** entity's world */
	private World _world;
	
	/** entity's world pos */
	private Vector3f	_pos;
	private Vector3f	_rot;
	private Vector3f	_pos_vel;
	private Vector3f	_rot_vel;
	
	public Entity()
	{
		this(null);
	}
	
	public Entity(World world)
	{
		this._world = world;
		this._pos = new Vector3f(0, 0, 0);
		this._pos_vel = new Vector3f(0, 0, 0);
		this._rot = new Vector3f(0, 0, 0);
		this._rot_vel = new Vector3f(0, 0, 0);
	}
	
	/** update the entity */
	public void	update()
	{
		this._pos.x += this._pos_vel.x;
		this._pos.y += this._pos_vel.y;
		this._pos.z += this._pos_vel.z;
		
		this._rot.x += this._rot_vel.x;
		this._rot.y += this._rot_vel.y;
		this._rot.z += this._rot_vel.z;
		this.updateEntity();
	}
	
	/** update the entity */
	protected abstract void updateEntity();
	
	
	/** get entity position */
	public Vector3f	getPosition()
	{
		return (this._pos);
	}
	
	/** get entity position */
	public Vector3f	getRotation()
	{
		return (this._rot);
	}
	
	/** get entity position */
	public Vector3f	getPositionVelocity()
	{
		return (this._pos_vel);
	}
	
	/** get entity position */
	public Vector3f	getRotationVelocity()
	{
		return (this._rot_vel);
	}

	public void setPosition(Vector3f pos)
	{
		this._pos = pos;
	}
	
	public void setWorld(World world)
	{
		this._world = world;
	}
}
