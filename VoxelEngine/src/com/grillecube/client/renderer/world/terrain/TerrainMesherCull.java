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

package com.grillecube.client.renderer.world.terrain;

import java.util.Stack;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.resources.BlockRendererManager;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.faces.Face;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

/** the simplest terrain mesher (cull colliding faces) */
public class TerrainMesherCull extends TerrainMesher {

	/**
	 * fill an array of dimension
	 * [Terrain.BLOCK_SIZEIZE_X][Terrain.BLOCK_SIZEIZE_Y][Terrain.
	 * BLOCK_SIZEIZE_Z][6] of terrain faces visibility
	 */
	protected Stack<TerrainMeshVertex> getVertexStack(Terrain terrain) {
		// prepare the mesh vertex stack
		Stack<TerrainMeshVertex> stack = new Stack<TerrainMeshVertex>();

		// get visibile faces
		BlockFace[][][][] faces = this.getFacesVisibility(terrain, stack);
		if (faces == null) {
			return (stack);
		}

		// for each face
		for (Face face : Face.faces) {
			for (int z = 0; z < Terrain.DIM; z++) {
				for (int y = 0; y < Terrain.DIM; y++) {
					for (int x = 0; x < Terrain.DIM; x++) {
						BlockFace blockFace = faces[face.getID()][x][y][z];
						if (blockFace == null) {
							continue;
						}
						// Vector3i n = face.getVector();
						// if (faces[face.getOpposite().getID()][x + n.x][y +
						// n.y][z + n.z] != null) {
						// continue;
						// }
						blockFace.pushVertices(stack);
					}
				}
			}
		}

		return (stack);

	}

	/**
	 * return an array which contains standart block faces informations. Non
	 * cubic blocks are pushed to the stack
	 */
	protected BlockFace[][][][] getFacesVisibility(Terrain terrain, Stack<TerrainMeshVertex> stack) {

		short[] blocks = terrain.getRawBlocks();

		if (blocks == null) {
			return (null);
		}

		BlockFace[][][][] faces = new BlockFace[Face.faces.length][Terrain.DIM][Terrain.DIM][Terrain.DIM];

		// for each block
		int index = 0; // x + Terrain.DIM * (y + Terrain.DIM * z)
		for (int z = 0; z < Terrain.DIM; ++z) {
			for (int y = 0; y < Terrain.DIM; ++y) {
				for (int x = 0; x < Terrain.DIM; ++x) {
					Block block = Blocks.getBlockByID(blocks[index]);

					if (block == null) {
						continue;
					}

					// if the block is visible
					if (block.isVisible()) {
						BlockRenderer blockRenderer = BlockRendererManager.instance().getBlockRenderer(block);
						blockRenderer.generateBlockVertices(this, terrain, block, x, y, z, faces, stack);
					}
					++index;
				}
			}
		}
		return (faces);
	}

}
