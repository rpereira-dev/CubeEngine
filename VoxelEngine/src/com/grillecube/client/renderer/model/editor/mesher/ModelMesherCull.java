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

import java.util.Stack;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;

public class ModelMesherCull extends ModelMesher {

	public final void generate(ModelBuildingData modelBuildingData) {
		// prepare the mesh vertex stack
		Stack<ModelMeshVertex> vertices = new Stack<ModelMeshVertex>();
		Stack<Short> indices = new Stack<Short>();

		for (Face face : Face.faces) {
			for (int x = 0; x < modelBuildingData.getSizeX(); x++) {
				for (int y = 0; y < modelBuildingData.getSizeY(); y++) {
					for (int z = 0; z < modelBuildingData.getSizeZ(); z++) {
						ModelBlockData modelBlockData = modelBuildingData.getBlockData(x, y, z);
						if (modelBlockData == null) {
							continue;
						}
						Vector3i d = face.getVector();
						ModelBlockData neighbor = modelBuildingData.getBlockData(x + d.x, y + d.y, z + d.z);
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
						vertices.add(this.generateVertex(modelBuildingData, face, modelBlockData, x, y, z, 0));
						vertices.add(this.generateVertex(modelBuildingData, face, modelBlockData, x, y, z, 1));
						vertices.add(this.generateVertex(modelBuildingData, face, modelBlockData, x, y, z, 2));
						vertices.add(this.generateVertex(modelBuildingData, face, modelBlockData, x, y, z, 3));
					}
				}
			}
		}
	}

	private final ModelMeshVertex generateVertex(ModelBuildingData modelBuildingData, Face face,
			ModelBlockData modelBlockData, int x, int y, int z, int vertexID) {

		ModelMeshVertex vertex = new ModelMeshVertex();

		float sizeUnit = modelBuildingData.getBlockSizeUnit();

		vertex.x = (x + BlockRenderer.FACES_VERTICES[face.getID()][vertexID].x) * sizeUnit;
		vertex.y = (y + BlockRenderer.FACES_VERTICES[face.getID()][vertexID].y) * sizeUnit;
		vertex.z = (z + BlockRenderer.FACES_VERTICES[face.getID()][vertexID].z) * sizeUnit;

		vertex.uvx = 0;
		vertex.uvy = 0;

		vertex.nx = face.getNormal().getX();
		vertex.ny = face.getNormal().getY();
		vertex.nz = face.getNormal().getZ();

		vertex.j1 = modelBlockData.getJoint(0);
		vertex.j2 = modelBlockData.getJoint(1);
		vertex.j3 = modelBlockData.getJoint(2);

		vertex.w1 = modelBlockData.getJointWeight(0);
		vertex.w2 = modelBlockData.getJointWeight(1);
		vertex.w3 = modelBlockData.getJointWeight(2);

		return (vertex);
	}
}
