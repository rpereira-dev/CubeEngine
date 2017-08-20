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

package com.grillecube.client.renderer.terrain;

import java.nio.ByteBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;

import com.grillecube.common.world.terrain.Terrain;

/** an object which is used to generate terrain meshes dynamically */
public abstract class TerrainMesher {

	/** number of block is need to know how to calculate UVs */
	public TerrainMesher() {
	}

	public final ByteBuffer generateVertices(Terrain terrain) {

		Stack<TerrainMeshVertex> stack = this.getVertexStack(terrain);
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
		return (buffer);
	}

	/**
	 * generate a stack which contains every vertices ordered to render back
	 * face culled triangles
	 */
	protected abstract Stack<TerrainMeshVertex> getVertexStack(Terrain terrain);
}
