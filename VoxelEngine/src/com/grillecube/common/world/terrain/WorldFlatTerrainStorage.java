package com.grillecube.common.world.terrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;
import com.grillecube.common.event.world.EventTerrainDespawn;
import com.grillecube.common.event.world.EventTerrainSpawn;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector2i;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.block.instances.BlockInstance;

public class WorldFlatTerrainStorage extends WorldTerrainStorage {

	private final HashMap<Vector3i, WorldObjectTerrain> terrains;
	private final HashMap<Vector2i, WorldObjectTerrain> topTerrains;
	private final HashMap<Vector2i, WorldObjectTerrain> botTerrains;
	private final ArrayList<WorldObjectTerrain> loadedTerrains;

	/**
	 * the maximum and minimum terrain y coordinates to make a terrain valid for
	 * this world
	 */
	private int maxY;
	private int minY;

	public WorldFlatTerrainStorage(World world) {
		super(world);
		this.terrains = new HashMap<Vector3i, WorldObjectTerrain>(4096 * 4);
		this.topTerrains = new HashMap<Vector2i, WorldObjectTerrain>(4096);
		this.botTerrains = new HashMap<Vector2i, WorldObjectTerrain>(4096);
		this.loadedTerrains = new ArrayList<WorldObjectTerrain>(128);
		this.setMinHeightIndex(-32);
		this.setMaxHeightIndex(32);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
		this.updateLoadedTerrains();

		tasks.add(engine.new Callable<Taskable>() {
			@Override
			public WorldFlatTerrainStorage call() throws Exception {
				for (WorldObjectTerrain terrain : loadedTerrains) {
					terrain.update();
					if (terrain.getBlockCount() == 0) {
						remove(terrain);
					}
				}
				return (WorldFlatTerrainStorage.this);
			}

			@Override
			public String getName() {
				return ("TerrainStorage update");
			}
		});
	}

	private void updateLoadedTerrains() {
		this.loadedTerrains.clear();
		for (WorldObjectTerrain terrain : this.terrains.values()) {
			this.loadedTerrains.add(terrain);
		}
	}

	@Override
	public final WorldObjectTerrain add(WorldObjectTerrain terrain) {
		WorldObjectTerrain previous = this.get(terrain.getWorldIndex());
		if (previous != null) {
			Logger.get().log(Level.WARNING,
					"Tried to spawn a terrain on an already existed terrain at: " + terrain.getWorldIndex());
			return (previous);
		}

		int height = terrain.getWorldIndex().z;
		if (height > this.getMaxHeightIndex()) {
			Logger.get().log(Level.WARNING, "Tried to spawn a terrain above the current maximum height! " + height
					+ " / " + this.getMaxHeightIndex());
			return (null);
		}

		if (height < this.getMinHeightIndex()) {
			Logger.get().log(Level.WARNING, "Tried to spawn a terrain under the current minimum height! " + height
					+ " / " + this.getMinHeightIndex());
			return (null);
		}

		terrain.preSpawned();

		this.terrains.put(terrain.getWorldIndex(), terrain);
		this.loadedTerrains.add(terrain);

		Vector2i index2 = terrain.getWorldIndex();
		WorldObjectTerrain top = this.topTerrains.get(index2);
		if (top == null || top.getWorldIndex().z < terrain.getWorldIndex().z) {
			this.topTerrains.put(index2, terrain);
		}

		WorldObjectTerrain bot = this.botTerrains.get(index2);
		if (bot == null || bot.getWorldIndex().z > terrain.getWorldIndex().z) {
			this.botTerrains.put(index2, terrain);
		}

		terrain.onSpawned(this.getWorld());
		this.invokeEvent(new EventTerrainSpawn(terrain));
		terrain.postSpawned();

		return (terrain);
	}

	@Override
	public final WorldObjectTerrain remove(WorldObjectTerrain terrain) {
		if (terrain == null || this.terrains.containsKey(terrain.getWorldIndex())) {
			return (null);
		}

		terrain.destroy();
		this.invokeEvent(new EventTerrainDespawn(terrain));

		Vector3i index = terrain.getWorldIndex();
		this.terrains.remove(index);
		this.loadedTerrains.remove(terrain);

		WorldObjectTerrain topest = this.getTop(index);
		WorldObjectTerrain botest = this.getTop(index);
		if (topest == botest) {
			// this column is now empty
			this.topTerrains.remove(index);
			this.botTerrains.remove(index);
		} else if (topest == terrain) {
			this.topTerrains.remove(index);
			Vector3i index3 = new Vector3i(terrain.getWorldIndex());
			while (--index3.y >= this.getMinHeightIndex()) {
				topest = this.get(index3);
				if (topest != null) {
					this.topTerrains.put(index, topest);
					break;
				}
			}
		} else if (this.getBot(index) == terrain) {
			this.botTerrains.remove(index);
			Vector3i index3 = new Vector3i(terrain.getWorldIndex());
			while (++index3.y <= this.getMaxHeightIndex()) {
				botest = this.get(index3);
				if (botest != null) {
					this.botTerrains.put(index, botest);
					break;
				}
			}
		}
		return (terrain);
	}

	/** get the terrain at the given world coordinates */
	public WorldObjectTerrain get(float x, float y, float z) {
		int ix = this.getIndex(x, WorldObjectTerrain.DIMX);
		int iy = this.getIndex(y, WorldObjectTerrain.DIMY);
		int iz = this.getIndex(z, WorldObjectTerrain.DIMZ);
		return (this.get(ix, iy, iz));
	}

	/** get the terrain at the given index */
	@Override
	public WorldObjectTerrain get(int indexx, int indexy, int indexz) {
		return (this.get(new Vector3i(indexx, indexy, indexz)));
	}

	@Override
	public WorldObjectTerrain get(Vector3i index) {
		return (this.terrains.get(index));
	}

	/** return every terrains */
	@Override
	public final Collection<WorldObjectTerrain> get() {
		return (this.terrains.values());
	}

	/** get every loaded terrains */
	@Override
	public final WorldObjectTerrain[] getLoaded() {
		return (this.loadedTerrains.toArray(new WorldObjectTerrain[this.loadedTerrains.size()]));
	}

	/** get the terrain location (x, y, z) for the given world location */
	private final int getIndex(float coord, int dim) {
		if (coord < 0) {
			coord -= dim - 1;
		}
		return ((int) coord / dim);
	}

	/** return positions relative to the terrain */
	public int getRelativeCoordinate(int index, int dim) {
		index = index % dim;
		if (index < 0) {
			index += dim;
		}
		return (index);
	}

	@Override
	public Vector3i getIndex(float x, float y, float z, Vector3i dst) {
		if (dst == null) {
			dst = new Vector3i();
		}
		int ix = this.getIndex(x, WorldObjectTerrain.DIMX);
		int iy = this.getIndex(y, WorldObjectTerrain.DIMY);
		int iz = this.getIndex(z, WorldObjectTerrain.DIMZ);
		return (dst.set(ix, iy, iz));
	}

	@Override
	public final boolean canHold(WorldObjectTerrain terrain) {
		int y = terrain.getWorldIndex().y;
		return (y >= this.minY && y <= this.maxY);
	}

	public final int getMaxHeight() {
		return (this.maxY * WorldObjectTerrain.DIMY);
	}

	public final int getMinHeight() {
		return (this.minY * WorldObjectTerrain.DIMY);
	}

	public final int getMaxHeightIndex() {
		return (this.maxY);
	}

	public final int getMinHeightIndex() {
		return (this.minY);
	}

	/** the minimum height in term of block that this world can reach */
	public void setMinHeight(int height) {
		this.minY = (height + Maths.abs(height) % WorldObjectTerrain.DIMY) / WorldObjectTerrain.DIMY - 1;
	}

	/** the minimum height in term of block that this world can reach */
	public void setMinHeightIndex(int height) {
		this.minY = height;
	}

	/** the maxium height in term of block that this world can reach */
	public void setMaxHeight(int height) {
		this.maxY = (height + Maths.abs(height) % WorldObjectTerrain.DIMY) / WorldObjectTerrain.DIMY;
	}

	/** the minimum height in term of block that this world can reach */
	public void setMaxHeightIndex(int height) {
		this.maxY = height;
	}

	public boolean hasTerrain(WorldObjectTerrain terrain) {
		return (this.terrains.containsValue(terrain));
	}

	/** get the block at the given world relative position */
	@Override
	public Block getBlock(float x, float y, float z) {
		WorldObjectTerrain terrain = this.get(x, y, z);
		if (terrain == null) {
			return (Blocks.AIR);
		}
		int xx = this.getRelativeCoordinate((int) x, WorldObjectTerrain.DIMX);
		int yy = this.getRelativeCoordinate((int) y, WorldObjectTerrain.DIMY);
		int zz = this.getRelativeCoordinate((int) z, WorldObjectTerrain.DIMZ);
		return (terrain.getBlockAt(xx, yy, zz));
	}

	@Override
	public void setBlockDurability(byte durability, float x, float y, float z) {
		WorldObjectTerrain terrain = this.get(x, y, z);
		if (terrain == null) {
			return;
		}
		int xx = this.getRelativeCoordinate((int) x, WorldObjectTerrain.DIMX);
		int yy = this.getRelativeCoordinate((int) y, WorldObjectTerrain.DIMY);
		int zz = this.getRelativeCoordinate((int) z, WorldObjectTerrain.DIMZ);
		terrain.setDurability(xx, yy, zz, durability);
	}

	/** get the block light at the given world relative position */
	@Override
	public byte getBlockLight(float x, float y, float z) {
		WorldObjectTerrain terrain = this.get(x, y, z);
		if (terrain == null) {
			return (0);
		}
		int xx = this.getRelativeCoordinate((int) x, WorldObjectTerrain.DIMX);
		int yy = this.getRelativeCoordinate((int) y, WorldObjectTerrain.DIMY);
		int zz = this.getRelativeCoordinate((int) z, WorldObjectTerrain.DIMZ);
		int index = terrain.getIndex(xx, yy, zz);
		return (terrain.getBlockLight(index));
	}

	/** world position */
	@Override
	public byte getBlockLight(Vector3f pos) {
		return (this.getBlockLight(pos.x, pos.y, pos.z));
	}

	/**
	 * set a block, world coordinates, return the terrain on which the block was set
	 */
	@Override
	public WorldObjectTerrain setBlock(Block block, float x, float y, float z) {

		WorldObjectTerrain terrain = this.get(x, y, z);

		if (terrain == null) {
			return (null);
		}

		int xx = this.getRelativeCoordinate((int) x, WorldObjectTerrain.DIMX);
		int yy = this.getRelativeCoordinate((int) y, WorldObjectTerrain.DIMY);
		int zz = this.getRelativeCoordinate((int) z, WorldObjectTerrain.DIMZ);

		terrain.setBlock(block, xx, yy, zz);

		return (terrain);
	}

	@Override
	public int getTerrainCount() {
		return (this.terrains.size());
	}

	/** world location */
	@Override
	public BlockInstance getBlockInstance(float x, float y, float z) {
		WorldObjectTerrain terrain = this.get(x, y, z);
		if (terrain == null) {
			return (null);
		}
		int xx = this.getRelativeCoordinate((int) x, WorldObjectTerrain.DIMX);
		int yy = this.getRelativeCoordinate((int) y, WorldObjectTerrain.DIMY);
		int zz = this.getRelativeCoordinate((int) z, WorldObjectTerrain.DIMZ);
		return (terrain.getBlockInstance(xx, yy, zz));
	}

	/** return true if the given terrain is loaded */
	@Override
	public boolean isLoaded(WorldObjectTerrain terrain) {
		return (this.loadedTerrains.contains(terrain));
	}

	/** return the topest terrain at the given (x, z) coordinates */
	public WorldObjectTerrain getTop(Vector2i index2) {
		return (this.topTerrains.get(index2));
	}

	/** return the topest terrain at the given (x, z) coordinates */
	public WorldObjectTerrain getBot(Vector2i index2) {
		return (this.botTerrains.get(index2));
	}

	/**
	 * return the world maximum block height for the given (x, z) coordinates
	 * relative to the given terrain
	 */
	public final int getHeight(Vector2i index2, int x, int z) {
		WorldObjectTerrain terrain = this.getTopestTerrainWithNonEmptyColumn(index2, x, z);
		if (terrain == null) {
			return (this.getMinHeight() - 1);
		}
		int iy = terrain.getWorldIndex().y;
		int h = terrain.getHeightAt(x, z);
		return (iy * WorldObjectTerrain.DIMY + h);
	}

	/**
	 * get the topest terrain where the column (located in the (ix, iz) world
	 * terrain index, at coordinates (x, z) relatively to the terrain) is non empty
	 */
	public final WorldObjectTerrain getTopestTerrainWithNonEmptyColumn(Vector2i index2, int x, int z) {
		WorldObjectTerrain topest = this.getTop(index2);
		if (topest == null) {
			return (null);
		}
		WorldObjectTerrain botest = this.getBot(index2);
		Vector3i index3 = new Vector3i(topest.getWorldIndex());
		while (topest != botest) {
			if (topest != null && topest.getHeightAt(x, z) != 0) {
				return (topest);
			}
			--index3.y;
			topest = this.get(index3);
		}

		return (topest);
	}

	/**
	 * return the world maximum block height for the given (x, z) coordinates
	 * relative to the given terrain
	 */
	public int getHeight(float wx, float wz) {
		int ix = this.getIndex(wx, WorldObjectTerrain.DIMX);
		int iz = this.getIndex(wz, WorldObjectTerrain.DIMZ);
		int x = this.getRelativeCoordinate((int) wx, WorldObjectTerrain.DIMX);
		int z = this.getRelativeCoordinate((int) wz, WorldObjectTerrain.DIMZ);
		return (this.getHeight(new Vector2i(ix, iz), x, z));
	}

	@Override
	public int size() {
		return (this.terrains.size());
	}
}
