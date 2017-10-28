package com.grillecube.client.renderer.blocks;

import java.util.ArrayList;

import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMeshVertex;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.client.renderer.world.flat.BlockFace;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the default cube renderer */
public class BlockRendererPlant extends BlockRenderer {

	/** the texture ids */
	private int textureID;

	private BlockRendererPlant() {
	}

	/**
	 * @param textureID
	 *            : plant texture id
	 */
	public BlockRendererPlant(int textureID) {
		this();
		this.textureID = textureID;
	}

	/** return the array of texture for the given block */
	public final int getTextureID() {
		return (this.textureID);
	}

	@Override
	public void generateBlockVertices(TerrainMesher terrainMesher, Terrain terrain, Block block, int x, int y, int z,
			BlockFace[][][][] faces, ArrayList<TerrainMeshTriangle> stack) {
		for (Face face : Face.faces) {
			Vector3i vec = face.getVector();
			Block neighbor = terrain.getBlock(x + vec.x, y + vec.y, z + vec.z);
			if (!neighbor.isVisible() || neighbor.isTransparent()) {
				this.createPlantVertices(terrain, block, x, y, z, stack);
				break;
			}
		}
	}

	private final void createPlantVertices(Terrain terrain, Block block, int x, int y, int z,
			ArrayList<TerrainMeshTriangle> stack) {
		TerrainMeshVertex v0 = this.createBlockFaceVertex(terrain, Face.F_RIGHT, x, y, z, 0);
		TerrainMeshVertex v1 = this.createBlockFaceVertex(terrain, Face.F_RIGHT, x, y, z, 1);
		TerrainMeshVertex v2 = this.createBlockFaceVertex(terrain, Face.F_RIGHT, x, y, z, 2);
		TerrainMeshVertex v3 = this.createBlockFaceVertex(terrain, Face.F_RIGHT, x, y, z, 3);
		TerrainMeshVertex v4 = this.createBlockFaceVertex(terrain, Face.F_FRONT, x, y, z, 0);
		TerrainMeshVertex v5 = this.createBlockFaceVertex(terrain, Face.F_FRONT, x, y, z, 1);
		TerrainMeshVertex v6 = this.createBlockFaceVertex(terrain, Face.F_FRONT, x, y, z, 2);
		TerrainMeshVertex v7 = this.createBlockFaceVertex(terrain, Face.F_FRONT, x, y, z, 3);

		stack.add(new TerrainMeshTriangle(v0, v1, v2));
		stack.add(new TerrainMeshTriangle(v0, v2, v3));

		stack.add(new TerrainMeshTriangle(v4, v5, v6));
		stack.add(new TerrainMeshTriangle(v4, v6, v7));
	}

	/**
	 * return the vertex for the given vertex ID at the given coordinates
	 */
	public TerrainMeshVertex createBlockFaceVertex(Terrain terrain, Face face, int x, int y, int z, int vertexID) {
		Vector3i[] neighboors = FACES_NEIGHBORS[face.getID()][vertexID];

		// position
		float px = (x + FACES_VERTICES[face.getID()][vertexID].x - face.getNormal().x * 0.5f) * Terrain.BLOCK_SIZE;
		float py = (y + FACES_VERTICES[face.getID()][vertexID].y) * Terrain.BLOCK_SIZE;
		float pz = (z + FACES_VERTICES[face.getID()][vertexID].z - face.getNormal().z * 0.5f) * Terrain.BLOCK_SIZE;

		// uv
		float uvx = FACES_UV[vertexID][0];
		float uvy = FACES_UV[vertexID][1];
		float atlasX = super.getAtlasX(textureID);
		float atlasY = super.getAtlasY(textureID);

		// get light value

		// the ambiant occlusion
		Block side1 = terrain.getBlock(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		Block side2 = terrain.getBlock(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		Block corner = terrain.getBlock(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);
		float ao = 0;// this.getVertexAO(terrain, side1, side2, corner);

		// the block light
		byte l1, l2, l3;

		l1 = terrain.getBlockLight(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		l2 = terrain.getBlockLight(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		l3 = terrain.getBlockLight(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);
		float blockLight = (l1 + l2 + l3) / (3.0f * 16.0f);

		// the sun light
		l1 = terrain.getSunLight(x + neighboors[0].x, y + Maths.abs(neighboors[0].y), z + neighboors[0].z);
		l2 = terrain.getSunLight(x + neighboors[1].x, y + Maths.abs(neighboors[1].y), z + neighboors[1].z);
		l3 = terrain.getSunLight(x + neighboors[2].x, y + Maths.abs(neighboors[2].y), z + neighboors[2].z);
		float sunLight = (l1 + l2 + l3) / (3.0f * 16.0f);

		// final brightness
		float brightness = 0.1f + sunLight + blockLight - ao;
		if (brightness < 0.0f) {
			brightness = 0.0f;
		}

		// light color
		int color = 0xFFFFFFFF;// ColorInt.get(255, 255, 255, 255);
		return (new TerrainMeshVertex(px, py, pz, face.getNormal(), atlasX, atlasY, uvx, uvy, color, brightness, ao));
	}
}
