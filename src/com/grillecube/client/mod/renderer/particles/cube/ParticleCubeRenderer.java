package com.grillecube.client.mod.renderer.particles.cube;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;

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
	private int	_vaoID;
	private int	_vboID;

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
		
		particle.setPosition(x, y, z);
		particle.setPositionVel(velx / 16, vely  / 16, velz / 16);
		particle.setRotationVel(rotx / 16, roty  / 16, rotz  / 16);
		this._particles.add(particle);
	}

	/** prepare opengl stuff */
	private void initOpenGLBuffers()
	{
		
		this._vaoID = GL30.glGenVertexArrays();
		this._vboID = GL15.glGenBuffers();
		
		GL30.glBindVertexArray(this._vaoID);
		{
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
			GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 4 * 4, 0);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.generateCubeFloatBuffer(), GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		GL30.glBindVertexArray(0);
	}

	/** generate cube vertices */
	private FloatBuffer generateCubeFloatBuffer()
	{
		float[] vertices = {
			    
			    //left
				0, 1, 0, 0.84f,
			    0, 0, 0, 0.84f,
			    0, 0, 1, 0.84f,
			    0, 1, 1, 0.84f,
			    
			    //right
				1, 1, 0, 0.84f,
			    1, 0, 0, 0.84f,
			    1, 0, 1, 0.84f,
			    1, 1, 1, 0.84f,
			    
			    //top
				0, 1, 0, 1.0f,
			    1, 1, 0, 1.0f,
			    1, 1, 1, 1.0f,
			    0, 1, 1, 1.0f,
			    
			    //bot
				0, 0, 0, 1.0f,
			    1, 0, 0, 1.0f,
			    1, 0, 1, 1.0f,
			    0, 0, 1, 1.0f,
			    
				//front
				0, 1, 0, 0.7f,
			    0, 0, 0, 0.7f,
			    1, 0, 0, 0.7f,
			    1, 1, 0, 0.7f,
			    
			    //back
				0, 1, 1, 0.7f,
			    0, 0, 1, 0.7f,
			    1, 0, 1, 0.7f,
			    1, 1, 1, 0.7f
		};
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		return (buffer);
	}

	@Override
	public void stop()
	{
		this._program.stop();
		GL15.glDeleteBuffers(this._vboID);
		GL30.glDeleteVertexArrays(this._vaoID);
	}

	@Override
	public void render()
	{
		this._program.useStart();

		GL30.glBindVertexArray(this._vaoID);
		GL20.glEnableVertexAttribArray(0);
		
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
				GL11.glDrawArrays(GL11.GL_QUADS, 0, 24);
			}
			++i;
		}
		
		GL30.glBindVertexArray(0);

		this._program.useStop();
	}

}
