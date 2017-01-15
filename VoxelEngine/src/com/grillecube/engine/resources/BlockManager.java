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

package com.grillecube.engine.resources;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngine.Side;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLTexture;
import com.grillecube.engine.opengl.object.ImageUtils;
import com.grillecube.engine.world.block.Block;

public class BlockManager extends GenericManager<Block> {
	/** every blocks and it textures, only used on initialization */

	private ArrayList<BufferedImage> _textures_list;
	private int _block_texture_count;

	/** singleton */
	private static BlockManager _instance;

	public BlockManager(ResourceManager manager) {
		super(manager);
		_instance = this;
	}

	/** singleton */
	public static BlockManager instance() {
		return (_instance);
	}

	/**
	 * every game's blocks, build on initialization from ('this._blocks_list')
	 */
	private GLTexture[] _gl_texture_atlas;

	// texture dimension
	public static final int TEXTURE_WIDTH = 16;
	public static final int TEXTURE_HEIGHT = 16;
	public static final int TEXTURE_PER_LINE = 16;
	public static final int TEXTURE_PER_COL = 16;
	public static final int ATLAS_WIDTH = TEXTURE_WIDTH * TEXTURE_PER_LINE;
	public static final int ATLAS_HEIGHT = TEXTURE_HEIGHT * TEXTURE_PER_COL;

	/** opengl texture id arrays */
	public static final int RESOLUTION_16x16 = 0;
	public static final int RESOLUTION_8x8 = 1;
	public static final int RESOLUTION_4x4 = 2;
	public static final int RESOLUTION_2x2 = 3;
	public static final int RESOLUTION_1x1 = 4;
	private static final int RESOLUTION_MAX = 5;

	/** create a texture atlas with all given textures into the array list */
	private BufferedImage generateTextureAtlas(BufferedImage[] textures) {
		this._textures_list.toArray(textures);
		if (textures.length == 0) {
			return (null);
		}
		BufferedImage atlas = new BufferedImage(BlockManager.ATLAS_WIDTH, BlockManager.ATLAS_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = atlas.getGraphics();
		int x = 0;
		int y = 0;
		for (BufferedImage img : textures) {
			g.drawImage(img, x * BlockManager.TEXTURE_WIDTH, y * BlockManager.TEXTURE_HEIGHT, null);
			++x;
			if (x % BlockManager.TEXTURE_PER_LINE == 0) {
				x = 0;
				++y;
			}
		}
		g.dispose();
		return (atlas);
	}

	/** return the opengl texture map for the given resolution */
	public GLTexture getTextureAtlas(int bitmapID) {
		return (this._gl_texture_atlas[bitmapID]);
	}

	/** register a block texture , and return it textureID */
	public int registerBlockTexture(String filepath) {
		if (VoxelEngine.instance().getSide() == VoxelEngine.Side.CLIENT) {
			BufferedImage img = ImageUtils.readImage(filepath);
			int textureID = this._textures_list.size();
			if (img == null) {
				return (-1);
			}
			this._textures_list.add(img);
			return (textureID);
		}
		return (-1);
	}

	/**
	 * register the given block to the engine
	 */
	public Block registerBlock(Block block) {
		Logger.get().log(Logger.Level.FINE, "Registering a block: " + block.toString());
		super.registerObject(block);
		return (block);
	}

	/**
	 * load the texture atlas and store every loaded blocks to a static array
	 */
	private void createBlocksTextureAtlases() {
		if (this._textures_list.size() == 0) {
			Logger.get().log(Level.WARNING, "No block textures registered!");
		}

		if (super.getObjectCount() == 0) {
			Logger.get().log(Level.WARNING, "No blocks registered!");
		}

		BufferedImage[] textures = new BufferedImage[this._textures_list.size()];
		BufferedImage atlas = this.generateTextureAtlas(textures);

		if (atlas != null) {
			this._gl_texture_atlas[BlockManager.RESOLUTION_16x16] = GLH
					.glhGenTexture(this.resizeTextureAtlas(atlas, 1));
			this._gl_texture_atlas[BlockManager.RESOLUTION_8x8] = GLH.glhGenTexture(this.resizeTextureAtlas(atlas, 2));
			this._gl_texture_atlas[BlockManager.RESOLUTION_4x4] = GLH.glhGenTexture(this.resizeTextureAtlas(atlas, 4));
			this._gl_texture_atlas[BlockManager.RESOLUTION_2x2] = GLH.glhGenTexture(this.resizeTextureAtlas(atlas, 8));
			this._gl_texture_atlas[BlockManager.RESOLUTION_1x1] = GLH.glhGenTexture(this.resizeTextureAtlas(atlas, 16));
		}

		// no longer need bufferedimages
		this._block_texture_count = this._textures_list.size();
		this._textures_list.clear();
	}

	private BufferedImage resizeTextureAtlas(BufferedImage atlas, float factor) {
		int width = (int) (BlockManager.ATLAS_WIDTH / factor);
		int height = (int) (BlockManager.ATLAS_HEIGHT / factor);
		Image img = atlas.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = resized.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		this.exportTextureAtlas(width + "x" + height, resized);
		return (resized);
	}

	private void exportTextureAtlas(String label, BufferedImage atlas) {
		try {
			ImageIO.write(atlas, "png", new File(R.getResPath("textures/blocks/atlas_" + label + ".png")));
		} catch (IOException e) {
			Logger.get().log(Logger.Level.WARNING, "Cant save texture atlas: " + e.getMessage());
		}
	}

	/** get block by id */
	public Block getBlockByID(int id) {
		return (super.getObjectByID(id));
	}

	public int getBlockCount() {
		return (super.getObjectCount());
	}

	public int getBlockTextureCount() {
		return (this._block_texture_count);
	}

	@Override
	public void onInitialized() {
		// client side, then block need textures
		if (VoxelEngine.instance().getSide() == Side.CLIENT) {
			this._textures_list = new ArrayList<BufferedImage>();
			this._gl_texture_atlas = new GLTexture[BlockManager.RESOLUTION_MAX];
		}
	}

	@Override
	public void onStopped() {
	}

	@Override
	public void onLoaded() {
		// client side, we create the texture atlas for terrain rendering
		if (VoxelEngine.instance().getSide() == VoxelEngine.Side.CLIENT) {
			this.createBlocksTextureAtlases();
		}
	}

	@Override
	public void onCleaned() {
		if (VoxelEngine.instance().getSide() == VoxelEngine.Side.CLIENT) {
			for (GLTexture texture : this._gl_texture_atlas) {
				if (texture != null) {
					texture.delete();
				}
			}
		}
	}

	@Override
	protected void onObjectRegistered(Block object) {
	}
}
