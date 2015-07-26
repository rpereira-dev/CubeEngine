package com.grillecube.client.world;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.blocks.Blocks;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.TerrainLocation;

public class TerrainClient extends Terrain
{
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
		
		Random rand = new Random();
		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
			{
				int	y = Terrain.SIZE_Y - 1 - (Math.abs(rand.nextInt()) % 3);
				
				for ( ; y >= 0 ; y--)
				{
					this._blocks[x][y][z] = Blocks.DIRT;
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
	public Terrain[] getNeighboors()
	{
		Terrain[]		terrains;
		TerrainLocation	loc;
		
		terrains = new Terrain[6];
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
