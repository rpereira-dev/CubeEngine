package com.grillecube.common.world.block.instances;

import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.block.Block;

/** a class which represent an unique block instance in the world */
public abstract class BlockInstance {

	private final Terrain terrain;
	private final Block block;
	private final int index;

	/**
	 * this constructor should be call with given argument inside function:
	 * Block.createBlockInstance(terrain, x, y, z)
	 */
	public BlockInstance(Terrain terrain, Block block, int index) {
		this.terrain = terrain;
		this.block = block;
		this.index = index;
	}

	/** update function, called every ticks */
	public abstract void update();

	/** called when this instance is set */
	public abstract void onSet();

	/** called when this instance is unset */
	public abstract void onUnset();

	/** get the terrain where this block instance is */
	public Terrain getTerrain() {
		return (this.terrain);
	}

	/** return the block type of this instance */
	public Block getBlock() {
		return (this.block);
	}

	/** get the position of this block instance relative to the terrain */
	public final int getIndex() {
		return (this.index);
	}
}
