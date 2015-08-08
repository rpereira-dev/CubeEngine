package com.grillecube.client.mod.renderer.particles.quad;

/** there should be only one instance per texture of this object */
public class ParticleSprite
{
	private int _cols;
	private int _lines;
	private int _glID;
	
	/** particle constructor which generate a gl texture name, so it should be call in a gl context!*/
	public ParticleSprite(String filepath, int cols, int lines)
	{
//		this._glID = 
	}
	
	/** return the number of cols for this sprite */
	public int getCols()
	{
		return (this._cols);
	}
	
	/** return number of lines for this sprite */
	public int getLines()
	{
		return (this._lines);
	}
	
	/** return opengl texture id */
	public int getGLID()
	{
		return (this._glID);
	}
}
