package com.grillecube.common.world.terrain;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.World;
import com.grillecube.common.world.WorldStorage;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.instances.BlockInstance;

public abstract class WorldTerrainStorage extends WorldStorage<WorldObjectTerrain> {

	public WorldTerrainStorage(World world) {
		super(world);
	}

	/** get the terrain index of the given world position */
	public abstract Vector3i getIndex(float x, float y, float z, Vector3i dst);

	public final Vector3i getIndex(Vector3f pos, Vector3i dst) {
		return (this.getIndex(pos.x, pos.y, pos.z, dst));
	}

	/**
	 * set the block durabiltiy at the given world relative position
	 * 
	 * @see {@link Terrain.setDurability(byte, int)}
	 */
	public abstract void setBlockDurability(byte durability, float x, float y, float z);

	/** world position */
	public final Block getBlock(Vector3f pos) {
		return (this.getBlock(pos.x, pos.y, pos.z));
	}

	public abstract Block getBlock(float x, float y, float z);

	/** get the block light at the given world relative position */
	public abstract byte getBlockLight(float x, float y, float z);

	/** world position */
	public abstract byte getBlockLight(Vector3f pos);

	/**
	 * set a block, world coordinates, return the terrain on which the block was set
	 */
	public abstract WorldObjectTerrain setBlock(Block block, float x, float y, float z);

	/** total number of terrains */
	public abstract int getTerrainCount();

	/** block instance */
	public abstract BlockInstance getBlockInstance(float x, float y, float z);

	/** true if the terrain is loaded */
	public abstract boolean isLoaded(WorldObjectTerrain terrain);

	/**
	 * return the terrain with the given location, or null if the terrain doesnt
	 * exists / is empty
	 */
	public abstract WorldObjectTerrain get(int x, int y, int z);

	public WorldObjectTerrain get(Vector3i pos) {
		return (this.get(pos.x, pos.y, pos.z));
	}

	/** get the loaded terrains */
	public abstract WorldObjectTerrain[] getLoaded();

	/** return true if the terrain can be spawned */
	public abstract boolean canHold(WorldObjectTerrain terrain);

	/** remove the terrain */
	public final void remove(Vector3i index) {
		this.remove(this.get(index));
	}

	@Override
	public void delete() {
		for (WorldObjectTerrain terrain : this) {
			this.remove(terrain);
		}
		this.onDeleted();
	}

	protected void onDeleted() {
	}
}
