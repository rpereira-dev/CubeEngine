package com.grillecube.client.mod.renderer.particles.points;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;

/**
 * 	Implementation follows: http://web.engr.oregonstate.edu/~mjb/cs557/Handouts/compute.shader.1pp.pdf
 */
public class ParticlePointRenderer extends ARenderer
{	
	public ParticlePointRenderer(Game game)
	{
		super(game);
	}

	/** compute program */
	private ProgramComputeParticles	_compute_program;
	private int	_ssboPos;
	private int	_ssboVel;
	private int	_ssboColor;

	/** compute program constants */
	private static final int MAX_PARTICLES	= 8192;
	private static final int WORKER_SIZE	= 256;
	private static final int WORKER_X 		= MAX_PARTICLES / WORKER_SIZE;
	
	/** rendering program */
	private ProgramRenderParticles	_render_program;
	private int	_vaoID;
	
	@Override
	public void start()
	{
		this._compute_program = new ProgramComputeParticles();
		this._render_program = new ProgramRenderParticles();
		this.initComputeProgram();
		this.initRenderProgram();
	}

	private void initRenderProgram()
	{
		this._vaoID = GL30.glGenVertexArrays();

		GL30.glBindVertexArray(this._vaoID);
		{
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._ssboPos);
			GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 4 * 4, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._ssboColor);
			GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 4 * 4, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		GL30.glBindVertexArray(0);
	}

	private void initComputeProgram()
	{			
		this._ssboPos = GL15.glGenBuffers();
		this._ssboColor = GL15.glGenBuffers();
		this._ssboVel = GL15.glGenBuffers();

		this.setSSBOData(this._ssboPos, this.generateRandomFloatArray(MAX_PARTICLES * 4));
		this.setSSBOData(this._ssboColor, this.generateRandomFloatArray(MAX_PARTICLES * 4));
		this.setSSBOData(this._ssboVel, this.generateRandomFloatArray(MAX_PARTICLES * 4));
	}
	
	private float[]	generateRandomFloatArray(int size)
	{
		Random r = new Random();
		float[]	array = new float[size];
		
		for (int i = 0 ; i < size ; i++)
		{
			array[i] = r.nextFloat();
		}
		return (array);
	}
	
	private void setSSBOData(int ssboID, float[] data)
	{
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssboID);
		{
			FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
			buffer.put(data);
			buffer.flip();
			GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		}
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}

	//TODO
//	@Override
//	public void stop()
//	{
//		this._compute_program.delete();
//		GL15.glDeleteBuffers(this._ssboPos);
//		GL15.glDeleteBuffers(this._ssboVel);
//		GL15.glDeleteBuffers(this._ssboColor);
//		GL30.glDeleteVertexArrays(this._vaoID);
//	}

	@Override
	public void render()
	{
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, this._ssboPos);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, this._ssboVel);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, this._ssboColor);

		this._compute_program.useStart();
		this._compute_program.compute(WORKER_X, 1, 1);
		this._compute_program.useStop();
		
		this._render_program.useStart();
		this._render_program.loadUniforms(this.getCamera());
		
		GL30.glBindVertexArray(this._vaoID);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		   
		GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE);
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
				
		GL11.glDrawArrays(GL11.GL_POINTS, 0, MAX_PARTICLES);

		GL30.glBindVertexArray(0);

		this._render_program.useStop();
	}

}
