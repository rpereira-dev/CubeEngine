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
import java.util.Random;
import java.util.Stack;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.common.Logger;
import com.grillecube.common.faces.Face;

public class ModelMesherCull extends ModelMesher {

	/** generate the model planes, skins, and mesh */
	public final void doGenerate(EditableModel editableModel, Stack<ModelMeshVertex> vertices, Stack<Short> indices,
			ArrayList<BufferedImage> skinsData) {
		long t = System.currentTimeMillis();

		// GENERATE MODEL PLANS LIST
		ArrayList<ModelPlane> planes = this.generateModelPlans(editableModel);

		// GENERATE MESH FROM PLANS
		this.generateMeshAndSkin(editableModel, planes, vertices, indices, skinsData);

		Logger.get().log(Logger.Level.DEBUG, "generation took: " + (System.currentTimeMillis() - t));
	}

	/** generate the model mesh */
	private final void generateMeshAndSkin(EditableModel editableModel, ArrayList<ModelPlane> planes,
			Stack<ModelMeshVertex> vertices, Stack<Short> indices, ArrayList<BufferedImage> skinsData) {

		// calculate texture width/height
		int txWidth = 0;
		int txHeight = 0;
		for (ModelPlane plane : planes) {
			plane.setUV(txWidth, 0);
			txWidth += plane.getTextureWidth();
			if (plane.getTextureHeight() >= txHeight) {
				txHeight = plane.getTextureHeight();
			}
		}

		// generate skins
		Random rng = new Random();
		for (ModelSkin skin : editableModel.getSkins()) {
			int px = skin.getPixelsPerLine();
			BufferedImage img = new BufferedImage(txWidth * px, txHeight * px, BufferedImage.TYPE_INT_ARGB);
			for (ModelPlane plane : planes) {
				int rgba = rng.nextInt(Integer.MAX_VALUE) | 0xFF000000;
				int beginx = plane.getU() * px;
				int beginy = plane.getV() * px;
				int endx = beginx + plane.getTextureWidth() * px;
				int endy = beginy + plane.getTextureHeight() * px;
				for (int x = beginx; x < endx; x++) {
					for (int y = beginy; y < endy; y++) {
						img.setRGB(x, y, rgba);
					}
				}
			}
			skinsData.add(img);
		}

		// get raw blocks
		ModelBlockData[][][] datas = editableModel.getRawBlockDatas();
		float uw = 1.0f / (float) txWidth;
		float uh = 1.0f / (float) txHeight;
		for (ModelPlane plane : planes) {
			Face planeFace = plane.getFace();
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
						float ux, uy, vx, vy;
						if (planeFace.equals(Face.F_TOP) || planeFace.equals(Face.F_BOT)) {
							ux = (plane.getU() + dx) * uw;
							uy = (plane.getV() + dz) * uh;
							vx = (plane.getU() + dx + 1) * uw;
							vy = (plane.getV() + dz + 1) * uh;
						} else if (planeFace.equals(Face.F_RIGHT) || planeFace.equals(Face.F_LEFT)) {
							ux = (plane.getU() + dx) * uw;
							uy = (plane.getV() + dy) * uh;
							vx = (plane.getU() + dx + 1) * uw;
							vy = (plane.getV() + dy + 1) * uh;
						} else {
							ux = (plane.getU() + dy) * uw;
							uy = (plane.getV() + dz) * uh;
							vx = (plane.getU() + dy + 1) * uw;
							vy = (plane.getV() + dz + 1) * uh;
						}
						vertices.push(this.generateVertex(editableModel, planeFace, ux, uy, data, 0));
						vertices.push(this.generateVertex(editableModel, planeFace, vx, uy, data, 1));
						vertices.push(this.generateVertex(editableModel, planeFace, vx, vy, data, 2));
						vertices.push(this.generateVertex(editableModel, planeFace, ux, vy, data, 3));
					}
				}
			}
		}
	}

	/** generate model planes */
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
							blockData.setPlane(null, face);
							continue;
						}

						ModelPlane modelPlane = null;

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
							modelPlane = new ModelPlane(face, x, y, z, width, 1, depth, width, depth);
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
							modelPlane = new ModelPlane(face, x, y, z, width, height, 1, width, height);
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
							modelPlane = new ModelPlane(face, x, y, z, 1, height, depth, height, depth);
							this.setVisited(visited, faceID, x, y, z, 1, height, depth);
						}
						modelPlanes.add(modelPlane);
						blockData.setPlane(modelPlane, face);
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

	private final ModelMeshVertex generateVertex(EditableModel editableModel, Face face, float uvx, float uvy,
			ModelBlockData modelBlockData, int vertexID) {

		ModelMeshVertex vertex = new ModelMeshVertex();

		float sizeUnit = editableModel.getBlockSizeUnit();
		int x = modelBlockData.getX();
		int y = modelBlockData.getY();
		int z = modelBlockData.getZ();

		vertex.x = (x + BlockRenderer.EDGES[BlockRenderer.FACES_EDGES[face.getID()][vertexID]].x) * sizeUnit;
		vertex.y = (y + BlockRenderer.EDGES[BlockRenderer.FACES_EDGES[face.getID()][vertexID]].y) * sizeUnit;
		vertex.z = (z + BlockRenderer.EDGES[BlockRenderer.FACES_EDGES[face.getID()][vertexID]].z) * sizeUnit;

		vertex.uvx = uvx;
		vertex.uvy = uvy;

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
