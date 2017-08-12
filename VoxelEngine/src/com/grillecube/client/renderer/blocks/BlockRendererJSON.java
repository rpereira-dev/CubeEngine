package com.grillecube.client.renderer.blocks;

import java.util.Stack;

import com.grillecube.client.renderer.world.terrain.BlockFace;
import com.grillecube.client.renderer.world.terrain.TerrainMeshVertex;
import com.grillecube.client.renderer.world.terrain.TerrainMesher;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the default cube renderer */
public class BlockRendererJSON extends BlockRenderer {

	@Override
	public void generateBlockVertices(TerrainMesher terrainMesher, Terrain terrain, Block block, int x, int y, int z,
			BlockFace[][][][] faces, Stack<TerrainMeshVertex> stack) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasTransparency() {
		// TODO Auto-generated method stub
		return false;
	}
	// TODO
}
