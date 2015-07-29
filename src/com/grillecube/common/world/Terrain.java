package com.grillecube.common.world;

import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.world.blocks.Block;

import fr.toss.lib.Vector3i;

public abstract class Terrain
{
	/** terrain dimensions */
	public static final int	SIZE_X = 16;
	public static final int	SIZE_Y = 64;
	public static final int	SIZE_Z = 16;
	
	protected TerrainLocation	_location;
	
	/** blocks */
	protected short[][][]	_blocks;
	
	public Terrain(TerrainLocation location)
	{
		this._location = location;
		this._blocks = new short[Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z];
	}
	
	/** return block at given coordinates (terrain-relative) */
	public Block getBlock(int x, int y, int z)
	{
		return (BlockManager.getBlockByID(this._blocks[x][y][z]));
	}
	
	public Block getBlock(Vector3i terrainpos)
	{
		return (BlockManager.getBlockByID(this._blocks[terrainpos.x][terrainpos.y][terrainpos.z]));
	}
	
	/** return terrain location */
	public TerrainLocation	getLocation()
	{
		return (this._location);
	}

	public short[][][] getBlocks()
	{
		return (this._blocks);
	}
}
