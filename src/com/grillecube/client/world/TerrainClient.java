package com.grillecube.client.world;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.blocks.Blocks;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.TerrainLocation;

public class TerrainClient extends Terrain
{
	//terrain generator
	private static final SimplexNoise noise = new SimplexNoise((int) System.currentTimeMillis());
		
	
	/** world */
	private WorldClient	_world;
	
	/** world position */
	private Vector3f	_world_position;

	/** terrain meshes */
	private TerrainMesh	_mesh;
	
	public TerrainClient(WorldClient world, TerrainLocation location)
	{
		super(location);
		
		this._world = world;
		this._world_position = new Vector3f(location.getX() * Terrain.SIZE_X,
											location.getY() * Terrain.SIZE_Y,
											location.getZ() * Terrain.SIZE_Z);
		
		this._mesh = new TerrainMesh(this);

		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
			{
				for (int y = 0 ; y < Terrain.SIZE_Y - 1 ; y++)
				{
					if (noise.noise((this._world_position.x + x + 1) / 32.0f,
							(this._world_position.y + y + 1) / 32.0f,
							(this._world_position.z + z + 1) / 32.0f) < 0)
					{
						this._blocks[x][y][z] = Blocks.DIRT;
					}
					else
					{
						this._blocks[x][y][z] = Blocks.AIR;
					}
				}
			}
		}
		
		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
			{
				for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
				{
					if (this._blocks[x][y][z] == Blocks.DIRT && this._blocks[x][y + 1][z] == Blocks.AIR)
					{
						this._blocks[x][y][z] = Blocks.GRASS;
					}
				}
			}
		}
	}
	
	public TerrainMesh	getMesh()
	{
		return (this._mesh);
	}

	public Vector3f getWorldPosition()
	{
		return (this._world_position);
	}
	
	@Override
	public String	toString()
	{
		return ("Terrain: " + this.getLocation());
	}

	
	/** store in a Terrain array the neighboor terrains 
	 * 0: bot
	 * 1: top
	 * 2: left
	 * 3: right
	 * 4: front
	 * 5: back
	 */
	public TerrainClient[] getNeighboors()
	{
		TerrainClient[]	terrains;
		TerrainLocation	loc;
		
		terrains = new TerrainClient[6];
		loc = new TerrainLocation();
		terrains[0] = this._world.getTerrain(loc.set(this._location.getX(), this._location.getY() - 1, this._location.getZ()));
		terrains[1] = this._world.getTerrain(loc.set(this._location.getX(), this._location.getY() + 1, this._location.getZ()));
		terrains[2] = this._world.getTerrain(loc.set(this._location.getX() - 1, this._location.getY(), this._location.getZ()));
		terrains[3] = this._world.getTerrain(loc.set(this._location.getX() + 1, this._location.getY(), this._location.getZ()));
		terrains[4] = this._world.getTerrain(loc.set(this._location.getX(), this._location.getY(), this._location.getZ() - 1));
		terrains[5] = this._world.getTerrain(loc.set(this._location.getX(), this._location.getY(), this._location.getZ() + 1));
		
		return (terrains);
	}

}
