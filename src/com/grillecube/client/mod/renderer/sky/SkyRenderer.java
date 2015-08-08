package com.grillecube.client.mod.renderer.sky;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;
import com.grillecube.client.renderer.opengl.GLH;
import com.grillecube.client.renderer.opengl.geometry.GLGeometry;
import com.grillecube.client.renderer.opengl.geometry.Sphere;
import com.grillecube.client.renderer.opengl.object.VertexArray;
import com.grillecube.client.renderer.opengl.object.VertexBuffer;

public class SkyRenderer extends ARenderer
{
	public SkyRenderer(Game game)
	{
		super(game);
	}

	/** program */
	private ProgramSky	_sky_program;
	
	/** vao / vbo for icosphere */
	private VertexArray _vao;
	private VertexBuffer _vbo;

	@Override
	public void start()
	{
		this._sky_program = new ProgramSky();
		
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();
		
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, GLGeometry.generateSphere(3), GL15.GL_STATIC_DRAW);
		this._vao.bind();
		this._vao.setAttribute(this._vbo, 0, 3, GL11.GL_FLOAT, false, 4 * 3, 0);
		this._vao.unbind();
	}

	@Override
	public void render()
	{
		this.getWeather().update();

		this._sky_program.useStart();
		this._sky_program.loadUniforms(this.getWeather(), this.getCamera());
		
		this._vao.bind();
		this._vao.enableAttribute(0);
		this._vao.draw(GL11.GL_TRIANGLES, 0, Sphere.getVertexCount(3));
		this._vao.unbind();
		
		this._sky_program.useStop();
	}

}