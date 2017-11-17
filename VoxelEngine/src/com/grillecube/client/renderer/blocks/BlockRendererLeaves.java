package com.grillecube.client.renderer.blocks;

import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.block.Block;

public class BlockRendererLeaves extends BlockRendererCube {

	public BlockRendererLeaves(int textureID) {
		super(textureID);
	}

	@Override
	protected boolean canRenderFace(Terrain terrain, Block block, Face face, int x, int y, int z) {
		Vector3i vec = face.getVector();
		// get the neighbor of this face
		Block neighbor = terrain.getBlock(x + vec.x, y + vec.y, z + vec.z);

		return (!neighbor.isVisible());
	}

}
