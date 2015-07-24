package com.grillecube.common.world;

import java.util.ArrayList;

public class TerrainLocation
{
	private int	_x;
	private int	_y;
	private int	_z;
	
	public TerrainLocation()
	{
		this._x = 0;
		this._y = 0;
		this._z = 0;
	}
	
	public int	getX()
	{
		return (this._x);
	}
	
	public int	getY()
	{
		return (this._y);
	}
	
	public int	getZ()
	{
		return (this._z);
	}

	@Override
	public int	hashCode()
	{
		int	hash;
		
		hash = 5381;
		hash = ((hash << 5) + hash) + this._x;
		hash = ((hash << 5) + hash) + this._y;
		hash = ((hash << 5) + hash) + this._z;
		return (hash);
	}

	public void set(int x, int y, int z)
	{
		this._x = x;
		this._y = y;
		this._z = z;
	}
}