/**
 ** 
 **				7-------------6
 **			   /|            /|
 **			  / |           / |
 **			 /  |          /  |
 **			3-------------2---|
 **			|   |         |   |
 **			|   |         |   |
 **			|   |         |   |
 **			|   4---------|---5
 **			|  /          |  /
 **			| /           | /
 **			|/            |/
 **			0-------------1
 **
 **
 **
 **
 **							TOP
 **
 **										RIGHT
 **						x------6------x
 **					   /|            /|	
 **					 11 |          10 |
 **					 /  |          /  |
 **					x-------2-----x---|
 **					|   7         |   |
 **					|   |         |   5			BACK
 **		FRONT		|   |         |   |
 **					3   x-----4---1---x
 **					|  /          |  /
 **					| 8           | 9
 **					|/            |/
 **			LEFT	x------0------x
 **
 **						BOT
 **
 **			^
 **			|    ^
 **			|   /
 **			y  z
 **			| /
 **			|/
 **			------x----->
 **/

package com.grillecube.client.renderer.blocks;

import java.util.ArrayList;

import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.client.renderer.world.flat.BlockFace;
import com.grillecube.client.resources.BlockRendererManager;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the block renderer class */
public abstract class BlockRenderer {

	/** edges offset */
	public static final Vector3i[] VERTICES = new Vector3i[8];

	/** vertices faces */
	public static final int[][] VERTICES_FACES = new int[8][3];

	/** vertices id relatively to the face */
	public static final int[][] VERTICES_FACES_ID = new int[8][Face.faces.length];

	/** vertices for a face */
	public static final int[][] FACES_VERTICES = new int[Face.faces.length][4];
	/**
	 * lists the index of the endpoint vertices for each of the 12edges of the
	 * cube
	 */
	public static final int EDGES[][] = new int[12][2];

	/**
	 * 12x3 : lists the direction vector (vertex1-vertex0) for each edge in the
	 * cube
	 */
	public static final Vector3i[] EDGES_DIRECTIONS = new Vector3i[12];

	/** blocks offset which affect ambiant occlusion */
	public static final Vector3i[][] VERTEX_NEIGHBORS = new Vector3i[8][4];
	public static final Vector3i[][][] FACES_NEIGHBORS = new Vector3i[6][4][3];

	static {
		VERTICES[0] = new Vector3i(0, 0, 0);
		VERTICES[1] = new Vector3i(1, 0, 0);
		VERTICES[2] = new Vector3i(1, 1, 0);
		VERTICES[3] = new Vector3i(0, 1, 0);
		VERTICES[4] = new Vector3i(0, 0, 1);
		VERTICES[5] = new Vector3i(1, 0, 1);
		VERTICES[6] = new Vector3i(1, 1, 1);
		VERTICES[7] = new Vector3i(0, 1, 1);

		/** edges connections */
		EDGES[0][0] = 0;
		EDGES[0][1] = 1;
		EDGES[1][0] = 1;
		EDGES[1][1] = 2;
		EDGES[2][0] = 2;
		EDGES[2][1] = 3;
		EDGES[3][0] = 3;
		EDGES[3][1] = 0;

		EDGES[4][0] = 4;
		EDGES[4][1] = 5;
		EDGES[5][0] = 5;
		EDGES[5][1] = 6;
		EDGES[6][0] = 6;
		EDGES[6][1] = 7;
		EDGES[7][0] = 7;
		EDGES[7][1] = 4;

		EDGES[8][0] = 0;
		EDGES[8][1] = 4;
		EDGES[9][0] = 1;
		EDGES[9][1] = 5;
		EDGES[10][0] = 2;
		EDGES[10][1] = 6;
		EDGES[11][0] = 3;
		EDGES[11][1] = 7;

		/** edge directions */
		for (int i = 0; i < EDGES.length; i++) {
			EDGES_DIRECTIONS[i] = Vector3i.sub(VERTICES[EDGES[i][1]], VERTICES[EDGES[i][0]], null);
		}

		/** left face */
		FACES_VERTICES[Face.LEFT][0] = 2;
		FACES_VERTICES[Face.LEFT][1] = 1;
		FACES_VERTICES[Face.LEFT][2] = 0;
		FACES_VERTICES[Face.LEFT][3] = 3;

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
		FACES_VERTICES[Face.RIGHT][0] = 7;
		FACES_VERTICES[Face.RIGHT][1] = 4;
		FACES_VERTICES[Face.RIGHT][2] = 5;
		FACES_VERTICES[Face.RIGHT][3] = 6;

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
		FACES_VERTICES[Face.BACK][0] = 6;
		FACES_VERTICES[Face.BACK][1] = 5;
		FACES_VERTICES[Face.BACK][2] = 1;
		FACES_VERTICES[Face.BACK][3] = 2;

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
		FACES_VERTICES[Face.FRONT][0] = 3;
		FACES_VERTICES[Face.FRONT][1] = 0;
		FACES_VERTICES[Face.FRONT][2] = 4;
		FACES_VERTICES[Face.FRONT][3] = 7;

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
		FACES_VERTICES[Face.BOT][0] = 0;
		FACES_VERTICES[Face.BOT][1] = 1;
		FACES_VERTICES[Face.BOT][2] = 5;
		FACES_VERTICES[Face.BOT][3] = 4;

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
		FACES_VERTICES[Face.TOP][0] = 2;
		FACES_VERTICES[Face.TOP][1] = 3;
		FACES_VERTICES[Face.TOP][2] = 7;
		FACES_VERTICES[Face.TOP][3] = 6;

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

		/** vertices faces */
		for (int vertexID = 0; vertexID < 8; vertexID++) {
			int faceID = 0;
			for (Face face : Face.faces) {
				for (int vertex : FACES_VERTICES[face.getID()]) {
					if (vertexID == vertex) {
						VERTICES_FACES[vertexID][faceID++] = face.getID();
						break;
					}
				}
			}
		}

		/** vertices id relatively to faces */
		for (int vertexID = 0; vertexID < 8; vertexID++) {
			for (int faceID = 0; faceID < Face.faces.length; faceID++) {
				VERTICES_FACES_ID[vertexID][faceID] = -1;
				for (int i = 0; i < 4; i++) {
					if (FACES_VERTICES[faceID][i] == vertexID) {
						VERTICES_FACES_ID[vertexID][faceID] = i;
						break;
					}
				}
			}
		}
	};

	public static final float[][] FACES_UV = { { 0, 0 }, { 0, 1 }, { 1, 1 }, { 1, 0 } };

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
			int z, BlockFace[][][][] faces, ArrayList<TerrainMeshTriangle> stack);

	/** return true if this block has transparency */
	// public abstract boolean hasTransparency(); //TODO : implement it here,
	// instead of #Terrain#opaqueBlockCount and stuff...

	/** return the x texture coodinates for this textureID */
	public static final int getAtlasX(int textureID) {
		return (textureID % BlockRendererManager.TEXTURE_PER_LINE);
	}

	/** return the y texture coodinates for this textureID */
	public static final int getAtlasY(int textureID) {
		return (textureID / BlockRendererManager.TEXTURE_PER_LINE);
	}

	/** get block light by getting the average of neighboors blocks */
	public static final float getBlockLight(Terrain terrain, int x, int y, int z, Vector3i... neighboors) {
		float blockLight = 0.0f;
		for (Vector3i n : neighboors) {
			blockLight += terrain.getBlockLight(x + n.x, y + n.y, z + n.z);
		}
		return (blockLight / (neighboors.length * 16.0f));
	}

	/** get block light by getting the average of neighboors blocks */
	public static final float getBlockLight(Terrain terrain, int x, int y, int z, int faceID, int faceVertexID) {
		return (getBlockLight(terrain, x, y, z, getNeighboors(faceID, faceVertexID)));
	}

	/** get block light by getting the average of neighboors blocks */
	public static final float getSunLight(Terrain terrain, int x, int y, int z, Vector3i... neighboors) {
		float sunLight = 0.0f;
		for (Vector3i n : neighboors) {
			sunLight += terrain.getSunLight(x + n.x, y + Maths.abs(n.y), z + n.z);
		}
		return (sunLight / (neighboors.length * 16.0f));
	}

	/** get block light by getting the average of neighboors blocks */
	public static final float getSunLight(Terrain terrain, int x, int y, int z, int faceID, int vertexID) {
		return (getSunLight(terrain, x, y, z, getNeighboors(faceID, vertexID)));
	}

	/**
	 * return the default texture id of this block renderer for the given face
	 */
	public abstract int getDefaultTextureID(int faceID);

	public static final float AO_UNIT = 0.3f;

	public static final float getAmbiantOcclusion(Terrain terrain, int x, int y, int z, Vector3i... neighboors) {
		Block side1 = terrain.getBlock(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		Block side2 = terrain.getBlock(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		Block corner = terrain.getBlock(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);

		boolean s1 = side1.isVisible() && !side1.isTransparent();
		boolean s2 = side2.isVisible() && !side2.isTransparent();
		boolean c = corner.isVisible() && !corner.isTransparent();
		if (s1 && s2) {
			return (3.0f * AO_UNIT);
		}

		if (s1 || s2) {
			return (c ? 2.0f * AO_UNIT : AO_UNIT);
		}
		return (c ? AO_UNIT : 0.0f);
	}

	public static final float getAmbiantOcclusion(Terrain terrain, int x, int y, int z, int faceID, int vertexID) {
		return (getAmbiantOcclusion(terrain, x, y, z, getNeighboors(faceID, vertexID)));
	}

	/**
	 * get the offset of the neighbors block of the given faceID (0-5) and
	 * faceVertexID (0-3)
	 * 
	 * @param faceID
	 * @param vertexID
	 * @return
	 */
	public static final Vector3i[] getNeighboors(int faceID, int faceVertexID) {
		return (BlockRenderer.FACES_NEIGHBORS[faceID][faceVertexID]);
	}
}

/**
 * FACES_NEIGHBORS[Face.LEFT][0][0] = new Vector3i(0, 1, -1);
 * FACES_NEIGHBORS[Face.LEFT][0][1] = new Vector3i(1, 0, -1);
 * FACES_NEIGHBORS[Face.LEFT][0][2] = new Vector3i(1, 1, -1);
 * 
 * FACES_NEIGHBORS[Face.LEFT][0][0] = new Vector3i(0, 1, 0);
 * FACES_NEIGHBORS[Face.LEFT][0][1] = new Vector3i(1, 1, -1);
 * FACES_NEIGHBORS[Face.LEFT][0][2] = new Vector3i(1, 1, 0);
 * 
 * FACES_NEIGHBORS[Face.LEFT][1][0] = new Vector3i(0, -1, -1);
 * FACES_NEIGHBORS[Face.LEFT][1][1] = new Vector3i(1, 0, -1);
 * FACES_NEIGHBORS[Face.LEFT][1][2] = new Vector3i(1, -1, -1);
 * 
 * FACES_NEIGHBORS[Face.LEFT][2][0] = new Vector3i(0, -1, -1);
 * FACES_NEIGHBORS[Face.LEFT][2][1] = new Vector3i(-1, 0, -1);
 * FACES_NEIGHBORS[Face.LEFT][2][2] = new Vector3i(-1, -1, -1);
 * 
 * FACES_NEIGHBORS[Face.LEFT][3][0] = new Vector3i(0, 1, -1);
 * FACES_NEIGHBORS[Face.LEFT][3][1] = new Vector3i(-1, 0, -1);
 * FACES_NEIGHBORS[Face.LEFT][3][2] = new Vector3i(-1, 1, -1);
 * 
 * FACES_VERTICES[Face.RIGHT][0] = 7; FACES_VERTICES[Face.RIGHT][1] = 4;
 * FACES_VERTICES[Face.RIGHT][2] = 5; FACES_VERTICES[Face.RIGHT][3] = 6;
 * 
 * FACES_NEIGHBORS[Face.RIGHT][0][0] = new Vector3i(0, 1, 1);
 * FACES_NEIGHBORS[Face.RIGHT][0][1] = new Vector3i(-1, 0, 1);
 * FACES_NEIGHBORS[Face.RIGHT][0][2] = new Vector3i(-1, 1, 1);
 * 
 * FACES_NEIGHBORS[Face.RIGHT][1][0] = new Vector3i(0, -1, 1);
 * FACES_NEIGHBORS[Face.RIGHT][1][1] = new Vector3i(-1, 0, 1);
 * FACES_NEIGHBORS[Face.RIGHT][1][2] = new Vector3i(-1, -1, 1);
 * 
 * FACES_NEIGHBORS[Face.RIGHT][2][0] = new Vector3i(0, -1, 1);
 * FACES_NEIGHBORS[Face.RIGHT][2][1] = new Vector3i(1, 0, 1);
 * FACES_NEIGHBORS[Face.RIGHT][2][2] = new Vector3i(1, -1, 1);
 * 
 * FACES_NEIGHBORS[Face.RIGHT][3][0] = new Vector3i(0, 1, 1);
 * FACES_NEIGHBORS[Face.RIGHT][3][1] = new Vector3i(1, 0, 1);
 * FACES_NEIGHBORS[Face.RIGHT][3][2] = new Vector3i(1, 1, 1);
 * 
 * FACES_VERTICES[Face.BACK][0] = 6; FACES_VERTICES[Face.BACK][1] = 5;
 * FACES_VERTICES[Face.BACK][2] = 1; FACES_VERTICES[Face.BACK][3] = 2;
 * 
 * FACES_NEIGHBORS[Face.BACK][0][0] = new Vector3i(1, 1, 0);
 * FACES_NEIGHBORS[Face.BACK][0][1] = new Vector3i(1, 0, 1);
 * FACES_NEIGHBORS[Face.BACK][0][2] = new Vector3i(1, 1, 1);
 * 
 * FACES_NEIGHBORS[Face.BACK][1][0] = new Vector3i(1, -1, 0);
 * FACES_NEIGHBORS[Face.BACK][1][1] = new Vector3i(1, 0, 1);
 * FACES_NEIGHBORS[Face.BACK][1][2] = new Vector3i(1, -1, 1);
 * 
 * FACES_NEIGHBORS[Face.BACK][2][0] = new Vector3i(1, -1, 0);
 * FACES_NEIGHBORS[Face.BACK][2][1] = new Vector3i(1, 0, -1);
 * FACES_NEIGHBORS[Face.BACK][2][2] = new Vector3i(1, -1, -1);
 * 
 * FACES_NEIGHBORS[Face.BACK][3][0] = new Vector3i(1, 1, 0);
 * FACES_NEIGHBORS[Face.BACK][3][1] = new Vector3i(1, 0, -1);
 * FACES_NEIGHBORS[Face.BACK][3][2] = new Vector3i(1, 1, -1);
 * 
 * FACES_VERTICES[Face.FRONT][0] = 3; FACES_VERTICES[Face.FRONT][1] = 0;
 * FACES_VERTICES[Face.FRONT][2] = 4; FACES_VERTICES[Face.FRONT][3] = 7;
 * 
 * FACES_NEIGHBORS[Face.FRONT][0][0] = new Vector3i(-1, 1, 0);
 * FACES_NEIGHBORS[Face.FRONT][0][1] = new Vector3i(-1, 0, -1);
 * FACES_NEIGHBORS[Face.FRONT][0][2] = new Vector3i(-1, 1, -1);
 * 
 * FACES_NEIGHBORS[Face.FRONT][1][0] = new Vector3i(-1, -1, 0);
 * FACES_NEIGHBORS[Face.FRONT][1][1] = new Vector3i(-1, 0, -1);
 * FACES_NEIGHBORS[Face.FRONT][1][2] = new Vector3i(-1, -1, -1);
 * 
 * FACES_NEIGHBORS[Face.FRONT][2][0] = new Vector3i(-1, -1, 0);
 * FACES_NEIGHBORS[Face.FRONT][2][1] = new Vector3i(-1, 0, 1);
 * FACES_NEIGHBORS[Face.FRONT][2][2] = new Vector3i(-1, -1, 1);
 * 
 * FACES_NEIGHBORS[Face.FRONT][3][0] = new Vector3i(-1, 1, 0);
 * FACES_NEIGHBORS[Face.FRONT][3][1] = new Vector3i(-1, 0, 1);
 * FACES_NEIGHBORS[Face.FRONT][3][2] = new Vector3i(-1, 1, 1);
 * 
 * FACES_VERTICES[Face.BOT][0] = 0; FACES_VERTICES[Face.BOT][1] = 1;
 * FACES_VERTICES[Face.BOT][2] = 5; FACES_VERTICES[Face.BOT][3] = 4;
 * 
 * FACES_NEIGHBORS[Face.BOT][0][0] = new Vector3i(0, -1, -1);
 * FACES_NEIGHBORS[Face.BOT][0][1] = new Vector3i(-1, -1, 0);
 * FACES_NEIGHBORS[Face.BOT][0][2] = new Vector3i(-1, -1, -1);
 * 
 * FACES_NEIGHBORS[Face.BOT][1][0] = new Vector3i(0, -1, -1);
 * FACES_NEIGHBORS[Face.BOT][1][1] = new Vector3i(1, -1, 0);
 * FACES_NEIGHBORS[Face.BOT][1][2] = new Vector3i(1, -1, -1);
 * 
 * FACES_NEIGHBORS[Face.BOT][2][0] = new Vector3i(0, -1, 1);
 * FACES_NEIGHBORS[Face.BOT][2][1] = new Vector3i(1, -1, 0);
 * FACES_NEIGHBORS[Face.BOT][2][2] = new Vector3i(1, -1, 1);
 * 
 * FACES_NEIGHBORS[Face.BOT][3][0] = new Vector3i(0, -1, 1);
 * FACES_NEIGHBORS[Face.BOT][3][1] = new Vector3i(-1, -1, 0);
 * FACES_NEIGHBORS[Face.BOT][3][2] = new Vector3i(-1, -1, 1);
 * 
 * FACES_VERTICES[Face.TOP][0] = 2; FACES_VERTICES[Face.TOP][1] = 3;
 * FACES_VERTICES[Face.TOP][2] = 7; FACES_VERTICES[Face.TOP][3] = 6;
 * 
 * FACES_NEIGHBORS[Face.TOP][0][0] = new Vector3i(0, 1, -1);
 * FACES_NEIGHBORS[Face.TOP][0][1] = new Vector3i(1, 1, 0);
 * FACES_NEIGHBORS[Face.TOP][0][2] = new Vector3i(1, 1, -1);
 * 
 * FACES_NEIGHBORS[Face.TOP][1][0] = new Vector3i(0, 1, -1);
 * FACES_NEIGHBORS[Face.TOP][1][1] = new Vector3i(-1, 1, 0);
 * FACES_NEIGHBORS[Face.TOP][1][2] = new Vector3i(-1, 1, -1);
 * 
 * FACES_NEIGHBORS[Face.TOP][2][0] = new Vector3i(0, 1, 1);
 * FACES_NEIGHBORS[Face.TOP][2][1] = new Vector3i(-1, 1, 0);
 * FACES_NEIGHBORS[Face.TOP][2][2] = new Vector3i(-1, 1, 1);
 * 
 * FACES_NEIGHBORS[Face.TOP][3][0] = new Vector3i(0, 1, 1);
 * FACES_NEIGHBORS[Face.TOP][3][1] = new Vector3i(1, 1, 0);
 * FACES_NEIGHBORS[Face.TOP][3][2] = new Vector3i(1, 1, 1);
 */
