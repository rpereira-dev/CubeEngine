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
import java.util.HashMap;
import java.util.Stack;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.model.ModelMeshVertex;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.common.Logger;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.utils.Color;

public class ModelMesherCull extends ModelMesher {

	/** generate the model planes, skins, and mesh */
	public final void doGenerate(EditableModel editableModel, Stack<ModelMeshVertex> vertices, Stack<Short> indices,
			HashMap<ModelSkin, BufferedImage> skinsData) {
		long t = System.currentTimeMillis();

		// GENERATE MODEL PLANS LIST
		ArrayList<ModelPlane> planes = this.generateModelPlans(editableModel);

		// GENERATE MESH FROM PLANS
		this.generateMeshAndSkin(editableModel, planes, vertices, indices, skinsData);

		Logger.get().log(Logger.Level.DEBUG, "generation took: " + (System.currentTimeMillis() - t));
	}

	/** generate the model mesh */
	private final void generateMeshAndSkin(EditableModel editableModel, ArrayList<ModelPlane> planes,
			Stack<ModelMeshVertex> vertices, Stack<Short> indices, HashMap<ModelSkin, BufferedImage> skinsData) {

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
		for (ModelSkin skin : editableModel.getSkins()) {
			int pxl = skin.getPixelsPerLine();
			BufferedImage img = new BufferedImage(txWidth * pxl, txHeight * pxl, BufferedImage.TYPE_INT_ARGB);
			skinsData.put(skin, img);
		}

		// get raw blocks
		HashMap<Vector3i, ModelBlockData> datas = editableModel.getRawBlockDatas();
		Vector3i pos = new Vector3i();
		float uw = 1.0f / (float) txWidth;
		float uh = 1.0f / (float) txHeight;
		for (ModelPlane plane : planes) {
			Face planeFace = plane.getFace();
			for (int d1 = 0; d1 < plane.getTextureWidth(); d1++) {
				for (int d2 = 0; d2 < plane.getTextureHeight(); d2++) {

					// add vertices
					int x = plane.getXMin();
					int y = plane.getYMin();
					int z = plane.getZMin();
					float ux, uy, vx, vy;
					if (planeFace.equals(Face.F_TOP) || planeFace.equals(Face.F_BOT)) {
						x += d1;
						z += d2;
						ux = (plane.getU() + d1) * uw;
						uy = (plane.getV() + d2) * uh;
						vx = (plane.getU() + d1 + 1) * uw;
						vy = (plane.getV() + d2 + 1) * uh;
					} else if (planeFace.equals(Face.F_RIGHT) || planeFace.equals(Face.F_LEFT)) {
						x += d1;
						y += d2;
						ux = (plane.getU() + d1) * uw;
						uy = (plane.getV() + d2) * uh;
						vx = (plane.getU() + d1 + 1) * uw;
						vy = (plane.getV() + d2 + 1) * uh;
					} else {
						y += d1;
						z += d2;
						ux = (plane.getU() + d1) * uw;
						uy = (plane.getV() + d2) * uh;
						vx = (plane.getU() + d1 + 1) * uw;
						vy = (plane.getV() + d2 + 1) * uh;
					}

					// the block data
					ModelBlockData data = datas.get(pos.set(x, y, z));

					// add vertices

					// add indices (quad)
					int i = vertices.size();

					ModelMeshVertex v0 = this.generateVertex(editableModel, planeFace, ux, uy, data, 0);
					ModelMeshVertex v1 = this.generateVertex(editableModel, planeFace, vx, uy, data, 1);
					ModelMeshVertex v2 = this.generateVertex(editableModel, planeFace, vx, vy, data, 2);
					ModelMeshVertex v3 = this.generateVertex(editableModel, planeFace, ux, vy, data, 3);
					vertices.push(v0);
					vertices.push(v1);
					vertices.push(v2);
					vertices.push(v3);

					if (v0.ao + v2.ao < v1.ao + v3.ao) {
						indices.add((short) i);
						indices.add((short) (i + 1));
						indices.add((short) (i + 2));
						indices.add((short) i);
						indices.add((short) (i + 2));
						indices.add((short) (i + 3));
					} else {
						indices.add((short) (i + 1));
						indices.add((short) (i + 2));
						indices.add((short) (i + 3));
						indices.add((short) (i + 1));
						indices.add((short) (i + 3));
						indices.add((short) i);
					}

					// skins
					for (ModelSkin skin : editableModel.getSkins()) {
						BufferedImage img = skinsData.get(skin);
						int pxl = skin.getPixelsPerLine();
						Color color = data.getColor(skin);
						int r = (int) (color.getRed() * planeFace.getFaceFactor());
						int g = (int) (color.getRed() * planeFace.getFaceFactor());
						int b = (int) (color.getRed() * planeFace.getFaceFactor());
						int beginx = plane.getU() * pxl;
						int beginy = plane.getV() * pxl;
						int endx = beginx + plane.getTextureWidth() * pxl;
						int endy = beginy + plane.getTextureHeight() * pxl;
						for (int px = beginx; px < endx; px++) {
							for (int py = beginy; py < endy; py++) {
								img.setRGB(px, py, 0xFF000000 | (r << 16) | (g << 8) | b);
							}
						}
					}
				}
			}
		}
	}

	/** generate model planes */
	private final ArrayList<ModelPlane> generateModelPlans(EditableModel editableModel) {
		// extract plans
		ArrayList<ModelPlane> modelPlanes = new ArrayList<ModelPlane>();
		HashMap<Vector3i, ModelBlockData> blockDatas = editableModel.getRawBlockDatas();
		Vector3i tmp = new Vector3i(); // vector3i buffer

		// for each face
		for (Face face : Face.faces) {
			int faceID = face.getID();
			HashMap<Vector3i, Boolean> visited = new HashMap<Vector3i, Boolean>();
			for (ModelBlockData blockData : blockDatas.values()) {

				// if already visited, continue
				if (visited.get(blockData.getPos()) != null) {
					continue;
				}

				// else, set visited and find the plan
				visited.put(blockData.getPos(), true);

				int x = blockData.getPos().x;
				int y = blockData.getPos().y;
				int z = blockData.getPos().z;

				// get the neighbor block, to ensure that this face is
				// actually visible
				int nx = x + face.getVector().x;
				int ny = y + face.getVector().y;
				int nz = z + face.getVector().z;

				// if neighbor face is visible
				if (blockDatas.get(tmp.set(nx, ny, nz)) != null) {
					continue;
				}

				ModelPlane modelPlane = null;

				// then this face is visible, generate plans
				// TOP OR BOT FACE
				if (faceID == Face.TOP || faceID == Face.BOT) {
					int width = 1;
					int depth = 1;

					// generate the rectangle width
					while (x + width < editableModel.getMaxx() && visited.get(tmp.set(x + width, y, z)) != null
							&& blockDatas.get(tmp.set(x + width, y, z)) != null) {
						++width;
					}

					// generate the rectangle depth
					depth_test: while (z + depth < editableModel.getMaxz()) {
						for (int dx = 0; dx < width; dx++) {
							if (visited.get(tmp.set(x + dx, y, z + depth)) != null
									|| blockDatas.get(tmp.set(x + dx, y, z + depth)) == null) {
								break depth_test;
							}
							visited.put(tmp.set(x + dx, y, z + depth), true);
						}
						++depth;
					}
					modelPlane = new ModelPlane(face, x, y, z, width, depth);
					x += width - 1;
				}
				// RIGHT OR LEFT FACE
				else if (faceID == Face.RIGHT || faceID == Face.LEFT) {
					int width = 1;
					int height = 1;

					// generate the rectangle width
					while (x + width < editableModel.getMaxx() && visited.get(tmp.set(x + width, y, z)) != null
							&& blockDatas.get(tmp.set(x + width, y, z)) != null) {
						++width;
					}

					// generate the rectangle depth
					height_test: while (y + height < editableModel.getMaxy()) {
						for (int dx = 0; dx < width; dx++) {
							if (visited.get(tmp.set(x + dx, y + height, z)) != null
									|| blockDatas.get(tmp.set(x + dx, y + height, z)) == null) {
								break height_test;
							}
							visited.put(tmp.set(x + dx, y + height, z), true);
						}
						++height;
					}
					modelPlane = new ModelPlane(face, x, y, z, width, height);
					x += width - 1;
				}
				// ELSE : FRONT OR BACK
				else {

					int depth = 1;
					int height = 1;

					// generate the rectangle width
					while (z + depth < editableModel.getMaxz() && visited.get(tmp.set(x, y, z + depth)) != null
							&& blockDatas.get(tmp.set(x, y, z + depth)) != null) {
						++depth;
					}

					// generate the rectangle depth
					height_test: while (y + height < editableModel.getMaxy()) {
						for (int dz = 0; dz < depth; dz++) {
							if (visited.get(tmp.set(x, y + height, z + dz)) != null
									|| blockDatas.get(tmp.set(x, y + height, z + dz)) == null) {
								break height_test;
							}
							visited.put(tmp.set(x, y + height, z + dz), true);
						}
						++height;
					}
					modelPlane = new ModelPlane(face, x, y, z, height, depth);
				}
				modelPlanes.add(modelPlane);
			}
		}
		return (modelPlanes);
	}

	private final ModelMeshVertex generateVertex(EditableModel editableModel, Face face, float uvx, float uvy,
			ModelBlockData modelBlockData, int vertexID) {

		ModelMeshVertex vertex = new ModelMeshVertex();

		// ambiant occlusion
		float ao = 1.0f - getAmbiantOcclusion(editableModel, modelBlockData.getX(), modelBlockData.getY(),
				modelBlockData.getZ(), BlockRenderer.getNeighboors(face.getID(), vertexID));

		float sizeUnit = editableModel.getBlockSizeUnit();
		int x = modelBlockData.getX();
		int y = modelBlockData.getY();
		int z = modelBlockData.getZ();

		vertex.x = (x + BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][vertexID]].x) * sizeUnit;
		vertex.y = (y + BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][vertexID]].y) * sizeUnit;
		vertex.z = (z + BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][vertexID]].z) * sizeUnit;

		vertex.uvx = uvx;
		vertex.uvy = uvy;

		vertex.nx = face.getNormal().getX();
		vertex.ny = face.getNormal().getY();
		vertex.nz = face.getNormal().getZ();

		vertex.b1 = 0;// modelBlockData.getBoneID(0);
		vertex.b2 = 0;// modelBlockData.getBoneID(1);
		vertex.b3 = 0;// modelBlockData.getBoneID(2);

		vertex.w1 = 1;// modelBlockData.getBoneWeight(0);
		vertex.w2 = 0;// modelBlockData.getBoneWeight(1);
		vertex.w3 = 0;// modelBlockData.getBoneWeight(2);

		vertex.ao = ao;

		return (vertex);
	}

	private static final float AO_UNIT = 0.16f;

	public static final float getAmbiantOcclusion(EditableModel model, int x, int y, int z, Vector3i... neighboors) {
		ModelBlockData side1 = model.getBlockData(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		ModelBlockData side2 = model.getBlockData(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		ModelBlockData corner = model.getBlockData(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);

		boolean s1 = side1 != null;
		boolean s2 = side2 != null;
		boolean c = corner != null;
		if (s1 && s2) {
			return (3.0f * AO_UNIT);
		}

		if (s1 || s2) {
			return (c ? 2.0f * AO_UNIT : AO_UNIT);
		}
		return (c ? AO_UNIT : 0.0f);
	}
}