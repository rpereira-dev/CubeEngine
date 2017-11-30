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

import com.grillecube.client.renderer.model.ModelMesh;
import com.grillecube.client.renderer.model.ModelMeshVertex;
import com.grillecube.client.renderer.model.ModelSkin;

/** an object which is used to generate model meshes dynamically */
public abstract class ModelMesher {

	/** number of block is need to know how to calculate UVs */
	public ModelMesher() {
	}

	public final void generate(EditableModel editableModel, EditableModelLayer modelLayer) {

		// if empty model
		if (modelLayer.getBlockDataCount() == 0) {
			modelLayer.setVertices(null);
			modelLayer.setIndices(null);
			return;
		}

		// prepare the mesh vertex stack
		Stack<ModelMeshVertex> vertexStack = new Stack<ModelMeshVertex>();
		Stack<Short> indicesStack = new Stack<Short>();
		HashMap<ModelSkin, BufferedImage> skinsData = new HashMap<ModelSkin, BufferedImage>();

		// do the generation
		this.doGenerate(editableModel, modelLayer, vertexStack, indicesStack, skinsData);

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
		modelLayer.setVertices(vertices);
		modelLayer.setIndices(indices);
	}

	protected abstract void doGenerate(EditableModel editableModel, EditableModelLayer modelLayer,
			Stack<ModelMeshVertex> vertices, Stack<Short> indices, HashMap<ModelSkin, BufferedImage> skinsData);

	/** merge editable model layers into the final mesh */
	public final void mergeLayers(EditableModel editableModel) {
		// if empty model
		if (editableModel.getBlockDataCount() == 0) {
			editableModel.getMesh().setVertices(null);
			editableModel.getMesh().setIndices(null);
			for (ModelSkin modelSkin : editableModel.getSkins()) {
				modelSkin.getGLTexture().setData(null);
			}
			return;
		}

		// else
		// ByteBuffer vertices, indices;
		// editableModel.getMesh().setVertices(vertices);
		// editableModel.getMesh().setIndices(indices);
		// for (ModelSkin modelSkin : editableModel.getSkins()) {
		// BufferedImage skin = skinsData.get(modelSkin);
		// if (skin == null) {
		// Logger.get().log(Logger.Level.WARNING, "ModelMesher didn't generate
		// every ModelSkins BufferedImage: "
		// + skinsData.size() + "/" + editableModel.getSkins().size());
		//
		// }
		// // may cause error if buffered image has a too high resolution
		// modelSkin.getGLTexture().setData(skin);
		// }
	}
}