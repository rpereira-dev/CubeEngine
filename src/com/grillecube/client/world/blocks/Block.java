package com.grillecube.client.world.blocks;

public class Block
{
	/**	face indices */
	public static final int	FACE_LEFT 	= 0;
	public static final int	FACE_RIGHT 	= 1;
	public static final int	FACE_TOP 	= 2;
	public static final int FACE_BOT 	= 3;
	public static final int	FACE_FRONT	= 4;
	public static final int	FACE_BACK	= 5;
	
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
		
		this._textureID[FACE_LEFT] 	= textureID;
		this._textureID[FACE_RIGHT]	= textureID;
		this._textureID[FACE_TOP] 	= textureID;
		this._textureID[FACE_BOT] 	= textureID;
		this._textureID[FACE_FRONT]	= textureID;
		this._textureID[FACE_BACK]	= textureID;
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
