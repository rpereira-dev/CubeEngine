package com.grillecube.client.world;

import com.grillecube.client.mod.blocks.ResourceBlocks;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.TerrainLocation;

import fr.toss.lib.Vector3i;

public class TerrainClient extends Terrain
{
	//terrain generator
	private static final SimplexNoise noise = new SimplexNoise((int) System.currentTimeMillis());
	
	/** world */
	private WorldClient	_world;
	
	/** world position */
	private Vector3i	_world_position;

	/** terrain meshes */
	private TerrainMesh	_mesh;
	
	public TerrainClient(WorldClient world, TerrainLocation location)
	{
		super(location);
		
		this._world = world;
		this._world_position = new Vector3i(location.getX() * Terrain.SIZE_X,
											location.getY() * Terrain.SIZE_Y,
											location.getZ() * Terrain.SIZE_Z);
		
		this._mesh = new TerrainMesh(this);

		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
			{
				for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
				{
					if (noise.noise((this._world_position.x + x) / 128.0f,
									(this._world_position.y + y) / 128.0f,
									(this._world_position.z + z) / 128.0f) < 0)
					{
						if (y < Terrain.SIZE_Y - 4 && noise.noise((this._world_position.x + x + 1024) / 128.0f,
								(this._world_position.y + y + 1024) / 128.0f,
								(this._world_position.z + z + 1024) / 128.0f) < 0)
						{
							this._blocks[x][y][z] = ResourceBlocks.STONE;
						}
						else if (y == Terrain.SIZE_Y - 1)
						{
							this._blocks[x][y][z] = ResourceBlocks.GRASS;
						}
						else
						{
							this._blocks[x][y][z] = ResourceBlocks.DIRT;
						}
					}
					else
					{
						this._blocks[x][y][z] = ResourceBlocks.AIR;
					}
				}
			}
		}

	}
	
	public TerrainMesh	getMesh()
	{
		return (this._mesh);
	}

	public Vector3i getWorldPosition()
	{
		return (this._world_position);
	}
	
	@Override
	public String	toString()
	{
		return ("Terrain: " + this.getLocation());
	}

	
	/** store in a Terrain array the neighboor terrains  */
	public TerrainClient[][][] getNeighboors()
	{
		TerrainClient[][][]	terrains;
		TerrainLocation		loc;
		
		terrains = new TerrainClient[3][3][3];
		loc = new TerrainLocation();
		for (int x = 0 ; x < 3 ; x++)
		{
			for (int y = 0 ; y < 3 ; y++)
			{
				for (int z = 0 ; z < 3 ; z++)
				{
					loc.set(this._location.getX() + x - 1, this._location.getY() + y - 1, this._location.getZ() + z - 1);
					terrains[x][y][z] = this._world.getTerrain(loc);
				}
			}
		}
		return (terrains);
	}

	public WorldClient	getWorld()
	{
		return (this._world);
	}

	public void setBlock(int x, int y, int z, short blockID)
	{
		this._blocks[x][y][z] = blockID;
		this._mesh.unsetState(TerrainMesh.STATE_VERTICES_UP_TO_DATE);
	}

	public void setBlock(Vector3i vec, short blockID)
	{
		this.setBlock(vec.x, vec.y, vec.z, blockID);
	}
}
