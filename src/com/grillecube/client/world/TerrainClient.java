package com.grillecube.client.world;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.blocks.Blocks;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.TerrainLocation;

public class TerrainClient extends Terrain
{
	/** world position */
	private Vector3f	_world_position;

	/** terrain meshes */
	private TerrainMesh[]	_meshes;
	
	public TerrainClient(TerrainLocation location)
	{
		super(location);
		
		this._world_position = new Vector3f(location.getX() * Terrain.TERRAIN_SIZE_X,
											location.getY() * Terrain.TERRAIN_SIZE_Y,
											location.getZ() * Terrain.TERRAIN_SIZE_Z);
		
		this._meshes = new TerrainMesh[TerrainMesh.MESH_PER_TERRAIN];
		for (int i = 0 ; i < TerrainMesh.MESH_PER_TERRAIN ; i++)
		{
			this._meshes[i] = new TerrainMesh(this, i);
		}
		
		Random rand = new Random();
		for (int x = 0 ; x < Terrain.TERRAIN_SIZE_X ; x++)
		{
			for (int z = 0 ; z < Terrain.TERRAIN_SIZE_Z ; z++)
			{
				int	y = Terrain.TERRAIN_SIZE_Y - 1 - (Math.abs(rand.nextInt()) % 3);
				
				for ( ; y >= 0 ; y--)
				{
					this._blocks[x][y][z] = Blocks.DIRT;
				}
			}
		}
	}

	@Override
	public void update()
	{
		for (TerrainMesh mesh : this._meshes)
		{
			mesh.update();
		}
	}
	
	public TerrainMesh[]	getMeshes()
	{
		return (this._meshes);
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

}
