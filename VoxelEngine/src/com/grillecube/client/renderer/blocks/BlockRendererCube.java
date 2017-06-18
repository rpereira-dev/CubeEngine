package com.grillecube.client.renderer.blocks;

import java.util.Stack;

import com.grillecube.client.renderer.world.terrain.BlockFace;
import com.grillecube.client.renderer.world.terrain.MeshVertex;
import com.grillecube.client.renderer.world.terrain.TerrainMesher;
import com.grillecube.common.Logger;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the default cube renderer */
public class BlockRendererCube extends BlockRenderer {

	/** the face texture ids */
	private int[] textureIDs;

	private BlockRendererCube() {
		this.textureIDs = new int[Face.faces.length];
	}

	/**
	 * set the texture of the given block, for the given faces
	 * 
	 * @param block
	 *            : the block
	 * @param ids
	 *            : the face and texture ID's
	 * 
	 *            e.g: new BlockRendererCube(Blocks.GRASS, Face.LEFT,
	 *            ClientBlocks.T_GRASS_SIDE, Face.RIGHT,
	 *            ClientBlocks.T_GRASS_SIDE, Face.FRONT,
	 *            ClientBlocks.T_GRASS_SIDE, Face.BACK,
	 *            ClientBlocks.T_GRASS_SIDE, Face.TOP, ClientBlocks.T_GRASS_TOP,
	 *            Face.BOT, ClientBlocks.T_DIRT);
	 */
	public BlockRendererCube(int... ids) {
		this();

		if (ids.length == 0) {
			Logger.get().log(Logger.Level.DEBUG,
					"Called setBlockFaceTextures() but no texture where given... cancelling");
			return;
		}

		if (ids.length % 2 != 0) {
			Logger.get().log(Logger.Level.DEBUG,
					"Called setBlockFaceTextures() with an impair number of arguments: missing a Face or a Texture");
			return;
		}

		for (int i = 0; i < ids.length; i += 2) {
			int faceID = ids[i];
			int textureID = ids[i + 1];
			if (faceID >= 0 && faceID < Face.faces.length) {
				this.textureIDs[faceID] = textureID;
			}
		}
	}

	/**
	 * BlockRenderer for a cube
	 * 
	 * @param textureID
	 *            : the texture to be applied on each faces
	 */
	public BlockRendererCube(int textureID) {
		this();
		for (Face face : Face.faces) {
			this.textureIDs[face.getID()] = textureID;
		}
	}

	/** return the array of texture for the given block */
	public int[] getTextureIDs() {
		return (this.textureIDs);
	}

	public int getTextureID(Face face) {
		return (this.textureIDs[face.getID()]);
	}

	@Override
	public void generateBlockVertices(TerrainMesher terrainMesher, Terrain terrain, Block block, int x, int y, int z,
			BlockFace[][][][] faces, Stack<MeshVertex> stack) {

		for (Face face : Face.faces) {
			faces[face.getID()][x][y][z] = this.createBlockFace(terrain, face, x, y, z);
		}
	}

	private BlockFace createBlockFace(Terrain terrain, Face face, int x, int y, int z) {

		Vector3i vec = face.getVector();

		// get the neighbor of this face
		Block neighbor = terrain.getBlock(x + vec.x, y + vec.y, z + vec.z);

		// if the face-neighboor block is visible and opaque
		if (neighbor.isVisible() && neighbor.isOpaque()) {
			// the face isnt visible
			return (null);
		}

		// else the face is visible, create it!
		MeshVertex v0 = this.createBlockFaceVertex(terrain, face, x, y, z, 0);
		MeshVertex v1 = this.createBlockFaceVertex(terrain, face, x, y, z, 1);
		MeshVertex v2 = this.createBlockFaceVertex(terrain, face, x, y, z, 2);
		MeshVertex v3 = this.createBlockFaceVertex(terrain, face, x, y, z, 3);
		BlockFace blockface = new BlockFace(this.textureIDs[face.getID()], v0, v1, v2, v3);
		return (blockface);

	}

	/**
	 * return the vertex for the given face at the given coordinates, for it
	 * given id
	 */
	public MeshVertex createBlockFaceVertex(Terrain terrain, Face face, int x, int y, int z, int vertexID) {
		Vector3i[] neighboors = FACES_NEIGHBORS[face.getID()][vertexID];

		// position
		float px = x * Terrain.BLOCK_SIZE + FACES_VERTICES[face.getID()][vertexID].x * Terrain.BLOCK_SIZE;
		float py = y * Terrain.BLOCK_SIZE + FACES_VERTICES[face.getID()][vertexID].y * Terrain.BLOCK_SIZE;
		float pz = z * Terrain.BLOCK_SIZE + FACES_VERTICES[face.getID()][vertexID].z * Terrain.BLOCK_SIZE;

		// uv
		float uvx = FACES_UV[vertexID][0];
		float uvy = FACES_UV[vertexID][1];
		int textureID = this.textureIDs[face.getID()];
		float atlasX = super.getAtlasX(textureID);
		float atlasY = super.getAtlasY(textureID);

		// get light value

		// the ambiant occlusion
		Block side1 = terrain.getBlock(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		Block side2 = terrain.getBlock(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		Block corner = terrain.getBlock(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);
		float ao = this.getVertexAO(terrain, side1, side2, corner);

		// the block light
		byte l1, l2, l3;

		l1 = terrain.getBlockLight(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		l2 = terrain.getBlockLight(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		l3 = terrain.getBlockLight(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);
		float blockLight = (l1 + l2 + l3) / (3.0f * 16.0f);

		// the sun light
		l1 = terrain.getSunLight(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		l2 = terrain.getSunLight(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		l3 = terrain.getSunLight(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);
		float sunLight = (l1 + l2 + l3) / (3.0f * 16.0f); // TODO : push the
															// sunlight
		// value to a specific vertex
		// attributes to deal with time
		// (sunlight on night == 0...)

		// final brightness
		float brightness = 1 + blockLight - ao;

		// light color
		int color = 0xFFFFFFFF;// ColorInt.get(255, 255, 255, 255);
		return (new MeshVertex(px, py, pz, face.getNormal(), atlasX, atlasY, uvx, uvy, color, brightness, ao));
	}

	// the float returned is the ratio of black which will be used for this
	// vertex
	protected static final float AO_0 = 0.00f;
	protected static final float AO_1 = 0.16f;
	protected static final float AO_2 = 0.32f;
	protected static final float AO_3 = 0.42f;

	protected float getVertexAO(Terrain terrain, Block side1, Block side2, Block corner) {
		boolean s1 = side1.isVisible();
		boolean s2 = side2.isVisible();
		boolean c = corner.isVisible();
		if (s1 && s2) {
			return (AO_3);
		}

		if (s1 || s2) {
			return (c ? AO_2 : AO_1);
		}

		return (c ? AO_1 : AO_0);
	}

	@Override
	public boolean hasTransparency() {
		return (false);
	}
}
