package com.grillecube.client.renderer.particles;

import java.util.ArrayList;
import java.util.Comparator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;
import com.grillecube.client.renderer.opengl.GLH;
import com.grillecube.client.renderer.opengl.geometry.GLGeometry;
import com.grillecube.client.renderer.opengl.object.VertexArray;
import com.grillecube.client.renderer.opengl.object.VertexBuffer;

/** a simple cube rendering system for particles */
public class ParticleRenderer extends ARenderer
{
	/** program */
	private ProgramParticleQuad _program_quad;
	private ProgramParticleCube _program_cube;
	
	//one array list is an array list of particles (one list for each sprite)
	private ArrayList<ParticleQuad> _quad_particles;
	private ArrayList<ParticleCube> _cube_particles;
	
	/** cube and quads vaos */
	private VertexArray _vao_quad;	
	private VertexArray _vao_cube;
	
	private Comparator<Particle> _particle_comparator = new Comparator<Particle>()
	{
		@Override
		public int compare(Particle a, Particle b)
		{
			if (a.getCameraSquareDistance() < b.getCameraSquareDistance())
			{
				return (1);
			}
			else if (a.getCameraSquareDistance() > b.getCameraSquareDistance())
			{
				return (-1);
			}
			return (0);
		}

	};

	public ParticleRenderer(Game game)
	{
		super(game);
	}

	@Override
	public void start()
	{
		this._program_quad = new ProgramParticleQuad();
		this._program_cube = new ProgramParticleCube();
		this._quad_particles = new ArrayList<ParticleQuad>();
		this._cube_particles = new ArrayList<ParticleCube>();
		
		this.prepareQuad();
		this.prepareCube();
	}
	
	/** add a particule to the update functions */
	public void spawnParticle(ParticleQuad particle)
	{
		this._quad_particles.add(particle);
	}
	
	/** add a particule to the update functions */
	public void spawnParticle(ParticleCube particle)
	{
		this._cube_particles.add(particle);
	}

	/** create a new cube object */
	private void prepareCube()
	{
		this._vao_cube = GLH.glhGenVAO();
		VertexBuffer vbo = GLH.glhGenVBO();

		vbo.bufferData(GL15.GL_ARRAY_BUFFER, GLGeometry.generateCube(1), GL15.GL_STATIC_DRAW);

		this._vao_cube.bind();
		this._vao_cube.setAttribute(vbo, 0, 4, GL11.GL_FLOAT, false, 4 * 4, 0);
		this._vao_cube.unbind();		
	}
	
	/** create a new quad object */
	private void prepareQuad()
	{
		this._vao_quad = GLH.glhGenVAO();
		VertexBuffer vbo = GLH.glhGenVBO();

		vbo.bufferData(GL15.GL_ARRAY_BUFFER, GLGeometry.generateQuadTrianglesUV(1), GL15.GL_STATIC_DRAW);

		this._vao_quad.bind();
		this._vao_quad.setAttribute(vbo, 0, 3, GL11.GL_FLOAT, false, 4 * (3 + 2), 0); //position
		this._vao_quad.setAttribute(vbo, 1, 2, GL11.GL_FLOAT, false, 4 * (3 + 2), 4 * 3); //uv
		this._vao_quad.unbind();		
	}

	@Override
	public void render()
	{
		this.renderCubeParticles();
		this.renderQuadParticles();
	}

	/** render every quad particles */
	private void renderQuadParticles()
	{
		if (this._quad_particles.size() == 0)
		{
			return ;
		}
		
		this._program_quad.useStart();

		this._vao_quad.bind();
		this._vao_quad.enableAttribute(0);
		this._vao_quad.enableAttribute(1);

		this._program_quad.loadGlobalUniforms(this.getCamera());
		
		this._quad_particles.sort(this._particle_comparator);

		float max_dist = this.getCamera().getRenderDistanceSquared() / 2;
		
		long t = System.currentTimeMillis();
		int i = 0;
		while (i < this._quad_particles.size())
		{
			ParticleQuad particle = this._quad_particles.get(i);
			particle.update(this.getCamera());
			if (particle.isDead())
			{
				this._quad_particles.remove(i);
				continue ;
			}

			if (particle.getCameraSquareDistance() < max_dist && this.getCamera().isInFrustum(particle.getPosition(), 10))
			{
				this._program_quad.loadSpriteUniform(particle.getSprite());
				this._program_quad.loadInstanceUniforms(particle);
				this._vao_quad.draw(GL11.GL_TRIANGLES, 0, 6);
			}
			++i;
		}
		System.out.println("took : " + (System.currentTimeMillis() - t));
		this._vao_cube.disableAttribute(0);
		this._vao_cube.disableAttribute(1);
		this._vao_cube.unbind();		
	
	}

	/** render every cube particles */
	private void renderCubeParticles()
	{
		if (this._cube_particles.size() == 0)
		{
			return ;
		}
		
		this._program_cube.useStart();

		this._vao_cube.bind();
		this._vao_cube.enableAttribute(0);

		this._program_cube.loadGlobalUniforms(this.getCamera());
		
		int i = 0;
		while (i < this._cube_particles.size())
		{
			ParticleCube particle = this._cube_particles.get(i);
			particle.update(this.getCamera());
			if (particle.isDead())
			{
				this._cube_particles.remove(i);
				continue ;
			}
			if (this.getCamera().isInFrustum(particle.getPosition(), 0))
			{
				this._program_cube.loadInstanceUniforms(particle);
				this._vao_cube.draw(GL11.GL_QUADS, 0, 24);
			}
			++i;
		}
		
		this._vao_cube.disableAttribute(0);
		this._vao_cube.unbind();		
	}
	
}
