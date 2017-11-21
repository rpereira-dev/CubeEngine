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
import java.util.HashMap;
import java.util.Stack;

import org.lwjgl.BufferUtils;

import com.grillecube.client.renderer.model.EditableModel;
import com.grillecube.client.renderer.model.ModelMesh;
import com.grillecube.client.renderer.model.ModelMeshVertex;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.common.Logger;

/** an object which is used to generate model meshes dynamically */
public abstract class ModelMesher {

	/** number of block is need to know how to calculate UVs */
	public ModelMesher() {
	}

	public final void generate(EditableModel editableModel) {

		// prepare the mesh vertex stack
		Stack<ModelMeshVertex> vertexStack = new Stack<ModelMeshVertex>();
		Stack<Short> indicesStack = new Stack<Short>();
		HashMap<ModelSkin, BufferedImage> skinsData = new HashMap<ModelSkin, BufferedImage>();

		// do the generation
		this.doGenerate(editableModel, vertexStack, indicesStack, skinsData);

		// stack to buffer
		ByteBuffer vertices = BufferUtils.createByteBuffer(vertexStack.size() * ModelMesh.BYTES_PER_VERTEX);
		for (ModelMeshVertex vertex : vertexStack) {
			vertex.store(vertices);
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
		for (ModelSkin modelSkin : editableModel.getSkins()) {
			BufferedImage skin = skinsData.get(modelSkin);
			if (skin == null) {
				Logger.get().log(Logger.Level.WARNING, "ModelMesher didn't generate every ModelSkins BufferedImage: "
						+ skinsData.size() + "/" + editableModel.getSkins().size());

			}
			modelSkin.getGLTexture().setData(skin);
		}
	}

	protected abstract void doGenerate(EditableModel editableModel, Stack<ModelMeshVertex> vertices,
			Stack<Short> indices, HashMap<ModelSkin, BufferedImage> skinsData);
}