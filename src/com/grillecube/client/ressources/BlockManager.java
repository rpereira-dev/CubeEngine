package com.grillecube.client.ressources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.blocks.Block;
import com.grillecube.common.logger.Logger;

public class BlockManager
{
	//texture dimension
	public static final int TEXTURE_WIDTH = 16;
	public static final int TEXTURE_HEIGHT = 16;
	
	/** opengl texture id arrays */
	private static int	_gl_texture_atlas;

	/** every block textures */
	private ArrayList<Block>			_blocks_list;
	private ArrayList<BufferedImage>	_textures_list;
	
	/** every game's blocks */
	private static Block[]	_blocks;
	
	/** number of registered texture */
	private int _textures_count;
	private short _blocks_count;
	
	public BlockManager(ResourceManager manager)
	{
		this._textures_list = new ArrayList<BufferedImage>();
		this._blocks_list = new ArrayList<Block>();
		this._textures_count = 0;
		this._blocks_count = 0;
	}
	
	/** create a texture atlas with all given textures into the array list */
	private BufferedImage	generateTextureAtlas()
	{
		BufferedImage[]	images;
		BufferedImage	atlas;
		Graphics		g;
		int				y;
		
		if (this._textures_list.size() == 0)
		{
			return (null);
		}
		images = new BufferedImage[this._textures_list.size()];
		this._textures_list.toArray(images);
		atlas = new BufferedImage(BlockManager.TEXTURE_WIDTH,
									BlockManager.TEXTURE_HEIGHT * images.length,
									BufferedImage.TYPE_INT_ARGB);
		g = atlas.getGraphics();
		y = 0;
		for (BufferedImage img : images)
		{
			g.drawImage(img, 0, y, null);
			y += BlockManager.TEXTURE_HEIGHT;
		}
		
		g.dispose();
		try
		{
			ImageIO.write(atlas, "png", new File("./assets/textures/blocks/atlas.png"));
		}
		catch (IOException e)
		{
			Logger.get().log(Logger.Level.WARNING, "Cant save texture atlas: " + e.getMessage());
		}
		return (atlas);		
	}

	/** return the opengl texture map for the given resolution */
	public int getTextureAtlas()
	{
		return (_gl_texture_atlas);
	}
	
	/** register a block texture , and return it textureID */
	public int	registerBlockTexture(String filepath)
	{
		BufferedImage	img;
		
		img = TextureManager.readImage(filepath);
		if (img == null)
		{
			return (-1);
		}
		this._textures_list.add(img);
		this._textures_count++;
		return (this._textures_count - 1);
	}
	
	/**
	 * create block and add it to the blocks array.
	 * @return generated blockID
	 */
	public short registerBlock(Block block)
	{
		short	blockID;
		
		blockID = this._blocks_count;
		this._blocks_list.add(block);
		this._blocks_count++;
		Logger.get().log(Logger.Level.FINE, "Adding a block: " + block.toString());
		return (blockID);
	}
	
	/** load the texture atlas and store every loaded blocks to a static array */
	public void createBlocks()
	{
		BufferedImage	atlas;
		
		_gl_texture_atlas = TextureManager.newGLTexture();
		atlas = this.generateTextureAtlas();
		TextureManager.setGLTextureData(_gl_texture_atlas, atlas);
		
		//set data for the mesher
		TerrainMesh.UVX = 1;
		TerrainMesh.UVY = 1 / (float)this._textures_list.size();
		
		//no longer need bufferedimages
		this._textures_list.clear();
		this._textures_list = null;
		
		//create the static block array */
		_blocks = new Block[this._blocks_count];
		_blocks = this._blocks_list.toArray(_blocks);
		this._blocks_list.clear();
		this._blocks_list = null;
	}
	
	/** get block by id */
	public static Block getBlockByID(short id)
	{
		return (_blocks[id]);
	}
}
