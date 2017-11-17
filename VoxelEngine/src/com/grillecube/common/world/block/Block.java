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

package com.grillecube.common.world.block;

import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.block.instances.BlockInstance;

public abstract class Block {

	/** unique block id, set when the block is registered */
	private final short id;

	/**
	 * private final Vector3f _color;
	 * 
	 * /** args:
	 * 
	 * @param blockID
	 *            : block unique ID
	 */
	public Block(int blockID) {
		this.id = (short) blockID;
	}

	/** to string function */
	public String toString() {
		return ("Block: " + this.getName());
	}

	/** get block name */
	public abstract String getName();

	/** return true if this block is visible */
	public abstract boolean isVisible();

	/**
	 * return true if every pixel of this block are opaques (channel alpha at 0 or
	 * 1)
	 */
	public abstract boolean isOpaque();

	/**
	 * return true if some pixels of this block have an alpha channel different from
	 */
	public final boolean isTransparent() {
		return (!this.isOpaque() || this.hasTransparency());
	}

	/**
	 * return true if some pixels of this block have an alpha channel != 1
	 */
	public abstract boolean hasTransparency();

	/**
	 * update this block for this terrain at given coordinates (relative to the
	 * given terrain)
	 * 
	 * WARNING : this update every block of this type. If you want one special block
	 * to act a certain way, you should have a look at BlockInstance
	 * 
	 * @see createBlockInstance(Terrain terrain, short index)
	 */
	public abstract void update(Terrain terrain, int x, int y, int z);

	public final short getID() {
		return (this.id);
	}

	/**
	 * create a new BlockInstance for this block
	 *
	 * @param terrain
	 *            : the terrain where a block of this type as been set
	 * @param index
	 *            : index coordinate (relative to the terrain)
	 *
	 * @return a BlockInstance for this block, or null if the block should be
	 *         instanced (i.e, if the block is a static cube)
	 */
	public BlockInstance createBlockInstance(Terrain terrain, int index) {
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

	/** return true if one can pass though this block, ignoring collision */
	public boolean isCrossable() {
		return (false);
	}

	/** return true if this block bypass raycast */
	public boolean bypassRaycast() {
		return (false);
	}
}