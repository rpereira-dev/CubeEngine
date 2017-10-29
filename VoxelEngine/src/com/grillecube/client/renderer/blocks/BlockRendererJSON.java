package com.grillecube.client.renderer.blocks;

import java.util.ArrayList;

import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMeshVertex;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.client.renderer.world.flat.BlockFace;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the default cube renderer */
public class BlockRendererJSON extends BlockRenderer {

	private final ArrayList<TerrainMeshTriangle> triangles;

	public BlockRendererJSON(String filepath) {
		super();
		this.triangles = new ArrayList<TerrainMeshTriangle>();
		// TODO : parse the file and generate local triangles
	}

	@Override
	public void generateBlockVertices(TerrainMesher terrainMesher, Terrain terrain, Block block, int x, int y, int z,
			BlockFace[][][][] faces, ArrayList<TerrainMeshTriangle> stack) {
		for (TerrainMeshTriangle triangle : this.triangles) {
			TerrainMeshTriangle clone = (TerrainMeshTriangle) triangle.clone();
			this.offsetVertex(clone.v0, x, y, z);
			this.offsetVertex(clone.v1, x, y, z);
			this.offsetVertex(clone.v2, x, y, z);
			stack.add(clone);
		}
	}

	private final void offsetVertex(TerrainMeshVertex v, int x, int y, int z) {
		// offset position
		v.posx = (v.posx + x) * Terrain.BLOCK_SIZE;
		v.posy = (v.posy + y) * Terrain.BLOCK_SIZE;
		v.posz = (v.posz + z) * Terrain.BLOCK_SIZE;

		// TODO : light (using vertex normal)
	}
}
