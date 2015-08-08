package com.grillecube.client.renderer.font;

import com.grillecube.client.ressources.TextureManager;

public class Font
{
	/** step between each char, default is 0 */
	private float _step;
	
	/** opengl textureID */
	private int	_glID;
	
	public Font(String filepath, float step)
	{
		this._glID = TextureManager.loadGLTexture(filepath);
		this._step = step;
	}

	public float getStep()
	{
		return (this._step);
	}

	public int getTextureID()
	{
		return (this._glID);
	}
}
