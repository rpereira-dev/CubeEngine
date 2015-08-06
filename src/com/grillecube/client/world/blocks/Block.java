package com.grillecube.client.world.blocks;

import com.grillecube.client.world.Faces;

public class Block
{	
	/** block name */
	private String	_name;
	
	/** block opengl textureID (see faces indices) */
	private int[]	_textureID;
	
	/** args:
	 * 
	 * @param name		: block name
	 * @param blockID	: block unique ID
	 */
	public Block(String name, int textureID)
	{
		this._textureID = new int[6];
		this._name = name;
		
		this._textureID[Faces.LEFT] 	= textureID;
		this._textureID[Faces.RIGHT]	= textureID;
		this._textureID[Faces.TOP] 		= textureID;
		this._textureID[Faces.BOT] 		= textureID;
		this._textureID[Faces.FRONT]	= textureID;
		this._textureID[Faces.BACK]		= textureID;
	}
	
	/** @param faces		: special faces (BLOCK_FACE_FRONT, TEXTURE_ID....) */
	public Block(String name, int textureID, int ... faces)
	{
		this(name, textureID);
		
		for (int i = 0 ; i < faces.length ; i += 2)
		{
			this._textureID[faces[i]] = faces[i + 1];
		}
	}
	
	public Block(String string)
	{
		this(string, 0);
	}

	public Block setFace(int faceID, int textureID)
	{
		if (faceID >= 0 && faceID < 6)
		{
			this._textureID[faceID] = textureID;
		}
		return (this);
	}
	
	/** to string function */
	public String	toString()
	{
		return ("Block: " + this.getName());
	}
	
	/** get block name */
	public String	getName()
	{
		return (this._name);
	}

	public boolean isVisible()
	{
		return (true);
	}

	public boolean isOpaque()
	{
		return (true);
	}
	
	public int	getTextureIDForFace(int face)
	{
		return (this._textureID[face]);
	}
}
