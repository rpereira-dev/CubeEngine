/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.particles;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;

/** there should be only one instance per texture of this object */
public class TextureSprite
{
	private GLTexture _texture;

	private int _cols;
	private int _lines;
	private int _maxID;
	
	/** particle constructor which generate a gl texture name, so it should be call in a gl context!*/
	public TextureSprite(String filepath, int cols, int lines)
	{
		this._texture = GLH.glhGenTexture(filepath);
		this._cols = cols;
		this._lines = lines;
		this._maxID = cols * lines;
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
	public GLTexture getTexture()
	{
		return (this._texture);
	}

	public int getMaxID()
	{
		return (this._maxID);
	}
}
