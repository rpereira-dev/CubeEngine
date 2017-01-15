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
import java.util.Arrays;
import java.util.Stack;

import com.grillecube.engine.Logger;
import com.grillecube.engine.faces.Face;
import com.grillecube.engine.maths.Maths;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.resources.Compress;
import com.grillecube.engine.world.block.Block;
import com.grillecube.engine.world.block.BlockLight;
import com.grillecube.engine.world.block.Blocks;
import com.grillecube.engine.world.block.instances.BlockInstance;

public class Terrain {
	/** terrain states */
	public static final int STATE_VERTICES_UP_TO_DATE = 1 << 0;
	public static final int STATE_LIGHT_UP_TO_DATE = 1 << 1;
	public static final int STATE_BLOCK_COMPRESSED = 1 << 2;
	public static final int STATE_LIGHT_COMPRESSED = 1 << 3;
	public static final int STATE_FACE_VISIBILTY_UP_TO_DATE = 1 << 4;

	/** terrain dimensions */
	public static final float BLOCK_SIZE = 1.0f; // block size unit , maybe
													// remove it
	// (and use 1 as implicit value to
	// optimize calculations)
	public static final float BLOCK_DEMI_SIZE = BLOCK_SIZE / 2.0f;

	public static Vector3f BLOCK_SIZE_VEC = new Vector3f(BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

	public static final int DIM = 16; // terrain is 16x16x16
	public static final int DIM2 = DIM * DIM;
	public static final int DIM3 = DIM * DIM * DIM;
	public static final int DEMI_DIM = DIM / 2;
	public static final int MAX_BLOCK_INDEX = Terrain.DIM * Terrain.DIM * Terrain.DIM;
	public static final float SIZE_DIAGONAL = (float) Vector3f.distance(new Vector3f(0, 0, 0),
			new Vector3f(DIM * BLOCK_SIZE, DIM * BLOCK_SIZE, DIM * BLOCK_SIZE));

	public static float DIM_SIZE = DIM * BLOCK_SIZE;
	public static Vector3f TERRAIN_SIZE = new Vector3f(DIM_SIZE, DIM_SIZE, DIM_SIZE);
	public static float DEMI_SIZE_DIAGONAL = SIZE_DIAGONAL / 2.0f;
	public static float DEMI_DIM_SIZE = DIM_SIZE / 2.0f;

	/** neighboors */
	private Terrain[] _neighboors;

	/** terrain location */
	protected TerrainLocation _location;

	/** block instances */
	private ArrayList<BlockInstance> _block_instances;

	/** block ids */
	protected short[] _blocks;

	/** number of blocks visible */
	private short _visible_blocks;

	/** blocks */
	private ArrayList<BlockLightData> _lights_data;
	protected byte[] _lights; // light values for every blocks

	/** world in which this terrain depends */
	private World _world;

	private int _state;

	/** which face can see another */
	private boolean[][] _faces_visibility;

	public Terrain(int x, int y, int z) {
		this(new Vector3i(x, y, z));
	}

	public Terrain(Vector3i index) {
		this(new TerrainLocation(index));
	}

	public Terrain(TerrainLocation location) {
		this._location = location;
		this._blocks = null;
		this._block_instances = null;
		this._lights = null;
		this._lights_data = null;
		this._neighboors = new Terrain[6];
		this._faces_visibility = new boolean[6][6];
		for (Face a : Face.faces) {
			for (Face b : Face.faces) {
				this._faces_visibility[a.getID()][b.getID()] = true;
			}
		}
		this.setState(STATE_FACE_VISIBILTY_UP_TO_DATE);
		this.setState(STATE_LIGHT_UP_TO_DATE);
		this.setState(STATE_VERTICES_UP_TO_DATE);
	}

	/******
	 * EVERY TERRAINS UPDATE RELATIVE FUNCTIONS (COMPLEX ALGORYTHM) ARE CALLED
	 * IN A SEPARATE THREAD. STARTS HERE
	 */
	/** update the terrain once */
	public void update() {

		this.checkFaceVisibility();
		this.checkLights();
		this.updateBlockInstances();
		this.updateBlocks();
	}

	private void checkFaceVisibility() {

		if (!this.hasState(Terrain.STATE_FACE_VISIBILTY_UP_TO_DATE)) {
			this.updateFaceVisiblity();
		}
	}

	private void checkLights() {
		// if lights arent up to date
		if (!this.hasState(Terrain.STATE_LIGHT_UP_TO_DATE)) {

			// if no light, return
			if (this._lights_data == null) {
				this.setState(Terrain.STATE_LIGHT_UP_TO_DATE);
				return;
			}

			// we get every terrains linked by lights (which means light of each
			// affect an other terrain lighting
			ArrayList<Terrain> affected = new ArrayList<Terrain>();
			this.getTerrainsLinkedByLights(affected);

			// for every terrain affected, reset their lights
			for (Terrain terrain : affected) {
				if (terrain._lights == null) {
					continue;
				}
				Arrays.fill(terrain._lights, BlockLight.MIN_LIGHT_VALUE);
			}

			// for each lights of each terrains, apply flood filling algorythm
			// to update lighting
			for (Terrain terrain : affected) {
				if (terrain._lights_data != null) {
					for (BlockLightData light : terrain._lights_data) {
						terrain.floodFillLight(light.x, light.y, light.z, light.lightvalue, light.lightvalue);
					}
				}
				terrain.setState(Terrain.STATE_LIGHT_UP_TO_DATE);
				terrain.requestMeshUpdate();
			}
		}
	}

	private void getTerrainsLinkedByLights(ArrayList<Terrain> affected) {
		affected.add(this);
	}

	/**
	 * flood fill the light to find affected terrain and add them to the
	 * 'affected' list
	 */
	private void floodFillLightLinkedTerrain(ArrayList<Terrain> affected, boolean[][][] map, int x, int y, int z,
			byte maxlightvalue, byte lightvalue) {
		int ix = x + maxlightvalue;
		int iy = y + maxlightvalue;
		int iz = z + maxlightvalue;

		if (map[ix][iy][iz]) {
			return;
		}

		map[ix][iy][iz] = true;

		Terrain terrain = this.getRelativeTerrain(x, y, z);
		if (terrain == null) {
			return;
		}

		if (!affected.contains(terrain)) {
			affected.add(terrain);
		}

		Block block = this.getBlock(x, y, z);
		if (block.isOpaque() && lightvalue != maxlightvalue) {
			return;
		}

		--lightvalue;
		if (lightvalue <= 0) {
			return;
		}

		this.floodFillLightLinkedTerrain(affected, map, x + 1, y + 0, z + 0, maxlightvalue, lightvalue);
		this.floodFillLightLinkedTerrain(affected, map, x - 1, y + 0, z + 0, maxlightvalue, lightvalue);

		this.floodFillLightLinkedTerrain(affected, map, x + 0, y + 1, z + 0, maxlightvalue, lightvalue);
		this.floodFillLightLinkedTerrain(affected, map, x + 0, y - 1, z + 0, maxlightvalue, lightvalue);

		this.floodFillLightLinkedTerrain(affected, map, x + 0, y + 0, z + 1, maxlightvalue, lightvalue);
		this.floodFillLightLinkedTerrain(affected, map, x + 0, y + 0, z - 1, maxlightvalue, lightvalue);

	}

	/** fill the map with by expanding the light value */
	private void floodFillLight(int x, int y, int z, byte maxlightvalue, byte lightvalue) {

		// block is already in the light
		byte value = (byte) (lightvalue / (float) maxlightvalue * BlockLight.MAX_LIGHT_VALUE);
		if (!this.updateLightValueAt(value, x, y, z)) {
			return;
		}

		Block block = this.getBlock(x, y, z);
		if (block.isOpaque() && lightvalue != maxlightvalue) {
			return;
		}

		--lightvalue;
		if (lightvalue <= 0) {
			return;
		}

		this.floodFillLight(x + 1, y + 0, z + 0, maxlightvalue, lightvalue);
		this.floodFillLight(x - 1, y + 0, z + 0, maxlightvalue, lightvalue);

		this.floodFillLight(x + 0, y + 1, z + 0, maxlightvalue, lightvalue);
		this.floodFillLight(x + 0, y - 1, z + 0, maxlightvalue, lightvalue);

		this.floodFillLight(x + 0, y + 0, z + 1, maxlightvalue, lightvalue);
		this.floodFillLight(x + 0, y + 0, z - 1, maxlightvalue, lightvalue);
	}

	/**
	 * coordinates are relative to the current terrain instance, if the current
	 * light value is < the given one, then the given one is set
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param lightvalue
	 * @return
	 */
	private boolean updateLightValueAt(byte lightvalue, int... xyz) {
		Terrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (false);
		}
		int index = terrain.getIndex(xyz);
		if (terrain.getLightAt(index) < lightvalue) {
			terrain.setLightAt(lightvalue, index);
			return (true);
		}
		return (false);
	}

	/** tick once block instances of this terrain */
	private void updateBlockInstances() {
		if (this._block_instances == null) {
			return;
		}

		for (int i = this._block_instances.size() - 1; i >= 0; i--) {
			BlockInstance instance = this._block_instances.get(i);
			if (instance == null) {
				continue;
			}
			instance.update();
		}
	}

	/** tick once every blocks of this terrain */
	private void updateBlocks() {

		if (this._blocks == null) {
			return;
		}

		int index = Maths.abs(this.getWorld().getRNG().nextInt()) % this._blocks.length;
		short block = this._blocks[index];

		int z = this.getZFromIndex(index);
		int y = this.getYFromIndex(index, z);
		int x = this.getXFromIndex(index, y, z);

		Blocks.getBlockByID(block).update(this, x, y, z);
	}

	/******
	 * EVERY TERRAINS UPDATE RELATIVE FUNCTIONS (COMPLEX ALGORYTHM) ARE CALLED
	 * IN A SEPARATE THREAD. END HERE
	 */

	public boolean hasState(int state) {
		return ((this._state & state) == state);
	}

	public void setState(int state) {
		this._state = this._state | state;
	}

	public void unsetState(int state) {
		this._state = this._state & ~(state);
	}

	public void switchState(int state) {
		this._state = this._state ^ state;
	}

	/**
	 * return the terrain at the given position, relative to this instance It
	 * fills the 'dst' Vector3i with it terrain-relative coordinates and return
	 * it
	 * 
	 * if the terrain didnt exists, it creates it
	 */
	private Terrain getRelativeTerrain(int... xyz) {
		Terrain terrain = this;

		// x test
		if (xyz[0] < 0) {
			do {
				xyz[0] += Terrain.DIM;
				terrain = terrain.getNeighbor(Face.FRONT);
			} while (xyz[0] < 0);
			if (terrain == null) {
				return (null);
			}
		} else if (xyz[0] >= Terrain.DIM) {
			do {
				xyz[0] -= Terrain.DIM;
				terrain = terrain.getNeighbor(Face.BACK);
			} while (xyz[0] >= Terrain.DIM);
			if (terrain == null) {
				return (null);
			}
		}

		// y test
		if (xyz[1] < 0) {
			do {
				xyz[1] += Terrain.DIM;
				terrain = terrain.getNeighbor(Face.BOT);
			} while (xyz[1] < 0);
			if (terrain == null) {
				return (null);
			}
		} else if (xyz[1] >= Terrain.DIM) {
			do {
				xyz[1] -= Terrain.DIM;
				terrain = terrain.getNeighbor(Face.TOP);
			} while (xyz[1] >= Terrain.DIM);
			if (terrain == null) {
				return (null);
			}
		}

		// z test
		if (xyz[2] < 0) {
			do {
				xyz[2] += Terrain.DIM;
				terrain = terrain.getNeighbor(Face.LEFT);
			} while (xyz[2] < 0);
			if (terrain == null) {
				return (null);
			}
		} else if (xyz[2] >= Terrain.DIM) {
			do {
				xyz[2] -= Terrain.DIM;
				terrain = terrain.getNeighbor(Face.RIGHT);
			} while (xyz[2] >= Terrain.DIM);
			if (terrain == null) {
				return (null);
			}
		}
		return (terrain);
	}

	public Block getBlock(int... xyz) {
		Terrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (Blocks.AIR);
		}
		return (terrain.getBlockAt(xyz));
	}

	/** this function isnt protected against overflow / underflow */
	public Block getBlockAt(int... xyz) {
		if (this._blocks == null) {
			return (Blocks.AIR);
		}
		return (Blocks.getBlockByID(this._blocks[this.getIndex(xyz)]));
	}

	/** secure function to set a block relative to this terrain */
	public BlockInstance setBlock(Block block, int... xyz) {
		Terrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (null);
		}
		return (terrain.setBlockAt(block, xyz));
	}

	/** secure function to set a block relative to this terrain */
	public void setBlock(Block block, Vector3i pos) {
		this.setBlock(block, pos.x, pos.y, pos.z);
	}

	/** function to set a block to this terrain */
	public BlockInstance setBlockAt(Block block, int... xyz) {

		// extract values
		int x = xyz[0];
		int y = xyz[1];
		int z = xyz[2];

		// if terrain was empty
		if (this._blocks == null) {
			// initialize it, fill it with air
			this._blocks = new short[Terrain.MAX_BLOCK_INDEX];
			Arrays.fill(this._blocks, (byte) Blocks.AIR_ID);
		}

		// get the index for the 1D block array
		int index = this.getIndex(x, y, z);
		// get the previous block in this location
		Block prevblock = Blocks.getBlockByID(this._blocks[index]);

		// unset the previous block
		prevblock.onUnset(this, x, y, z);

		// get the previous instance at this location, and remove it
		BlockInstance previnstance = this.popBlockInstance(x, y, z);

		// if there was a block instance
		if (previnstance != null) {
			// unset callback for this instance
			previnstance.onUnset();
		}

		// set the new block
		this._blocks[index] = block.getID();
		// set callback
		block.onSet(this, x, y, z);

		// update number of block set
		if (prevblock.isVisible() && !block.isVisible()) {
			--this._visible_blocks;
		} else if (!prevblock.isVisible() && block.isVisible()) {
			++this._visible_blocks;
		}

		// get a new block instance for this new block
		BlockInstance instance = block.createBlockInstance(this, x, y, z);
		// if this block actually have (need) an instance
		if (instance != null) {

			// initialiaze the instance list if needed. (i.e if it is the first
			// block instance for this terrain)
			if (this._block_instances == null) {
				this._block_instances = new ArrayList<BlockInstance>(1);
			}
			// add the instance to the list
			this._block_instances.add(instance);
			// instance set calback
			instance.onSet();
		}

		return (instance);
	}

	/** remove and return the block instance at the given location */
	private BlockInstance popBlockInstance(int x, int y, int z) {
		if (this._block_instances == null) {
			return (null);
		}

		for (int i = this._block_instances.size() - 1; i >= 0; i--) {
			BlockInstance instance = this._block_instances.get(i);
			if (instance == null) {
				continue;
			}
			if (instance.getX() == x && instance.getY() == y && instance.getZ() == z) {
				return (this._block_instances.remove(i));
			}
		}
		return (null);
	}

	/**
	 * get the block instance at the given location (relative to the terrain)
	 */
	public BlockInstance getBlockInstance(int x, int y, int z) {
		for (int i = this._block_instances.size() - 1; i >= 0; i--) {
			BlockInstance instance = this._block_instances.get(i);
			if (instance == null) {
				continue;
			}
			if (instance.getX() == x && instance.getY() == y && instance.getZ() == z) {
				return (instance);
			}
		}
		return (null);
	}

	/** add a light to the terrain list (do not update it!) */
	public void addLight(byte lightvalue, int x, int y, int z) {
		if (this._lights_data == null) {
			this._lights_data = new ArrayList<BlockLightData>(4);
		}
		BlockLightData light = new BlockLightData(lightvalue, x, y, z);
		this._lights_data.add(light);
	}

	/** remove a light from the terrain (do not update it!) */
	public void removeLight(int x, int y, int z) {
		for (int i = 0; i < this._lights_data.size(); i++) {
			BlockLightData light = this._lights_data.get(i);
			if (light.x == x && light.y == y && light.z == z) {
				this._lights_data.remove(i);
				break;
			}
		}
		if (this._lights_data.size() == 0) {
			this._lights_data = null;
		}
	}

	@Override
	public int hashCode() {
		return (this._location.hashCode() + this._world.hashCode());
	}

	public void requestLightUpdate() {
		this.unsetState(Terrain.STATE_LIGHT_UP_TO_DATE);
	}

	public void requestFaceVisibilityUpdate() {
		this.unsetState(Terrain.STATE_FACE_VISIBILTY_UP_TO_DATE);
	}

	public byte getLight(Vector3i pos) {
		return (this.getLight(pos.x, pos.y, pos.z));
	}

	/**
	 * return the light at given terrain relative coordinates, without checking
	 * bounds
	 */
	public byte getLightAt(Vector3i pos) {
		return (this.getLightAt(pos.x, pos.y, pos.z));
	}

	/**
	 * return the light at given terrain relative coordinates, without checking
	 * bounds
	 */
	public byte getLightAt(int... xyz) {
		return (this.getLightAt(this.getIndex(xyz)));
	}

	public byte getLightAt(int index) {
		if (this._lights == null) {
			return (BlockLight.MIN_LIGHT_VALUE);
		}
		return (this._lights[index]);
	}

	public void setLightAt(byte lightvalue, int... xyz) {
		this.setLightAt(lightvalue, this.getIndex(xyz));
	}

	public void setLightAt(byte lightvalue, int index) {
		if (this._lights == null) {
			this._lights = new byte[Terrain.MAX_BLOCK_INDEX];
		}
		this._lights[index] = lightvalue;
	}

	/** return terrain location */
	public TerrainLocation getLocation() {
		return (this._location);
	}

	public Vector3f getWorldPosition() {
		return (this._location.getWorldPosition());
	}

	public Vector3i getWorldIndex() {
		return (this._location.getWorldIndex());
	}

	@Override
	public String toString() {
		return ("Terrain: " + this.getLocation());
	}

	/**
	 * non overflow proof:
	 * 
	 * let D = Terrain.DIM
	 * 
	 * 0 <= x <= D - 1 0 <= y <= D - 1 0 <= z <= D - 1
	 *
	 * => 0 <= x + D * (y + z.D) <= (D - 1) + D . ((D - 1) + D . (D - 1)) => 0
	 * <= index <= (D - 1) + D . ((D - 1) + D^2 - D)) => 0 <= index <= (D - 1) +
	 * D . (D^2 - 1) => 0 <= index <= (D - 1) + D^3 - D) => 0 <= index <= D^3 -
	 * 1 => 0 <= index <= this._blocks.length : OK
	 *
	 * unicity proof: index = x + D * (y + D * z) = x + y.D + z.D^2 = z.D^2 +
	 * y.D + x
	 *
	 * if x < D, then x / D = 0 (we are doing division using integers). Then we
	 * know that:
	 *
	 * index / D = (z.D^2 + y.D + x) / D = z.D + y + x / D = z.D + y + 0 = z.D +
	 * y
	 *
	 * index / D^2 = (index / D) / D = (z.D + y) / D = z + y / D = z
	 *
	 * And so: y = index / D - z.D
	 *
	 * Finally: x = index - z.D^2 - y.D
	 *
	 * (x, y, z) are unique for a given index, and we found their value.
	 */
	public int getIndex(int... xyz) {
		return (xyz[0] + Terrain.DIM * (xyz[1] + Terrain.DIM * xyz[2]));
	}

	public int getXFromIndex(int index) {
		int z = this.getZFromIndex(index);
		return (this.getXFromIndex(index, this.getYFromIndex(index, z), z));
	}

	public int getXFromIndex(int index, int y, int z) {
		return (index - Terrain.DIM * (y + Terrain.DIM * z));
	}

	public int getYFromIndex(int index) {
		return (this.getYFromIndex(index, this.getZFromIndex(index)));
	}

	public int getYFromIndex(int index, int z) {
		return (index / Terrain.DIM - Terrain.DIM * z);
	}

	public int getZFromIndex(int index) {
		return (index / Terrain.DIM2);
	}

	/**
	 * thoses functions return the left, right, top, bot, front or back terrain
	 * to this one
	 */
	public Terrain getNeighbor(int id) {
		Terrain neighbor = this._neighboors[id];
		if (neighbor != null) {
			return (neighbor);
		}

		if (this._world == null) {
			return (null);
		}

		Vector3i index = this.getLocation().getWorldIndex();
		int x = index.x + Face.get(id).getVector().x;
		int y = index.y + Face.get(id).getVector().y;
		int z = index.z + Face.get(id).getVector().z;
		return (this._world.getTerrain(x, y, z));
	}

	/** return terrain center world position */
	public Vector3f getCenter() {
		return (this._location.getCenter());
	}

	public Terrain[] getNeighboors() {
		return (this._neighboors);
	}

	/** called when the terrain is added to the world */
	public final void onSpawned(World world) {
		this.setWorld(world);
		this.updateNeighboors(world);
		this.onGenerated(world);
		this.requestUpdateAll();
	}

	public void requestUpdateAll() {
		this.requestFaceVisibilityUpdate();
		this.requestLightUpdate();
		this.requestMeshUpdate();
	}

	public void onGenerated(World world) {

	}

	public void setWorld(World world) {
		this._world = world;
	}

	public World getWorld() {
		return (this._world);
	}

	/** update Neighboors terrain */
	public void updateNeighboors(World world) {
		for (Face face : Face.values()) {
			Vector3i index = Vector3i.add(this._location.getWorldIndex(), face.getVector());
			Terrain terrain = world.getTerrain(index);

			if (terrain != null) {
				terrain.setNeighboor(this, face.getOpposite());
			}
			this.setNeighboor(terrain, face);
		}
	}

	private void setNeighboor(Terrain terrain, Face face) {
		this._neighboors[face.getID()] = terrain;
	}

	/**
	 * return max height at given index for this terrain (relative to the
	 * terrain position)
	 */
	public int getHeightAt(int x, int z) {
		int y = 15;

		while (this.getBlockAt(x, y, z) == Blocks.AIR) {
			--y;
			if (y == -1) {
				return (-1);
			}
		}
		return (y);
	}

	/**
	 * this function updates the visibility of each face toward another using a
	 * flood fill algorythm for each cell which werent already visited: - use
	 * flood fill algorythm, and register which faces are touched by the flood -
	 * for each of touched faces, set the visibility linked with the others
	 * touched
	 */

	/**
	 * this set the 'this._faces_visibility' bits to 1 if faces can be seen from
	 * another
	 * 
	 * This uses an explicit stack (to avoid stackoverflow in recursive)
	 **/
	private void updateFaceVisiblity() {

		this.setState(STATE_FACE_VISIBILTY_UP_TO_DATE);

		if (this._faces_visibility == null) {
			this._faces_visibility = new boolean[6][6];
		}

		// reset visibility
		for (Face a : Face.faces) {
			for (Face b : Face.faces) {
				this._faces_visibility[a.getID()][b.getID()] = false;
			}
		}

		// virtual stack
		Stack<Vector3i> stack = new Stack<Vector3i>();
		short[][][] flood = new short[Terrain.DIM][Terrain.DIM][Terrain.DIM];
		short color = 1;
		boolean[] touched_by_flood = new boolean[6];

		for (int x = 0; x < Terrain.DIM; x++) {
			for (int y = 0; y < Terrain.DIM; y++) {
				for (int z = 0; z < Terrain.DIM; z++) {
					if (!this.getBlockAt(x, y, z).isOpaque() && flood[x][y][z] == 0) {
						for (int i = 0; i < 6; i++) {
							touched_by_flood[i] = false;
						}

						stack.push(new Vector3i(x, y, z));
						// this loop will empty the stack and propagate the
						// flood
						while (!stack.isEmpty()) {
							Vector3i pos = stack.pop();

							if (pos.x < 0) {
								touched_by_flood[Face.BACK] = true;
								continue;
							}

							if (pos.y < 0) {
								touched_by_flood[Face.BOT] = true;
								continue;
							}

							if (pos.z < 0) {
								touched_by_flood[Face.LEFT] = true;
								continue;
							}

							if (pos.x >= Terrain.DIM) {
								touched_by_flood[Face.FRONT] = true;
								continue;
							}

							if (pos.y >= Terrain.DIM) {
								touched_by_flood[Face.TOP] = true;
								continue;
							}

							if (pos.z >= Terrain.DIM) {
								touched_by_flood[Face.RIGHT] = true;
								continue;
							}

							if (this.getBlockAt(pos.x, pos.y, pos.z).isOpaque() || flood[pos.x][pos.y][pos.z] != 0) {
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
							if (touched_by_flood[i]) {
								for (int j = 0; j < 6; j++) {
									if (touched_by_flood[j]) {
										this._faces_visibility[i][j] = true;
										this._faces_visibility[j][i] = true;
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
	public boolean canFaceBeSeenFrom(int faceA, int faceB) {
		return (this._faces_visibility[faceA][faceB]);
	}

	public boolean canFaceBeSeenFrom(Face faceA, Face faceB) {
		return (this.canFaceBeSeenFrom(faceA.getID(), faceB.getID()));
	}

	public void destroy() {
		this._blocks = null;
		this._lights = null;
		this._visible_blocks = 0;
		if (this._block_instances != null) {
			this._block_instances.clear();
			this._block_instances = null;
		}
		this._faces_visibility = null;

		if (this._lights_data != null) {
			this._lights_data.clear();
			this._lights_data = null;
		}
	}

	/** request a rebuild of this terrain mesh */
	public void requestMeshUpdate() {
		this.unsetState(Terrain.STATE_VERTICES_UP_TO_DATE);
	}

	/** request a rebuild of this terrain neighboors meshes */
	public void requestNeighboorsMeshUpdate() {
		for (Terrain neighbor : this.getNeighboors()) {
			if (neighbor != null) {
				neighbor.requestMeshUpdate();
			}
		}
	}

	public class BlockLightData {
		public byte lightvalue;
		public int x;
		public int y;
		public int z;

		public BlockLightData(byte lightvalue, int x, int y, int z) {
			this.lightvalue = lightvalue;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public byte getLight() {
			return (this.lightvalue);
		}
	}

	/**
	 * this function return the light value at the given coordinates, relative
	 * to the terrain.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the light value
	 */
	public byte getLight(int... xyz) {
		Terrain terrain = this.getRelativeTerrain(xyz);
		if (terrain == null) {
			return (Block.MIN_LIGHT_VALUE);
		}
		return (terrain.getLightAt(xyz));
	}

	/** return raw block data */
	public short[] getRawBlocks() {
		return (this._blocks);
	}

	public byte[] getRawLights() {
		return (this._lights);
	}

	/** a simple compressing algorythm */
	public void compress() {

		if (!this.hasState(STATE_BLOCK_COMPRESSED)) {
			short[] blocks = Compress.compressShortArray(this._blocks);
			if (blocks != this._blocks) {
				this.setState(STATE_BLOCK_COMPRESSED);
			}
		}

		if (!this.hasState(STATE_LIGHT_COMPRESSED)) {
			byte[] lights = Compress.compressByteArray(this._lights);
			if (lights != this._lights) {
				this.setState(STATE_LIGHT_COMPRESSED);
			}
		}
	}

	public void decompress() {

		if (this.hasState(STATE_BLOCK_COMPRESSED)) {
			short[] blocks = Compress.decompressShortArray(this._blocks, Terrain.MAX_BLOCK_INDEX);
			if (blocks != this._blocks) {
				this.unsetState(STATE_BLOCK_COMPRESSED);
			}
		}

		if (this.hasState(STATE_LIGHT_COMPRESSED)) {
			byte[] lights = Compress.decompressByteArray(this._lights, Terrain.MAX_BLOCK_INDEX);
			if (lights != this._lights) {
				this.unsetState(STATE_LIGHT_COMPRESSED);
			}
		}
	}

	public int getVisibleBlocksCount() {
		return (this._visible_blocks);
	}
}