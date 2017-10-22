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
import java.util.ArrayList;
import java.util.Stack;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;

public class ModelMesherCull extends ModelMesher {

	public final void doGenerate(EditableModel editableModel, Stack<ModelMeshVertex> vertices, Stack<Short> indices,
			ArrayList<BufferedImage> skinsData) {
		
		//TODO : generate model plans
		
		//TODO : generate skins from it
		
		//TODO

		for (Face face : Face.faces) {
			for (int x = editableModel.getMinX(); x <= editableModel.getMaxX(); x++) {
				for (int y = editableModel.getMinY(); y <= editableModel.getMaxY(); y++) {
					for (int z = editableModel.getMinZ(); z <= editableModel.getMaxZ(); z++) {
						BlockData modelBlockData = editableModel.getBlockData(x, y, z);
						if (modelBlockData == null) {
							continue;
						}
						Vector3i d = face.getVector();
						BlockData neighbor = editableModel.getBlockData(x + d.x, y + d.y, z + d.z);
						if (neighbor != null) {
							continue;
						}

						// add indices (quad)
						int i = vertices.size();
						indices.add((short) i);
						indices.add((short) (i + 1));
						indices.add((short) (i + 2));
						indices.add((short) i);
						indices.add((short) (i + 2));
						indices.add((short) (i + 3));

						// add vertices
						vertices.add(this.generateVertex(editableModel, face, modelBlockData, x, y, z, 0));
						vertices.add(this.generateVertex(editableModel, face, modelBlockData, x, y, z, 1));
						vertices.add(this.generateVertex(editableModel, face, modelBlockData, x, y, z, 2));
						vertices.add(this.generateVertex(editableModel, face, modelBlockData, x, y, z, 3));
					}
				}
			}
		}
	}

	private final ModelMeshVertex generateVertex(EditableModel editableModel, Face face, BlockData modelBlockData,
			int x, int y, int z, int vertexID) {

		ModelMeshVertex vertex = new ModelMeshVertex();

		float sizeUnit = editableModel.getBlockSizeUnit();

		vertex.x = (x + BlockRenderer.FACES_VERTICES[face.getID()][vertexID].x) * sizeUnit;
		vertex.y = (y + BlockRenderer.FACES_VERTICES[face.getID()][vertexID].y) * sizeUnit;
		vertex.z = (z + BlockRenderer.FACES_VERTICES[face.getID()][vertexID].z) * sizeUnit;

		vertex.uvx = 0;
		vertex.uvy = 0;

		vertex.nx = face.getNormal().getX();
		vertex.ny = face.getNormal().getY();
		vertex.nz = face.getNormal().getZ();

		vertex.b1 = modelBlockData.getBoneID(0);
		vertex.b2 = modelBlockData.getBoneID(1);
		vertex.b3 = modelBlockData.getBoneID(2);

		vertex.w1 = modelBlockData.getBoneWeight(0);
		vertex.w2 = modelBlockData.getBoneWeight(1);
		vertex.w3 = modelBlockData.getBoneWeight(2);

		return (vertex);
	}
}
