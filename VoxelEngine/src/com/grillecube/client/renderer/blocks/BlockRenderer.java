package com.grillecube.client.renderer.blocks;

import java.util.Stack;

import com.grillecube.client.renderer.world.terrain.BlockFace;
import com.grillecube.client.renderer.world.terrain.MeshVertex;
import com.grillecube.client.renderer.world.terrain.TerrainMesher;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the block renderer class */
public abstract class BlockRenderer {

	/** mesher stuff starts here */

	public static Vector3i[][] FACES_VERTICES = new Vector3i[6][4];
	// blocks offset which affect ao
	public static Vector3i[][][] FACES_NEIGHBORS = new Vector3i[6][4][3];
	private static Vector3i[] VERTICES = new Vector3i[8];

	/*
	 * 7---------6 /| | 3---------2 | | | | | | | | | | 4______|__5 | / | /
	 * 0---------1
	 */

	static {
		VERTICES[0] = new Vector3i(0, 1, 0);
		VERTICES[1] = new Vector3i(0, 0, 0);
		VERTICES[2] = new Vector3i(0, 0, 1);
		VERTICES[3] = new Vector3i(0, 1, 1);
		VERTICES[4] = new Vector3i(1, 1, 0);
		VERTICES[5] = new Vector3i(1, 0, 0);
		VERTICES[6] = new Vector3i(1, 0, 1);
		VERTICES[7] = new Vector3i(1, 1, 1);

		/** left face */
		FACES_VERTICES[Face.LEFT][0] = VERTICES[4];
		FACES_VERTICES[Face.LEFT][1] = VERTICES[5];
		FACES_VERTICES[Face.LEFT][2] = VERTICES[1];
		FACES_VERTICES[Face.LEFT][3] = VERTICES[0];

		FACES_NEIGHBORS[Face.LEFT][0][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.LEFT][0][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][0][2] = new Vector3i(1, 1, -1);

		FACES_NEIGHBORS[Face.LEFT][1][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.LEFT][1][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][1][2] = new Vector3i(1, -1, -1);

		FACES_NEIGHBORS[Face.LEFT][2][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.LEFT][2][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][2][2] = new Vector3i(-1, -1, -1);

		FACES_NEIGHBORS[Face.LEFT][3][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.LEFT][3][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][3][2] = new Vector3i(-1, 1, -1);

		/** right face */
		FACES_VERTICES[Face.RIGHT][0] = VERTICES[3];
		FACES_VERTICES[Face.RIGHT][1] = VERTICES[2];
		FACES_VERTICES[Face.RIGHT][2] = VERTICES[6];
		FACES_VERTICES[Face.RIGHT][3] = VERTICES[7];

		FACES_NEIGHBORS[Face.RIGHT][0][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.RIGHT][0][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][0][2] = new Vector3i(-1, 1, 1);

		FACES_NEIGHBORS[Face.RIGHT][1][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.RIGHT][1][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][1][2] = new Vector3i(-1, -1, 1);

		FACES_NEIGHBORS[Face.RIGHT][2][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.RIGHT][2][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][2][2] = new Vector3i(1, -1, 1);

		FACES_NEIGHBORS[Face.RIGHT][3][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.RIGHT][3][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][3][2] = new Vector3i(1, 1, 1);

		/** back face */
		FACES_VERTICES[Face.BACK][0] = VERTICES[7];
		FACES_VERTICES[Face.BACK][1] = VERTICES[6];
		FACES_VERTICES[Face.BACK][2] = VERTICES[5];
		FACES_VERTICES[Face.BACK][3] = VERTICES[4];

		FACES_NEIGHBORS[Face.BACK][0][0] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.BACK][0][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.BACK][0][2] = new Vector3i(1, 1, 1);

		FACES_NEIGHBORS[Face.BACK][1][0] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BACK][1][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.BACK][1][2] = new Vector3i(1, -1, 1);

		FACES_NEIGHBORS[Face.BACK][2][0] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BACK][2][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.BACK][2][2] = new Vector3i(1, -1, -1);

		FACES_NEIGHBORS[Face.BACK][3][0] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.BACK][3][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.BACK][3][2] = new Vector3i(1, 1, -1);

		/** front face */
		FACES_VERTICES[Face.FRONT][0] = VERTICES[0];
		FACES_VERTICES[Face.FRONT][1] = VERTICES[1];
		FACES_VERTICES[Face.FRONT][2] = VERTICES[2];
		FACES_VERTICES[Face.FRONT][3] = VERTICES[3];

		FACES_NEIGHBORS[Face.FRONT][0][0] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.FRONT][0][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.FRONT][0][2] = new Vector3i(-1, 1, -1);

		FACES_NEIGHBORS[Face.FRONT][1][0] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.FRONT][1][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.FRONT][1][2] = new Vector3i(-1, -1, -1);

		FACES_NEIGHBORS[Face.FRONT][2][0] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.FRONT][2][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.FRONT][2][2] = new Vector3i(-1, -1, 1);

		FACES_NEIGHBORS[Face.FRONT][3][0] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.FRONT][3][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.FRONT][3][2] = new Vector3i(-1, 1, 1);

		/** bottom face */
		FACES_VERTICES[Face.BOT][0] = VERTICES[1];
		FACES_VERTICES[Face.BOT][1] = VERTICES[5];
		FACES_VERTICES[Face.BOT][2] = VERTICES[6];
		FACES_VERTICES[Face.BOT][3] = VERTICES[2];

		FACES_NEIGHBORS[Face.BOT][0][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.BOT][0][1] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][0][2] = new Vector3i(-1, -1, -1);

		FACES_NEIGHBORS[Face.BOT][1][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.BOT][1][1] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][1][2] = new Vector3i(1, -1, -1);

		FACES_NEIGHBORS[Face.BOT][2][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.BOT][2][1] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][2][2] = new Vector3i(1, -1, 1);

		FACES_NEIGHBORS[Face.BOT][3][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.BOT][3][1] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][3][2] = new Vector3i(-1, -1, 1);

		/** top face */
		FACES_VERTICES[Face.TOP][0] = VERTICES[4];
		FACES_VERTICES[Face.TOP][1] = VERTICES[0];
		FACES_VERTICES[Face.TOP][2] = VERTICES[3];
		FACES_VERTICES[Face.TOP][3] = VERTICES[7];

		FACES_NEIGHBORS[Face.TOP][0][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.TOP][0][1] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][0][2] = new Vector3i(1, 1, -1);

		FACES_NEIGHBORS[Face.TOP][1][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.TOP][1][1] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][1][2] = new Vector3i(-1, 1, -1);

		FACES_NEIGHBORS[Face.TOP][2][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.TOP][2][1] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][2][2] = new Vector3i(-1, 1, 1);

		FACES_NEIGHBORS[Face.TOP][3][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.TOP][3][1] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][3][2] = new Vector3i(1, 1, 1);

	};

	public static float[][] FACES_UV = { { 0, 0 }, { 0, 1 }, { 1, 1 }, { 1, 0 } };

	/**
	 * generate the vertices for the given block, in the given terrain and (x,
	 * y, z) terrain-relative coordinates.
	 *
	 * If this block has faces (1x1), it should be set: faces[faceID][x][y][z] =
	 * blockFace, so it can be cull if needed
	 * 
	 * If the block has a special rendering, add the vertices directly onto the
	 * stack
	 */
	public abstract void generateBlockVertices(TerrainMesher terrainMesher, Terrain terrain, Block block, int x, int y,
			int z, BlockFace[][][][] faces, Stack<MeshVertex> stack);

	/** return true if this block has transparency */
	public abstract boolean hasTransparency();

	/** return the x texture coodinates for this textureID */
	public int getAtlasX(int textureID) {
		return (textureID % BlockRendererManager.TEXTURE_PER_LINE);
	}

	/** return the y texture coodinates for this textureID */
	public int getAtlasY(int textureID) {
		return (textureID / BlockRendererManager.TEXTURE_PER_LINE);
	}

}
