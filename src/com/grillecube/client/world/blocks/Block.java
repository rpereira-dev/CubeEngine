package com.grillecube.client.world.blocks;

import com.grillecube.client.world.Faces;

public abstract class Block
{		
	/** block opengl textureID (see faces indices) */
	private int[] _textureID;
	
	/** args:
	 * 
	 * @param blockID	: block unique ID
	 */
	public Block(int textureID)
	{
		this._textureID = new int[6];
		
		this._textureID[Faces.LEFT] 	= textureID;
		this._textureID[Faces.RIGHT]	= textureID;
		this._textureID[Faces.TOP] 		= textureID;
		this._textureID[Faces.BOT] 		= textureID;
		this._textureID[Faces.FRONT]	= textureID;
		this._textureID[Faces.BACK]		= textureID;
	}
	
	/** @param faces		: special faces (BLOCK_FACE_FRONT, TEXTURE_ID....) */
	public Block(int textureID, int ... faces)
	{
		this(textureID);
		
		for (int i = 0 ; i < faces.length ; i += 2)
		{
			this._textureID[faces[i]] = faces[i + 1];
		}
	}

	public Block setFace(int faceID, int textureID)
	{
		if (faceID >= 0 && faceID < 6)
		{
			this._textureID[faceID] = textureID;
		}
		return (this);
	}
	
	public int	getTextureIDForFace(int face)
	{
		return (this._textureID[face]);
	}
	
	/** to string function */
	public String	toString()
	{
		return ("Block: " + this.getName());
	}
	
	/** get block name */
	public abstract String getName();

	public abstract boolean isVisible();

	public abstract boolean isOpaque();
}
