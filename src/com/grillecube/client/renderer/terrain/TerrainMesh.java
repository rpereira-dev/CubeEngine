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
import com.grillecube.client.world.blocks.BlockTextures;
import com.grillecube.common.world.Terrain;

public class TerrainMesh
{
	
	/** constants */
	private static final float S 	= 1;	//block size unit
	public static final float UVX 	= 1;
	public static final float UVY 	= (1 / (float)BlockTextures.MAX_ID);
	
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
		
		this._vertices = new float[stack.size() * 8]; //each vertex is 8 floats (x, y, z, nx, ny, nz, uvx, uvy)
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
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 0, 0, uvx, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 0, 0, uvx + UVX, uvy + UVY));
		
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 0, 0, uvx + UVX, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 0, 0, uvx + UVX, uvy));
	}
	
	//1, 5, 6 ; 1 6 2
	private void pushBotFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, 0, 0, uvx, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, 0, 0, uvx + UVX, uvy + UVY));
		
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, 0, 0, uvx + UVX, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, 0, 0, uvx + UVX, uvy));
	}
	
	//4, 5, 1 ; 4, 1, 0
	private void pushLeftFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, 0, 0, uvx, uvy + UVY));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, 0, 0, uvx + UVX, uvy + UVY));
		
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, 0, 0, uvx + UVX, uvy + UVY));
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 0, 0, uvx + UVX, uvy));
	}
	
	//3, 2, 6 ; 3, 6, 7
	private void pushRightFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, 0, 0, uvx, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, 0, 0, uvx + UVX, uvy + UVY));
		
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, 0, 0, uvx + UVX, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 0, 0, uvx + UVX, uvy));
	}
	
	//0, 1, 2 ; 0, 2, 3
	private void pushFrontFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + 0, 0, 0, 0, uvx, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, 0, 0, uvx + UVX, uvy + UVY));
		
		stack.push(new MeshVertex(X + 0, Y + S, Z + 0, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + S, Y + 0, Z + 0, 0, 0, 0, uvx + UVX, uvy + UVY));
		stack.push(new MeshVertex(X + S, Y + S, Z + 0, 0, 0, 0, uvx + UVX, uvy));
	}
	
	//7, 6, 5 ; 7, 5, 4
	private void pushBackFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvx, float uvy)
	{
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + S, Y + 0, Z + S, 0, 0, 0, uvx, uvy + UVY));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, 0, 0, uvx + UVX, uvy + UVY));
		
		stack.push(new MeshVertex(X + S, Y + S, Z + S, 0, 0, 0, uvx, uvy));
		stack.push(new MeshVertex(X + 0, Y + 0, Z + S, 0, 0, 0, uvx + UVX, uvy + UVY));
		stack.push(new MeshVertex(X + 0, Y + S, Z + S, 0, 0, 0, uvx + UVX, uvy));
	}
	
	private void	pushVisibleFaces(Stack<MeshVertex> stack)
	{
		Terrain	neighbors[];
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

						if ((x == 0 && neighbors[2] != null && (neighbors[2].getBlock(Terrain.SIZE_X - 1, y, z).isVisible() == false))
							|| (x > 0 && this._terrain.getBlock(x - 1, y, z).isVisible() == false))
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_LEFT) * UVY;
							this.pushLeftFace(stack, x, y, z, uvx, uvy);
						}

						
						if ((x == Terrain.SIZE_X - 1 && neighbors[3] != null && (neighbors[3].getBlock(0, y, z).isVisible() == false))
								|| (x < Terrain.SIZE_X - 1 && this._terrain.getBlock(x + 1, y, z).isVisible() == false))
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_RIGHT) * UVY;
							this.pushRightFace(stack, x, y, z, uvx, uvy);
						}
						
						if ((z == 0 && neighbors[4] != null && (neighbors[4].getBlock(x, y, Terrain.SIZE_Z - 1).isVisible() == false))
								|| (z > 0 && this._terrain.getBlock(x, y, z - 1).isVisible() == false))
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_FRONT) * UVY;
							this.pushFrontFace(stack, x, y, z, uvx, uvy);
						}

						if ((z == Terrain.SIZE_Z - 1 && neighbors[5] != null && (neighbors[5].getBlock(x, y, 0).isVisible() == false))
								|| (z < Terrain.SIZE_Z - 1 && this._terrain.getBlock(x, y, z + 1).isVisible() == false))
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_BACK) * UVY;
							this.pushBackFace(stack, x, y, z, uvx, uvy);
						}
						
						if ((y == 0 && neighbors[0] != null && neighbors[0].getBlock(x, Terrain.SIZE_Y - 1, z).isVisible() == false)
								|| (y > 0 && this._terrain.getBlock(x, y - 1, z).isVisible() == false))
						{
							uvx = 0;
							uvy = block.getTextureIDForFace(Block.FACE_BOT) * UVY;
							this.pushBotFace(stack, x, y, z, uvx, uvy);
						}

						if ((y == Terrain.SIZE_Y - 1 && (neighbors[1] == null || (neighbors[1].getBlock(x, 0, z).isVisible() == false)))
								|| (y < Terrain.SIZE_Y - 1 && this._terrain.getBlock(x, y + 1, z).isVisible() == false))
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

	/** initialize opengl stuff (vao, vbo) */
	private void	initialize()
	{
		this._vaoID = GL30.glGenVertexArrays();
		this._vboID = GL15.glGenBuffers();
		
		GL30.glBindVertexArray(this._vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2), 0);				//x, y, z
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2), 3 * 4);			//nx, ny, nz
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2), (3 + 3) * 4);	//uvx, uvy
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

	/** return true if this mesh is visible (executed in the rendernig main thread */
	public boolean isVisible(Camera camera)
	{
		return (true);
	}
}
