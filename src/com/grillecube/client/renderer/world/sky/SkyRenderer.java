package com.grillecube.client.renderer.world.sky;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.Game;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.geometry.GLGeometry;
import com.grillecube.client.opengl.geometry.Sphere;
import com.grillecube.client.opengl.object.VertexArray;
import com.grillecube.client.opengl.object.VertexBuffer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.particles.ParticleCube;
import com.grillecube.client.renderer.particles.ParticleRenderer;
import com.grillecube.client.world.World;

public class SkyRenderer extends Renderer
{
	private static final int SKYDOME_PRECISION = 3;
	private static final float SKYDOME_SIZE = 1.0f;
	
	public SkyRenderer(Game game)
	{
		super(game);
	}

	/** program */
	private ProgramSky _sky_program;
	
	/** vao for icosphere */
	private VertexArray _vao;

	@Override
	public void start()
	{
		this._sky_program = new ProgramSky();
		
		this._vao = GLH.glhGenVAO();
		VertexBuffer buffer = GLH.glhGenVBO();
		
		buffer.bufferData(GL15.GL_ARRAY_BUFFER, GLGeometry.generateSphere(SKYDOME_PRECISION, SKYDOME_SIZE), GL15.GL_STATIC_DRAW);
		this._vao.bind();
		this._vao.setAttribute(buffer, 0, 3, GL11.GL_FLOAT, false, 4 * 3, 0);
		this._vao.unbind();
	}

	@Override
	public void render()
	{
		World world = this.getWorld();
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		this._sky_program.useStart();
		this._sky_program.loadUniforms(world.getWeather(), this.getCamera());
		
		this._vao.bind();
		this._vao.enableAttribute(0);
		this._vao.draw(GL11.GL_TRIANGLES, 0, Sphere.getVertexCount(SKYDOME_PRECISION));
		this._vao.unbind();
		
		this._sky_program.useStop();
		
//		this.doSnow();
		this.ambientParticle();
		GL11.glEnable(GL11.GL_DEPTH_TEST);

	}

	/** testing particles system :D */
	private void doSnow()
	{
		ParticleRenderer renderer = this.getRenderer().getParticleRenderer();

		for (int i = 0 ; i < 64 ; i++)
		{
			ParticleCube cube = new ParticleCube();
			
			float x = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
			float y = this.getRNG().nextFloat();
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
	
	
	/** testing particles system :D */
	private void ambientParticle()
	{
		ParticleRenderer renderer = this.getRenderer().getParticleRenderer();

		ParticleCube cube = new ParticleCube();
		
		float x = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		float y = this.getRNG().nextFloat();
		float z = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		cube.setPosition(this.getCamera().getPosition().x + x * 16,
						this.getCamera().getPosition().y + y * 16,
						this.getCamera().getPosition().z + z * 16);
		float size = this.getRNG().nextFloat() * 0.1f;
		cube.setScale(size, size, size);
		cube.setHealth(120);
		cube.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		
		float velx = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		float vely = -this.getRNG().nextFloat();
		float velz = (this.getRNG().nextInt(2) == 0) ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		cube.setPositionVel(velx / 32.0f, vely / 32.0f, velz / 32.0f);
		renderer.spawnParticle(cube);
	}


}