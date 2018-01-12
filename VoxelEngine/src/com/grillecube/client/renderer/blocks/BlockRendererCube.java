package com.grillecube.client.renderer.blocks;

import java.util.ArrayList;

import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMeshVertex;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.client.renderer.world.flat.BlockFace;
import com.grillecube.common.Logger;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.block.Block;

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
	 *            ClientBlocks.T_GRASS_SIDE, Face.RIGHT, ClientBlocks.T_GRASS_SIDE,
	 *            Face.FRONT, ClientBlocks.T_GRASS_SIDE, Face.BACK,
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
			BlockFace[][][][] faces, ArrayList<TerrainMeshTriangle> stack) {

		for (Face face : Face.faces) {
			faces[face.getID()][x][y][z] = this.createBlockFace(terrain, block, face, x, y, z);
		}
	}

	private final BlockFace createBlockFace(Terrain terrain, Block block, Face face, int x, int y, int z) {

		// if the face-neighboor block is visible isnt transparent
		if (!this.canRenderFace(terrain, block, face, x, y, z)) {
			// the face isnt visible
			return (null);
		}

		byte durability = terrain.getDurability(x, y, z);

		// else the face is visible, create it!
		TerrainMeshVertex v0 = this.createBlockFaceVertex(terrain, face, x, y, z, 0);
		TerrainMeshVertex v1 = this.createBlockFaceVertex(terrain, face, x, y, z, 1);
		TerrainMeshVertex v2 = this.createBlockFaceVertex(terrain, face, x, y, z, 2);
		TerrainMeshVertex v3 = this.createBlockFaceVertex(terrain, face, x, y, z, 3);
		BlockFace blockface = new BlockFace(block, this.textureIDs[face.getID()], durability, v0, v1, v2, v3);
		return (blockface);

	}

	protected boolean canRenderFace(Terrain terrain, Block block, Face face, int x, int y, int z) {
		Vector3i vec = face.getVector();

		// get the neighbor of this face
		Block neighbor = terrain.getBlock(x + vec.x, y + vec.y, z + vec.z);

		return (!neighbor.isVisible() || neighbor.isTransparent());
	}

	/**
	 * return the vertex for the given face at the given coordinates, for it given
	 * id
	 */
	public TerrainMeshVertex createBlockFaceVertex(Terrain terrain, Face face, int x, int y, int z, int faceVertexID) {

		int vertexID = FACES_VERTICES[face.getID()][faceVertexID];

		// position
		float px = (x + VERTICES[vertexID].x);
		float py = (y + VERTICES[vertexID].y);
		float pz = (z + VERTICES[vertexID].z);

		// uv
		float u = FACES_UV[faceVertexID][0];
		float v = FACES_UV[faceVertexID][1];
		int textureID = this.textureIDs[face.getID()];
		float atlasX = super.getAtlasX(textureID);
		float atlasY = super.getAtlasY(textureID);

		// get light value

		// the ambiant occlusion
		float ao = BlockRenderer.getAmbiantOcclusion(terrain, x, y, z, face.getID(), faceVertexID);

		// the block light
		float blockLight = super.getBlockLight(terrain, x, y, z, face.getID(), faceVertexID);

		// the sun light
		float sunLight = super.getSunLight(terrain, x, y, z, face.getID(), faceVertexID);

		// durability
		byte durability = terrain.getDurability(x, y, z);

		// final brightness
		float brightness = 0.1f + sunLight + blockLight - ao;
		if (brightness < 0.0f) {
			brightness = 0.0f;
		}

		// light color
		int color = 0xFFFFFFFF;// ColorInt.get(255, 255, 255, 255);
		float nx = face.getNormal().x;
		float ny = face.getNormal().y;
		float nz = face.getNormal().z;
		return (new TerrainMeshVertex(px, py, pz, nx, ny, nz, atlasX, atlasY, u, v, color, brightness, ao, durability));
	}

	@Override
	public int getDefaultTextureID(int faceID) {
		return (this.textureIDs[faceID]);
	}
}
