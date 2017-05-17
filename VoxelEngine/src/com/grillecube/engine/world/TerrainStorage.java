package com.grillecube.engine.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngine.Callable;
import com.grillecube.engine.event.world.EventWorldSpawnTerrain;
import com.grillecube.engine.maths.Maths;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.world.block.Block;
import com.grillecube.engine.world.block.Blocks;
import com.grillecube.engine.world.block.instances.BlockInstance;
import com.grillecube.engine.world.entity.Entity;

public class TerrainStorage extends WorldStorage {

	private HashMap<Vector3i, Terrain> terrains;
	private ArrayList<Terrain> loadedTerrains;

	/**
	 * the maximum and minimum terrain y coordinates to make a terrain valid for
	 * this world
	 */
	private int _max_y;
	private int _min_y;

	public TerrainStorage(World world) {
		super(world);
		this.terrains = new HashMap<Vector3i, Terrain>(4096);
		this.loadedTerrains = new ArrayList<Terrain>(128);
		this.setMinHeightIndex(-4);
		this.setMaxHeightIndex(4);
	}

	/** the number of entity to be updated per tasks */
	public static final int TERRAIN_PER_TASK = 8;

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
		Terrain[] loaded = this.getLoaded();
		for (Terrain terrain : loaded) {
			terrain.update();
		}
		// for (int i = 0; i < loaded.length; i += TERRAIN_PER_TASK) {
		//
		// final int begin = i;
		// final int end = Maths.min(i + TERRAIN_PER_TASK, loaded.length);
		//
		// tasks.add(engine.new Callable<Taskable>() {
		//
		// @Override
		// public TerrainStorage call() throws Exception {
		// for (int j = begin; j < end; j++) {
		// Terrain terrain = loaded[j];
		// terrain.update();
		// }
		// return (TerrainStorage.this);
		// }
		//
		// @Override
		// public String getName() {
		// return ("TerrainStorage update n°" + begin + " to n°" + end + " on a
		// total of " + loaded.length);
		// }
		// });
		// }
	}

	/**
	 * add the terrain to the world, return true it if added sucessfully, return
	 * false else way
	 */
	public Terrain spawn(Terrain terrain) {
		Terrain previous = this.get(terrain.getLocation().getWorldIndex());
		if (previous != null) {
			Logger.get().log(Level.WARNING, "Tried to spawn a terrain on an already existed terrain at: "
					+ terrain.getLocation().getWorldIndex());
			return (previous);
		}

		int height = terrain.getLocation().getWorldIndex().y;
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

		this.terrains.put(terrain.getLocation().getWorldIndex(), terrain);
		this.loadedTerrains.add(terrain);
		terrain.onSpawned(this.getWorld());
		this.invokeEvent(new EventWorldSpawnTerrain(this.getWorld(), terrain));
		return (terrain);
	}

	/** remove the terrain */
	public void despawn(Terrain terrain) {

		if (terrain == null) {
			return;
		}

		terrain.destroy();
		this.terrains.remove(terrain);
		this.loadedTerrains.remove(terrain);
	}

	/** remove the terrain */
	public void despawn(Vector3i index) {
		this.despawn(this.get(index));
	}

	/** get the terrain at the given world coordinates */
	public Terrain get(float x, float y, float z) {
		int ix = this.getIndex(x);
		int iy = this.getIndex(y);
		int iz = this.getIndex(z);
		return (this.get(ix, iy, iz));
	}

	/** world coordinates */
	public Terrain get(Vector3f pos) {
		return (this.get(pos.x, pos.y, pos.z));
	}

	/** get the terrain at the given index */
	public Terrain get(int indexx, int indexy, int indexz) {
		return (this.get(new Vector3i(indexx, indexy, indexz)));
	}

	/** return the terrain hashmap */
	public Terrain[] get() {
		Collection<Terrain> collection = this.terrains.values();
		Terrain[] terrains = new Terrain[collection.size()];
		return (collection.toArray(terrains));
	}

	/** get every loaded terrains */
	public Terrain[] getLoaded() {
		return (this.loadedTerrains.toArray(new Terrain[this.loadedTerrains.size()]));
	}

	/**
	 * return the terrain with the given location, or null if the terrain doesnt
	 * exists / is empty
	 */
	public Terrain get(Vector3i index) {
		return (this.terrains.get(index));
	}

	/** get the terrain location (x, y, z) for the given world location */
	public int getIndex(float coord) {
		if (coord < 0) {
			coord -= Terrain.DIM - 1;
		}
		return ((int) coord / Terrain.DIM);
	}

	public Vector3i getIndex(Vector3f pos, Vector3i dst) {
		return (dst.set(this.getIndex(pos.x), this.getIndex(pos.y), this.getIndex(pos.z)));
	}

	/** return positions relative to the terrain */
	public int getRelativeCoordinate(int index) {
		index = index % Terrain.DIM;
		if (index < 0) {
			index += Terrain.DIM;
		}
		return (index);
	}

	/** return true if the terrain height is valid */
	public boolean canHold(Terrain terrain) {
		int y = terrain.getLocation().getWorldIndex().y;
		return (y >= this._min_y && y <= this._max_y);
	}

	public int getMaxHeight() {
		return (this._max_y * Terrain.DIM);
	}

	public int getMinHeight() {
		return (this._min_y * Terrain.DIM);
	}

	public int getMaxHeightIndex() {
		return (this._max_y);
	}

	public int getMinHeightIndex() {
		return (this._min_y);
	}

	/** the minimum height in term of block that this world can reach */
	public void setMinHeight(int height) {
		this._min_y = (height + Maths.abs(height) % Terrain.DIM) / Terrain.DIM - 1;
	}

	/** the minimum height in term of block that this world can reach */
	public void setMinHeightIndex(int height) {
		this._min_y = height;
	}

	/** the maxium height in term of block that this world can reach */
	public void setMaxHeight(int height) {
		this._max_y = (height + Maths.abs(height) % Terrain.DIM) / Terrain.DIM;
	}

	/** the minimum height in term of block that this world can reach */
	public void setMaxHeightIndex(int height) {
		this._max_y = height;
	}

	@Override
	public void delete() {
		Terrain[] terrains = this.get();
		for (Terrain terrain : terrains) {
			this.despawn(terrain);
		}
	}

	public boolean hasTerrain(Terrain terrain) {
		return (this.terrains.containsValue(terrain));
	}

	/** get the block at the given world relative position */
	public Block getBlock(float x, float y, float z) {
		Terrain terrain = this.get(x, y, z);
		if (terrain == null) {
			return (Blocks.AIR);
		}
		int xx = this.getRelativeCoordinate((int) x);
		int yy = this.getRelativeCoordinate((int) y);
		int zz = this.getRelativeCoordinate((int) z);
		return (terrain.getBlockAt(xx, yy, zz));
	}

	/** world position */
	public Block getBlock(Vector3f pos) {
		return (this.getBlock(pos.x, pos.y, pos.z));
	}

	/**
	 * set a block, world coordinates, return the terrain on which the block was
	 * set
	 */
	public Terrain setBlock(Block block, float x, float y, float z) {

		Terrain terrain = this.get(x, y, z);

		if (terrain == null) {
			return (null);
		}

		int xx = this.getRelativeCoordinate((int) x);
		int yy = this.getRelativeCoordinate((int) y);
		int zz = this.getRelativeCoordinate((int) z);

		terrain.setBlock(block, xx, yy, zz);
		return (terrain);
	}

	public int getTerrainCount() {
		return (this.terrains.size());
	}

	/** world location */
	public BlockInstance getBlockInstance(float x, float y, float z) {
		Terrain terrain = this.get(x, y, z);
		if (terrain == null) {
			return (null);
		}
		int xx = this.getRelativeCoordinate((int) x);
		int yy = this.getRelativeCoordinate((int) y);
		int zz = this.getRelativeCoordinate((int) z);
		return (terrain.getBlockInstance(xx, yy, zz));
	}

	public int getGroundHeight(Entity entity, int height) {

		if (height < 0) { // abs
			height = -height;
		}

		int indexx = this.getIndex(entity.getPosition().x);
		int indexy = this.getIndex(entity.getPosition().y);
		int indexz = this.getIndex(entity.getPosition().z);
		Terrain terrain = this.get(indexx, indexy, indexz);
		while (terrain == null) {
			--indexy;
			height -= Terrain.DIM;
			terrain = this.get(indexx, indexy, indexz);
		}

		int xx = this.getRelativeCoordinate((int) entity.getPosition().x);
		int yy = this.getRelativeCoordinate((int) entity.getPosition().y);
		int zz = this.getRelativeCoordinate((int) entity.getPosition().z);
		int i = 0;
		for (i = 0; i < height; i++) {
			Block block = terrain.getBlock(xx, yy - i, zz);
			if (block != Blocks.AIR) {
				return ((int) (entity.getPosition().y - i));
			}
			--height;
		}
		return (indexy * Terrain.DIM);
	}

	/** return true if the given terrain is loaded */
	public boolean isLoaded(Terrain terrain) {
		return (this.loadedTerrains.contains(terrain));
	}

}
