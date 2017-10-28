package com.grillecube.client.renderer.blocks;

import java.util.ArrayList;

import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.client.renderer.world.flat.BlockFace;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the default cube renderer */
public class BlockRendererJSON extends BlockRenderer {

	@Override
	public void generateBlockVertices(TerrainMesher terrainMesher, Terrain terrain, Block block, int x, int y, int z,
			BlockFace[][][][] faces, ArrayList<TerrainMeshTriangle> stack) {
		// TODO Auto-generated method stub

	}
}
