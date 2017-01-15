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

package com.grillecube.engine.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.maths.Maths;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.renderer.model.BoundingBox;
import com.grillecube.engine.world.block.Block;
import com.grillecube.engine.world.block.instances.BlockInstance;
import com.grillecube.engine.world.entity.Entity;
import com.grillecube.engine.world.entity.EntityModeled;

public abstract class World implements Taskable {

	// public static final int seed = (int) System.currentTimeMillis();
	public static final int seed = 42;
	public static final SimplexNoiseOctave NOISE_OCTAVE = new SimplexNoiseOctave(seed);

	/** the world generator */
	private WorldGenerator _generator;

	/** every loaded terrain are in */
	private TerrainStorage _terrains;

	/** every world entities. */
	private EntityStorage _entities;

	/** world weather */
	private Weather _weather;

	private Random _rng;

	/** number of updates which was made for the world */
	private long _tick;

	public World() {
		this._terrains = new TerrainStorage(this);
		this._entities = new EntityStorage(this);
		this._weather = new Weather();
		this._rng = new Random();
		this._tick = 0;
		this.setWorldGenerator(new WorldGeneratorEmpty());
	}

	/** tasks to be run to update the world */
	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.engine.VoxelEngine.Callable<Taskable>> tasks) {

		this._weather.getTasks(engine, tasks);
		this._entities.getTasks(engine, tasks);
		this._terrains.getTasks(engine, tasks);
		this.onTasksGet(engine, tasks);
		this.tick();
	}

	/** call back to add tasks to run */
	protected void onTasksGet(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
	}

	/** set the world generator */
	public void setWorldGenerator(WorldGenerator worldgen) {
		this._generator = worldgen;
	}

	public Random getRNG() {
		return (this._rng);
	}

	public void setWeather(Weather weather) {
		this._weather = weather;
	}

	/** delete the world : de-allocate every allocated memory */
	public void delete() {
		this._entities.delete();
		this._terrains.delete();
	}

	/**
	 * return the terrain with the given location, or null if the terrain doesnt
	 * exists / is empty
	 */
	public TerrainStorage getTerrainStorage() {
		return (this._terrains);
	}

	public EntityStorage getEntityStorage() {
		return (this._entities);
	}

	/** return world weather */
	public Weather getWeather() {
		return (this._weather);
	}

	/** get the block at the given world relative position */
	public Block getBlock(float x, float y, float z) {
		return (this._terrains.getBlock(x, y, z));
	}

	/** world position */
	public Block getBlock(Vector3f pos) {
		return (this._terrains.getBlock(pos.x, pos.y, pos.z));
	}

	/** set the block at the given world coordinates */
	public Terrain setBlock(Block block, float x, float y, float z) {
		return (this._terrains.setBlock(block, x, y, z));
	}

	/** return the terrain at the given index */
	public Terrain getTerrain(int x, int y, int z) {
		return (this._terrains.get(x, y, z));
	}

	/** return the terrain at the given index */
	public Terrain getTerrain(Vector3i pos) {
		return (this._terrains.get(pos));
	}

	/** return true if the given terrain is loaded */
	public boolean isTerrainLoaded(Terrain terrain) {
		return (this._terrains.isLoaded(terrain));
	}

	/** get every loaded terrains */
	public Terrain[] getLoadedTerrains() {
		return (this._terrains.getLoaded());
	}

	/** get the terrain index for the given world position */
	public Vector3i getTerrainIndex(Vector3f position) {
		return (this.getTerrainIndex(position, new Vector3i()));
	}

	public Vector3i getTerrainIndex(Vector3f position, Vector3i world_index) {
		return (this._terrains.getIndex(position, world_index));
	}

	/** return true if this world can hold this terrain */
	public boolean canHoldTerrain(Terrain terrain) {
		return (this._terrains.canHold(terrain));
	}

	/** spawn a terrain */
	public boolean spawnTerrain(Terrain terrain) {
		return (this._terrains.spawn(terrain));
	}

	/** tick the world once */
	public void tick() {
		this._tick++;
	}

	public long getTick() {
		return (this._tick);
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
			if (!(entity instanceof EntityModeled)) {
				continue;
			}

			if (((EntityModeled) entity).getModelInstance().getMaxBoundingBox().intersect(box)) {
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
		// if (!(e instanceof EntityModeled) || e == entity) {
		// continue;
		// }
		//
		// BoundingBox entity_box = ((EntityModeled)
		// e).getModelInstance().getMaxBoundingBox();
		// if (box.intersect(entity_box)) {
		// lst.add(entity_box);
		// }
		// }

		return (lst);
	}

	/** world location */
	public BlockInstance getBlockInstance(float x, float y, float z) {
		return (this._terrains.getBlockInstance(x, y, z));
	}

	/**
	 * return the ground height under the given entity, by testing 'height'
	 * block along the Y axis at most
	 */
	public int getGroundHeight(Entity entity, int height) {
		return (this._terrains.getGroundHeight(entity, height));
	}

	/** called whenever this world is set as the current one */
	public abstract void onSet();

	/** called whenever this world is unset */
	public abstract void onUnset();

	public void set() {
		this.onSet();
	}

	public void unset() {
		this.onUnset();
	}

	// TODO: save and load

	/**
	 * save the given world to the given folder
	 * 
	 * HOW WORLD SAVE (WS) WORKS: - 'WS_INFO_FILE' stores information about the
	 * save - 'WS_ENTITIES_FILE' stores entities - 'WS_TERRAINS_FILE + x_y_z'
	 * stores 'WS_TERRAINS_PER_FILE' terrains inside the same files.
	 * 
	 */

	private static final String WS_INFO_FILE = "save_info";
	private static final String WS_ENTITIES_FILE = "entities";
	private static final String WS_TERRAINS_FILE = "terrain";
	private static final int WS_TERRAINS_PER_FILE_WIDTH = 3;
	private static final int WS_TERRAINS_PER_FILE_HEIGHT = 3;
	private static final int WS_TERRAINS_PER_FILE_DEPTH = 3;
	private static final int WS_TERRAINS_PER_FILE = WS_TERRAINS_PER_FILE_WIDTH * WS_TERRAINS_PER_FILE_HEIGHT
			* WS_TERRAINS_PER_FILE_DEPTH;

	public static final void save(World world, String filepath) {
	}

	/** load the given folder as a world */
	public static final World load(String filepath) {
		return (null);
	}
}
