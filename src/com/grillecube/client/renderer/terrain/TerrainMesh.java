package com.grillecube.client.renderer.terrain;

import java.util.Stack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector3i;

import com.grillecube.client.renderer.opengl.GLH;
import com.grillecube.client.renderer.opengl.object.VertexArray;
import com.grillecube.client.renderer.opengl.object.VertexBuffer;
import com.grillecube.client.world.Faces;
import com.grillecube.client.world.Terrain;
import com.grillecube.client.world.blocks.Block;


/*
	  4-----7
	 /|    /|		7 4
	0-----3 |		6 5
	| 5___|_6 
	|/    | /
	1-----2
*/

public class TerrainMesh
{
	/** constants */
	private static final float S = 1;	//block size unit
	
	//unit uv for a block
	public static float UVX = 1;
	public static float UVY = 1;
	
	/** Mesh state */
	public static final int	STATE_INITIALIZED			= 1;
	public static final int	STATE_VBO_UP_TO_DATE		= 2;
	public static final int	STATE_VERTICES_UP_TO_DATE	= 4;
	
	private int	_state;
	
	/** terrain data */
	private Terrain _terrain;
	private float[] _vertices;
	private int	_vertex_count;
	
	/** opengl */
	private VertexArray _vao;
	private VertexBuffer _vbo;

	public TerrainMesh(Terrain terrain)
	{
		this._terrain = terrain;
		this._state = 0;
		this._vertices = null;
		this._vertex_count = 0;
	}
	
	/** set terrain mesh vertices depending on terrain block */
	public void	generateVertices()
	{
		// lock the vboUpdate() function, so it is not called before all vertices are generated
		this.setState(STATE_VBO_UP_TO_DATE);

		Stack<MeshVertex> stack = this.getVisibleVertexStack();
		this._vertices = new float[stack.size() * 9]; //each vertex is 9 floats
		this.fillVertices(stack);

		this.setState(STATE_VERTICES_UP_TO_DATE);
		this.unsetState(STATE_VBO_UP_TO_DATE);
	}
	
	/** fill 'this._vertices' with the given block's faces depending on visibility array */
	private void fillVertices(Stack<MeshVertex> stack)
	{
		int ptr = 0; //'this._vertices' buffer addr for next face's vertices
		
		for (MeshVertex vertex : stack)
		{
			this._vertices[ptr++] = vertex.posx;
			this._vertices[ptr++] = vertex.posy;
			this._vertices[ptr++] = vertex.posz;
			this._vertices[ptr++] = vertex.normalx;
			this._vertices[ptr++] = vertex.normaly;
			this._vertices[ptr++] = vertex.normalz;
			this._vertices[ptr++] = vertex.uvx;
			this._vertices[ptr++] = vertex.uvy;
			this._vertices[ptr++] = vertex.ao;
		}
	}
	
	private static Vector3i[][] faces_vertices = new Vector3i[6][6];
	
	static
	{
		faces_vertices[Faces.LEFT][0] = new Vector3i(0, 1, 1);
		faces_vertices[Faces.LEFT][1] = new Vector3i(0, 0, 1);
		faces_vertices[Faces.LEFT][2] = new Vector3i(0, 0, 0);
		faces_vertices[Faces.LEFT][3] = new Vector3i(0, 1, 0);

		faces_vertices[Faces.RIGHT][0] = new Vector3i(1, 1, 0);	
		faces_vertices[Faces.RIGHT][1] = new Vector3i(1, 0, 0);
		faces_vertices[Faces.RIGHT][2] = new Vector3i(1, 0, 1);
		faces_vertices[Faces.RIGHT][3] = new Vector3i(1, 1, 1);

		faces_vertices[Faces.TOP][0] = new Vector3i(0, 1, 1);
		faces_vertices[Faces.TOP][1] = new Vector3i(0, 1, 0);
		faces_vertices[Faces.TOP][2] = new Vector3i(1, 1, 0);
		faces_vertices[Faces.TOP][3] = new Vector3i(1, 1, 1);

		faces_vertices[Faces.BOT][0] = new Vector3i(0, 0, 0);
		faces_vertices[Faces.BOT][1] = new Vector3i(0, 0, 1);
		faces_vertices[Faces.BOT][2] = new Vector3i(1, 0, 1);
		faces_vertices[Faces.BOT][3] = new Vector3i(1, 0, 0);
	
		faces_vertices[Faces.FRONT][0] = new Vector3i(0, 1, 0);
		faces_vertices[Faces.FRONT][1] = new Vector3i(0, 0, 0);
		faces_vertices[Faces.FRONT][2] = new Vector3i(1, 0, 0);
		faces_vertices[Faces.FRONT][3] = new Vector3i(1, 1, 0);

		faces_vertices[Faces.BACK][0] = new Vector3i(1, 1, 1);
		faces_vertices[Faces.BACK][1] = new Vector3i(1, 0, 1);
		faces_vertices[Faces.BACK][2] = new Vector3i(0, 0, 1);
		faces_vertices[Faces.BACK][3] = new Vector3i(0, 1, 1);
	};
	
	private static float[][] faces_uv = {
			{0, 0},
			{0, 1},
			{1, 1},
			{1, 0}
	};

	/** fill an array of dimension [Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z][6] of terrain faces visibility */
	private Stack<MeshVertex> getVisibleVertexStack()
	{
		Stack<MeshVertex> stack = new Stack<MeshVertex>();
		
		//for each block
		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
				{
					Block block = this._terrain.getBlock(x, y, z);
					if (block.isVisible()) //if the block is visible
					{
						for (int faceID = 0 ; faceID < 6 ; faceID++) //for each of it face
						{
							Vector3i vec = Faces.getFaceVector(faceID);
							if (this.isBlockVisible(x + vec.x, y + vec.y, z + vec.z) == false) //if the face-neighboor block is invisible
							{
								this.pushFaceVertices(stack, x, y, z, faceID); //add the face
							}
						}
					}
				}
			}
		}
		return (stack);
	}
	
	private void pushFaceVertices(Stack<MeshVertex> stack, int x, int y, int z, int faceID)
	{
		Block block = this._terrain.getBlock(x, y, z);
		float uvx = 0;
		float uvy = block.getTextureIDForFace(faceID) * UVY;

		Vector3f normal = Faces.getFaceNormal(faceID);
		
		MeshVertex v0 = this.getMeshVertex(faceID, 0, x, y, z, normal, uvx, uvy);
		MeshVertex v1 = this.getMeshVertex(faceID, 1, x, y, z, normal, uvx, uvy);
		MeshVertex v2 = this.getMeshVertex(faceID, 2, x, y, z, normal, uvx, uvy);
		MeshVertex v3 = this.getMeshVertex(faceID, 3, x, y, z, normal, uvx, uvy);
		
		if (v0.ao + v2.ao > v1.ao + v3.ao)
		{
			stack.push(v0);
			stack.push(v1);
			stack.push(v2);
			
			stack.push(v0);
			stack.push(v2);
			stack.push(v3);
		}
		else
		{
			stack.push(v3);
			stack.push(v0);
			stack.push(v1);
			
			stack.push(v3);
			stack.push(v1);
			stack.push(v2);
		}
	}
	
	private MeshVertex getMeshVertex(int faceID, int vertexID, int x, int y, int z, Vector3f normal, float uvx, float uvy)
	{
		return (new MeshVertex(x + faces_vertices[faceID][vertexID].x * S,
								y + faces_vertices[faceID][vertexID].y * S,
								z + faces_vertices[faceID][vertexID].z * S,
								normal,
								uvx + faces_uv[vertexID][0] * UVX,
								uvy + faces_uv[vertexID][1] * UVY,
								this.getVertexAO(x, y, z, faces_vertices[faceID][vertexID])));
	}
	
	//the float returned is the ratio of black which will be used for this vertex
	private static final float AO_UNIT	= 0.24f;
	private static final float AO_MAX	= AO_UNIT * 3;
	private float getVertexAO(int x, int y, int z, Vector3i vertex)
	{
		int ox = (vertex.x == 0) ? -1 : 1;
		int oy = (vertex.y == 0) ?  0 : 1;
		int oz = (vertex.z == 0) ? -1 : 1;
		
		boolean side1 = this.isBlockVisible(x + ox, y + oy, z);
		boolean side2 = this.isBlockVisible(x, y + oy, z + oz);
		boolean corner = this.isBlockVisible(x + ox, y + oy, z + oz);
		if (side1 && side2)
		{
			return (AO_MAX);
		}
		
		float ao = 0;
		
		if (side1)
		{
			ao += AO_UNIT;
		}
		
		if (side2)
		{
			ao += AO_UNIT;
		}
		
		if (corner)
		{
			ao += AO_UNIT;
		}
		return (ao);
	}

	/** position are relative to Terrain */
	private boolean isBlockVisible(int posx, int posy, int posz)
	{
		return (this._terrain.getBlock(posx, posy, posz).isVisible());
	}

	/** initialize opengl stuff (vao, vbo) */
	private void	initialize()
	{
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();
		
		this._vao.bind();
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		
		this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), 0);					//x, y, z
		this._vao.setAttribute(1, 3, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), 3 * 4);				//nx, ny, nz
		this._vao.setAttribute(2, 2, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), (3 + 3) * 4);		//uvx, uvy
		this._vao.setAttribute(3, 2, GL11.GL_FLOAT, false, 4 * (3 + 3 + 2 + 1), (3 + 3 + 2) * 4);	//ao

		this._vbo.unbind(GL15.GL_ARRAY_BUFFER);
		this._vao.unbind();
				
		this.setState(STATE_INITIALIZED);
	}
	
	/** update vertex buffer object (vertices buffer opengl-side) */
	private void updateVBO()
	{
		if (this._vertices == null)
		{
			//mesh isnt ready
			return ;
		}
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, this._vertices, GL15.GL_STREAM_DRAW);
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
		
		this._vao.bind();
		this._vao.enableAttribute(0);
		this._vao.enableAttribute(1);
		this._vao.enableAttribute(2);
		this._vao.enableAttribute(3);

		this._vao.draw(GL11.GL_TRIANGLES, 0, this._vertex_count);
		
		this._vao.disableAttribute(0);
		this._vao.disableAttribute(1);
		this._vao.disableAttribute(2);
		this._vao.disableAttribute(3);
		GL30.glBindVertexArray(0);
	}
	
	public boolean hasState(int state)
	{
		return ((this._state & state) == state);
	}
	
	public void	setState(int state)
	{
		this._state = this._state | state;
	}
	
	public void	unsetState(int state)
	{
		this._state = this._state & ~(state);
	}
	
	public void	switchState(int state)
	{
		this._state = this._state ^ state;
	}


	/** update the mesh: rebuild it if needed */
	public void update()
	{
		if (!this.hasState(TerrainMesh.STATE_VERTICES_UP_TO_DATE))
		{
			this.generateVertices();
		}
	}

	public int getVertexCount()
	{
		return (this._vertex_count);
	}
}
