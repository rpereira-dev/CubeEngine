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

package com.grillecube.client.renderer.world;

import java.nio.ByteBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.world.flat.terrain.BlockFace;
import com.grillecube.client.resources.BlockRendererManager;
import com.grillecube.common.faces.Face;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.terrain.Terrain;

/** an object which is used to generate terrain meshes dynamically */
public abstract class TerrainMesher {

	/** number of block is need to know how to calculate UVs */
	public TerrainMesher() {
	}

	public final void generateVertices(Terrain terrain, TerrainMesh opaqueMesh, TerrainMesh transparentMesh) {
		Stack<TerrainMeshVertex> opaqueStack = new Stack<TerrainMeshVertex>();
		Stack<TerrainMeshVertex> transparentStack = new Stack<TerrainMeshVertex>();
		this.fillVertexStacks(terrain, opaqueStack, transparentStack);
		this.setMeshVertex(opaqueMesh, opaqueStack);
		this.setMeshVertex(transparentMesh, transparentStack);
	}

	private final void setMeshVertex(TerrainMesh mesh, Stack<TerrainMeshVertex> stack) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(stack.size() * TerrainMesh.BYTES_PER_VERTEX);
		for (TerrainMeshVertex vertex : stack) {
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
		mesh.setVertices(buffer);
	}

	/**
	 * generate a stack which contains every vertices ordered to render back
	 * face culled triangles
	 */
	protected abstract void fillVertexStacks(Terrain terrain, Stack<TerrainMeshVertex> opaqueStack,
			Stack<TerrainMeshVertex> transparentStack);

	/**
	 * return an array which contains standart block faces informations. Non
	 * cubic blocks are pushed to the stack
	 */
	protected final BlockFace[][][][] getFacesVisibility(Terrain terrain, Stack<TerrainMeshVertex> opaqueStack,
			Stack<TerrainMeshVertex> transparentStack) {

		short[] blocks = terrain.getRawBlocks();

		if (blocks == null) {
			return (null);
		}

		BlockFace[][][][] faces = new BlockFace[Face.faces.length][Terrain.DIMX][Terrain.DIMY][Terrain.DIMZ];

		// for each block
		int index = 0; // x + Terrain.DIM * (y + Terrain.DIM * z)
		for (int z = 0; z < Terrain.DIMZ; ++z) {
			for (int y = 0; y < Terrain.DIMY; ++y) {
				for (int x = 0; x < Terrain.DIMX; ++x) {
					Block block = Blocks.getBlockByID(blocks[index]);

					if (block == null) {
						continue;
					}

					// if the block is visible
					if (block.isVisible()) {
						BlockRenderer blockRenderer = BlockRendererManager.instance().getBlockRenderer(block);
						blockRenderer.generateBlockVertices(this, terrain, block, x, y, z, faces,
								block.isOpaque() ? opaqueStack : transparentStack);
					}
					++index;
				}
			}
		}
		return (faces);
	}
}
