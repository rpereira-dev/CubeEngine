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

package com.grillecube.common.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.instances.BlockInstance;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.WorldEntityStorage;
import com.grillecube.common.world.entity.collision.AABB;
import com.grillecube.common.world.generator.WorldGenerator;
import com.grillecube.common.world.generator.WorldGeneratorEmpty;
import com.grillecube.common.world.terrain.Terrain;

/**
 * TODO Main world class, may change to a "Planet" class, and a new World class
 * should be build, and load multiple "Planets"
 */
public abstract class World implements Taskable {

	// public static final int seed = (int) System.currentTimeMillis();
	public static final int seed = 42;
	public static final SimplexNoiseOctave NOISE_OCTAVE = new SimplexNoiseOctave(seed);
	public static final SimplexNoiseOctave[] NOISE_OCTAVES = { new SimplexNoiseOctave(seed + 1),
			new SimplexNoiseOctave(seed + 2), new SimplexNoiseOctave(seed + 3), new SimplexNoiseOctave(seed + 4) };

	/** the world generator */
	private WorldGenerator generator;

	/** every loaded terrain are in */
	private final WorldTerrainStorage terrains;

	/** every world entities. */
	private final WorldEntityStorage entities;

	/** rng */
	private final Random rng;

	/** number of updates which was made for the world */
	private long tick;

	public World() {
		this.terrains = this.instanciateTerrainStorage();
		this.entities = new WorldEntityStorage(this);
		this.rng = new Random();
		this.tick = 0;
		this.setWorldGenerator(new WorldGeneratorEmpty());
	}

	/** create the terrain storage of this world (can be null) */
	protected abstract WorldTerrainStorage instanciateTerrainStorage();

	/** tasks to be run to update the world */
	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.common.VoxelEngine.Callable<Taskable>> tasks) {
		this.entities.getTasks(engine, tasks);
		this.terrains.getTasks(engine, tasks);
		this.onTasksGet(engine, tasks);
		this.tick();
	}

	/** call back to add tasks to run */
	protected void onTasksGet(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
	}

	/**
	 * generate the terrain for the given coordinates, spawn it if un-existant
	 */
	public Terrain generateTerrain(int x, int y, int z) {
		Terrain terrain = this.getTerrain(x, y, z);
		if (terrain == null) {
			terrain = new Terrain(x, y, z);
			this.spawnTerrain(terrain);
		}
		return (this.generateTerrain(terrain));
	}

	public Terrain generateTerrain(Terrain terrain) {
		terrain.preGenerated();
		this.generator.generate(terrain);
		terrain.postGenerated();
		return (terrain);
	}

	/** set the world generator */
	public void setWorldGenerator(WorldGenerator worldgen) {
		this.generator = worldgen;
	}

	/** get the rng */
	public final Random getRNG() {
		return (this.rng);
	}

	/** delete the world : de-allocate every allocated memory */
	public void delete() {
		this.entities.delete();
		this.terrains.delete();
	}

	/**
	 * return the terrain with the given location, or null if the terrain doesnt
	 * exists / is empty
	 */
	public WorldTerrainStorage getTerrainStorage() {
		return (this.terrains);
	}

	public WorldEntityStorage getEntityStorage() {
		return (this.entities);
	}

	/** get the block at the given world relative position */
	public Block getBlock(float x, float y, float z) {
		return (this.terrains.getBlock(x, y, z));
	}

	/** world position */
	public Block getBlock(Vector3f pos) {
		return (this.terrains.getBlock(pos.x, pos.y, pos.z));
	}

	/** set the block at the given world coordinates */
	public Terrain setBlock(Block block, float x, float y, float z) {
		return (this.terrains.setBlock(block, x, y, z));
	}

	public byte getBlockLight(Vector3f pos) {
		return (this.terrains.getBlockLight(pos.x, pos.y, pos.z));
	}

	/** return the terrain at the given index */
	public Terrain getTerrain(int x, int y, int z) {
		return (this.terrains.get(x, y, z));
	}

	/** return the terrain at the given index */
	public Terrain getTerrain(Vector3i pos) {
		return (this.terrains.get(pos));
	}

	/** return true if the given terrain is loaded */
	public boolean isTerrainLoaded(Terrain terrain) {
		return (this.terrains.isLoaded(terrain));
	}

	/** get every loaded terrains */
	public Terrain[] getLoadedTerrains() {
		return (this.terrains.getLoaded());
	}

	/** get the terrain index for the given world position */
	public Vector3i getTerrainIndex(Vector3f position) {
		return (this.getTerrainIndex(position, new Vector3i()));
	}

	public Vector3i getTerrainIndex(Vector3f position, Vector3i world_index) {
		return (this.terrains.getIndex(position, world_index));
	}

	/** return true if this world can hold this terrain */
	public boolean canHoldTerrain(Terrain terrain) {
		return (this.terrains.canHold(terrain));
	}

	/** spawn a terrain */
	public final Terrain spawnTerrain(Terrain terrain) {
		return (this.terrains.add(terrain));
	}

	/** spawn an entity into the world */
	public final Entity spawnEntity(Entity entity) {
		return (this.entities.spawn(entity));
	}

	/** tick the world once */
	public void tick() {
		this.tick++;

		// if (tick % 40 == 0) {
		// int count = 0;
		// long gain = 0;
		// Terrain[] t = this.terrains.get();
		// for (Terrain terrain : t) {
		// if (terrain.hasState(Terrain.STATE_BLOCK_COMPRESSED)) {
		// count++;
		// gain += terrain.gainBlock;
		// gain += terrain.gainLight;
		// }
		// }
		// Logger.get().log(Logger.Level.DEBUG, count, t.length, gain);
		// }
	}

	public long getTick() {
		return (this.tick);
	}

	@Override
	public String toString() {
		return ("World: " + this.getName());
	}

	/** return world name */
	public abstract String getName();

	/** return every blocks which collides with the given bounding box */
	public ArrayList<Block> getCollidingBlocks(AABB box) {
		ArrayList<Block> lst = new ArrayList<Block>();

		int minx = Maths.floor(box.getMinX());
		int maxx = Maths.floor(box.getMaxX() + 1.0D);
		int miny = Maths.floor(box.getMinY());
		int maxy = Maths.floor(box.getMaxY() + 1.0D);
		int minz = Maths.floor(box.getMinZ());
		int maxz = Maths.floor(box.getMaxZ() + 1.0D);

		Vector3i pos = new Vector3i();

		// iterate though each blocks
		for (pos.x = minx; pos.x < maxx; ++pos.x) {
			for (pos.z = minz; pos.z < maxz; ++pos.z) {
				for (pos.y = miny; pos.y < maxy; ++pos.y) {
					Block block = this.getBlock(pos.x, pos.y, pos.z);
					if (block.isWalkable()) {
						lst.add(block);
					}
				}
			}
		}
		return (lst);
	}

	/** return every blocks which collides with the given bounding box */
	public ArrayList<Entity> getCollidingEntities(AABB box) {
		ArrayList<Entity> lst = new ArrayList<Entity>();
		Collection<Entity> entities = this.getEntityStorage().getEntities();
		for (Entity entity : entities) {
			if (entity.getBoundingBox().intersect(box)) {
				lst.add(entity);
			}
		}

		return (lst);
	}

	/** world location */
	public BlockInstance getBlockInstance(float x, float y, float z) {
		return (this.terrains.getBlockInstance(x, y, z));
	}
	// TODO: save and load

	/**
	 * save the given world to the given folder
	 */

	public static final void save(World world, String filepath) {
	}

	/** load the given folder as a world */
	public static final World load(String filepath) {
		return (null);
	}

	public final void load() {
		this.onLoaded();
	}

	/** called when this world is loaded */
	protected void onLoaded() {

	}

	public final void unload() {
		this.onUnloaded();
	}

	private void onUnloaded() {
	}
}
