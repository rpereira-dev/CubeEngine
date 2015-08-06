package com.grillecube.client.world;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector3i;

import com.grillecube.client.mod.blocks.ResourceBlocks;
import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.world.blocks.Block;

public class Terrain
{
	//terrain generator
	private static final SimplexNoise noise = new SimplexNoise((int) System.currentTimeMillis());
	
	/** terrain meshes */
	private TerrainMesh	_mesh;
	
	/** neighboors */
	private Terrain[]	_neighboors;
	
	/** camera distance */
	private float _camera_dist;
	
	/** terrain dimensions */
	public static final int	SIZE_X = 16;
	public static final int	SIZE_Y = 16;
	public static final int	SIZE_Z = 16;
	
	protected TerrainLocation _location;
	
	/** blocks */
	protected short[][][]	_blocks;
	
	public Terrain(TerrainLocation location)
	{
		this._location = location;
		this._blocks = new short[Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z];
		this._mesh = new TerrainMesh(this);
		this._neighboors = new Terrain[6];
		this.generateRandomTerrain();
	}
	
	private void generateRandomTerrain()
	{
		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
				{
					if (noise.noise((this.getWorldLocation().x + x) / 64.0f,
									(this.getWorldLocation().y + y) / 64.0f,
									(this.getWorldLocation().z + z) / 64.0f) < 0)
					{
						if (y < Terrain.SIZE_Y - 4 && noise.noise(
								(this.getWorldLocation().x + x + 1024) / 64.0f,
								(this.getWorldLocation().y + y + 1024) / 64.0f,
								(this.getWorldLocation().z + z + 1024) / 64.0f) < 0)
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
	
	/** return block at given coordinates (terrain-relative)
	 * 
	 *	if coordinates are negative or superior to the terrain dimension,
	 *	it tries to get block from neighboors terrain 
	 **/
	public Block getBlock(int x, int y, int z)
	{
		Terrain terrain = this;
		
		//x test
		if (x < 0)
		{
			x = x % Terrain.SIZE_X + Terrain.SIZE_X;
			terrain = terrain.getNeighbor(Faces.LEFT);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		else if (x >= Terrain.SIZE_X)
		{
			x = x % Terrain.SIZE_X;
			terrain = terrain.getNeighbor(Faces.RIGHT);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		
		
		//y test
		if (y < 0)
		{
			y = y % Terrain.SIZE_Y + Terrain.SIZE_Y;
			terrain = terrain.getNeighbor(Faces.BOT);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		else if (y >= Terrain.SIZE_Y)
		{
			y = y % Terrain.SIZE_Y;
			terrain = terrain.getNeighbor(Faces.TOP);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		
		
		//z test
		if (z < 0)
		{
			z = z % Terrain.SIZE_Z + Terrain.SIZE_Z;
			terrain = terrain.getNeighbor(Faces.FRONT);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		else if (z >= Terrain.SIZE_Z)
		{
			z = z % Terrain.SIZE_Z;
			terrain = terrain.getNeighbor(Faces.BACK);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		return (BlockManager.getBlockByID(terrain._blocks[x][y][z]));
	}
	
	public Block getBlock(Vector3i pos)
	{
		return (this.getBlock(pos.x, pos.y, pos.z));
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

	public TerrainMesh	getMesh()
	{
		return (this._mesh);
	}

	public Vector3f getWorldLocation()
	{
		return (this._location.toWorldLocation());
	}
	
	@Override
	public String	toString()
	{
		return ("Terrain: " + this.getLocation());
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
	
	/** thoses functions return the left, right, top, bot, front or back terrain to this one */
	public Terrain getNeighbor(int id)
	{
		return (this._neighboors[id]);
	}

	/** return true if this terrain mesh in the given Camera frustum */
	public boolean	isInFrustum(Camera camera)
	{
		Vector3f center = this.getLocation().getCenter();
		float dx = camera.getPosition().getX() - center.x;
		float dy = camera.getPosition().getY() - center.y;
		float dz = camera.getPosition().getZ() - center.z;
		
		this._camera_dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		
		if (this._camera_dist >= camera.getRenderDistance())
		{
			return (false);
		}
		return (camera.isInFrustum(center, Terrain.SIZE_X / 2, Terrain.SIZE_Y / 2, Terrain.SIZE_Z / 2));
	}
	
	/** return distance from camera */
	public float getCameraDistance()
	{
		return (this._camera_dist);
	}

	public Terrain[] getNeighboors()
	{
		return (this._neighboors);
	}

	/** called when the terrain is added to the world */
	public void onSpawned(World world)
	{
		this.updateNeighboors(world);
		for (Terrain terrain : this._neighboors)
		{
			if (terrain != null)
			{
				terrain.getMesh().unsetState(TerrainMesh.STATE_VERTICES_UP_TO_DATE);
			}	
		}
	}

	/** update Neighboors terrain */
	private void updateNeighboors(World world)
	{
		for (int faceID = 0 ; faceID < 6 ; faceID++)
		{
			Vector3i face = Faces.getFaceVector(faceID);
			Vector3i index = Vector3i.add(this._location.toWorldIndex(), face);
			Terrain terrain = world.getTerrain(index);

			if (terrain != null)
			{
				terrain.setNeighboor(this, Faces.getOppositeFace(faceID));
			}
			this._neighboors[faceID] = terrain;		
		}
	}

	private void setNeighboor(Terrain terrain, int faceID)
	{
		this._neighboors[faceID] = terrain;
	}
}
