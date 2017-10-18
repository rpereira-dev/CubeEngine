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

package com.grillecube.client.resources;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.opengl.object.ImageUtils;
import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.resources.GenericManager;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.block.Block;

public class BlockRendererManager extends GenericManager<String> {

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

	/** singleton */
	private static BlockRendererManager BLOCK_TEXTURE_MANAGER_INSTANCE;

	/** the hashmap of blocks and texture links */
	private HashMap<Block, BlockRenderer> blockRenderers;

	/**
	 * every game's blocks, build on initialization from ('this._blocks_list')
	 */
	private GLTexture[] glTextureAtlas;

	public BlockRendererManager(ResourceManager manager) {
		super(manager);
		BLOCK_TEXTURE_MANAGER_INSTANCE = this;
	}

	/** singleton */
	public static BlockRendererManager instance() {
		return (BLOCK_TEXTURE_MANAGER_INSTANCE);
	}

	/** create a texture atlas with all given textures into the array list */
	private BufferedImage generateTextureAtlas() {

		BufferedImage atlas = new BufferedImage(BlockRendererManager.ATLAS_WIDTH, BlockRendererManager.ATLAS_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = atlas.getGraphics();
		int x = 0;
		int y = 0;
		for (String imagePath : super.getObjects()) {
			BufferedImage img = ImageUtils.readImage(imagePath);
			g.drawImage(img, x * BlockRendererManager.TEXTURE_WIDTH, y * BlockRendererManager.TEXTURE_HEIGHT, null);
			++x;
			if (x % BlockRendererManager.TEXTURE_PER_LINE == 0) {
				x = 0;
				++y;
			}
		}
		g.dispose();
		return (atlas);
	}

	/** return the opengl texture map for the given resolution */
	public GLTexture getTextureAtlas(int bitmapID) {
		return (this.glTextureAtlas[bitmapID]);
	}

	/** register a block texture , and return it textureID */
	public int registerBlockTexture(String filepath) {
		return (super.registerObject(filepath));
	}

	/**
	 * load the texture atlas and store every loaded blocks to a static array
	 */
	private void createBlocksTextureAtlases() {
		if (this.getBlockTextureCount() == 0) {
			Logger.get().log(Level.WARNING, "No block textures registered!");
		}

		BufferedImage atlas = this.generateTextureAtlas();

		if (atlas != null) {
			this.glTextureAtlas[BlockRendererManager.RESOLUTION_16x16] = GLH
					.glhGenTexture(this.resizeTextureAtlas(atlas, 1));
			this.glTextureAtlas[BlockRendererManager.RESOLUTION_8x8] = GLH
					.glhGenTexture(this.resizeTextureAtlas(atlas, 2));
			this.glTextureAtlas[BlockRendererManager.RESOLUTION_4x4] = GLH
					.glhGenTexture(this.resizeTextureAtlas(atlas, 4));
			this.glTextureAtlas[BlockRendererManager.RESOLUTION_2x2] = GLH
					.glhGenTexture(this.resizeTextureAtlas(atlas, 8));
			this.glTextureAtlas[BlockRendererManager.RESOLUTION_1x1] = GLH
					.glhGenTexture(this.resizeTextureAtlas(atlas, 16));
		}
	}

	private BufferedImage resizeTextureAtlas(BufferedImage atlas, float factor) {
		int width = (int) (BlockRendererManager.ATLAS_WIDTH / factor);
		int height = (int) (BlockRendererManager.ATLAS_HEIGHT / factor);
		Image img = atlas.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = resized.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		this.exportTextureAtlas(width + "x" + height, resized);
		return (resized);
	}

	private void exportTextureAtlas(String label, BufferedImage atlas) {
		String filepath = R.getResPath("textures/block_atlas/" + label + ".png");
		ImageUtils.exportPNGImage(filepath, atlas);
	}

	/** get block texture by id */
	public String getBlockTextureByID(int id) {
		return (super.getObjectByID(id));
	}

	/** get the number of block textures */
	public int getBlockTextureCount() {
		return (super.getObjectCount());
	}

	/** set the BlockRenderer for the given block */
	public BlockRenderer setBlockRenderer(Block block, BlockRenderer blockRenderer) {
		this.blockRenderers.put(block, blockRenderer);
		return (blockRenderer);
	}

	/** get the BlockRenderer for the given block */
	public BlockRenderer getBlockRenderer(Block block) {
		return (this.blockRenderers.get(block));
	}

	@Override
	public void onInitialized() {
		this.glTextureAtlas = new GLTexture[BlockRendererManager.RESOLUTION_MAX];
		this.blockRenderers = new HashMap<Block, BlockRenderer>();
	}

	@Override
	public void onDeinitialized() {
		this.destroyTextureAtlas();
	}

	@Override
	public void onLoaded() {
		this.createBlocksTextureAtlases();
	}

	@Override
	public void onUnloaded() {
		this.destroyTextureAtlas();
	}

	private final void destroyTextureAtlas() {
		for (GLTexture texture : this.glTextureAtlas) {
			if (texture != null) {
				texture.delete();
			}
		}
	}

	@Override
	protected void onObjectRegistered(String object) {
	}
}
