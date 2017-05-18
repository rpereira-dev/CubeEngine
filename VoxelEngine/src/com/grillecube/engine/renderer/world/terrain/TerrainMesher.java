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

package com.grillecube.engine.renderer.world.terrain;

import java.nio.ByteBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;

import com.grillecube.engine.faces.Face;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.resources.BlockManager;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.block.Block;

/** an object which is used to generate terrain meshes dynamically */
public abstract class TerrainMesher {

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

	/** number of block is need to know how to calculate UVs */
	public TerrainMesher() {
	}

	public final ByteBuffer generateVertices(Terrain terrain) {

		Stack<MeshVertex> stack = this.getVertexStack(terrain);
		ByteBuffer buffer = BufferUtils.createByteBuffer(stack.size() * TerrainMesh.FLOAT_PER_VERTEX * 4);

		for (MeshVertex vertex : stack) {
			buffer.putFloat(vertex.posx);
			buffer.putFloat(vertex.posy);
			buffer.putFloat(vertex.posz);
			buffer.putFloat(vertex.normalx);
			buffer.putFloat(vertex.normaly);
			buffer.putFloat(vertex.normalz);
			buffer.putFloat(vertex.atlasX);
			buffer.putFloat(vertex.atlasY);
			buffer.putFloat(vertex.uvx);
			buffer.putFloat(vertex.uvy);
			buffer.putInt(vertex.color);
			buffer.putFloat(vertex.brightness);
		}
		buffer.flip();
		return (buffer);
	}

	/**
	 * generate a stack which contains every vertices ordered to render back
	 * face culled triangles
	 */
	protected abstract Stack<MeshVertex> getVertexStack(Terrain terrain);

	/** represent a block face */
	protected class BlockFace {

		// the vertices
		public MeshVertex[] vertices;

		// allowed movement for this face
		public Face face;

		// the texture
		public int texture;

		public BlockFace(Block block, Face face, MeshVertex... vertices) {
			this.face = face;
			this.texture = block.getTextureIDForFace(face);
			this.vertices = vertices;
		}

		public boolean hasSameTexture(BlockFace other) {
			return (other.texture == this.texture);
		}

		public boolean hasSameBrightness(BlockFace other) {
			return (this.vertices[0].brightness == other.vertices[0].brightness
					&& this.vertices[1].brightness == other.vertices[1].brightness
					&& this.vertices[2].brightness == other.vertices[2].brightness
					&& this.vertices[3].brightness == other.vertices[3].brightness);
		}

		@Override
		public boolean equals(Object object) {
			if (object == null || !(object instanceof BlockFace)) {
				return (false);
			}

			BlockFace other = (BlockFace) object;

			return (this.hasSameTexture(other) && this.hasSameBrightness(other));
		}

		/** push this face vertices to the stack */
		public void pushVertices(Stack<MeshVertex> stack) {

			MeshVertex v0 = this.vertices[0];
			MeshVertex v1 = this.vertices[1];
			MeshVertex v2 = this.vertices[2];
			MeshVertex v3 = this.vertices[3];

			if (v0.getAO() + v2.getAO() < v1.getAO() + v3.getAO()) {
				stack.push(v0);
				stack.push(v1);
				stack.push(v2);
				stack.push(v0);
				stack.push(v2);
				stack.push(v3);
			} else {
				// flip quad
				stack.push(v1);
				stack.push(v2);
				stack.push(v3);
				stack.push(v1);
				stack.push(v3);
				stack.push(v0);
			}
		}
	}

	/** return the given block face data informations */
	public BlockFace getBlockFace(Terrain terrain, Block block, int x, int y, int z, Face face) {

		Vector3i vec = face.getVector();

		// get the neighbor of this face
		Block neighbor = terrain.getBlock(x + vec.x, y + vec.y, z + vec.z);

		// if the face-neighboor block is visible and opaque
		if (neighbor.isVisible() && neighbor.isOpaque()) {
			// the face isnt visible
			return (null);
		}

		// else the face is visible, create it!
		MeshVertex v0 = this.getBlockFaceVertex(terrain, block, face, 0, x, y, z);
		MeshVertex v1 = this.getBlockFaceVertex(terrain, block, face, 1, x, y, z);
		MeshVertex v2 = this.getBlockFaceVertex(terrain, block, face, 2, x, y, z);
		MeshVertex v3 = this.getBlockFaceVertex(terrain, block, face, 3, x, y, z);
		BlockFace blockface = new BlockFace(block, face, v0, v1, v2, v3);
		return (blockface);
	}

	/**
	 * return the vertex for the given face at the given coordinates, for it
	 * given id
	 */
	public MeshVertex getBlockFaceVertex(Terrain terrain, Block block, Face face, int vertexID, int x, int y, int z) {
		Vector3i[] neighboors = FACES_NEIGHBORS[face.getID()][vertexID];

		// position
		float px = x * Terrain.BLOCK_SIZE + FACES_VERTICES[face.getID()][vertexID].x * Terrain.BLOCK_SIZE;
		float py = y * Terrain.BLOCK_SIZE + FACES_VERTICES[face.getID()][vertexID].y * Terrain.BLOCK_SIZE;
		float pz = z * Terrain.BLOCK_SIZE + FACES_VERTICES[face.getID()][vertexID].z * Terrain.BLOCK_SIZE;

		// uv
		float uvx = FACES_UV[vertexID][0];
		float uvy = FACES_UV[vertexID][1];
		int textureID = block.getTextureIDForFace(face);
		float atlasX = this.getAtlasX(textureID);
		float atlasY = this.getAtlasY(textureID);

		// get light value

		// the ambiant occlusion
		Block side1 = terrain.getBlock(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		Block side2 = terrain.getBlock(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		Block corner = terrain.getBlock(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);
		float ao = this.getVertexAO(terrain, side1, side2, corner);

		// the light
		byte lightValue = terrain.getBlockLight(x, y, z);
		float light;
		if (lightValue != 0) {
			light = lightValue / 16.0F;
		} else {
			float l1 = terrain.getBlockLight(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
			float l2 = terrain.getBlockLight(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
			float l3 = terrain.getBlockLight(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);
			light = (l1 + l2 + l3) / (3 * 16.0F);
		}

		// final brightness
		float brightness = 1 + light - ao;

		// light color
		int color = 0xFFFFFFFF;// ColorInt.get(255, 255, 255, 255);
		return (new MeshVertex(px, py, pz, face.getNormal(), atlasX, atlasY, uvx, uvy, color, brightness, ao));
	}

	// the float returned is the ratio of black which will be used for this
	// vertex
	private static final float AO_0 = 0.00f;
	private static final float AO_1 = 0.16f;
	private static final float AO_2 = 0.32f;
	private static final float AO_3 = 0.42f;

	public float getVertexAO(Terrain terrain, Block side1, Block side2, Block corner) {
		boolean s1 = side1.isVisible() && side1.influenceAO();
		boolean s2 = side2.isVisible() && side2.influenceAO();
		boolean c = corner.isVisible() && corner.influenceAO();
		if (s1 && s2) {
			return (AO_3);
		}

		if (s1 || s2) {
			return (c ? AO_2 : AO_1);
		}

		return (c ? AO_1 : AO_0);
	}

	/** return the x texture coodinates for this textureID */
	public int getAtlasX(int textureID) {
		return (textureID % BlockManager.TEXTURE_PER_LINE);
	}

	/** return the y texture coodinates for this textureID */
	public int getAtlasY(int textureID) {
		return (textureID / BlockManager.TEXTURE_PER_LINE);
	}
}
