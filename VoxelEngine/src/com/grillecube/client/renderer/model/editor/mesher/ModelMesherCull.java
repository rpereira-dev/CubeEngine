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
import com.grillecube.common.Logger;
import com.grillecube.common.faces.Face;

public class ModelMesherCull extends ModelMesher {

	/** generate the model planes, skins, and mesh */
	public final void doGenerate(EditableModel editableModel, Stack<ModelMeshVertex> vertices, Stack<Short> indices,
			ArrayList<BufferedImage> skinsData) {
		long t = System.currentTimeMillis();

		// GENERATE MODEL PLANS LIST
		ArrayList<ModelPlane> planes = this.generateModelPlans(editableModel);

		// TODO : GENERATE TEXTURE AND UV FROM PLANES
		

		// GENERATE MESH FROM PLANS
		this.generateMesh(editableModel, planes, vertices, indices);

		Logger.get().log(Logger.Level.DEBUG, "generation took: " + (System.currentTimeMillis() - t));
	}

	/** generate the model mesh */
	private final void generateMesh(EditableModel editableModel, ArrayList<ModelPlane> planes,
			Stack<ModelMeshVertex> vertices, Stack<Short> indices) {
		ModelBlockData[][][] datas = editableModel.getRawBlockDatas();

		for (ModelPlane plane : planes) {
			for (int dx = 0; dx < plane.getWidth(); dx++) {
				for (int dy = 0; dy < plane.getHeight(); dy++) {
					for (int dz = 0; dz < plane.getDepth(); dz++) {

						int x = plane.getXMin() + dx;
						int y = plane.getYMin() + dy;
						int z = plane.getZMin() + dz;

						ModelBlockData data = datas[x][y][z];

						// add indices (quad)
						int i = vertices.size();
						indices.add((short) i);
						indices.add((short) (i + 1));
						indices.add((short) (i + 2));
						indices.add((short) i);
						indices.add((short) (i + 2));
						indices.add((short) (i + 3));

						// add vertices
						vertices.push(this.generateVertex(editableModel, plane.getFace(), data, 0));
						vertices.push(this.generateVertex(editableModel, plane.getFace(), data, 1));
						vertices.push(this.generateVertex(editableModel, plane.getFace(), data, 2));
						vertices.push(this.generateVertex(editableModel, plane.getFace(), data, 3));
					}
				}
			}
		}
	}

	private final ArrayList<ModelPlane> generateModelPlans(EditableModel editableModel) {
		ModelBlockData[][][] blockDatas = editableModel.getRawBlockDatas();
		int sx = editableModel.getSizeX();
		int sy = editableModel.getSizeY();
		int sz = editableModel.getSizeZ();

		// extract plans
		ArrayList<ModelPlane> modelPlanes = new ArrayList<ModelPlane>();
		boolean[][][][] visited = new boolean[Face.faces.length][sx][sy][sz];

		// for each face
		for (Face face : Face.faces) {

			for (int z = 0; z < sz; z++) {
				for (int y = 0; y < sy; y++) {
					for (int x = 0; x < sx; x++) {
						ModelBlockData blockData = blockDatas[x][y][z];
						if (blockData == null) {
							continue;
						}

						int faceID = face.getID();

						// if already visited, continue
						if (visited[faceID][x][y][z]) {
							continue;
						}
						// else, set visited and find the plan
						visited[faceID][x][y][z] = true;

						// get the neighbor block, to ensure that this face is
						// actually visible
						int nx = x + face.getVector().x;
						int ny = y + face.getVector().y;
						int nz = z + face.getVector().z;
						boolean isFaceVisible = (nx < 0 || ny < 0 || nz < 0 || nx >= sx || ny >= sy || nz >= sz)
								|| blockDatas[nx][ny][nz] == null;
						if (!isFaceVisible) {
							continue;
						}

						// then this face is visible, generate plans
						// TOP OR BOT FACE
						if (faceID == Face.TOP || faceID == Face.BOT) {
							int width = 1;
							int depth = 1;

							// generate the rectangle width
							while (x + width < sx && !visited[faceID][x + width][y][z]
									&& blockDatas[x + width][y][z] != null) {
								++width;
							}

							// generate the rectangle depth
							depth_test: while (z + depth < sz) {
								for (int dx = 0; dx < width; dx++) {
									if (visited[faceID][x + dx][y][z + depth]
											|| blockDatas[x + dx][y][z + depth] == null) {
										break depth_test;
									}
								}
								++depth;
							}
							modelPlanes.add(new ModelPlane(face, x, y, z, width, 1, depth));
							this.setVisited(visited, faceID, x, y, z, width, 1, depth);
							x += width - 1;
						}
						// RIGHT OR LEFT FACE
						else if (faceID == Face.RIGHT || faceID == Face.LEFT) {
							int width = 1;
							int height = 1;

							// generate the rectangle width
							while (x + width < sx && !visited[faceID][x + width][y][z]
									&& blockDatas[x + width][y][z] != null) {
								++width;
							}

							// generate the rectangle depth
							height_test: while (y + height < sy) {
								for (int dx = 0; dx < width; dx++) {
									if (visited[faceID][x + dx][y + height][z]
											|| blockDatas[x + dx][y + height][z] == null) {
										break height_test;
									}
								}
								++height;
							}
							modelPlanes.add(new ModelPlane(face, x, y, z, width, height, 1));
							this.setVisited(visited, faceID, x, y, z, width, height, 1);
							x += width - 1;
						}
						// ELSE : FRONT OR BACK
						else {

							int depth = 1;
							int height = 1;

							// generate the rectangle width
							while (z + depth < sz && !visited[faceID][x][y][z + depth]
									&& blockDatas[x][y][z + depth] != null) {
								++depth;
							}

							// generate the rectangle depth
							height_test: while (y + height < sy) {
								for (int dz = 0; dz < depth; dz++) {
									if (visited[faceID][x][y + height][z + dz]
											|| blockDatas[x][y + height][z + dz] == null) {
										break height_test;
									}
								}
								++height;
							}
							modelPlanes.add(new ModelPlane(face, x, y, z, 1, height, depth));
							this.setVisited(visited, faceID, x, y, z, 1, height, depth);
						}
					}
				}
			}
		}

		return (modelPlanes);
	}

	private void setVisited(boolean[][][][] visited, int faceID, int x, int y, int z, int width, int height,
			int depth) {
		for (int dx = 0; dx < width; dx++) {
			for (int dy = 0; dy < height; dy++) {
				for (int dz = 0; dz < depth; dz++) {
					visited[faceID][x + dx][y + dy][z + dz] = true;
				}
			}
		}
	}

	private final ModelMeshVertex generateVertex(EditableModel editableModel, Face face, ModelBlockData modelBlockData,
			int vertexID) {

		ModelMeshVertex vertex = new ModelMeshVertex();

		float sizeUnit = editableModel.getBlockSizeUnit();
		int x = modelBlockData.getX();
		int y = modelBlockData.getY();
		int z = modelBlockData.getZ();

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
