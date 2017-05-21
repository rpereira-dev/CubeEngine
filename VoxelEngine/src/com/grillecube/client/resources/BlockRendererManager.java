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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.opengl.object.ImageUtils;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.faces.Face;
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
	private HashMap<Block, Integer[]> blockTextureLink;

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
		try {
			ImageIO.write(atlas, "png", new File(R.getResPath("textures/blocks/atlas_" + label + ".png")));
		} catch (IOException e) {
			Logger.get().log(Logger.Level.WARNING, "Cant save texture atlas: " + e.getMessage());
		}
	}

	/** get block texture by id */
	public String getBlockTextureByID(int id) {
		return (super.getObjectByID(id));
	}

	public int getBlockTextureCount() {
		return (super.getObjectCount());
	}

	@Override
	public void onInitialized() {
		this.glTextureAtlas = new GLTexture[BlockRendererManager.RESOLUTION_MAX];
		this.blockTextureLink = new HashMap<Block, Integer[]>();
	}

	@Override
	public void onStopped() {
	}

	@Override
	public void onLoaded() {
		this.createBlocksTextureAtlases();
	}

	@Override
	public void onCleaned() {
		for (GLTexture texture : this.glTextureAtlas) {
			if (texture != null) {
				texture.delete();
			}
		}
	}

	@Override
	protected void onObjectRegistered(String object) {
	}

	/**
	 * set the texture of the given block, for the given faces
	 * 
	 * @param block
	 *            : the block
	 * @param ids
	 *            : the face and texture ID's
	 * 
	 *            e.g: setBlockTextureFaces(Blocks.GRASS, Face.LEFT,
	 *            ClientBlocks.T_GRASS_SIDE, Face.RIGHT,
	 *            ClientBlocks.T_GRASS_SIDE, Face.FRONT,
	 *            ClientBlocks.T_GRASS_SIDE, Face.BACK,
	 *            ClientBlocks.T_GRASS_SIDE, Face.TOP, ClientBlocks.T_GRASS_TOP,
	 *            Face.BOT, ClientBlocks.T_DIRT);
	 */
	public void setBlockTextureFaces(Block block, int... ids) {

		if (ids.length == 0) {
			Logger.get().log(Logger.Level.DEBUG,
					"Called setBlockFaceTextures() but no texture where given... cancelling");
			return;
		}

		if (ids.length % 2 != 0) {
			Logger.get().log(Logger.Level.DEBUG,
					"Called setBlockFaceTextures() with an impair number of arguments: missing a Face or a Texture");
			return;
		}

		Integer[] textureIDs = this.getTextureIDs(block);
		if (textureIDs == null) {
			textureIDs = new Integer[Face.faces.length];
			this.blockTextureLink.put(block, textureIDs);
		}
		for (int i = 0; i < ids.length; i += 2) {
			int faceID = ids[i];
			int textureID = ids[i + 1];
			if (faceID >= 0 && faceID < Face.faces.length) {
				textureIDs[faceID] = textureID;
			}
		}
	}

	/** set the texture for all the face of the given block */
	public void setBlockTexture(Block block, int textureID) {
		Logger.get().log(Logger.Level.DEBUG, "in");

		Integer[] textureIDs = this.getTextureIDs(block);
		if (textureIDs == null) {
			textureIDs = new Integer[Face.faces.length];
			this.blockTextureLink.put(block, textureIDs);
		}

		for (Face face : Face.faces) {
			textureIDs[face.getID()] = textureID;
		}
	}

	/** return the array of texture for the given block */
	public Integer[] getTextureIDs(Block block) {
		return (this.blockTextureLink.get(block));
	}

	public int getTextureIDForFace(Block block, Face face) {
		Integer[] faces = this.blockTextureLink.get(block);
		if (faces == null) {
			return (0); // TODO return 0 or something else? default texture?
		}
		return (faces[face.getID()]);
	}
}
