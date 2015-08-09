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
import com.grillecube.client.renderer.particles.ParticleCube;
import com.grillecube.client.renderer.particles.ParticleRenderer;

public class SkyRenderer extends ARenderer
{
	public SkyRenderer(Game game)
	{
		super(game);
	}

	/** program */
	private ProgramSky	_sky_program;
	
	/** vao for icosphere */
	private VertexArray _vao;

	@Override
	public void start()
	{
		this._sky_program = new ProgramSky();
		
		this._vao = GLH.glhGenVAO();
		VertexBuffer buffer = GLH.glhGenVBO();
		
		buffer.bufferData(GL15.GL_ARRAY_BUFFER, GLGeometry.generateSphere(3), GL15.GL_STATIC_DRAW);
		this._vao.bind();
		this._vao.setAttribute(buffer, 0, 3, GL11.GL_FLOAT, false, 4 * 3, 0);
		this._vao.unbind();
	}

	@Override
	public void render()
	{
		this._sky_program.useStart();
		this._sky_program.loadUniforms(this.getWeather(), this.getCamera());
		
		this._vao.bind();
		this._vao.enableAttribute(0);
		this._vao.draw(GL11.GL_TRIANGLES, 0, Sphere.getVertexCount(3));
		this._vao.unbind();
		
		this._sky_program.useStop();
		
		this.doSnow();
	}

	/** testing particles system :D */
	private void doSnow()
	{
		ParticleRenderer renderer = this.getRenderer().getParticleRenderer();

		for (int i = 0 ; i < 64 ; i++)
		{
			ParticleCube cube = new ParticleCube();
			
			float x = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
			float y = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
			float z = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
			cube.setPosition(this.getCamera().getPosition().x + x * 16,
							this.getCamera().getPosition().y + y * 16,
							this.getCamera().getPosition().z + z * 16);
			float size = this.getRNG().nextFloat() * 0.1f;
			cube.setScale(size, size, size);
			cube.setHealth(120);
			cube.setColor(1, 1, 1, 0.5f);
			
			float velx = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
			float vely = -this.getRNG().nextFloat();
			float velz = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
			cube.setPositionVel(velx / 32.0f, vely / 4.0f, velz / 32.0f);
			renderer.spawnParticle(cube);
		}
	}

}