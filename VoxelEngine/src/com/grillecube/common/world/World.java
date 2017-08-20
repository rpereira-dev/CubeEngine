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
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector2i;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.instances.BlockInstance;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.EntityStorage;
import com.grillecube.common.world.generator.WorldGenerator;
import com.grillecube.common.world.generator.WorldGeneratorEmpty;
import com.grillecube.common.world.terrain.Terrain;
import com.grillecube.common.world.terrain.TerrainStorage;

public abstract class World implements Taskable {

	// public static final int seed = (int) System.currentTimeMillis();
	public static final int seed = 42;
	public static final SimplexNoiseOctave NOISE_OCTAVE = new SimplexNoiseOctave(seed);

	/** the world generator */
	private WorldGenerator generator;

	/** every loaded terrain are in */
	private TerrainStorage terrains;

	/** every world entities. */
	private EntityStorage entities;

	/** world weather */
	private Weather weather;

	private Random rng;

	/** number of updates which was made for the world */
	private long tick;

	public World() {
		this.terrains = new TerrainStorage(this);
		this.entities = new EntityStorage(this);
		this.weather = new Weather();
		this.rng = new Random();
		this.tick = 0;
		this.setWorldGenerator(new WorldGeneratorEmpty());
	}

	/** tasks to be run to update the world */
	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.common.VoxelEngine.Callable<Taskable>> tasks) {

		this.weather.getTasks(engine, tasks);
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

	public Random getRNG() {
		return (this.rng);
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
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
	public TerrainStorage getTerrainStorage() {
		return (this.terrains);
	}

	public EntityStorage getEntityStorage() {
		return (this.entities);
	}

	/** return world weather */
	public Weather getWeather() {
		return (this.weather);
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
		return (this.terrains.spawn(terrain));
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
	public ArrayList<Block> getCollidingBlocks(BoundingBox box) {
		ArrayList<Block> lst = new ArrayList<Block>();

		int minx = Maths.floor(box.getMin().x);
		int maxx = Maths.floor(box.getMax().x + 1.0D);
		int miny = Maths.floor(box.getMin().y);
		int maxy = Maths.floor(box.getMax().y + 1.0D);
		int minz = Maths.floor(box.getMin().z);
		int maxz = Maths.floor(box.getMax().z + 1.0D);

		Vector3i pos = new Vector3i();

		// iterate though each blocks
		for (pos.x = minx; pos.x < maxx; ++pos.x) {
			for (pos.z = minz; pos.z < maxz; ++pos.z) {
				for (pos.y = miny; pos.y < maxy; ++pos.y) {
					Block block = this.getBlock(pos.x, pos.y, pos.z);
					if (block.influenceCollisions()) {
						lst.add(block);
					}
				}
			}
		}
		return (lst);
	}

	/** return every blocks which collides with the given bounding box */
	public ArrayList<Entity> getCollidingEntities(BoundingBox box) {
		ArrayList<Entity> lst = new ArrayList<Entity>();
		Collection<Entity> entities = this.getEntityStorage().getEntities();
		for (Entity entity : entities) {
			if (entity.getBoundingBox().intersect(box)) {
				lst.add(entity);
			}
		}

		return (lst);
	}

	/**
	 * return every bounding box which collides with the given bounding box,
	 * excluding the given entity
	 */
	public Collection<BoundingBox> getCollidingBoundingBox(Entity entity, BoundingBox box) {
		return (this.getCollidingBoundingBox(entity, box, new ArrayList<BoundingBox>()));
	}

	/**
	 * @param entity
	 *            : the entity to exclude from the collision test
	 * @param box
	 *            : the box to test
	 * @param lst
	 *            : the list to push boxes
	 * @return
	 */
	public Collection<BoundingBox> getCollidingBoundingBox(Entity entity, BoundingBox box,
			Collection<BoundingBox> lst) {
		lst.clear();

		int minx = Maths.floor(box.getMin().x);
		int maxx = Maths.floor(box.getMax().x + 1.0D);
		int miny = Maths.floor(box.getMin().y);
		int maxy = Maths.floor(box.getMax().y + 1.0D);
		int minz = Maths.floor(box.getMin().z);
		int maxz = Maths.floor(box.getMax().z + 1.0D);

		// iterate though each block
		for (int x = minx; x < maxx; ++x) {
			for (int z = minz; z < maxz; ++z) {
				for (int y = miny; y < maxy; ++y) {
					Block block = this.getBlock(x, y, z);
					if (block.influenceCollisions()) {
						// generate the AABB for this block
						BoundingBox blockbox = new BoundingBox();
						blockbox.setMinSize(blockbox.getMin().set(x, y, z), Terrain.BLOCK_SIZE_VEC);
						if (box.intersect(blockbox)) {
							lst.add(blockbox);
						}
					}
				}
			}
		}

		// for (Entity e : this.getEntities()) {
		// if (!(e instanceof Entity) || e == entity) {
		// continue;
		// }
		//
		// BoundingBox entity_box = ((Entity)
		// e).getModelInstance().getMaxBoundingBox();
		// if (box.intersect(entity_box)) {
		// lst.add(entity_box);
		// }
		// }

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

	/** return the top loaded terrain for the given (x, z) coordinates */
	public final Terrain getTopTerrain(Vector2i index2) {
		return (this.terrains.getTop(index2));
	}
}
