package com.grillecube.engine.world.block.instances;

import com.grillecube.engine.maths.Maths;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.block.Block;
import com.grillecube.engine.world.block.BlockLiquid;
import com.grillecube.engine.world.block.Blocks;

/**
 * the block instance class of a liquid
 * 
 * basically, it contains data for a water voxel
 * 
 * if the voxel is full of water, then it is replace with a static water block,
 * and this instance is destroyed (this will allow to create huge water spaces
 * without memory issues)
 *
 * this instance should only exists if the block is a 'flowing' water voxel
 */
public class BlockInstanceLiquid extends BlockInstance {

	/**
	 * amount of water for this block instance. ( [MIN_LIQUID_AMOUNT,
	 * MAX_LIQUID_AMOUNT] )
	 */
	private int amount;
	private long lastUpdate;

	private Block blockUnder;

	/** minimum amount of water before dispersion */
	private static final int TICK_TO_UPDATE = 4;
	public static final int MAX_LIQUID_AMOUNT = 16;
	public static final int MIN_LIQUID_AMOUNT = 1;
	public static final float LIQUID_HEIGHT_UNIT = MIN_LIQUID_AMOUNT / (float) MAX_LIQUID_AMOUNT;
	private static final int DEFAULT_WATER_AMOUNT = MAX_LIQUID_AMOUNT;

	public BlockInstanceLiquid(Terrain terrain, Block block, short index) {
		super(terrain, block, index);
		this.setAmount(DEFAULT_WATER_AMOUNT);
	}

	/**
	 * set the amount of liquid for this instance.
	 * 
	 * @param amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * try to transfer water to the given block instance
	 * 
	 * @return the amount of liquid transfered
	 */
	public void transferLiquid(BlockInstanceLiquid dst, int amount) {

		// get the great amount we can transfer
		int to_transfer = Maths.min(amount, this.getAmount());

		// get the greatest amount the block under can hold
		int transfered = Maths.min(to_transfer, MAX_LIQUID_AMOUNT - dst.getAmount());
		dst.setAmount(dst.getAmount() + transfered);

		this.setAmount(this.getAmount() - transfered);

		if (transfered > 0) {
			super.getTerrain().requestMeshUpdate();
			dst.getTerrain().requestMeshUpdate();
		}
	}

	public int getAmount() {
		return (this.amount);
	}

	@Override
	public void update() {

		long tick = this.getTerrain().getWorld().getTick();
		if (tick - this.lastUpdate < TICK_TO_UPDATE) {
			return;
		}
		this.lastUpdate = tick;

		if (this.amount > MIN_LIQUID_AMOUNT) {
			this.flow();
		}
	}

	/** flow the water around */
	private void flow() {

		this.blockUnder = super.getTerrain().getBlockAt(super.getIndex());

		// if the block under is air
		if (this.blockUnder == Blocks.AIR) {
			this.fall();
		} else {
			// if there is a liquid under
			if (this.blockUnder instanceof BlockLiquid) {
				// transfer liquid to it
				this.flowUnder();
			}

			// if there is still water remaining in this block
			if (this.getAmount() > MIN_LIQUID_AMOUNT) {
				// flow it around
				this.flowAround();
			}
		}
	}

	/** the block try to give some amount of water to the block under */
	private void flowUnder() {
		BlockInstance under = super.getNeighborInstance(0, -1, 0);
		if (under != null && under instanceof BlockInstanceLiquid) {

			// get the liquid under
			BlockInstanceLiquid liquid = (BlockInstanceLiquid) under;

			// get the maximum amount to transfer
			this.transferLiquid(liquid, this.getAmount());
		}
	}

	/** make the water fall */
	private void fall() {
		super.removeBlock();

		BlockInstanceLiquid instance = (BlockInstanceLiquid) super.getNeighborInstance(0, -1, 0);
		if (instance != null) {
			instance.lastUpdate = this.lastUpdate;
			instance.setAmount(0);
			this.transferLiquid(instance, this.getAmount());
		}
	}

	private static final Vector3i[] NEIGHBOR = { new Vector3i(1, 0, 0), new Vector3i(-1, 0, 0), new Vector3i(0, 0, 1),
			new Vector3i(0, 0, -1), };

	/** flow the water in surrounding voxels */
	private void flowAround() {

		int z = this.getTerrain().getZFromIndex(this.getIndex());
		int y = this.getTerrain().getYFromIndex(this.getIndex(), z);
		int x = this.getTerrain().getXFromIndex(this.getIndex(), y, z);

		// iterate though the neighbor array
		for (int i = 0; i < 4; i++) {

			int bx = x + NEIGHBOR[i].x;
			int by = y + NEIGHBOR[i].y;
			int bz = z + NEIGHBOR[i].z;

			short index = super.getTerrain().getIndex(bx, by, bz);

			Block block = super.getTerrain().getBlockAt(index);

			// if the neighbor is air
			if (block == Blocks.AIR) {
				// then set it to liquid and transfer some liquid to it
				BlockInstance instance = super.getTerrain().setBlock(super.getBlock(), index, x, y, z);
				if (instance != null && instance instanceof BlockInstanceLiquid) {

					BlockInstanceLiquid liquid = ((BlockInstanceLiquid) instance);
					liquid.setAmount(0);
					liquid.lastUpdate = this.lastUpdate;

					// note we only transfer the min liquid amount here to
					// create the flowing effect
					this.transferLiquid(liquid, MIN_LIQUID_AMOUNT);
				}
			} else {
				// if the neighbor isnt air

				// get the block
				BlockInstance instance = super.getTerrain().getBlockInstance(bx, by, bz);
				// if it does have an instance and it is a liquid instance
				if (instance != null && instance instanceof BlockInstanceLiquid) {
					BlockInstanceLiquid liquid = (BlockInstanceLiquid) instance;
					// if it is the same liquid type and it has less liquid
					if (liquid.getBlock() == this.getBlock()
							&& liquid.getAmount() + MIN_LIQUID_AMOUNT < this.getAmount()) {
						// transfer liquid to it
						this.transferLiquid(liquid, MIN_LIQUID_AMOUNT);
					}
				}
			}
		}
	}

	@Override
	public void onSet() {
	}

	@Override
	public void onUnset() {
	}

	public Block getBlockUnder() {
		return (this.blockUnder);
	}

	@Override
	public boolean shouldBeRemoved() {
		return (this.amount <= MIN_LIQUID_AMOUNT);
	}
}
