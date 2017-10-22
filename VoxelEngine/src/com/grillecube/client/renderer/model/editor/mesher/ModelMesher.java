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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;

import org.lwjgl.BufferUtils;

import com.grillecube.client.renderer.model.ModelMesh;

/** an object which is used to generate model meshes dynamically */
public abstract class ModelMesher {

	/** number of block is need to know how to calculate UVs */
	public ModelMesher() {
	}

	public final void generate(EditableModel editableModel) {
		// prepare the mesh vertex stack
		Stack<ModelMeshVertex> vertexStack = new Stack<ModelMeshVertex>();
		Stack<Short> indicesStack = new Stack<Short>();
		ArrayList<BufferedImage> skinsData = new ArrayList<BufferedImage>();

		// do the generation
		this.doGenerate(editableModel, vertexStack, indicesStack, skinsData);

		// stack to buffer
		ByteBuffer vertices = BufferUtils.createByteBuffer(vertexStack.size() * ModelMesh.BYTES_PER_VERTEX);
		for (ModelMeshVertex vertex : vertexStack) {
			vertices.putFloat(vertex.x);
			vertices.putFloat(vertex.y);
			vertices.putFloat(vertex.z);
			vertices.putFloat(vertex.uvx);
			vertices.putFloat(vertex.uvy);
			vertices.putFloat(vertex.nx);
			vertices.putFloat(vertex.ny);
			vertices.putFloat(vertex.nz);
			vertices.putInt(vertex.b1);
			vertices.putInt(vertex.b2);
			vertices.putInt(vertex.b3);
			vertices.putFloat(vertex.w1);
			vertices.putFloat(vertex.w2);
			vertices.putFloat(vertex.w3);
		}
		vertices.flip();

		// stack to buffer
		ByteBuffer indices = BufferUtils.createByteBuffer(indicesStack.size() * 2);
		for (Short index : indicesStack) {
			indices.putShort(index);
		}
		indices.flip();

		// set model data
		editableModel.getMesh().setVertices(vertices);
		editableModel.getMesh().setIndices(indices);
		for (int i = 0; i < skinsData.size(); i++) {
			editableModel.getSkin(i).getGLTexture().setData(skinsData.get(i));
		}
	}

	protected abstract void doGenerate(EditableModel editableModel, Stack<ModelMeshVertex> vertices,
			Stack<Short> indices, ArrayList<BufferedImage> skinsData);
}
