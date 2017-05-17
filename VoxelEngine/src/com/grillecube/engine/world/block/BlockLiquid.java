package com.grillecube.engine.world.block;

import java.util.Stack;

import com.grillecube.engine.faces.Face;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.renderer.world.terrain.MeshVertex;
import com.grillecube.engine.renderer.world.terrain.TerrainMesher;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.block.instances.BlockInstance;
import com.grillecube.engine.world.block.instances.BlockInstanceLiquid;

public abstract class BlockLiquid extends Block {
	public BlockLiquid(int blockID) {
		super(blockID, Blocks.T_LIQUID);
	}

	@Override
	public abstract String getName();

	@Override
	public boolean isVisible() {
		return (true);
	}

	@Override
	public boolean isOpaque() {
		return (false);
	}

	@Override
	public boolean influenceAO() {
		return (false);
	}

	/** a liquid block need it own instance */
	public BlockInstance createBlockInstance(Terrain terrain, short index) {
		return (new BlockInstanceLiquid(terrain, this, index));
	}

	@Override
	public void update(Terrain terrain, int x, int y, int z) {
	}

	public void onSet(Terrain terrain, int x, int y, int z) {
	}

	public void onUnset(Terrain terrain, int x, int y, int z) {
	}

	@Override
	public void pushVertices(TerrainMesher mesher, Terrain terrain, Stack<MeshVertex> stack, int x, int y, int z) {
		for (Face face : Face.values()) // for each of it face
		{
			Vector3i vec = face.getVector();
			// get the neighbor of this face
			Block neighbor = terrain.getBlock(x + vec.x, y + vec.y, z + vec.z);

			// if the face-neighboor block is invisible or non opaque
			if (!neighbor.isVisible() || !neighbor.isOpaque()) {
				// then add the face
				this.pushFaceVertices(mesher, terrain, neighbor, stack, x, y, z, face);
			}
		}
	}

	/** push vertices for a liquid */
	private void pushFaceVertices(TerrainMesher mesher, Terrain terrain, Block neighbor, Stack<MeshVertex> stack, int x,
			int y, int z, Face face) {

		// get the instance for this block
		BlockInstanceLiquid instance = (BlockInstanceLiquid) terrain.getBlockInstance(x, y, z);
		if (instance == null) {
			return;
		}

		// check neighbor, and do not render invisible faces
		int nx = x + face.getVector().x;
		int ny = y + face.getVector().y;
		int nz = z + face.getVector().z;

		if (neighbor instanceof BlockLiquid) {
			BlockInstanceLiquid other = (BlockInstanceLiquid) terrain.getBlockInstance(nx, ny, nz);
			if (other != null && instance.getAmount() < other.getAmount()) {
				return;
			}
		}

		// get every vertices as a standart cube
		MeshVertex v0 = mesher.getBlockFaceVertex(terrain, this, face, 0, x, y, z);
		MeshVertex v1 = mesher.getBlockFaceVertex(terrain, this, face, 1, x, y, z);
		MeshVertex v2 = mesher.getBlockFaceVertex(terrain, this, face, 2, x, y, z);
		MeshVertex v3 = mesher.getBlockFaceVertex(terrain, this, face, 3, x, y, z);

		// offset the standart vertices to create flowing effect

		float unit = 1 - instance.getAmount() * BlockInstanceLiquid.LIQUID_HEIGHT_UNIT;

		if (instance.getBlockUnder() != Blocks.AIR) {
			int faceID = face.getID();
			if (faceID == Face.TOP) {
				v0.posy -= unit;
				v1.posy -= unit;
				v2.posy -= unit;
				v3.posy -= unit;
			} else if (faceID != Face.BOT) {
				v0.posy -= unit;
				v3.posy -= unit;
			}
		}

		stack.push(v0);
		stack.push(v1);
		stack.push(v2);

		stack.push(v0);
		stack.push(v2);
		stack.push(v3);
	}

	public boolean hasSpecialRendering() {
		return (true);
	}

	public boolean influenceCollisions() {
		return (false);
	}
}
