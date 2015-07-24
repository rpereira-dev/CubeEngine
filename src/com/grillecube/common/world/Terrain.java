package com.grillecube.common.world;

public abstract class Terrain
{
	/** terrain dimensions */
	public static final int	TERRAIN_SIZE_X = 16;
	public static final int	TERRAIN_SIZE_Y = 64;
	public static final int	TERRAIN_SIZE_Z = 16;
	
	private TerrainLocation	_location;
	
	public Terrain(TerrainLocation location)
	{
		this._location = location;
	}
	
	/** return terrain location */
	public TerrainLocation	getLocation()
	{
		return (this._location);
	}

	public abstract void update();
}
