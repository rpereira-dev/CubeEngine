package com.grillecube.client.renderer.sky;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.IRenderer;
import com.grillecube.client.world.WorldClient;

public class SkyRenderer implements IRenderer
{
	/** program */
	private ProgramSky	_sky_program;
	
	/** vao / vbo for icosphere */
	private int	_vaoID;
	private int	_vboID;
	
	public void start()
	{
		this._sky_program = new ProgramSky();
		
		float[]	vertices = SkyDome.make_sphere();
		FloatBuffer	buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		
		this._vaoID = GL30.glGenVertexArrays();
		this._vboID	= GL15.glGenBuffers();
		
		GL30.glBindVertexArray(this._vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
		
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 4 * 3, 0);
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
		
	}

	public void stop()
	{
		this._sky_program.stop();
	}

	public void render(WorldClient world, Camera camera)
	{
		this._sky_program.useStart();
		this._sky_program.loadUniforms(world.getWeather(), camera);
		GL30.glBindVertexArray(this._vaoID);
		
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, SkyDome.VERTICES_COUNT);

		GL30.glBindVertexArray(0);
		
		this._sky_program.useStop();
	}

}
