package com.grillecube.common.world;

import com.grillecube.client.world.blocks.Block;
import com.grillecube.client.world.blocks.Blocks;

public abstract class Terrain
{
	/** terrain dimensions */
	public static final int	SIZE_X = 16;
	public static final int	SIZE_Y = 64;
	public static final int	SIZE_Z = 16;
	
	protected TerrainLocation	_location;
	
	/** blocks */
	protected byte[][][]	_blocks;
	
	public Terrain(TerrainLocation location)
	{
		this._location = location;
		this._blocks = new byte[Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z];
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

	public byte[][][] getBlocks()
	{
		return (this._blocks);
	}
}
