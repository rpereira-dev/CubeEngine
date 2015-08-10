package com.grillecube.client.renderer.font;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.Texture;

public class Font
{
	/** step between each char, default is 0 */
	private float _step;
	
	/** opengl textureID */
	private Texture _texture;
	
	public Font(String filepath, float step)
	{
		this._texture = GLH.glhGenTexture(filepath);
		this._step = step;
	}

	public float getStep()
	{
		return (this._step);
	}

	public Texture getTexture()
	{
		return (this._texture);
	}
}
