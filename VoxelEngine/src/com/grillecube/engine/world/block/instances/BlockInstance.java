package com.grillecube.engine.world.block.instances;

import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.block.Block;
import com.grillecube.engine.world.block.Blocks;

/** a class which represent an unique block instance in the world */
public abstract class BlockInstance {

	private final Terrain terrain;
	private final Block block;
	private final short index;

	/**
	 * this constructor should be call with given argument inside function:
	 * Block.createBlockInstance(terrain, x, y, z)
	 */
	public BlockInstance(Terrain terrain, Block block, short index) {
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

	/**
	 * return the block at the given neighbor location
	 * 
	 * @param dx
	 *            : dx offset
	 * @param dy
	 *            : dy offset
	 * @param dz
	 *            : dz offset
	 */
	protected Block getNeighborBlock(int dx, int dy, int dz) {
		int z = this.getTerrain().getZFromIndex(this.getIndex());
		int y = this.getTerrain().getYFromIndex(this.getIndex(), z);
		int x = this.getTerrain().getXFromIndex(this.getIndex(), y, z);
		return (this.getTerrain().getBlock(x + dx, y + dy, z + dz));
	}

	protected BlockInstance getNeighborInstance(int dx, int dy, int dz) {
		int z = this.getTerrain().getZFromIndex(this.getIndex());
		int y = this.getTerrain().getYFromIndex(this.getIndex(), z);
		int x = this.getTerrain().getXFromIndex(this.getIndex(), y, z);
		return (this.getTerrain().getBlockInstance(x + dx, y + dy, z + dz));
	}

	/** remove this block from the terrain (and the instance) */
	public void removeBlock() {
		this.getTerrain().setBlock(Blocks.AIR, this.getIndex());
	}

	/** return the block type of this instance */
	public Block getBlock() {
		return (this.block);
	}

	/** get the position of this block instance relative to the terrain */
	public final short getIndex() {
		return (this.index);
	}

	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 * @return true if this instance should be removed, false else
	 */
	public boolean shouldBeRemoved() {
		return (false);
	}
}
