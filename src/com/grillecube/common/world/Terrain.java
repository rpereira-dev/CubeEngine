package com.grillecube.common.world;

import com.grillecube.client.world.blocks.Block;
import com.grillecube.client.world.blocks.Blocks;

public abstract class Terrain
{
	/** terrain dimensions */
	public static final int	TERRAIN_SIZE_X = 16;
	public static final int	TERRAIN_SIZE_Y = 64;
	public static final int	TERRAIN_SIZE_Z = 16;
	
	private TerrainLocation	_location;
	
	/** blocks */
	protected byte[][][]	_blocks;
	
	public Terrain(TerrainLocation location)
	{
		this._location = location;
		this._blocks = new byte[Terrain.TERRAIN_SIZE_X][Terrain.TERRAIN_SIZE_Y][Terrain.TERRAIN_SIZE_Z];
	}
	
	/** return block at given coordinates (terrain-relative) */
	public Block	getBlock(int x, int y, int z)
	{
		return (Blocks.getBlockByID(this._blocks[x][y][z]));
	}
	
	/** return terrain location */
	public TerrainLocation	getLocation()
	{
		return (this._location);
	}

	public abstract void update();
}
