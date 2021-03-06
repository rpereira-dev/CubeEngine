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

package com.grillecube.common.world.terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import com.grillecube.common.event.Event;
import com.grillecube.common.event.world.EventTerrainBlocklightUpdate;
import com.grillecube.common.event.world.EventTerrainDurabilityChanged;
import com.grillecube.common.event.world.EventTerrainSetBlock;
import com.grillecube.common.event.world.EventTerrainSunlightUpdate;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.block.instances.BlockInstance;
import com.grillecube.common.world.physic.WorldObject;

public class WorldObjectTerrain extends WorldObject {
	/** terrain states */
	public static final int STATE_FACE_VISIBILTY_UP_TO_DATE = 1 << 0;

	/** terrain dimensions */
	// block size unit
	public static final float BLOCK_SIZE = 1.0f;
	public static final float BLOCKS_PER_METER = 4.0f / BLOCK_SIZE; // 4 blocks = 1 m

	// (and use 1 as implicit value to
	// optimize calculations)
	public static final float BLOCK_DEMI_SIZE = BLOCK_SIZE / 2.0f;

	public static Vector3f BLOCK_SIZE_VEC = new Vector3f(BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

	public static final int DIMX = 16;
	public static final int DIMY = DIMX;
	public static final int DIMZ = 64;
	public static final int DEMI_DIMX = DIMX / 2;
	public static final int DEMI_DIMY = DIMY / 2;
	public static final int DEMI_DIMZ = DIMZ / 2;
	public static final int DIM3 = DIMX * DIMY * DIMZ;
	public static final int MAX_BLOCK_INDEX = WorldObjectTerrain.DIMX * WorldObjectTerrain.DIMY
			* WorldObjectTerrain.DIMZ;
	public static final float SIZE_DIAGONAL3 = (float) Vector3f.distance(new Vector3f(0, 0, 0),
			new Vector3f(DIMX * BLOCK_SIZE, DIMY * BLOCK_SIZE, DIMZ * BLOCK_SIZE));
	public static float DIMX_SIZE = DIMX * BLOCK_SIZE;
	public static float DIMY_SIZE = DIMY * BLOCK_SIZE;
	public static float DIMZ_SIZE = DIMZ * BLOCK_SIZE;
	public static Vector3f TERRAIN_SIZE = new Vector3f(DIMX_SIZE, DIMY_SIZE, DIMZ_SIZE);
	public static float DEMI_SIZE_DIAGONAL = SIZE_DIAGONAL3 / 2.0f;
	public static float DEMI_DIMX_SIZE = DIMX_SIZE / 2.0f;
	public static float DEMI_DIMY_SIZE = DIMY_SIZE / 2.0f;
	public static float DEMI_DIMZ_SIZE = DIMZ_SIZE / 2.0f;

	/** max durability of a block */
	public static final byte MIN_DURABILITY = 0;
	public static final byte MAX_DURABILITY = 15;

	/** terrain location */
	private final Vector3i worldIndex;
	private final Vector3f worldPos;

	/** block instances */
	private HashMap<Integer, BlockInstance> blockInstances;

	/** block ids */
	protected short[] blocks;

	/** this terrain heightmap */
	protected byte[] heightmap;

	/** number of blocks opaque or transparent */
	private int blockCount;

	/** block lights (4 bits : sun , 4 bits: block) */
	protected byte[] lights; // light values for every blocks

	/** blocks durability */
	protected byte[] durability;

	// TODO : list of planes (for rendering and bullet collisions)

	/** terrain mass (sum of block mass) */
	private float mass;

	/** terrain states */
	private int state;

	/** the lights lists */
	private Stack<LightNodeAdd> lightBlockAddQueue;
	private Stack<LightNodeRemoval> lightBlockRemovalQueue;
	private Stack<LightNodeAdd> sunLightAddQueue;
	private Stack<LightNodeRemoval> sunLightRemovalQueue;

	/** which face can see another */
	private boolean[][] facesVisibility;

	public WorldObjectTerrain(World world, Vector3i index) {
		this(world, index.x, index.y, index.z);
	}

	// this.worldPosCenter = new Vector3f(this.worldPos.x +
	// WorldObjectTerrain.DEMI_DIMX_SIZE,
	// this.worldPos.y + WorldObjectTerrain.DEMI_DIMY_SIZE,
	// this.worldPos.z + WorldObjectTerrain.DEMI_DIMZ_SIZE);

	public WorldObjectTerrain(World world, int ix, int iy, int iz) {
		super(world);
		this.worldIndex = new Vector3i(ix, iy, iz);
		this.worldPos = new Vector3f(ix * WorldObjectTerrain.DIMX_SIZE, iy * WorldObjectTerrain.DIMY_SIZE,
				iz * WorldObjectTerrain.DIMZ_SIZE);
		this.blocks = null;
		this.blockInstances = null;
		this.lights = null;
		this.durability = null;
		this.blockCount = 0;
		this.facesVisibility = new boolean[6][6];
		this.lightBlockAddQueue = null;
		this.lightBlockRemovalQueue = null;
		for (Face a : Face.faces) {
			for (Face b : Face.faces) {
				this.facesVisibility[a.getID()][b.getID()] = true;
			}
		}
		this.setState(STATE_FACE_VISIBILTY_UP_TO_DATE);
		this.mass = 0.0f;
	}

	/******
	 * EVERY TERRAINS UPDATE RELATIVE FUNCTIONS (COMPLEX ALGORYTHM) ARE CALLED IN A
	 * SEPARATE THREAD. STARTS HERE
	 */
	/** update the terrain once */
	public void update() {
		this.updateFaceVisibility();
		this.updateBlockInstances();
		this.updateSunLight();
		this.updateBlockLights();
		// this.updateBlocks();
	}

	/** terrain face visibility update */
	private void updateFaceVisibility() {
		if (!this.hasState(WorldObjectTerrain.STATE_FACE_VISIBILTY_UP_TO_DATE)) {
			this.updateFaceVisiblity();
			// TODO : if top face... update sun light
		}
	}

	/** tick once block instances of this terrain */
	private void updateBlockInstances() {
		if (this.blockInstances == null) {
			return;
		}

		BlockInstance[] blockInstances = this.blockInstances.values()
				.toArray(new BlockInstance[this.blockInstances.size()]);
		for (BlockInstance blockInstance : blockInstances) {
			if (blockInstance == null) {
				continue;
			}
			blockInstance.update();
		}
	}

	/** tick once a random block of this terrain */
	private final void updateBlocks() {

		if (this.blocks == null) {
			return;
		}

		int index = (short) (Maths.abs(this.getWorld().getRNG().nextInt()) % this.blocks.length);
		int z = this.getZFromIndex(index);
		int y = this.getYFromIndex(index, z);
		int x = this.getXFromIndex(index, y, z);

		Block block = this.getBlockAt(index);
		block.update(this, x, y, z);
	}

	/******
	 * EVERY TERRAINS UPDATE RELATIVE FUNCTIONS (COMPLEX ALGORYTHM) ARE CALLED IN A
	 * SEPARATE THREAD. END HERE
	 */

	public boolean hasState(int state) {
		return ((this.state & state) == state);
	}

	public void setState(int state) {
		this.state = this.state | state;
	}

	public void unsetState(int state) {
		this.state = this.state & ~(state);
	}

	public void switchState(int state) {
		this.state = this.state ^ state;
	}

	/**
	 * return the terrain at the given position, relative to this instance It fills
	 * the 'dst' Vector3i with it terrain-relative coordinates and return it
	 * 
	 * if the terrain didnt exists, it creates it
	 */
	public WorldObjectTerrain getRelativeTerrain(int xyz[]) {
		WorldObjectTerrain terrain = this;

		// x test
		if (xyz[0] < 0) {
			do {
				xyz[0] += WorldObjectTerrain.DIMX;
				terrain = terrain.getNeighbor(Face.BACK);
			} while (xyz[0] < 0);
			if (terrain == null) {
				return (null);
			}
		} else if (xyz[0] >= WorldObjectTerrain.DIMX) {
			do {
				xyz[0] -= WorldObjectTerrain.DIMX;
				terrain = terrain.getNeighbor(Face.FRONT);
			} while (xyz[0] >= WorldObjectTerrain.DIMX);
			if (terrain == null) {
				return (null);
			}
		}

		// y test
		if (xyz[1] < 0) {
			do {
				xyz[1] += WorldObjectTerrain.DIMY;
				terrain = terrain.getNeighbor(Face.LEFT);
			} while (xyz[1] < 0);
			if (terrain == null) {
				return (null);
			}
		} else if (xyz[1] >= WorldObjectTerrain.DIMY) {
			do {
				xyz[1] -= WorldObjectTerrain.DIMY;
				terrain = terrain.getNeighbor(Face.RIGHT);
			} while (xyz[1] >= WorldObjectTerrain.DIMY);
			if (terrain == null) {
				return (null);
			}
		}

		// z test
		if (xyz[2] < 0) {
			do {
				xyz[2] += WorldObjectTerrain.DIMZ;
				terrain = terrain.getNeighbor(Face.BOT);
			} while (xyz[2] < 0);
			if (terrain == null) {
				return (null);
			}
		} else if (xyz[2] >= WorldObjectTerrain.DIMZ) {
			do {
				xyz[2] -= WorldObjectTerrain.DIMZ;
				terrain = terrain.getNeighbor(Face.TOP);
			} while (xyz[2] >= WorldObjectTerrain.DIMZ);
			if (terrain == null) {
				return (null);
			}
		}
		return (terrain);
	}

	public final Block getBlock(int x, int y, int z) {
		return (this.getBlock(new int[] { x, y, z }));
	}

	public final Block getBlock(int xyz[]) {
		WorldObjectTerrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (Blocks.AIR);
		}
		return (terrain.getBlockAt(xyz[0], xyz[1], xyz[2]));
	}

	/** this function doesnt check bounds */
	public Block getBlockAt(int x, int y, int z) {
		return (this.getBlockAt(this.getIndex(x, y, z)));
	}

	public Block getBlockAt(int index) {
		if (this.blocks == null) {
			return (Blocks.AIR);
		}
		Block block = Blocks.getBlockByID(this.blocks[index]);
		return (block != null ? block : Blocks.AIR);

	}

	/** secure function to set a block relative to this terrain */
	public BlockInstance setBlock(Block block, int x, int y, int z) {
		int[] xyz = { x, y, z };
		WorldObjectTerrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (null);
		}
		int index = this.getIndex(xyz[0], xyz[1], xyz[2]);
		return (terrain.setBlock(block, index, xyz[0], xyz[1], xyz[2]));
	}

	/** secure function to set a block relative to this terrain */
	public void setBlock(Block block, Vector3i pos) {
		this.setBlock(block, pos.x, pos.y, pos.z);
	}

	public BlockInstance setBlock(Block block, int index) {
		int z = this.getZFromIndex(index);
		int y = this.getYFromIndex(index, z);
		int x = this.getXFromIndex(index, y, z);
		return (this.setBlock(block, index, x, y, z));
	}

	/**
	 * WARNING : this function doest check bound, only use if you know what you're
	 * doing
	 * 
	 * @param block
	 * @param xyz
	 * @return
	 */
	public BlockInstance setBlockAt(Block block, int x, int y, int z) {
		return (this.setBlock(block, this.getIndex(x, y, z), x, y, z));
	}

	/** @see #setBlock(Block, int, int, int, int) */
	public final BlockInstance setBlock(Block block, int index, int xyz[]) {
		return (this.setBlock(block, index, xyz[0], xyz[1], xyz[2]));
	}

	/** function to set a block to this terrain */
	public final BlockInstance setBlock(Block block, int index, int x, int y, int z) {

		// if terrain was empty
		if (this.blocks == null) {
			// if setting a air block
			if (block == Blocks.AIR) {
				// nothing to be done...
				return (null);
			}
			// else, initialize it, fill it with air
			this.blocks = new short[WorldObjectTerrain.MAX_BLOCK_INDEX];
			Arrays.fill(this.blocks, (byte) Blocks.AIR_ID);
		}

		// get the previous block in this location
		Block prevblock = Blocks.getBlockByID(this.blocks[index]);

		// unset the previous block
		prevblock.onUnset(this, x, y, z);

		// get the previous instance at this location, and remove it
		BlockInstance previnstance = this.removeBlockInstance(index);

		// if there was a block instance
		if (previnstance != null) {
			// unset callback for this instance
			previnstance.onUnset();
		}

		// set the new block
		this.blocks[index] = block.getID();

		// set callback
		block.onSet(this, x, y, z);

		// update number of block set
		if (prevblock.getID() != Blocks.AIR_ID && block.getID() == Blocks.AIR_ID) {
			--this.blockCount;
			int zmax = this.heightmap[x + WorldObjectTerrain.DIMY * y];
			while (zmax > 0 && this.blocks[this.getIndex(x, y, zmax - 1)] == Blocks.AIR_ID) {
				--zmax;
			}
			this.heightmap[x + WorldObjectTerrain.DIMX * y] = (byte) (zmax - 1);
			this.mass -= prevblock.getMass();
		} else if (prevblock.getID() == Blocks.AIR_ID && block.getID() != Blocks.AIR_ID) {
			++this.blockCount;
			if (this.heightmap == null) {
				this.heightmap = new byte[WorldObjectTerrain.DIMX * WorldObjectTerrain.DIMZ];

			}
			int heightmapIndex = x + WorldObjectTerrain.DIMX * y;
			if (this.heightmap[heightmapIndex] <= z) {
				this.heightmap[heightmapIndex] = (byte) (z + 1);
			}
			this.mass += prevblock.getMass();
		}

		// get a new block instance for this new block
		BlockInstance instance = block.createBlockInstance(this, index);

		// if this block actually have (need) an instance
		if (instance != null) {

			// initialiaze the instance list if needed. (i.e if it is the first
			// block instance for this terrain)
			if (this.blockInstances == null) {
				this.blockInstances = new HashMap<Integer, BlockInstance>();
			}
			// add the instance to the list
			this.blockInstances.put(index, instance);
			// instance set calback
			instance.onSet();
		}
		this.invokeEvent(new EventTerrainSetBlock(this, block, index)); // TODO : pool object for this
		return (instance);
	}

	private void invokeEvent(Event event) {
		EventManager.instance().invokeEvent(event);
	}

	/** remove and return the block instance at the given location */
	private BlockInstance removeBlockInstance(Integer index) {
		if (this.blockInstances == null) {
			return (null);
		}
		BlockInstance blockInstance = this.blockInstances.remove(index);

		// if there is no longer block instances, delete the hashmap
		if (this.blockInstances.size() == 0) {
			this.blockInstances = null;
		}
		return (blockInstance);
	}

	/**
	 * get the block instance at the given location (relative to the terrain)
	 */
	public BlockInstance getBlockInstanceAt(int index) {
		if (this.blockInstances == null) {
			return (null);
		}
		return (this.blockInstances.get(index));
	}

	/**
	 * get the block instance at the given location (relative to the terrain)
	 */
	public final BlockInstance getBlockInstance(int x, int y, int z) {
		return (this.getBlockInstanceAt(this.getIndex(x, y, z)));
	}

	/**
	 * LIGHT BEGINS HERE:
	 * 
	 * THE IMPLEMENTATION IS BASED ON THIS WORK:
	 * https://www.seedofandromeda.com/blogs/29-fast-flood-fill-lighting-in-a-blocky-voxel-game-pt-1
	 * 
	 * SPECIAL THANK TO SOA TEAM FOR THEIR SHARING
	 */

	public class LightNodeAdd {
		public final WorldObjectTerrain terrain;
		public final int index;

		public LightNodeAdd(WorldObjectTerrain terrain, int index) {
			this.terrain = terrain;
			this.index = index;
		}
	}

	public class LightNodeRemoval {
		public final WorldObjectTerrain terrain;
		public final int index;
		public final byte value;

		public LightNodeRemoval(WorldObjectTerrain terrain, int index, byte value) {
			this.terrain = terrain;
			this.index = index;
			this.value = value;
		}
	}

	/**
	 * 
	 * SUN LIGHT PROPAGATION ABOVE
	 * 
	 */

	/** get sunlight value */
	public final byte getSunLight(int xyz[]) {
		WorldObjectTerrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (15);
		}
		return (terrain.getSunLight(terrain.getIndex(xyz[0], xyz[1], xyz[2])));
	}

	public final byte getSunLight(int x, int y, int z) {
		return (this.getSunLight(new int[] { x, y, z }));
	}

	/** get sunlight value */
	public final byte getSunLight(int index) {
		if (this.lights == null) {
			return (0);
		}
		return (byte) ((this.lights[index] >> 4) & 0xF);
	}

	/** set the sunlight value */
	private final void setSunLight(byte value, int index) {
		if (this.lights == null) {
			// initialize it, fill it with 0
			this.lights = new byte[WorldObjectTerrain.MAX_BLOCK_INDEX];
			Arrays.fill(this.lights, (byte) 0);
		}
		this.lights[index] = (byte) ((this.lights[index] & 0xF) | (value << 4));
	}

	private void addSunLight(byte lightValue, int x, int y, int z) {
		this.addSunLight(lightValue, this.getIndex(x, y, z));
	}

	/** add a light to the terrain */
	private void addSunLight(byte lightValue, int index) {
		if (lightValue <= 0) {
			return;
		}
		if (this.sunLightAddQueue == null) {
			this.sunLightAddQueue = new Stack<LightNodeAdd>();
		}
		this.sunLightAddQueue.add(new LightNodeAdd(this, index));
		this.setSunLight(lightValue, index);
	}

	/** remove the light at given index */
	private void removeSunLight(int index) {
		this.removeSunLight(index, this.getBlockLight(index));
	}

	private void removeSunLight(int index, byte value) {
		if (value <= 0) {
			return;
		}
		if (this.sunLightRemovalQueue == null) {
			this.sunLightRemovalQueue = new Stack<LightNodeRemoval>();
		}

		this.sunLightRemovalQueue.push(new LightNodeRemoval(this, index, value));
		this.setSunLight((byte) 0, index);
	}

	/** empty the sunlight queue */
	private void updateSunLight() {

		if (this.sunLightAddQueue == null && this.sunLightRemovalQueue == null) {
			return;
		}

		// removing a light
		// the affected terrains
		ArrayList<WorldObjectTerrain> processedTerrains = new ArrayList<WorldObjectTerrain>(4);

		if (this.sunLightRemovalQueue != null) {
			this.propagateSunlightRemovalQueue(processedTerrains);
			this.sunLightRemovalQueue = null;
		}

		if (this.sunLightAddQueue != null) {
			this.propagateSunLightAddQueue(processedTerrains);
			this.sunLightAddQueue = null;
		}

		// update meshes
		for (WorldObjectTerrain terrain : processedTerrains) {
			terrain.invokeEvent(new EventTerrainSunlightUpdate(terrain));
		}
	}

	private void propagateSunLightAddQueue(ArrayList<WorldObjectTerrain> processedTerrains) {
		// do the algorithm
		while (!this.sunLightAddQueue.isEmpty()) {

			// get the light value
			LightNodeAdd lightNode = this.sunLightAddQueue.pop();

			WorldObjectTerrain nodeTerrain = lightNode.terrain;

			if (!processedTerrains.contains(nodeTerrain)) {
				processedTerrains.add(nodeTerrain);
			}

			// get the index
			int index = lightNode.index;
			byte lightValue = nodeTerrain.getSunLight(index);

			// next value
			byte nextLightValue = (byte) (lightValue - 1);

			// if reached the end; stop propagation
			if (nextLightValue == 0) {
				continue;
			}

			int z = nodeTerrain.getZFromIndex(index);
			int y = nodeTerrain.getYFromIndex(index, z);
			int x = nodeTerrain.getXFromIndex(index, y, z);

			// propagate thought x negative
			if (x > 0) {
				nodeTerrain.floodFillSunlightAdd(nodeTerrain.getIndex(x - 1, y, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BACK);
				if (terrain != null) {
					terrain.floodFillSunlightAdd(terrain.getIndex(WorldObjectTerrain.DIMX - 1, y, z), nextLightValue);
				}
			}

			// propagate thought y negative
			if (y > 0) {
				nodeTerrain.floodFillSunlightAdd(nodeTerrain.getIndex(x, y - 1, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.LEFT);
				if (terrain != null) {
					terrain.floodFillSunlightAdd(terrain.getIndex(x, WorldObjectTerrain.DIMX - 1, z), nextLightValue);
				}
			}

			// propagate thought z negative
			if (z > 0) {
				nodeTerrain.floodFillSunlightAdd(nodeTerrain.getIndex(x, y, z - 1), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BOT);
				if (terrain != null) {
					terrain.floodFillSunlightAdd(terrain.getIndex(x, y, WorldObjectTerrain.DIMZ - 1), nextLightValue);
				}
			}

			// propagate thought x positive
			if (x < WorldObjectTerrain.DIMX - 1) {
				nodeTerrain.floodFillSunlightAdd(nodeTerrain.getIndex(x + 1, y, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.FRONT);
				if (terrain != null) {
					terrain.floodFillSunlightAdd(terrain.getIndex(0, y, z), nextLightValue);
				}
			}

			// propagate thought y positive
			if (y < WorldObjectTerrain.DIMY - 1) {
				nodeTerrain.floodFillSunlightAdd(nodeTerrain.getIndex(x, y + 1, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.RIGHT);
				if (terrain != null) {
					terrain.floodFillSunlightAdd(terrain.getIndex(x, 0, z), nextLightValue);
				}
			}

			// propagate thought z positive
			if (z < WorldObjectTerrain.DIMZ - 1) {
				nodeTerrain.floodFillSunlightAdd(nodeTerrain.getIndex(x, y, z + 1), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.TOP);
				if (terrain != null) {
					terrain.floodFillSunlightAdd(terrain.getIndex(x, y, 0), nextLightValue);
				}
			}
		}
	}

	private void floodFillSunlightAdd(int index, byte nextLightValue) {
		Block next = this.getBlockAt(index);
		if (next.isTransparent()) {
			if (this.getSunLight(index) < nextLightValue) {
				this.addSunLight(nextLightValue, index);
			}
		}
	}

	private void propagateSunlightRemovalQueue(ArrayList<WorldObjectTerrain> processedTerrains) {

		// bfs algorithm
		int x, y, z;

		while (!this.sunLightRemovalQueue.isEmpty()) {

			// get the light value
			LightNodeRemoval lightValueNode = sunLightRemovalQueue.pop();
			WorldObjectTerrain nodeTerrain = lightValueNode.terrain;

			if (!processedTerrains.contains(nodeTerrain)) {
				processedTerrains.add(nodeTerrain);
			}

			// clear the light value
			int nodeIndex = lightValueNode.index;
			byte lightLevel = lightValueNode.value;

			z = nodeTerrain.getZFromIndex(nodeIndex);
			y = nodeTerrain.getYFromIndex(nodeIndex, z);
			x = nodeTerrain.getXFromIndex(nodeIndex, y, z);

			// propagate thought x negative
			if (x > 0) {
				nodeTerrain.floodFillSunlightRemove(nodeTerrain.getIndex(x - 1, y, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BACK);
				if (terrain != null) {
					terrain.floodFillSunlightRemove(terrain.getIndex(WorldObjectTerrain.DIMX - 1, y, z), lightLevel);
				}
			}

			// propagate thought y negative
			if (y > 0) {
				nodeTerrain.floodFillSunlightRemove(nodeTerrain.getIndex(x, y - 1, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.LEFT);
				if (terrain != null) {
					terrain.floodFillSunlightRemove(terrain.getIndex(x, WorldObjectTerrain.DIMY - 1, z), lightLevel);
				}
			}

			// propagate thought z negative
			if (z > 0) {
				nodeTerrain.floodFillSunlightRemove(nodeTerrain.getIndex(x, y, z - 1), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BOT);
				if (terrain != null) {
					terrain.floodFillSunlightRemove(terrain.getIndex(x, y, WorldObjectTerrain.DIMZ - 1), lightLevel);
				}
			}

			// propagate thought x positive
			if (x < WorldObjectTerrain.DIMX - 1) {
				nodeTerrain.floodFillSunlightRemove(nodeTerrain.getIndex(x + 1, y, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.FRONT);
				if (terrain != null) {
					terrain.floodFillSunlightRemove(terrain.getIndex(0, y, z), lightLevel);
				}
			}

			// propagate thought y positive
			if (y < WorldObjectTerrain.DIMY - 1) {
				nodeTerrain.floodFillSunlightRemove(nodeTerrain.getIndex(x, y + 1, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.RIGHT);
				if (terrain != null) {
					terrain.floodFillSunlightRemove(terrain.getIndex(x, 0, z), lightLevel);
				}
			}

			// propagate thought z positive
			if (z < WorldObjectTerrain.DIMZ - 1) {
				nodeTerrain.floodFillSunlightRemove(nodeTerrain.getIndex(x, y, z + 1), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.TOP);
				if (terrain != null) {
					terrain.floodFillSunlightRemove(terrain.getIndex(x, y, 0), lightLevel);
				}
			}
		}
	}

	private void floodFillSunlightRemove(int index, byte lightLevel) {
		byte neighborLevel = this.getBlockLight(index);
		if (neighborLevel != 0 && neighborLevel < lightLevel) {
			this.removeSunLight(index);
		} else if (neighborLevel >= lightLevel) {
			this.addSunLight(neighborLevel, index);
		}
	}

	/**
	 * 
	 * BLOCK LIGHT PROPAGATION ABOVE
	 * 
	 */
	/** get the block light value */
	public final byte getBlockLight(int xyz[]) {
		WorldObjectTerrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (0);
		}
		return (terrain.getBlockLight(terrain.getIndex(xyz[0], xyz[1], xyz[2])));
	}

	public final byte getBlockLight(int x, int y, int z) {
		return (this.getBlockLight(new int[] { x, y, z }));
	}

	/** get the block light value */
	public byte getBlockLight(int index) {
		if (this.lights == null) {
			return (0);
		}
		return ((byte) (this.lights[index] & 0xF));
	}

	/** set tje block light value */
	private final void setBlockLight(byte val, int index) {
		if (this.lights == null) {
			// initialize it, fill it with 0
			this.lights = new byte[WorldObjectTerrain.MAX_BLOCK_INDEX];
			Arrays.fill(this.lights, (byte) 0);
		}
		this.lights[index] = (byte) ((this.lights[index] & 0xF0) | val);
	}

	public void addBlockLight(byte lightValue, int x, int y, int z) {
		this.addBlockLight(lightValue, this.getIndex(x, y, z));
	}

	/** add a light to the terrain */
	public void addBlockLight(byte lightValue, int index) {
		if (lightValue == 0) {
			return;
		}
		if (this.lightBlockAddQueue == null) {
			this.lightBlockAddQueue = new Stack<LightNodeAdd>();
		}
		this.lightBlockAddQueue.add(new LightNodeAdd(this, index));
		this.setBlockLight(lightValue, index);
	}

	/** remove the light at given coordinates */
	public void removeLight(int x, int y, int z) {
		this.removeLight(this.getIndex(x, y, z));
	}

	/** remove the light at given index */
	public void removeLight(int index) {
		this.removeLight(index, this.getBlockLight(index));
	}

	private void removeLight(int index, byte value) {

		if (value <= 0) {
			return;
		}

		if (this.lightBlockRemovalQueue == null) {
			this.lightBlockRemovalQueue = new Stack<LightNodeRemoval>();
		}

		this.lightBlockRemovalQueue.push(new LightNodeRemoval(this, index, value));
		this.setBlockLight((byte) 0, index);
	}

	/** update the lighting */
	private void updateBlockLights() {

		if (this.lightBlockAddQueue == null && this.lightBlockRemovalQueue == null) {
			return;
		}

		// removing a light
		// the affected terrains
		ArrayList<WorldObjectTerrain> processedTerrains = new ArrayList<WorldObjectTerrain>(27);

		if (this.lightBlockRemovalQueue != null) {
			this.propagateLightRemovalQueue(processedTerrains);
			this.lightBlockRemovalQueue = null;
		}

		if (this.lightBlockAddQueue != null) {
			this.propagateLightAddQueue(processedTerrains);
			this.lightBlockAddQueue = null;
		}

		// update meshes
		for (WorldObjectTerrain terrain : processedTerrains) {
			terrain.invokeEvent(new EventTerrainBlocklightUpdate(terrain));
		}
	}

	private void propagateLightAddQueue(ArrayList<WorldObjectTerrain> processedTerrains) {
		// do the algorithm
		while (!this.lightBlockAddQueue.isEmpty()) {

			// get the light value
			LightNodeAdd lightNode = this.lightBlockAddQueue.pop();

			WorldObjectTerrain nodeTerrain = lightNode.terrain;

			if (!processedTerrains.contains(nodeTerrain)) {
				processedTerrains.add(nodeTerrain);
			}

			// get the index
			int index = lightNode.index;
			byte lightValue = nodeTerrain.getBlockLight(index);

			// next value
			byte nextLightValue = (byte) (lightValue - 1);

			// if reached the end; stop propagation
			if (nextLightValue == 0) {
				continue;
			}

			int z = nodeTerrain.getZFromIndex(index);
			int y = nodeTerrain.getYFromIndex(index, z);
			int x = nodeTerrain.getXFromIndex(index, y, z);

			// propagate thought x negative
			if (x > 0) {
				nodeTerrain.floodFillLightAdd(nodeTerrain.getIndex(x - 1, y, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BACK);
				if (terrain != null) {
					terrain.floodFillLightAdd(terrain.getIndex(WorldObjectTerrain.DIMX - 1, y, z), nextLightValue);
				}
			}

			// propagate thought y negative
			if (y > 0) {
				nodeTerrain.floodFillLightAdd(nodeTerrain.getIndex(x, y - 1, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.LEFT);
				if (terrain != null) {
					terrain.floodFillLightAdd(terrain.getIndex(x, WorldObjectTerrain.DIMY - 1, z), nextLightValue);
				}
			}

			// propagate thought z negative
			if (z > 0) {
				nodeTerrain.floodFillLightAdd(nodeTerrain.getIndex(x, y, z - 1), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BOT);
				if (terrain != null) {
					terrain.floodFillLightAdd(terrain.getIndex(x, y, WorldObjectTerrain.DIMZ - 1), nextLightValue);
				}
			}

			// propagate thought x positive
			if (x < WorldObjectTerrain.DIMX - 1) {
				nodeTerrain.floodFillLightAdd(nodeTerrain.getIndex(x + 1, y, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.FRONT);
				if (terrain != null) {
					terrain.floodFillLightAdd(terrain.getIndex(0, y, z), nextLightValue);
				}
			}

			// propagate thought y positive
			if (y < WorldObjectTerrain.DIMY - 1) {
				nodeTerrain.floodFillLightAdd(nodeTerrain.getIndex(x, y + 1, z), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.RIGHT);
				if (terrain != null) {
					terrain.floodFillLightAdd(terrain.getIndex(x, 0, z), nextLightValue);
				}
			}

			// propagate thought z positive
			if (z < WorldObjectTerrain.DIMZ - 1) {
				nodeTerrain.floodFillLightAdd(nodeTerrain.getIndex(x, y, z + 1), nextLightValue);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.TOP);
				if (terrain != null) {
					terrain.floodFillLightAdd(terrain.getIndex(x, y, 0), nextLightValue);
				}
			}
		}
	}

	private void floodFillLightAdd(int index, byte nextLightValue) {
		Block next = this.getBlockAt(index);
		if (next.isTransparent()) {
			if (this.getBlockLight(index) < nextLightValue) {
				this.addBlockLight(nextLightValue, index);
			}
		}
	}

	private void propagateLightRemovalQueue(ArrayList<WorldObjectTerrain> processedTerrains) {

		// bfs algorithm
		int x, y, z;

		while (!this.lightBlockRemovalQueue.isEmpty()) {

			// get the light value
			LightNodeRemoval lightValueNode = lightBlockRemovalQueue.pop();
			WorldObjectTerrain nodeTerrain = lightValueNode.terrain;

			if (!processedTerrains.contains(nodeTerrain)) {
				processedTerrains.add(nodeTerrain);
			}

			// clear the light value
			int nodeIndex = lightValueNode.index;
			byte lightLevel = lightValueNode.value;

			z = nodeTerrain.getZFromIndex(nodeIndex);
			y = nodeTerrain.getYFromIndex(nodeIndex, z);
			x = nodeTerrain.getXFromIndex(nodeIndex, y, z);

			// propagate thought x negative
			if (x > 0) {
				nodeTerrain.floodFillLightRemove(nodeTerrain.getIndex(x - 1, y, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BACK);
				if (terrain != null) {
					terrain.floodFillLightRemove(terrain.getIndex(WorldObjectTerrain.DIMX - 1, y, z), lightLevel);
				}
			}

			// propagate thought y negative
			if (y > 0) {
				nodeTerrain.floodFillLightRemove(nodeTerrain.getIndex(x, y - 1, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.LEFT);
				if (terrain != null) {
					terrain.floodFillLightRemove(terrain.getIndex(x, WorldObjectTerrain.DIMX - 1, z), lightLevel);
				}
			}

			// propagate thought z negative
			if (z > 0) {
				nodeTerrain.floodFillLightRemove(nodeTerrain.getIndex(x, y, z - 1), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.BOT);
				if (terrain != null) {
					terrain.floodFillLightRemove(terrain.getIndex(x, y, WorldObjectTerrain.DIMZ - 1), lightLevel);
				}
			}

			// propagate thought x positive
			if (x < WorldObjectTerrain.DIMX - 1) {
				nodeTerrain.floodFillLightRemove(nodeTerrain.getIndex(x + 1, y, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.FRONT);
				if (terrain != null) {
					terrain.floodFillLightRemove(terrain.getIndex(0, y, z), lightLevel);
				}
			}

			// propagate thought y positive
			if (y < WorldObjectTerrain.DIMY - 1) {
				nodeTerrain.floodFillLightRemove(nodeTerrain.getIndex(x, y + 1, z), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.RIGHT);
				if (terrain != null) {
					terrain.floodFillLightRemove(terrain.getIndex(x, 0, z), lightLevel);
				}
			}

			// propagate thought z positive
			if (z < WorldObjectTerrain.DIMZ - 1) {
				nodeTerrain.floodFillLightRemove(nodeTerrain.getIndex(x, y, z + 1), lightLevel);
			} else {
				WorldObjectTerrain terrain = nodeTerrain.getNeighbor(Face.TOP);
				if (terrain != null) {
					terrain.floodFillLightRemove(terrain.getIndex(x, y, 0), lightLevel);
				}
			}
		}
	}

	private void floodFillLightRemove(int index, byte lightLevel) {
		byte neighborLevel = this.getBlockLight(index);
		if (neighborLevel != 0 && neighborLevel < lightLevel) {
			this.removeLight(index);
		} else if (neighborLevel >= lightLevel) {
			this.addBlockLight(neighborLevel, index);
		}
	}

	/** LIGHTS ENDS HERE */

	// @Override
	// public int hashCode() {
	// return (this.terrainLocation.hashCode() + this.world.hashCode());
	// }

	public void requestFaceVisibilityUpdate() {
		this.unsetState(WorldObjectTerrain.STATE_FACE_VISIBILTY_UP_TO_DATE);
	}

	/** return terrain location */
	public Vector3i getWorldIndex() {
		return (this.worldIndex);
	}

	public Vector3f getWorldPosition() {
		return (this.worldPos);
	}

	@Override
	public String toString() {
		return ("Terrain: " + this.worldIndex);
	}

	/**
	 * non overflow proof:
	 * 
	 * let D = Terrain.DIM
	 * 
	 * 0 <= x <= D - 1 0 <= y <= D - 1 0 <= z <= D - 1
	 *
	 * => 0 <= x + D * (y + z.D) <= (D - 1) + D . ((D - 1) + D . (D - 1)) => 0 <=
	 * index <= (D - 1) + D . ((D - 1) + D^2 - D)) => 0 <= index <= (D - 1) + D .
	 * (D^2 - 1) => 0 <= index <= (D - 1) + D^3 - D) => 0 <= index <= D^3 - 1 => 0
	 * <= index <= this.blocks.length : OK
	 *
	 * unicity proof: index = x + D * (y + D * z) = x + y.D + z.D^2 = z.D^2 + y.D +
	 * x
	 *
	 * if x < D, then x / D = 0 (we are doing division using integers). Then we know
	 * that:
	 *
	 * index / D = (z.D^2 + y.D + x) / D = z.D + y + x / D = z.D + y + 0 = z.D + y
	 *
	 * index / D^2 = (index / D) / D = (z.D + y) / D = z + y / D = z
	 *
	 * And so: y = index / D - z.D
	 *
	 * Finally: x = index - z.D^2 - y.D
	 *
	 * (x, y, z) are unique for a given index, and we found their value.
	 */
	public final int getIndex(int x, int y, int z) {
		return (x + WorldObjectTerrain.DIMX * (y + WorldObjectTerrain.DIMY * z));
	}

	public final int getIndex(int xyz[]) {
		return (this.getIndex(xyz[0], xyz[1], xyz[2]));
	}

	public final int getXFromIndex(int index) {
		int z = this.getZFromIndex(index);
		return (this.getXFromIndex(index, this.getYFromIndex(index, z), z));
	}

	public final int getXFromIndex(int index, int y, int z) {
		return (index - WorldObjectTerrain.DIMX * (y + WorldObjectTerrain.DIMY * z));
	}

	public final int getYFromIndex(int index) {
		return (this.getYFromIndex(index, this.getZFromIndex(index)));
	}

	public final int getYFromIndex(int index, int z) {
		return (index / WorldObjectTerrain.DIMX - WorldObjectTerrain.DIMY * z);
	}

	public final int getZFromIndex(int index) {
		return (index / (WorldObjectTerrain.DIMX * DIMY));
	}

	public final int[] getPosFromIndex(int index) {
		int xyz[] = new int[3];
		xyz[2] = this.getZFromIndex(index);
		xyz[1] = this.getYFromIndex(index, xyz[2]);
		xyz[0] = this.getXFromIndex(index, xyz[1], xyz[2]);
		return (xyz);
	}

	/**
	 * thoses functions return the left, right, top, bot, front or back terrain to
	 * this one
	 */
	public WorldObjectTerrain getNeighbor(int id) {

		if (this.getWorld() == null) {
			return (null);
		}

		int x = this.worldIndex.x + Face.get(id).getVector().x;
		int y = this.worldIndex.y + Face.get(id).getVector().y;
		int z = this.worldIndex.z + Face.get(id).getVector().z;
		return (this.getWorld().getTerrain(x, y, z));
	}

	/** called when the terrain is added to the world */
	public final void onSpawned(World world) {
		this.setWorld(world);
		this.requestFaceVisibilityUpdate();
	}

	/** called before this terrain is generated by a world generator */
	public void preGenerated() {
	}

	/** called after this terrain is generated by a world generator */
	public void postGenerated() {
		// get the upper world terrain
		// if this terrain is the toppest, set sunlight to maximum for
		// toppest blocks
		for (int x = 0; x < WorldObjectTerrain.DIMX; x++) {
			for (int y = 0; y < WorldObjectTerrain.DIMY; y++) {
				// if this height is the toppest, set light value to max
				WorldFlatTerrainStorage terrainStorage = (WorldFlatTerrainStorage) this.getWorld().getTerrainStorage();
				WorldObjectTerrain terrain = terrainStorage.getTopestTerrainWithNonEmptyColumn(this.worldIndex, x, y);
				if (this == terrain) {
					int z = terrain.getHeightAt(x, y) + 1;
					if (z < WorldObjectTerrain.DIMZ) {
						terrain.addSunLight((byte) 15, x, y, z);
					}
				}

				// TODO : add light from differents terrain faces ?
			}
		}
	}

	/** called right before this terrain is spawned */
	public void preSpawned() {

	}

	/** called right after this terrain is spawned */
	public void postSpawned() {
	}

	/**
	 * @return the y coordinate of the first air block in this column, (0 if the
	 *         column is full of air, 16 if column is full of blocks)
	 */
	public int getHeightAt(int x, int y) {
		if (this.heightmap == null) {
			return (0);
		}
		return (this.heightmap[x + WorldObjectTerrain.DIMX * y]);
	}

	/**
	 * this function updates the visibility of each face toward another using a
	 * flood fill algorythm for each cell which werent already visited: - use flood
	 * fill algorythm, and register which faces are touched by the flood - for each
	 * of touched faces, set the visibility linked with the others touched
	 */

	/**
	 * this set the 'this.facesVisibility' bits to 1 if faces can be seen from
	 * another
	 * 
	 * This uses an explicit stack (to avoid stackoverflow in recursive)
	 **/
	private void updateFaceVisiblity() {

		this.setState(STATE_FACE_VISIBILTY_UP_TO_DATE);

		if (this.facesVisibility == null) {
			this.facesVisibility = new boolean[6][6];
		}

		// reset visibility
		for (Face a : Face.faces) {
			for (Face b : Face.faces) {
				this.facesVisibility[a.getID()][b.getID()] = false;
			}
		}

		// virtual stack
		Stack<Vector3i> stack = new Stack<Vector3i>();
		short[][][] flood = new short[WorldObjectTerrain.DIMX][WorldObjectTerrain.DIMY][WorldObjectTerrain.DIMZ];
		short color = 1;
		boolean[] touchedByFlood = new boolean[6];

		for (int x = 0; x < WorldObjectTerrain.DIMX; x++) {
			for (int y = 0; y < WorldObjectTerrain.DIMY; y++) {
				for (int z = 0; z < WorldObjectTerrain.DIMZ; z++) {
					if (this.getBlockAt(x, y, z).isTransparent() && flood[x][y][z] == 0) {
						for (int i = 0; i < 6; i++) {
							touchedByFlood[i] = false;
						}

						stack.push(new Vector3i(x, y, z));
						// this loop will empty the stack and propagate the
						// flood
						while (!stack.isEmpty()) {
							Vector3i pos = stack.pop();

							if (pos.x < 0) {
								touchedByFlood[Face.BACK] = true;
								continue;
							}

							if (pos.y < 0) {
								touchedByFlood[Face.LEFT] = true;
								continue;
							}

							if (pos.z < 0) {
								touchedByFlood[Face.BOT] = true;
								continue;
							}

							if (pos.x >= WorldObjectTerrain.DIMX) {
								touchedByFlood[Face.FRONT] = true;
								continue;
							}

							if (pos.y >= WorldObjectTerrain.DIMY) {
								touchedByFlood[Face.RIGHT] = true;
								continue;
							}

							if (pos.z >= WorldObjectTerrain.DIMZ) {
								touchedByFlood[Face.TOP] = true;
								continue;
							}

							if (this.getBlockAt(pos.x, pos.y, pos.z).isTransparent()
									|| flood[pos.x][pos.y][pos.z] != 0) {
								// hitted a full block
								continue;
							}

							flood[pos.x][pos.y][pos.z] = color;

							stack.push(new Vector3i(pos.x + 1, pos.y + 0, pos.z + 0));
							stack.push(new Vector3i(pos.x - 1, pos.y + 0, pos.z + 0));
							stack.push(new Vector3i(pos.x + 0, pos.y + 1, pos.z + 0));
							stack.push(new Vector3i(pos.x + 0, pos.y - 1, pos.z + 0));
							stack.push(new Vector3i(pos.x + 0, pos.y + 0, pos.z + 1));
							stack.push(new Vector3i(pos.x + 0, pos.y + 0, pos.z - 1));
						}

						for (int i = 0; i < 6; i++) {
							if (touchedByFlood[i]) {
								for (int j = 0; j < 6; j++) {
									if (touchedByFlood[j]) {
										this.facesVisibility[i][j] = true;
										this.facesVisibility[j][i] = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/** return true if the given faces id can be seen from another */
	public final boolean canFaceBeSeenFrom(int faceA, int faceB) {
		return (this.facesVisibility[faceA][faceB]);
	}

	public final boolean canFaceBeSeenFrom(Face faceA, Face faceB) {
		return (this.canFaceBeSeenFrom(faceA.getID(), faceB.getID()));
	}

	public final void destroy() {
		this.blocks = null;
		this.lights = null;
		this.blockCount = 0;
		if (this.blockInstances != null) {
			this.blockInstances.clear();
			this.blockInstances = null;
		}
		this.facesVisibility = null;
		this.onDestroyed();
	}

	protected void onDestroyed() {

	}

	/** return raw block data */
	public final short[] getRawBlocks() {
		return (this.blocks);
	}

	/** get the raw light map */
	public final byte[] getRawLights() {
		return (this.lights);
	}

	/**
	 * @return the number of non-air blocks set in this terrain
	 */
	public final int getBlockCount() {
		return (this.blockCount);
	}

	/**
	 * set the durability of a block
	 * 
	 * @param index
	 *            : block index
	 * @param durability
	 *            : durability value (in range of ([{@link #MIN_DURABILITY} ,
	 *            {@link #MAX_DURABILITY} ])
	 */
	public final void setDurabilityAt(int index, byte durability) {
		if (this.durability == null) {
			if (this.blocks == null) {
				return;
			}
			this.durability = new byte[WorldObjectTerrain.MAX_BLOCK_INDEX];
		}
		byte old = this.durability[index];
		this.durability[index] = durability < WorldObjectTerrain.MIN_DURABILITY ? WorldObjectTerrain.MIN_DURABILITY
				: durability > WorldObjectTerrain.MAX_DURABILITY ? WorldObjectTerrain.MAX_DURABILITY : durability;
		this.invokeEvent(new EventTerrainDurabilityChanged(this, old, index));
	}

	public final void setDurabilityAt(int x, int y, int z, byte durability) {
		this.setDurabilityAt(this.getIndex(x, y, z), durability);
	}

	public final void setDurability(int x, int y, int z, byte durability) {
		this.setDurability(new int[] { x, y, z }, durability);
	}

	public final void setDurability(int[] xyz, byte durability) {
		WorldObjectTerrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return;
		}
		terrain.setDurabilityAt(xyz[0], xyz[1], xyz[2], durability);
	}

	/** decrease the durabiltiy of a block */
	public final void decreaseDurability(int index) {
		if (this.durability == null) {
			return;
		}
		this.setDurabilityAt(index, (byte) (this.durability[index] + 1));
	}

	/** increase the durabiltiy of a block */
	public final void increaseDurability(int index) {
		if (this.durability == null) {
			return;
		}
		this.setDurabilityAt(index, (byte) (this.durability[index] - 1));
	}

	/**
	 * get the durability of the block at given address
	 * 
	 * @param index
	 * @return
	 */
	public final byte getDurabilityAt(int index) {
		if (this.durability == null) {
			return (WorldObjectTerrain.MIN_DURABILITY);
		}
		return (this.durability[index]);
	}

	public final byte getDurabilityAt(int x, int y, int z) {
		return (this.getDurabilityAt(this.getIndex(x, y, z)));
	}

	public final byte getDurability(int xyz[]) {
		WorldObjectTerrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (WorldObjectTerrain.MIN_DURABILITY);
		}
		return (terrain.getDurabilityAt(xyz[0], xyz[1], xyz[2]));
	}

	public final byte getDurability(int x, int y, int z) {
		return (this.getDurability(new int[] { x, y, z }));
	}

	@Override
	public float getPositionX() {
		return (this.worldPos.x);
	}

	@Override
	public float getPositionY() {
		return (this.worldPos.y);
	}

	@Override
	public float getPositionZ() {
		return (this.worldPos.z);
	}

	@Override
	public float getPositionVelocityX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPositionVelocityY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPositionVelocityZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPositionAccelerationX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPositionAccelerationY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPositionAccelerationZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPositionX(float x) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionY(float y) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionZ(float z) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionVelocityX(float vx) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionVelocityY(float vy) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionVelocityZ(float vz) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionAccelerationX(float ax) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionAccelerationY(float ay) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setPositionAccelerationZ(float az) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public float getRotationX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationVelocityX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationVelocityY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationVelocityZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationAccelerationX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationAccelerationY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotationAccelerationZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRotationX(float x) {
		throw new UnsupportedOperationException("Cannot rotate terrain yet!");
	}

	@Override
	public void setRotationY(float y) {
		throw new UnsupportedOperationException("Cannot rotate terrain yet!");
	}

	@Override
	public void setRotationZ(float z) {
		throw new UnsupportedOperationException("Cannot rotate terrain yet!");
	}

	@Override
	public void setRotationVelocityX(float vx) {
		throw new UnsupportedOperationException("Cannot rotate terrain yet!");
	}

	@Override
	public void setRotationVelocityY(float vy) {
		throw new UnsupportedOperationException("Cannot rotate terrain yet!");
	}

	@Override
	public void setRotationVelocityZ(float vz) {
		throw new UnsupportedOperationException("Cannot rotate terrain yet!");
	}

	@Override
	public void setRotationAccelerationX(float ax) {
		throw new UnsupportedOperationException("Cannot move terrain yet!");
	}

	@Override
	public void setRotationAccelerationY(float ay) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRotationAccelerationZ(float az) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getSizeX() {
		return (WorldObjectTerrain.DIMX_SIZE);
	}

	@Override
	public float getSizeY() {
		return (WorldObjectTerrain.DIMY_SIZE);
	}

	@Override
	public float getSizeZ() {
		return (WorldObjectTerrain.DIMZ_SIZE);
	}

	@Override
	public float getSizeVelocityX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSizeVelocityY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSizeVelocityZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSizeAccelerationX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSizeAccelerationY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSizeAccelerationZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSizeX(float x) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeY(float y) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeZ(float z) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeVelocityX(float vx) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeVelocityY(float vy) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeVelocityZ(float vz) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeAccelerationX(float ax) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeAccelerationY(float ay) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public void setSizeAccelerationZ(float az) {
		throw new UnsupportedOperationException("Cannot resize terrain yet!");
	}

	@Override
	public float getMass() {
		return (this.mass);
	}

	@Override
	public void setMass(float mass) {
		throw new UnsupportedOperationException("Cannot set mass explicitly!");
	}

	/**
	 * recalculate the mass of this terrain (to be called when raw blocks are
	 * modified explicitly)
	 */
	public final void calculateMass() {
		if (this.blocks == null) {
			return;
		}
		this.mass = 0.0f;
		for (short blockID : this.blocks) {
			this.mass += Blocks.getBlockByID(blockID).getMass();
		}
	}
}