package com.grillecube.engine.world.block.instances;

import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.block.Block;
import com.grillecube.engine.world.block.Blocks;

/** a class which represent an unique block instance in the world */
public abstract class BlockInstance {

	private final Terrain _terrain;
	private Block _block;
	private final int _x;
	private final int _y;
	private final int _z;

	/**
	 * this constructor should be call with given argument inside function:
	 * Block.createBlockInstance(terrain, x, y, z)
	 */
	public BlockInstance(Terrain terrain, Block block, int x, int y, int z) {
		this._terrain = terrain;
		this._block = block;
		this._x = x;
		this._y = y;
		this._z = z;
	}

	/** update function, called every ticks */
	public abstract void update();

	/** called when this instance is set */
	public abstract void onSet();

	/** called when this instance is unset */
	public abstract void onUnset();

	/** get the terrain where this block instance is */
	public Terrain getTerrain() {
		return (this._terrain);
	}

	/** get the x coordinate relative to the block instance terrain */
	public int getX() {
		return (this._x);
	}

	/** get the y coordinate relative to the block instance terrain */
	public int getY() {
		return (this._y);
	}

	/** get the z coordinate relative to the block instance terrain */
	public int getZ() {
		return (this._z);
	}

	/**
	 * return the block at the given neighbor location
	 * 
	 *	@param x : x offset
	 *	@param y : y offset
	 *	@param z : z offset
	 */
	protected Block getNeighbor(int x, int y, int z) {
		return (this.getTerrain().getBlock(this.getX() + x, this.getY() + y, this.getZ() + z));
	}
	
	protected Block getNeighbor(Vector3i offset) {
		return (this.getTerrain().getBlock(offset.x, offset.y, offset.z));
	}

	/** remove this block from the terrain (and the instance) */
	public void removeBlock() {
		this.getTerrain().setBlock(Blocks.AIR, this.getX(), this.getY(), this.getZ());
	}

	/** return the block type of this instance */
	public Block getBlock() {
		return (this._block);
	}
}
