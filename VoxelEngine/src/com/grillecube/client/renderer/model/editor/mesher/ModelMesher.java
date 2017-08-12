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

package com.grillecube.client.renderer.model.editor.mesher;

import java.nio.ByteBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;

import com.grillecube.client.renderer.model.ModelMesh;

/** an object which is used to generate model meshes dynamically */
public abstract class ModelMesher {

	private ByteBuffer vertices;
	private ByteBuffer indices;

	/** number of block is need to know how to calculate UVs */
	public ModelMesher() {
	}

	public abstract void generate(EditableModel modelBuildingData);

	public final ByteBuffer getVertices() {
		return (this.vertices);
	}

	public final ByteBuffer getIndices() {
		return (this.indices);
	}

	protected final void setIndices(Stack<Short> indexStack) {
		this.indices = BufferUtils.createByteBuffer(indexStack.size() * 2);
		for (Short index : indexStack) {
			this.indices.putShort(index);
		}
		this.indices.flip();
	}

	protected final void setVertices(Stack<ModelMeshVertex> vertexStack) {
		this.vertices = BufferUtils.createByteBuffer(vertexStack.size() * ModelMesh.BYTES_PER_VERTEX);
		for (ModelMeshVertex vertex : vertexStack) {
			this.vertices.putFloat(vertex.x);
			this.vertices.putFloat(vertex.y);
			this.vertices.putFloat(vertex.z);
			this.vertices.putFloat(vertex.uvx);
			this.vertices.putFloat(vertex.uvy);
			this.vertices.putFloat(vertex.nx);
			this.vertices.putFloat(vertex.ny);
			this.vertices.putFloat(vertex.nz);
			this.vertices.putInt(vertex.j1);
			this.vertices.putInt(vertex.j2);
			this.vertices.putInt(vertex.j3);
			this.vertices.putFloat(vertex.w1);
			this.vertices.putFloat(vertex.w2);
			this.vertices.putFloat(vertex.w3);
		}
		this.vertices.flip();
	}
}
