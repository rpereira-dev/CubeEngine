package com.grillecube.client.renderer.terrain;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.renderer.opengl.GLH;
import com.grillecube.client.renderer.opengl.object.VertexArray;
import com.grillecube.client.renderer.opengl.object.VertexBuffer;
import com.grillecube.client.world.Terrain;

public class TerrainMesh
{
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

	/** update this mesh vertices */
	public void updateVertices(TerrainMesher mesher)
	{
		// lock the vboUpdate() function, so it is not called before all vertices are generated
		this.setState(TerrainMesh.STATE_VBO_UP_TO_DATE);

		this._vertices = mesher.generateVertices(this._terrain);

		this.setState(TerrainMesh.STATE_VERTICES_UP_TO_DATE);
		this.unsetState(TerrainMesh.STATE_VBO_UP_TO_DATE);
	}

	public int getVertexCount()
	{
		return (this._vertex_count);
	}
}
