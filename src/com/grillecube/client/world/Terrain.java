package com.grillecube.client.world;

import java.util.Stack;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector3i;

import com.grillecube.client.mod.blocks.ResourceBlocks;
import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.client.renderer.world.terrain.TerrainMesh;
import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.world.blocks.Block;

public class Terrain
{
	//terrain generator
	private static final SimplexNoise noise = new SimplexNoise(4000);
	
	/** terrain meshes */
	private TerrainMesh	_mesh;
	
	/** neighboors */
	private Terrain[]	_neighboors;
	
	/** which face can see another */
	private boolean[][]	_faces_visibility;
	
	
	/** terrain dimensions */
	public static final int	SIZE_X = 16;
	public static final int	SIZE_Y = 16;
	public static final int	SIZE_Z = 16;

	public static final double SIZE_DIAGONAL = Math.sqrt(Terrain.SIZE_X * Terrain.SIZE_X + Terrain.SIZE_Y * Terrain.SIZE_Y + Terrain.SIZE_Z * Terrain.SIZE_Z);
	
	protected TerrainLocation _location;
	
	/** blocks */
	protected short[][][] _blocks;
	
	public Terrain(TerrainLocation location)
	{
		this._location = location;
		this._blocks = new short[Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z];
		this._mesh = new TerrainMesh(this);
		this._neighboors = new Terrain[6];
		this._faces_visibility = new boolean[6][6];
		this.generateRandomTerrain();
	}
	
	private void generateRandomTerrain()
	{
//		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
//		{
//			for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
//			{
//				for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
//				{
//					this._blocks[x][y][z] = ResourceBlocks.DIRT;
//				}
//			}
//		}


		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
				{

					if (noise.noise((this.getWorldLocation().x + x) / 128.0f,
							(this.getWorldLocation().y + y) / 128.0f,
							(this.getWorldLocation().z + z) / 128.0f) < 0)
					{
						this._blocks[x][y][z] = ResourceBlocks.DIRT;
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
			do {
				x += Terrain.SIZE_X;
			} while (x < 0);
			terrain = terrain.getNeighbor(Faces.FRONT);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		else if (x >= Terrain.SIZE_X)
		{
			do {
				x -= Terrain.SIZE_X;
			} while (x >= Terrain.SIZE_X);
			terrain = terrain.getNeighbor(Faces.BACK);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		
		
		//y test
		if (y < 0)
		{
			do {
				y += Terrain.SIZE_Y;
			} while (y < 0);
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
			do {
				z += Terrain.SIZE_X;
			} while (z < 0);
			terrain = terrain.getNeighbor(Faces.LEFT);
			if (terrain == null)
			{
				return (BlockManager.getBlockByID(ResourceBlocks.AIR));
			}
		}
		else if (z >= Terrain.SIZE_Z)
		{
			z = z % Terrain.SIZE_Z;
			terrain = terrain.getNeighbor(Faces.RIGHT);
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
	
	public Block getBlockUnsafe(int x, int y, int z)
	{
		return (BlockManager.getBlockByID(this._blocks[x][y][z]));
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
		return (camera.isInFrustum(this.getLocation().getCenter(), Terrain.SIZE_X / 2, Terrain.SIZE_Y / 2, Terrain.SIZE_Z / 2));
	}
	
	/** return true if this terrain mesh in the given Camera frustum (+ imprecision) */
	public boolean	isInFrustum(Camera camera, float imprecision)
	{
		return (camera.isInFrustum(this.getLocation().getCenter(), Terrain.SIZE_X / 2, Terrain.SIZE_Y / 2, Terrain.SIZE_Z / 2, imprecision));
	}
	
	/** return distance from camera */
	public float getCameraDistance(Camera camera)
	{
		return ((float) (Vector3f.distance(camera.getPosition(), this.getCenter())));
	}
	
	/** return terrain center world position */
	public Vector3f getCenter()
	{
		return (this._location.getCenter());
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
			this.setNeighboor(terrain, faceID);
		}
	}

	private void setNeighboor(Terrain terrain, int faceID)
	{
		this._neighboors[faceID] = terrain;
	}
	
	/**
	 * this function updates the visibility of each face toward another using a flood fill algorythm
	 * for each cell which werent already visited:
	 * 		- use flood fill algorythm, and register which faces are touched by the flood
	 * 		- for each of touched faces, set the visibility linked with the others touched
	 */

	/** this set the 'this._faces_visibility' bits to 1 if faces can be seen from another 
	 * 
	 *	This uses an explicit stack (to avoid stackoverflow in recursive) 
	 **/
	public void updateFacesVisiblity()
	{
		Stack<Vector3i>	stack;	//explicit stack
		short[][][] flood;
		short		color;
		boolean[]	touched_by_flood;

		stack = new Stack<Vector3i>();
		flood = new short[Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z];
		color = 1;
		touched_by_flood = new boolean[6];

		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
				{
					if (!BlockManager.getBlockByID(this._blocks[x][y][z]).isOpaque() && flood[x][y][z] == 0)
					{
						for (int i = 0 ; i < 6 ; i++)
						{
							touched_by_flood[i] = false;
						}
						
						stack.push(new Vector3i(x, y, z));
						while (!stack.isEmpty())	//this loop will empty the stack and propagate the flood
						{
							Vector3i pos = stack.pop();
							
							if (pos.x < 0)
							{
								touched_by_flood[Faces.FRONT] = true;
								continue ;
							}
			
							if (pos.y < 0)
							{
								touched_by_flood[Faces.BOT] = true;
								continue ;
							}
							
							if (pos.z < 0)
							{
								touched_by_flood[Faces.LEFT] = true;
								continue ;
							}
							
							if (pos.x >= Terrain.SIZE_X)
							{
								touched_by_flood[Faces.BACK] = true;
								continue ;
							}
			
							if (pos.y >= Terrain.SIZE_Y)
							{
								touched_by_flood[Faces.TOP] = true;
								continue ;
							}
							
							if (pos.z >= Terrain.SIZE_Z)
							{
								touched_by_flood[Faces.RIGHT] = true;
								continue ;
							}
							
							if (BlockManager.getBlockByID(this._blocks[pos.x][pos.y][pos.z]).isOpaque() || flood[pos.x][pos.y][pos.z] != 0)
							{
								//hitted a full block
								continue ;
							}

							flood[pos.x][pos.y][pos.z] = color;
							
							stack.push(new Vector3i(pos.x + 1, pos.y + 0, pos.z + 0));
							stack.push(new Vector3i(pos.x - 1, pos.y + 0, pos.z + 0));
							stack.push(new Vector3i(pos.x + 0, pos.y + 1, pos.z + 0));
							stack.push(new Vector3i(pos.x + 0, pos.y - 1, pos.z + 0));
							stack.push(new Vector3i(pos.x + 0, pos.y + 0, pos.z + 1));
							stack.push(new Vector3i(pos.x + 0, pos.y + 0, pos.z - 1));
						}
						
						for (int i = 0 ; i < 6 ; i++)
						{
							if (touched_by_flood[i])
							{
								for (int j = 0 ; j < 6 ; j++)
								{
									if (touched_by_flood[j])
									{
										this._faces_visibility[i][j] = true;
										this._faces_visibility[j][i] = true;
									}
								}	
							}
						}
					}
				}
			}
		}
	}
	
	/** return true if the given faces id can be seen from another */
	public boolean canBeSeen(int faceA, int faceB)
	{
		return (this._faces_visibility[faceA][faceB]);
	}
}
