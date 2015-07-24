package com.grillecube.client.renderer.model;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ModelPart
{
	/** opengl */
	private int	_vaoID;
	private int	_vboID;
	private int	_vertex_count;

	/** animations */
	private Animation[]	_animations;
	
	/** model part state */
	private static final int STATE_ININITIALIZED	= 1;
	private int	_state;

	public ModelPart()
	{
		this._state = 0;
		this._vaoID = 0;
		this._vboID = 0;
		this._vertex_count = 0;
		this._animations = null;
	}
	
	/** initalize opengl vao / vbo */
	private void prepareMesh()
	{
		this._vertex_count = 0;
		this._vaoID = GL30.glGenVertexArrays();
		this._vboID	= GL15.glGenBuffers();
		
		GL30.glBindVertexArray(this._vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4 + 4 * 4, 0);		//xyz
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 3 * 4 + 4 * 4, 3 * 4);	//rgba
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
	}
	
	/** send vertices to opengl */
	private void	setMeshData(float[] vertices)
	{
		FloatBuffer	buffer;

		buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
			
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
		{
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		this._vertex_count = vertices.length / 6;
	}
	
	/** set modelaprt vertices, should only be called once ! */
	public void	setVertices(float[] vertices)
	{
		if (this.hasState(STATE_ININITIALIZED))
		{
			return ;
		}
		this.prepareMesh();
		this.setMeshData(vertices);
		this.setState(STATE_ININITIALIZED);
	}
	
	/** set animations for this modelpart */
	public void	setAnimations(Animation[] animations)
	{
		this._animations = animations;
	}
	
	/** return animations for this modelpart */
	public Animation[]	getAnimations()
	{
		return (this._animations);
	}
	
	/** return the animation at index i */
	public Animation getAnimationAt(int i)
	{
		if (i < 0 || i >= this._animations.length)
		{
			return (null);
		}
		return (this._animations[i]);
	}
	
	/** return the animation with the given id */
	public Animation getAnimationByID(int id)
	{
		for (Animation animation : this._animations)
		{
			if (animation.getID() == id)
			{
				return (animation);
			}
		}
		return (null);
	}
	
	/** set a state */
	public void	setState(int state)
	{
		this._state = this._state | state;
	}
	
	/** unset a state */
	public void	unsetState(int state)
	{
		this._state = this._state & ~(state);
	}
	
	/** return true if the state is set */
	public boolean	hasState(int state)
	{
		return ((this._state & state) == state);
	}
}
