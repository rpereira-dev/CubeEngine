package com.grillecube.common.world.terrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector2i;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.World;
import com.grillecube.common.world.WorldStorage;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.instances.BlockInstance;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.events.EventWorldSpawnTerrain;

public class TerrainStorage extends WorldStorage {

	private HashMap<Vector3i, Terrain> terrains;
	private HashMap<Vector2i, Terrain> topTerrains;
	private ArrayList<Terrain> loadedTerrains;

	/**
	 * the maximum and minimum terrain y coordinates to make a terrain valid for
	 * this world
	 */
	private int maxY;
	private int minY;

	public TerrainStorage(World world) {
		super(world);
		this.terrains = new HashMap<Vector3i, Terrain>(4096 * 4);
		this.topTerrains = new HashMap<Vector2i, Terrain>(4096);
		this.loadedTerrains = new ArrayList<Terrain>(128);
		this.setMinHeightIndex(-4096);
		this.setMaxHeightIndex(4096);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
		final Terrain[] loaded = this.getLoaded();

		// for (Terrain terrain : loaded) {
		// terrain.update();
		// }

		tasks.add(engine.new Callable<Taskable>() {
			@Override
			public TerrainStorage call() throws Exception {
				for (int i = 0; i < loaded.length; i++) {
					Terrain terrain = loaded[i];
					terrain.update();
				}
				return (TerrainStorage.this);
			}

			@Override
			public String getName() {
				return ("TerrainStorage update");
			}
		});
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

		Vector2i index2 = terrain.getLocation().getWorldIndex().xz();
		Terrain top = this.topTerrains.get(index2);
		if (top == null || top.getWorldIndex().y < terrain.getWorldIndex().y) {
			this.topTerrains.put(index2, terrain);
		}
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
		return (y >= this.minY && y <= this.maxY);
	}

	public int getMaxHeight() {
		return (this.maxY * Terrain.DIM);
	}

	public int getMinHeight() {
		return (this.minY * Terrain.DIM);
	}

	public int getMaxHeightIndex() {
		return (this.maxY);
	}

	public int getMinHeightIndex() {
		return (this.minY);
	}

	/** the minimum height in term of block that this world can reach */
	public void setMinHeight(int height) {
		this.minY = (height + Maths.abs(height) % Terrain.DIM) / Terrain.DIM - 1;
	}

	/** the minimum height in term of block that this world can reach */
	public void setMinHeightIndex(int height) {
		this.minY = height;
	}

	/** the maxium height in term of block that this world can reach */
	public void setMaxHeight(int height) {
		this.maxY = (height + Maths.abs(height) % Terrain.DIM) / Terrain.DIM;
	}

	/** the minimum height in term of block that this world can reach */
	public void setMaxHeightIndex(int height) {
		this.maxY = height;
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

	/** get the block light at the given world relative position */
	public byte getBlockLight(float x, float y, float z) {
		Terrain terrain = this.get(x, y, z);
		if (terrain == null) {
			return (0);
		}
		int xx = this.getRelativeCoordinate((int) x);
		int yy = this.getRelativeCoordinate((int) y);
		int zz = this.getRelativeCoordinate((int) z);
		short index = terrain.getIndex(xx, yy, zz);
		return (terrain.getBlockLight(index));
	}

	/** world position */
	public byte getBlockLight(Vector3f pos) {
		return (this.getBlockLight(pos.x, pos.y, pos.z));
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

	/** return the topest terrain at the given (x, z) coordinates */
	public Terrain getTop(int x, int z) {
		// TODO : avoid Vector2i instanciation
		return (this.topTerrains.get(new Vector2i(x, z)));
	}

}
