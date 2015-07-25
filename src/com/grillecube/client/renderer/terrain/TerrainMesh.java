package com.grillecube.client.renderer.terrain;

import java.nio.FloatBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.blocks.Block;
import com.grillecube.client.world.blocks.BlockTextures;
import com.grillecube.common.world.Terrain;
import com.grillecube.server.Game;

import fr.toss.lib.Logger;

/* 
//POINTS:
		 
  4-----7
 /|    /|
0-----3 |
| 5___|_6 
|/    | /
1-----2		               		
*/

	//POINT0(X, Y, Z, S) new_vec3(-S + X,  S + Y, -S + Z)
	//POINT1(X, Y, Z, S) new_vec3(-S + X, -S + Y, -S + Z)
	//POINT2(X, Y, Z, S) new_vec3(-S + X, -S + Y,  S + Z)
	//POINT3(X, Y, Z, S) new_vec3(-S + X,  S + Y,  S + Z)
	//POINT4(X, Y, Z, S) new_vec3( S + X,  S + Y, -S + Z)
	//POINT5(X, Y, Z, S) new_vec3( S + X, -S + Y, -S + Z)
	//POINT6(X, Y, Z, S) new_vec3( S + X, -S + Y,  S + Z)
	//POINT7(X, Y, Z, S) new_vec3( S + X,  S + Y,  S + Z)

/*
	UVS (texture):
	0--3
	|  |
	1--2
*/

public class TerrainMesh
{
	/** constants */
	private static final float S = 1;	//block size unit
	public static final int MESH_PER_TERRAIN	= 4;
	public static final int MESH_SIZE_Y			= Terrain.TERRAIN_SIZE_Y / MESH_PER_TERRAIN;
	
	/** Mesh state */
	private static final int	STATE_INITIALIZED			= 1;
	private static final int	STATE_VBO_UP_TO_DATE		= 2;
	private static final int	STATE_VERTICES_UP_TO_DATE	= 4;
	private int	_state;
	
	/** terrain data */
	private TerrainClient	_terrain;
	private int 			_meshID;
	private float[]			_vertices;
	private int				_vertex_count;
	
	/** opengl */
	private int	_vaoID;
	private int	_vboID;
	
	public TerrainMesh(TerrainClient terrain, int meshID)
	{
		this._terrain = terrain;
		this._meshID = meshID;
		this._state = 0;
		this._vertices = null;
		this._vertex_count = 0;
	}
	
	/** called in the world update thread */
	public void update()
	{
		if (!this.hasState(STATE_VERTICES_UP_TO_DATE))
		{
			this.generateVertices();
			Game.getLogger().log(Logger.LoggerLevel.DEBUG, "generating mesh vertices!");
		}
	}
	
	/** set terrain mesh vertices depending on terrain block */
	private void	generateVertices()
	{
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
	}

	/** 4 5 1		|	4 1 0 */
	private void pushLeftFace(Stack<MeshVertex> stack, int X, int Y, int Z, float uvy)
	{
		stack.add(new MeshVertex(-S + X,  S + Y, -S + Z, 0.0f, 0.0f, -1.0f, BlockTextures.TEXTURE_UV_WIDTH_UNIT, uvy + BlockTextures.TEXTURE_UV_HEIGHT_UNIT));	//1
		stack.add(new MeshVertex(S + X, -S + Y, -S + Z, 0.0f, 0.0f, -1.0f, 0.0f, uvy + BlockTextures.TEXTURE_UV_HEIGHT_UNIT));	//5
		stack.add(new MeshVertex(S + X,  S + Y, -S + Z, 0.0f, 0.0f, -1.0f, 0.0f, uvy));		//4
		
		stack.add(new MeshVertex(-S + X,  S + Y, -S + Z, 0.0f, 0.0f, -1.0f, BlockTextures.TEXTURE_UV_WIDTH_UNIT, uvy));		//4
		stack.add(new MeshVertex(S + X,  S + Y, -S + Z, 0.0f, 0.0f, -1.0f, 0.0f, uvy));		//4
		stack.add(new MeshVertex(-S + X,  S + Y, -S + Z, 0.0f, 0.0f, -1.0f, BlockTextures.TEXTURE_UV_WIDTH_UNIT, uvy + BlockTextures.TEXTURE_UV_HEIGHT_UNIT));	//1
	}
	
	private void	pushVisibleFaces(Stack<MeshVertex> stack)
	{
		stack.push(new MeshVertex(-1, 1, -1, 0, 0, 0, 0, 0));
		stack.push(new MeshVertex(-1, -1, -1, 0, 0, 0, 0, 0));
		stack.push(new MeshVertex(1, -1, -1, 0, 0, 0, 0, 0));
		
		stack.push(new MeshVertex(-1, 1, -1, 0, 0, 0, 0, 0));
		stack.push(new MeshVertex(1, -1, -1, 0, 0, 0, 0, 0));
		stack.push(new MeshVertex(1, 1, -1, 0, 0, 0, 0, 0));
		/*
		Block	block;
		int		starty;
		int		endy;
		float	uvy;
		
		starty = this._meshID * TerrainMesh.MESH_SIZE_Y;
		endy = starty + TerrainMesh.MESH_SIZE_Y;
		
		for (int x = 0 ; x < TerrainClient.TERRAIN_SIZE_X ; x++)
		{
			for (int y = starty ; y < endy ; y++)
			{
				for (int z = 0 ; z < TerrainClient.TERRAIN_SIZE_Z ; z++)
				{
					block = this._terrain.getBlock(x, y, z);
					if (block.isVisible())
					{
						if (x == 0 || !this._terrain.getBlock(x - 1, y, z).isVisible())
						{
							uvy = block.getTextureIDForFace(Block.FACE_LEFT) * BlockTextures.TEXTURE_UV_HEIGHT_UNIT;
							//this.pushLeftFace(stack, x, y, z, uvy);							
						}
						
						if (x == Terrain.TERRAIN_SIZE_X - 1 || !this._terrain.getBlock(x + 1, y, z).isVisible())
						{
							uvy = block.getTextureIDForFace(Block.FACE_RIGHT) * BlockTextures.TEXTURE_UV_HEIGHT_UNIT;
							//this.pushRightFace(stack, x, y, z, uvy);
						}
						
						if (z == 0 || !this._terrain.getBlock(x, y, z - 1).isVisible())
						{
							uvy = block.getTextureIDForFace(Block.FACE_FRONT) * BlockTextures.TEXTURE_UV_HEIGHT_UNIT;
							//this.pushFrontFace(stack, x, y, z, uvy);
						}
						
						if (z == Terrain.TERRAIN_SIZE_Z - 1 || !this._terrain.getBlock(x, y, z + 1).isVisible())
						{
							uvy = block.getTextureIDForFace(Block.FACE_BACK) * BlockTextures.TEXTURE_UV_HEIGHT_UNIT;
							//this.pushBackFace(stack, x, y, z, uvy);
						}

						
						if (y == 0 || !this._terrain.getBlock(x, y - 1, z).isVisible())
						{
							uvy = block.getTextureIDForFace(Block.FACE_BOT) * BlockTextures.TEXTURE_UV_HEIGHT_UNIT;
							//this.pushBotFace(stack, x, y, z, uvy);
						}
						
						if (y == Terrain.TERRAIN_SIZE_Y - 1 || !this._terrain.getBlock(x, y + 1, z).isVisible())
						{
							uvy = block.getTextureIDForFace(Block.FACE_TOP) * BlockTextures.TEXTURE_UV_HEIGHT_UNIT;
							//this.pushTopFace(stack, x, y, z, uvy);							
						}
					}
				}
			}
		}
		*/
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
		Game.getLogger().log(Logger.LoggerLevel.DEBUG, "initializing mesh!");
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
		
		Game.getLogger().log(Logger.LoggerLevel.DEBUG, "update mesh vbo!");
	}
	
	/** called in the rendering thread */
	public void	render()
	{
		if (!this.hasState(STATE_INITIALIZED))
		{
			this.initialize();
		}
		
		if (!this.hasState(STATE_VBO_UP_TO_DATE))
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
}
