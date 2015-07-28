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
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.blocks.Block;
import com.grillecube.common.world.Terrain;

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
	public static final int	STATE_VISIBLE				= 8;
	private int	_state;
	
	/** terrain data */
	private TerrainClient	_terrain;
	private float[]			_vertices;
	private int				_vertex_count;
	
	/** distance to camera */
	private Vector3f	_center;
	private float		_camera_dist;
	
	/** opengl */
	private int	_vaoID;
	private int	_vboID;
	
	public TerrainMesh(TerrainClient terrain)
	{
		this._terrain = terrain;
		this._state = 0;
		this._vertices = null;
		this._vertex_count = 0;
		this._center = new Vector3f(terrain.getLocation().getX() * Terrain.SIZE_X + Terrain.SIZE_X / 2,
									terrain.getLocation().getY() * Terrain.SIZE_Y + Terrain.SIZE_Y / 2,
									terrain.getLocation().getZ() * Terrain.SIZE_Z + Terrain.SIZE_Z / 2);
		this._camera_dist = Float.MAX_VALUE;
		
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
		
		this._vertices = new float[stack.size() * 9]; //each vertex is 9 floats (x, y, z, nx, ny, nz, uvx, uvy, shade)
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
			this._vertices[i++] = 0;

			stack.pop();
		}
		stack.clear();
		
		this.setState(STATE_VERTICES_UP_TO_DATE);
		this.unsetState(STATE_VBO_UP_TO_DATE);
	}
	
	 
/*
		  4-----7
		 /|    /|		7 4
		0-----3 |		6 5
		| 5___|_6 
		|/    | /
		1-----2
*/
	//4, 0, 3 ; 4, 3, 7
	private void pushTopFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 1, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 1, 0, uvx, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 1, 0, uvx + UVX, uvy + UVY, 0));
		
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 1, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 1, 0, uvx + UVX, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 1, 0, uvx + UVX, uvy, 0 ));
	}
	
	//1, 5, 6 ; 1 6 2
	private void pushBotFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, -1, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, -1, 0, uvx, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, -1, 0, uvx + UVX, uvy + UVY, 0));
		
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, -1, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, -1, 0, uvx + UVX, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, -1, 0, uvx + UVX, uvy, 0));
	}
	
	//0, 1, 2 ; 0, 2, 3
	private void pushFrontFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 0, -1, uvx, uvy, 0));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, 0, -1, uvx, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, 0, -1, uvx + UVX, uvy + UVY, 0));
		
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 0, -1, uvx, uvy, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, 0, -1, uvx + UVX, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 0, -1, uvx + UVX, uvy, 0));
	}
	
	//7, 6, 5 ; 7, 5, 4
	private void pushBackFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 0, 1, uvx, uvy, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, 0, 1, uvx, uvy + UVY, 0));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, 0, 1, uvx + UVX, uvy + UVY, 0));

		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 0, 1, uvx, uvy, 0));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, 0, 1, uvx + UVX, uvy + UVY, 0));
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 0, 1, uvx + UVX, uvy, 0));
	}
	
	//4, 5, 1 ; 4, 1, 0
	private void pushLeftFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, -1, 0, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, -1, 0, 0, uvx, uvy + UVY, 0));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, -1, 0, 0, uvx + UVX, uvy + UVY, 0));
		
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, -1, 0, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, -1, 0, 0, uvx + UVX, uvy + UVY, 0));
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, -1, 0, 0, uvx + UVX, uvy, 0));
	}
	
	//3, 2, 6 ; 3, 6, 7
	private void pushRightFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 1, 0, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 1, 0, 0, uvx, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 1, 0, 0, uvx + UVX, uvy + UVY, 0));
		
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 1, 0, 0, uvx, uvy, 0));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 1, 0, 0, uvx + UVX, uvy + UVY, 0));
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 1, 0, 0, uvx + UVX, uvy, 0));
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
							this.pushLeftFace(stack, x, y, z, uvx, uvy);
						}
						
						if (this.isBlockVisible(neighbors, x + 1, y, z) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_RIGHT) * UVY;
							this.pushRightFace(stack, x, y, z, uvx, uvy);
						}
						
						if (this.isBlockVisible(neighbors, x, y, z - 1) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_FRONT) * UVY;
							this.pushFrontFace(stack, x, y, z, uvx, uvy);
						}

						if (this.isBlockVisible(neighbors, x, y, z + 1) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_BACK) * UVY;
							this.pushBackFace(stack, x, y, z, uvx, uvy);
						}
						
						if (this.isBlockVisible(neighbors, x, y - 1, z) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_BOT) * UVY;
							this.pushBotFace(stack, x, y, z, uvx, uvy);
						}

						if (this.isBlockVisible(neighbors, x, y + 1, z) == false)
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_TOP) * UVY;
							this.pushTopFace(stack, x, y, z, uvx, uvy);							
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
		GL20.glEnableVertexAttribArray(3);	//shade factor

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this._vertex_count);
								
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

	public void updateCameraDistance(Camera camera)
	{
	    float dx = camera.getPosition().getX() - this._center.x;
	    float dy = camera.getPosition().getY() - this._center.y;
	    float dz = camera.getPosition().getZ() - this._center.z;
	    
	    this._camera_dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public float	getCameraDistance()
	{
		return (this._camera_dist);
	}

	public Vector3f getCenter()
	{
		return (this._center);
	}
}
