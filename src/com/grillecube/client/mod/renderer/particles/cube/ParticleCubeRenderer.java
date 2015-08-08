package com.grillecube.client.mod.renderer.particles.cube;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;
import com.grillecube.client.renderer.opengl.GLH;
import com.grillecube.client.renderer.opengl.geometry.GLGeometry;
import com.grillecube.client.renderer.opengl.object.VertexArray;
import com.grillecube.client.renderer.opengl.object.VertexBuffer;

/**
 * 	Implementation follows: http://web.engr.oregonstate.edu/~mjb/cs557/Handouts/compute.shader.1pp.pdf
 */
public class ParticleCubeRenderer extends ARenderer
{	
	public ParticleCubeRenderer(Game game)
	{
		super(game);
	}

	/** program */
	private ProgramCubeParticles _program;
	
	/** cube data */
	private VertexArray _vao;
	private VertexBuffer _vbo;

	private ArrayList<CubeParticle> _particles;
	
	@Override
	public void start()
	{
		this._program = new ProgramCubeParticles();
		this._particles = new ArrayList<CubeParticle>();
		this.initOpenGLBuffers();
		
		
		for (int i = 0 ; i < 512 ; i++)
		{
			this.spawnRandomParticle(0, 64, 0);
		}
	}

	/** spawn a random particle at given world location */
	private void spawnRandomParticle(float x, float y, float z)
	{
		CubeParticle particle = new CubeParticle();
		
		float rotx = this.getRNG().nextInt(2) == 0 ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		float roty = this.getRNG().nextInt(2) == 0 ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		float rotz = this.getRNG().nextInt(2) == 0 ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		
		float velx = this.getRNG().nextInt(2) == 0 ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		float vely = this.getRNG().nextInt(2) == 0 ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		float velz = this.getRNG().nextInt(2) == 0 ? -this.getRNG().nextFloat() : this.getRNG().nextFloat();
		
		float scalex = this.getRNG().nextInt(2) == 0 ? -this.getRNG().nextFloat() : this.getRNG().nextFloat() * 2;
		
		particle.setPosition(x, y, z);
		particle.setPositionVel(velx / 16, vely  / 16, velz / 16);
		particle.setRotationVel(rotx / 16, roty  / 16, rotz  / 16);
		particle.setScale(scalex, scalex, scalex);
		this._particles.add(particle);
	}

	/** prepare opengl stuff */
	private void initOpenGLBuffers()
	{
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();

		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, GLGeometry.generateCube(1), GL15.GL_STATIC_DRAW);

		this._vao.bind();
		this._vao.setAttribute(this._vbo, 0, 4, GL11.GL_FLOAT, false, 4 * 4, 0);
		this._vao.unbind();
	}

	@Override
	public void render()
	{
		this._program.useStart();

		this._vao.bind();
		this._vao.enableAttribute(0);

		this._program.loadGlobalUniforms(this.getCamera());
		
		int i = 0;
		while (i < this._particles.size())
		{
			CubeParticle particle = this._particles.get(i);
			particle.update();
			if (particle.isDead())
			{
				this._particles.remove(i);
				continue ;
			}
			if (this.getCamera().isInFrustum(particle.getPosition(), 0))
			{
				this._program.loadInstanceUniforms(particle);
				this._vao.draw(GL11.GL_QUADS, 0, 24);
			}
			++i;
		}
		
		this._vao.disableAttribute(0);
		this._vao.unbind();
	}

}
