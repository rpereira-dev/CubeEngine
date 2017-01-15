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

package com.grillecube.engine.world.block;

import java.util.Stack;

import com.grillecube.engine.faces.Face;
import com.grillecube.engine.renderer.world.terrain.MeshVertex;
import com.grillecube.engine.renderer.world.terrain.TerrainMesher;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.block.instances.BlockInstance;

public abstract class Block {
	public static final byte MAX_LIGHT_VALUE = Byte.MAX_VALUE;
	public static final byte MIN_LIGHT_VALUE = 0;

	/** block opengl textureID (see faces indices) */
	private final int[] _textureID;

	/** unique block id, set when the block is registered */
	private final short _id;

	/** block density */
	private float _density;

	/**
	 * private final Vector3f _color;
	 * 
	 * /** args:
	 * 
	 * @param blockID
	 *            : block unique ID
	 */
	public Block(int blockID, int textureID) {
		this(blockID, Face.LEFT, textureID, Face.RIGHT, textureID, Face.TOP, textureID, Face.BOT, textureID, Face.FRONT,
				textureID, Face.BACK, textureID);
	}

	/**
	 * @param faces
	 *            : special faces (BLOCK_FACE_FRONT, TEXTURE_ID....)
	 */
	public Block(int blockID, int... faces) {
		this._id = (short) blockID;
		this._textureID = new int[6];
		for (int i = 0; i < faces.length; i += 2) {
			this._textureID[faces[i]] = faces[i + 1];
		}
		this.setDensity(1.0f);
	}

	public Block setDensity(float density) {
		this._density = density;
		return (this);
	}

	public Block setFace(int faceID, int textureID) {
		if (faceID >= 0 && faceID < 6) {
			this._textureID[faceID] = textureID;
		}
		return (this);
	}

	public int getTextureIDForFace(Face face) {
		return (this._textureID[face.getID()]);
	}

	/** to string function */
	public String toString() {
		return ("Block: " + this.getName());
	}

	/** get block name */
	public abstract String getName();

	/** return true if this block is visible */
	public abstract boolean isVisible();

	/** return true if this block is opaque */
	public abstract boolean isOpaque();

	/** return true if this block influence neighbors block ambiant occlusion */
	public abstract boolean influenceAO();

	/**
	 * update this block for this terrain at given coordinates (relative to the
	 * given terrain)
	 * 
	 * WARNING : this update every block of this type. If you want one special
	 * block to act a certain way, you should have a look at BlockInstance
	 * 
	 * @see createBlockInstance(Terrain terrain, int x, int y, int z)
	 */
	public abstract void update(Terrain terrain, int x, int y, int z);

	public final short getID() {
		return (this._id);
	}

	/**
	 * create a new BlockInstance for this block
	 *
	 * @param terrain
	 *            : the terrain where a block of this type as been set
	 * @param x
	 *            : x coordinate (relative to the terrain)
	 * @param y
	 *            : y coordinate (relative to the terrain)
	 * @param z
	 *            : z coordinate (relative to the terrain)
	 *
	 * @return a BlockInstance for this block, or null if the block should be
	 *         instanced (i.e, if the block is a static cube)
	 */
	public BlockInstance createBlockInstance(Terrain terrain, int x, int y, int z) {
		return (null);
	}

	/**
	 * called when this block is set
	 * 
	 * @param terrain
	 *            : terrain where this block is set
	 * @param x
	 *            : x coordinate (relative to the terrain)
	 * @param y
	 *            : y coordinate (relative to the terrain)
	 * @param z
	 *            : z coordinate (relative to the terrain)
	 */
	public abstract void onSet(Terrain terrain, int x, int y, int z);

	/** @see onSet */
	public abstract void onUnset(Terrain terrain, int x, int y, int z);

	/**
	 * a function which is called when meshing the terrain, if this block has
	 * 'hasSpecialRendering()' method overriden to return true
	 */
	public void pushVertices(TerrainMesher mesher, Terrain terrain, Stack<MeshVertex> stack, int x, int y, int z) {

	}

	public boolean influenceCollisions() {
		return (true);
	}

	/**
	 * this method should be overriden for every block having a special
	 * rendering (liquid, chair...)
	 */
	public boolean hasSpecialRendering() {
		return (false);
	}
}
