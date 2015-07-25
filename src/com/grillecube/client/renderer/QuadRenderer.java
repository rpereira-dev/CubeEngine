package com.grillecube.client.renderer;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.program.ProgramQuad;
import com.grillecube.client.world.blocks.BlockTextures;

public class QuadRenderer
{
	private ProgramQuad	_quad_program;
	
	private int	_vaoID;
	private int	_vboID;
	
	public QuadRenderer()
	{

	}
	
	public void	start()
	{
		this._quad_program = new ProgramQuad();
		this.initVao();
	}

	/*				//glpos ;  uv
	 * o-----o		//(-1, 1) ; (0, 0)		(1, 1) ; (1, 0)
	 * |     |
	 * |     |
	 * o-----o		//(-1, -1) ; (0, 1)		(1, -1) ; (1 ; 1)
	 */
	private void initVao()
	{
		ByteBuffer	buffer;
		float	vertices[] = {
				-1, 1, 0, 0,
				-1, -1, 0, 1,
				1, -1, 1, 1,
				
				-1, 1, 0, 0,
				1, -1, 1, 1,
				1, 1, 1, 0
		};

		buffer = BufferUtils.createByteBuffer(vertices.length * 4);
		buffer.asFloatBuffer().put(vertices);
		
		this._vaoID = GL30.glGenVertexArrays();
		this._vboID = GL15.glGenBuffers();
		
		GL30.glBindVertexArray(this._vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 2 * 4 + 2 * 4, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 2 * 4 + 2 * 4, 2 * 4);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
	}

	public void	renderQuad()
	{
		Matrix4f	mat;
		
		mat = new Matrix4f();
		mat.translate(new Vector2f(0, 0));
		mat.rotate(0, new Vector3f(1, 0, 0));
		mat.rotate(0, new Vector3f(0, 1, 0));
		mat.rotate(0, new Vector3f(0, 0, 1));
		mat.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		
		this._quad_program.useStart();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, BlockTextures.getGLTextureMap(BlockTextures.RESOLUTION_16));

		this._quad_program.loadUniformMatrix(this._quad_program.getTransfMatrix(), mat);
				
		GL30.glBindVertexArray(this._vaoID);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

		GL30.glBindVertexArray(0);
		
		this._quad_program.useStop();
	}
	
	
}
