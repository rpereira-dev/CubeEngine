package com.grillecube.client.renderer.terrain;

import java.nio.FloatBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.blocks.Block;
import com.grillecube.common.world.Terrain;

import fr.toss.lib.Vector3i;

public class TerrainMesh
{
	/** constants */
	private static final float S 	= 1;	//block size unit
	
	//unit uv for a block
	public static float UVX 	= 1;
	public static float UVY 	= 1;
	
	/** Mesh state */
	public static final int	STATE_INITIALIZED			= 1;
	public static final int	STATE_VBO_UP_TO_DATE		= 2;
	public static final int	STATE_VERTICES_UP_TO_DATE	= 4;
	private int	_state;
	
	/** terrain data */
	private TerrainClient	_terrain;
	private float[]			_vertices;
	private int				_vertex_count;
	
	/** distance to camera */
	private Vector3f	_center;
	
	/** opengl */
	private int	_vaoID;
	private int	_vboID;

	private float _camera_dist;
	
	public TerrainMesh(TerrainClient terrain)
	{
		this._terrain = terrain;
		this._state = 0;
		this._vertices = null;
		this._vertex_count = 0;
		this._center = new Vector3f(terrain.getLocation().getX() * Terrain.SIZE_X + Terrain.SIZE_X / 2,
									terrain.getLocation().getY() * Terrain.SIZE_Y + Terrain.SIZE_Y / 2,
									terrain.getLocation().getZ() * Terrain.SIZE_Z + Terrain.SIZE_Z / 2);		
	}
	
	/** set terrain mesh vertices depending on terrain block */
	public void	generateVertices()
	{
		// lock the vboUpdate() function, so it is not called before all vertices are generated
		this.setState(STATE_VBO_UP_TO_DATE);

		Stack<MeshVertex>	stack;
		MeshVertex			vertex;
		
		stack = new Stack<MeshVertex>();
		
		this.pushVisibleFaces(stack);
		
		this._vertices = new float[stack.size() * 9]; //each vertex is 9 floats (x, y, z, nx, ny, nz, uvx, uvy, ao)
		int i = 0;
		while (stack.size() > 0)
		{
			vertex = stack.peek();
			this._vertices[i++] = vertex.posx;
			this._vertices[i++] = vertex.posy;
			this._vertices[i++] = vertex.posz;
			this._vertices[i++] = vertex.normalx;
			this._vertices[i++] = vertex.normaly;
			this._vertices[i++] = vertex.normalz;
			this._vertices[i++] = vertex.uvx;
			this._vertices[i++] = vertex.uvy;
			this._vertices[i++] = vertex.ao;

			stack.pop();
		}
		stack.clear();
		
		this.setState(STATE_VERTICES_UP_TO_DATE);
		this.unsetState(STATE_VBO_UP_TO_DATE);
	}
	
	//the float returned is the ratio of black which will be used for this vertex
	private float getVertexAO(TerrainClient[][][] neighbors, int x, int y, int z, int ox, int oy, int oz)
	{
		boolean side1 = this.isBlockVisible(neighbors, x + ox, y + oy, z);
		boolean side2 = this.isBlockVisible(neighbors, x, y + oy, z + oz);
		boolean corner = this.isBlockVisible(neighbors, x + ox, y + oy, z + oz);
		if (side1 && side2)
		{
			return (0.5f);
		}
		float ao = 0;
		if (side1)
		{
			ao += 0.2f;
		}
		if (side2)
		{
			ao += 0.2f;
		}
		if (corner)
		{
			ao += 0.2f;
		}
		return (ao);
	}
	 
/*
		  4-----7
		 /|    /|		7 4
		0-----3 |		6 5
		| 5___|_6 
		|/    | /
		1-----2
*/
	//1, 5, 6 ; 1 6 2
	private void pushBotFace(TerrainClient[][][] neighbors, Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, -1, 0, uvx, uvy, this.getVertexAO(neighbors, X, Y, Z, -1, -1, -1)));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, -1, 0, uvx, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, -1, -1, 1)));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, -1, 0, uvx + UVX, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, 1, -1, 1)));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, -1, 0, uvx + UVX, uvy, this.getVertexAO(neighbors, X, Y, Z, 1, -1, -1)));
	}
	

	//4, 0, 3 ; 4, 3, 7
	private void pushTopFace(TerrainClient[][][] neighbors, Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 1, 0, uvx, uvy, this.getVertexAO(neighbors, X, Y, Z, -1, 1, 1)));
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 1, 0, uvx, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, -1, 1, -1)));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 1, 0, uvx + UVX, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, 1, 1, -1)));
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 1, 0, uvx + UVX, uvy, this.getVertexAO(neighbors, X, Y, Z, 1, 1, 1)));
	}

	
	//3, 2, 6 ; 3, 6, 7
	private void pushRightFace(TerrainClient[][][] neighbors, Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 1, 0, 0, uvx, uvy, this.getVertexAO(neighbors, X, Y, Z, 1, 1, -1)));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 1, 0, 0, uvx, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, 1, 0, -1)));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 1, 0, 0, uvx + UVX, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, 1, 0, 1)));
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 1, 0, 0, uvx + UVX, uvy, this.getVertexAO(neighbors, X, Y, Z, 1, 1, 1)));
	}
		
		
	//4, 5, 1 ; 4, 1, 0
	private void pushLeftFace(TerrainClient[][][] neighbors, Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, -1, 0, 0, uvx, uvy, this.getVertexAO(neighbors, X, Y, Z, -1, 1, 1)));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, -1, 0, 0, uvx, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, -1, 0, 1)));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, -1, 0, 0, uvx + UVX, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, -1, 0, -1)));
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, -1, 0, 0, uvx + UVX, uvy, this.getVertexAO(neighbors, X, Y, Z, -1, 1, -1)));
	}

	

	//7, 6, 5 ; 7, 5, 4
	private void pushBackFace(TerrainClient[][][] neighbors, Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 0, 1, uvx, uvy, this.getVertexAO(neighbors, X, Y, Z, 1, 1, 1)));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, 0, 1, uvx, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, 1, 0, 1)));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, 0, 1, uvx + UVX, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, -1, 0, 1)));
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 0, 1, uvx + UVX, uvy, this.getVertexAO(neighbors, X, Y, Z, -1, 1, 1)));
	}
	
	//0, 1, 2 ; 0, 2, 3
	private void pushFrontFace(TerrainClient[][][] neighbors, Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 0, -1, uvx, uvy, this.getVertexAO(neighbors, X, Y, Z, -1, 1, -1)));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, 0, -1, uvx, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, -1, 0, -1)));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, 0, -1, uvx + UVX, uvy + UVY, this.getVertexAO(neighbors, X, Y, Z, 1, 0, -1)));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 0, -1, uvx + UVX, uvy, this.getVertexAO(neighbors, X, Y, Z, 1, 1, -1)));
	}
	
	/**
	 * this can be optimized:
	 * 
	 * instead of calling 'WorldClient.getBlock()' several times,
	 * just get nearby terrain once, and then get blocks
	 */
	private void	pushVisibleFaces(Stack<MeshVertex> stack)
	{
		TerrainClient	neighbors[][][];
		Block	block;
		float	uvx;
		float	uvy;

		neighbors = this._terrain.getNeighboors();
		for (int x = 0 ; x < TerrainClient.SIZE_X ; x++)
		{
			for (int y = 0 ; y < TerrainClient.SIZE_Y ; y++)
			{
				for (int z = 0 ; z < TerrainClient.SIZE_Z ; z++)
				{
					block = this._terrain.getBlock(x, y, z);
					if (block.isVisible())
					{
						if (this.isBlockVisible(neighbors, x - 1, y, z) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_LEFT) * UVY;
							this.pushLeftFace(neighbors, stack, x, y, z, uvx, uvy);
						}
						
						if (this.isBlockVisible(neighbors, x + 1, y, z) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_RIGHT) * UVY;
							this.pushRightFace(neighbors, stack, x, y, z, uvx, uvy);
						}
						
						if (this.isBlockVisible(neighbors, x, y, z - 1) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_FRONT) * UVY;
							this.pushFrontFace(neighbors, stack, x, y, z, uvx, uvy);
						}

						if (this.isBlockVisible(neighbors, x, y, z + 1) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_BACK) * UVY;
							this.pushBackFace(neighbors, stack, x, y, z, uvx, uvy);
						}
						
						if (this.isBlockVisible(neighbors, x, y - 1, z) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_BOT) * UVY;
							this.pushBotFace(neighbors, stack, x, y, z, uvx, uvy);
						}

						if (this.isBlockVisible(neighbors, x, y + 1, z) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_TOP) * UVY;
							this.pushTopFace(neighbors, stack, x, y, z, uvx, uvy);							
						}
					}
				}
			}
		}
	}
	
	/** position are relative to this._terrain (=== neighbors[1][1][1]) */
	private boolean isBlockVisible(TerrainClient[][][] neighbors, int posx, int posy, int posz)
	{
		int	terrainx;
		int	terrainy;
		int	terrainz;
		
		terrainx = 1;
		terrainy = 1;
		terrainz = 1;
		if (posx < 0)
		{
			terrainx--;
			posx += Terrain.SIZE_X;
		}
		if (posy < 0)
		{
			terrainy--;
			posy += Terrain.SIZE_Y;
		}
		if (posz < 0)
		{
			terrainz--;
			posz += Terrain.SIZE_Z;
		}
		if (posx >= Terrain.SIZE_X)
		{
			terrainx++;
			posx -= Terrain.SIZE_X;
		}
		if (posy >= Terrain.SIZE_Y)
		{
			terrainy++;
			posy -= Terrain.SIZE_Y;
		}
		if (posz >= Terrain.SIZE_Z)
		{
			terrainz++;
			posz -= Terrain.SIZE_Z;
		}
		if (neighbors[terrainx][terrainy][terrainz] == null)
		{
			return (false);
		}
		return (neighbors[terrainx][terrainy][terrainz].getBlock(posx, posy, posz).isVisible());
	}

	/** initialize opengl stuff (vao, vbo) */
	private void	initialize()
	{
		this._vaoID = GL30.glGenVertexArrays();
		this._vboID = GL15.glGenBuffers();
		
		GL30.glBindVertexArray(this._vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), 0);					//x, y, z
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), 3 * 4);				//nx, ny, nz
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), (3 + 3) * 4);		//uvx, uvy
		GL20.glVertexAttribPointer(3, 2, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), (3 + 3 + 2) * 4);	//ao
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);
				
		this.setState(STATE_INITIALIZED);
	}
	
	/** update vertex buffer object (vertices buffer opengl-side) */
	private void	updateVBO()
	{
		FloatBuffer	vertex_buffer;
		
		if (this._vertices == null)
		{
			//mesh isnt ready
			return ;
		}
		vertex_buffer = BufferUtils.createFloatBuffer(this._vertices.length);
		vertex_buffer.put(this._vertices);
		vertex_buffer.flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 0, GL15.GL_STREAM_DRAW);	//reset buffer data
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer, GL15.GL_STREAM_DRAW);	//set vbo data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		this._vertex_count = this._vertices.length / 8;	//a vertex is 8 floats (x, y, z, nx, ny, nz, uvx, uvy)
		
		this._vertices = null;	//gc will delete it
		this.setState(STATE_VBO_UP_TO_DATE);
		
	}
	
	/** called in the rendering thread */
	public void	render()
	{
		if (!this.hasState(STATE_INITIALIZED))
		{
			this.initialize();
		}
		
		if (!this.hasState(STATE_VBO_UP_TO_DATE) && this.hasState(STATE_VERTICES_UP_TO_DATE))
		{
			this.updateVBO();
		}
		
		if (this._vertex_count <= 0)
		{
			return ;
		}
		
		GL30.glBindVertexArray(this._vaoID);
		
		GL20.glEnableVertexAttribArray(0);	//position
		GL20.glEnableVertexAttribArray(1);	//normal
		GL20.glEnableVertexAttribArray(2);	//uv
		GL20.glEnableVertexAttribArray(3);	//ao factor
		
		GL11.glDrawArrays(GL11.GL_QUADS, 0, this._vertex_count);
								
		GL30.glBindVertexArray(0);
	}
	
	public boolean	hasState(int state)
	{
		return ((this._state & state) == state);
	}
	
	public void		setState(int state)
	{
		this._state = this._state | state;
	}
	
	public void		unsetState(int state)
	{
		this._state = this._state & ~(state);
	}
	
	public void		switchState(int state)
	{
		this._state = this._state ^ state;
	}

	public Vector3f getCenter()
	{
		return (this._center);
	}

	/** return true if this terrain mesh in the given Camera frustum */
	public boolean	isInFrustum(Camera camera)
	{
		float dx = camera.getPosition().getX() - this._center.x;
		float dy = camera.getPosition().getY() - this._center.y;
		float dz = camera.getPosition().getZ() - this._center.z;
		
		this._camera_dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		
		if (this._camera_dist >= camera.getRenderDistance())
		{
			return (false);
		}
		return (camera.isInFrustum(this.getCenter(), Terrain.SIZE_X / 2, Terrain.SIZE_Y / 2, Terrain.SIZE_Z / 2));
	}

	/**
	 * implementations follows this:
	 * https://tomcc.github.io/2014/08/31/visibility-1.html
	 * 
	 * FIRST:
	 * 		In each terrain meshes, determines which faces can be seen from another one
	 * 		(visibility is registered in 'this._faces_visibility'
	 */

	/**
	 * this function updates the visibility of each face toward another using a flood fill algorythm
	 */
	private int	_faces_visibility;

	public static final int LEFT_RIGHT	= (1 << 0);
	public static final int LEFT_BOT	= (1 << 1);
	public static final int LEFT_TOP	= (1 << 2);
	public static final int LEFT_FRONT	= (1 << 3);
	public static final int LEFT_BACK	= (1 << 4);
	
	public static final int RIGHT_TOP	= (1 << 5);
	public static final int RIGHT_BOT	= (1 << 6);
	public static final int RIGHT_FRONT	= (1 << 7);
	public static final int RIGHT_BACK	= (1 << 8);
	
	public static final int TOP_BOT		= (1 << 9);
	public static final int TOP_FRONT	= (1 << 10);
	public static final int TOP_BACK	= (1 << 11);
	
	public static final int BOT_FRONT	= (1 << 12);
	public static final int BOT_BACK	= (1 << 13);
	
	public static final int FRONT_BACK	= (1 << 14);
	
	//index for the 'touch_by_flood' array
	private static final int LEFT 	= 0;
	private static final int RIGHT	= 1;
	private static final int FRONT	= 2;
	private static final int BACK	= 3;
	private static final int TOP	= 4;
	private static final int BOT	= 5;
	
	/** this set the 'this._faces_visibility' bits to 1 if faces can be seen from another 
	 * 
	 *	This uses an explicit stack (to avoid stackoverflow in recursive) 
	 **/
	public void updateFacesVisiblity()
	{
		Stack<Vector3i>	stack;	//explicit stack
		short[][][] blocks;
		short[][][] flood;
		short		color;
		boolean[]	touched_by_flood;

		this._faces_visibility = 0;
		stack = new Stack<Vector3i>();
		blocks = this._terrain.getBlocks();
		flood = new short[Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z];
		color = 1;
		touched_by_flood = new boolean[6];

		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
				{
					if (!BlockManager.getBlockByID(blocks[x][y][z]).isOpaque() && flood[x][y][z] == 0)
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
								touched_by_flood[LEFT] = true;
								continue ;
							}
			
							if (pos.y < 0)
							{
								touched_by_flood[BOT] = true;
								continue ;
							}
							
							if (pos.z < 0)
							{
								touched_by_flood[FRONT] = true;
								continue ;
							}
							
							if (pos.x >= Terrain.SIZE_X)
							{
								touched_by_flood[RIGHT] = true;
								continue ;
							}
			
							if (pos.y >= Terrain.SIZE_Y)
							{
								touched_by_flood[TOP] = true;
								continue ;
							}
							
							if (pos.z >= Terrain.SIZE_Z)
							{
								touched_by_flood[BACK] = true;
								continue ;
							}
							
							if (BlockManager.getBlockByID(blocks[pos.x][pos.y][pos.z]).isOpaque() || flood[pos.x][pos.y][pos.z] != 0)
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
						
						//the flood propagation is ended, the 'touch_by_flood' array contains linked face
						//time to create 15 conditions to set flags
						//left
						if (touched_by_flood[LEFT] && touched_by_flood[RIGHT])
						{
							this._faces_visibility = this._faces_visibility | LEFT_RIGHT;
						}
						
						if (touched_by_flood[LEFT] && touched_by_flood[TOP])
						{
							this._faces_visibility = this._faces_visibility | LEFT_TOP;
						}
						
						if (touched_by_flood[LEFT] && touched_by_flood[BOT])
						{
							this._faces_visibility = this._faces_visibility | LEFT_BOT;
						}
						
						if (touched_by_flood[LEFT] && touched_by_flood[FRONT])
						{
							this._faces_visibility = this._faces_visibility | LEFT_FRONT;
						}
						
						if (touched_by_flood[LEFT] && touched_by_flood[BACK])
						{
							this._faces_visibility = this._faces_visibility | LEFT_BACK;
						}
						
						//right
						if (touched_by_flood[RIGHT] && touched_by_flood[TOP])
						{
							this._faces_visibility = this._faces_visibility | RIGHT_TOP;
						}
						
						if (touched_by_flood[RIGHT] && touched_by_flood[BOT])
						{
							this._faces_visibility = this._faces_visibility | RIGHT_BOT;
						}
						
						if (touched_by_flood[RIGHT] && touched_by_flood[FRONT])
						{
							this._faces_visibility = this._faces_visibility | RIGHT_FRONT;
						}
						
						if (touched_by_flood[RIGHT] && touched_by_flood[BACK])
						{
							this._faces_visibility = this._faces_visibility | RIGHT_BACK;
						}
						
						//TOP
						if (touched_by_flood[TOP] && touched_by_flood[BOT])
						{
							this._faces_visibility = this._faces_visibility | TOP_BOT;
						}
						
						if (touched_by_flood[TOP] && touched_by_flood[FRONT])
						{
							this._faces_visibility = this._faces_visibility | TOP_FRONT;
						}
						
						if (touched_by_flood[TOP] && touched_by_flood[BACK])
						{
							this._faces_visibility = this._faces_visibility | TOP_BACK;
						}
						
						//BOT
						if (touched_by_flood[BOT] && touched_by_flood[FRONT])
						{
							this._faces_visibility = this._faces_visibility | BOT_FRONT;
						}

						if (touched_by_flood[BOT] && touched_by_flood[BACK])
						{
							this._faces_visibility = this._faces_visibility | BOT_BACK;
						}
						
						//FRONT
						if (touched_by_flood[FRONT] && touched_by_flood[BACK])
						{
							this._faces_visibility = this._faces_visibility | FRONT_BACK;
						}
					}
				}
			}
		}
	}
	
	/** return true if the given faces id are linked (e.g: FRONT_BACK) */
	public boolean areFacesLinked(int id)
	{
		return ((this._faces_visibility & id) == id);
	}
}
