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
import java.util.Random;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.extras.gimpact.GImpactCollisionAlgorithm;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.instances.BlockInstance;
import com.grillecube.common.world.entity.WorldEntity;
import com.grillecube.common.world.entity.WorldEntityStorage;
import com.grillecube.common.world.generator.SimplexNoiseOctave;
import com.grillecube.common.world.generator.WorldGenerator;
import com.grillecube.common.world.generator.WorldGeneratorEmpty;
import com.grillecube.common.world.physic.WorldObject;
import com.grillecube.common.world.physic.WorldObjectBlock;
import com.grillecube.common.world.terrain.WorldObjectTerrain;
import com.grillecube.common.world.terrain.WorldTerrainStorage;

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

	/** world bullet physics */
	private final DiscreteDynamicsWorld dynamicsWorld;

	public World() {
		this.terrains = this.instanciateTerrainStorage();
		this.entities = new WorldEntityStorage(this);
		this.rng = new Random();
		this.tick = 0;
		this.setWorldGenerator(new WorldGeneratorEmpty());

		// bullet setup
		BroadphaseInterface broadphase = new DbvtBroadphase();
		DefaultCollisionConfiguration config = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(config);
		GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		this.dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, config);
		this.dynamicsWorld.setGravity(new javax.vecmath.Vector3f(0, 0, -9.81f * WorldObjectTerrain.BLOCKS_PER_METER));
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
		double dt = engine.getTimer().getDt();

		for (WorldEntity entity : this.entities) {
			entity.preWorldUpdate(engine.getTimer().getDt());
		}

		this.dynamicsWorld.stepSimulation((float) dt);

		for (WorldEntity entity : this.entities) {
			entity.postWorldUpdate(dt);
		}
	}

	/**
	 * generate the terrain for the given coordinates, spawn it if un-existant
	 */
	public WorldObjectTerrain generateTerrain(int x, int y, int z) {
		WorldObjectTerrain terrain = this.getTerrain(x, y, z);
		if (terrain == null) {
			terrain = new WorldObjectTerrain(this, x, y, z);
			this.spawnTerrain(terrain);
		}
		return (this.generateTerrain(terrain));
	}

	public WorldObjectTerrain generateTerrain(WorldObjectTerrain terrain) {
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
	public final void delete() {
		this.entities.delete();
		this.terrains.delete();
		this.onDelete();
	}

	protected void onDelete() {

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

	/**
	 * set the block durabiltiy at the given world relative position
	 * 
	 * @see {@link Terrain.setDurability(byte, int)}
	 */
	public final void setBlockDurability(byte durability, float x, float y, float z) {
		this.terrains.setBlockDurability(durability, x, y, z);
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
	public WorldObjectTerrain setBlock(Block block, float x, float y, float z) {
		return (this.terrains.setBlock(block, x, y, z));
	}

	public byte getBlockLight(Vector3f pos) {
		return (this.terrains.getBlockLight(pos.x, pos.y, pos.z));
	}

	/** return the terrain at the given index */
	public WorldObjectTerrain getTerrain(int x, int y, int z) {
		return (this.terrains.get(x, y, z));
	}

	/** return the terrain at the given index */
	public WorldObjectTerrain getTerrain(Vector3i pos) {
		return (this.terrains.get(pos));
	}

	/** return true if the given terrain is loaded */
	public boolean isTerrainLoaded(WorldObjectTerrain terrain) {
		return (this.terrains.isLoaded(terrain));
	}

	/** get every loaded terrains */
	public WorldObjectTerrain[] getLoadedTerrains() {
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
	public boolean canHoldTerrain(WorldObjectTerrain terrain) {
		return (this.terrains.canHold(terrain));
	}

	/** spawn a terrain */
	public final WorldObjectTerrain spawnTerrain(WorldObjectTerrain terrain) {
		return (this.terrains.add(terrain));
	}

	/** spawn an entity into the world */
	public final WorldEntity spawnEntity(WorldEntity entity) {
		return (this.entities.add(entity));
	}

	/** tick the world once */
	public final void tick() {
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

	/**
	 * get the PhysicObjects (blocks and entities) colliding with the PhysicObject
	 * 
	 * @param physicObject
	 * @return : the physic object list
	 */
	public final ArrayList<WorldObject> getCollidingPhysicObjects(WorldObject exclude, float minx, float miny,
			float minz, float maxx, float maxy, float maxz) {
		ArrayList<WorldObject> lst = new ArrayList<WorldObject>();

		int mx = Maths.floor(minx);
		int Mx = Maths.ceil(maxx);
		int my = Maths.floor(miny);
		int My = Maths.ceil(maxy);
		int mz = Maths.floor(minz);
		int Mz = Maths.ceil(maxz);

		// iterate though each blocks
		for (int x = mx; x < Mx; x++) {
			for (int y = my; y < My; y++) {
				for (int z = mz; z < Mz; z++) {
					Block block = this.getBlock(x, y, z);
					if (!block.isCrossable()) {
						lst.add(new WorldObjectBlock(this, block, x, y, z));
					}
				}
			}
		}
		return (lst);
	}
}
