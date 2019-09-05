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
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.model.ModelMeshVertex;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.common.Logger;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.utils.Color;

public class ModelMesherCull extends ModelMesher {

	/** generate the model planes, skins, and mesh */
	public final void doGenerate(EditableModel editableModel, Stack<ModelMeshVertex> vertices, Stack<Short> indices,
			HashMap<ModelSkin, BufferedImage> skinsData) {
		long t = System.currentTimeMillis();

		// GENERATE MODEL PLANS LIST
		for (EditableModelLayer modelLayer : editableModel.getRawLayers().values()) {
			if (!modelLayer.arePlanesUpToDate()) {
				this.generateLayerPlanes(modelLayer);
				modelLayer.setPlanesUpToDate();
			}
		}

		// GENERATE MESH FROM PLANS
		this.generateVerticesAndSkins(editableModel, vertices, indices, skinsData);

		Logger.get().log(Logger.Level.DEBUG, "generation took: " + (System.currentTimeMillis() - t));
	}

	/** generate the model mesh */
	private final void generateVerticesAndSkins(EditableModel editableModel, Stack<ModelMeshVertex> vertices,
			Stack<Short> indices, HashMap<ModelSkin, BufferedImage> skinsData) {

		ArrayList<ModelPlane> planes = new ArrayList<ModelPlane>();
		Collection<EditableModelLayer> layers = editableModel.getRawLayers().values();
		for (EditableModelLayer modelLayer : layers) {
			planes.addAll(modelLayer.getPlanes());
		}
		ModelSkinPacker.fit(planes);

		int txWidth = 0, txHeight = 0;

		// calculate texture width/height
		for (ModelPlane plane : planes) {
			int w = plane.getU() + plane.getTextureWidth();
			if (w > txWidth) {
				txWidth = w;
			}

			int h = plane.getV() + plane.getTextureHeight();
			if (h > txHeight) {
				txHeight = h;
			}
		}

		// generate skins
		for (ModelSkin skin : editableModel.getSkins()) {
			BufferedImage img = new BufferedImage(txWidth, txHeight, BufferedImage.TYPE_INT_ARGB);
			skinsData.put(skin, img);
		}

		Vector3i pos = new Vector3i();
		float uw = 1.0f / (float) txWidth;
		float uh = 1.0f / (float) txHeight;

		Random rng = new Random();
		for (EditableModelLayer modelLayer : layers) {

			HashMap<Vector3i, ModelBlockData> datas = modelLayer.getRawBlockDatas();
			ArrayList<ModelPlane> layerPlanes = modelLayer.getPlanes();

			for (ModelPlane plane : layerPlanes) {
				int planeColor = rng.nextInt(Integer.MAX_VALUE) | 0xFF000000;
				int faceID = plane.getFace();

				for (int d1 = 0; d1 < plane.getTextureWidth(); d1++) {
					for (int d2 = 0; d2 < plane.getTextureHeight(); d2++) {

						// add vertices
						int x = plane.getXMin();
						int y = plane.getYMin();
						int z = plane.getZMin();
						float ux, uy, vx, vy;
						if (faceID == Face.TOP || faceID == Face.BOT) {
							x += d1;
							y += d2;
							ux = (plane.getU() + d1) * uw;
							uy = (plane.getV() + d2) * uh;
							vx = (plane.getU() + d1 + 1) * uw;
							vy = (plane.getV() + d2 + 1) * uh;
						} else if (faceID == Face.RIGHT || faceID == Face.LEFT) {
							x += d1;
							z += d2;
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
						if (data == null) {
							// Logger.get().log(Logger.Level.ERROR, 2, x, y, z, d1, d2);
							continue;
						}

						// add vertices

						// add indices (quad)
						int i = vertices.size();

						ModelMeshVertex v0 = this.generateVertex(editableModel, modelLayer, faceID, ux, uy, data, 0);
						ModelMeshVertex v1 = this.generateVertex(editableModel, modelLayer, faceID, vx, uy, data, 1);
						ModelMeshVertex v2 = this.generateVertex(editableModel, modelLayer, faceID, vx, vy, data, 2);
						ModelMeshVertex v3 = this.generateVertex(editableModel, modelLayer, faceID, ux, vy, data, 3);

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
							Color color = data.getColor(skin, faceID);
							int r, g, b, a;
							int c;
							if (color != null) {
								float factor = Face.get(faceID).getFaceFactor();
								r = Maths.clamp((int) (color.getRed() * factor), 0, 255);
								g = Maths.clamp((int) (color.getGreen() * factor), 0, 255);
								b = Maths.clamp((int) (color.getBlue() * factor), 0, 255);
								a = color.getAlpha();
								c = (a << 24) | (r << 16) | (g << 8) | (b << 0);
							} else {
								c = planeColor;
							}
							int px = plane.getU() + d1;
							int py = plane.getV() + d2;
							img.setRGB(px, py, c);
						}
					}
				}
			}
		}
	}

	/** generate model planes */
	private final void generateLayerPlanes(EditableModelLayer editableModelLayer) {
		// extract plans
		ArrayList<ModelPlane> modelPlanes = editableModelLayer.getPlanes();
		modelPlanes.clear();

		HashMap<Vector3i, ModelBlockData> blockDatas = editableModelLayer.getRawBlockDatas();

		int mx = editableModelLayer.getMinx();
		int my = editableModelLayer.getMiny();
		int mz = editableModelLayer.getMinz();
		int Mx = editableModelLayer.getMaxx();
		int My = editableModelLayer.getMaxy();
		int Mz = editableModelLayer.getMaxz();
		int w = Mx - mx;
		int d = My - my;
		int h = Mz - mz;

		Vector3i pos = new Vector3i();

		// for each face
		for (int faceID = 0; faceID < Face.values().length; faceID++) {

			boolean visited[][][] = new boolean[w + 1][d + 1][h + 1];

			for (int iz = 0; iz <= h; iz++) {
				for (int iy = 0; iy <= d; iy++) {
					for (int ix = 0; ix <= w; ix++) {
						int x = ix + mx;
						int y = iy + my;
						int z = iz + mz;

						// if already visited, continue
						if (visited[ix][iy][iz]) {
							continue;
						}
						visited[ix][iy][iz] = true;

						ModelBlockData blockData = blockDatas.get(pos.set(x, y, z));

						if (blockData == null) {
							continue;
						}

						// get the neighbor block, to ensure that this face is
						// actually visible
						Vector3i n = Face.get(faceID).getVector();
						int nx = x + n.x;
						int ny = y + n.y;
						int nz = z + n.z;

						// if neighbor face is visible
						ModelBlockData neighbor = blockDatas.get(pos.set(nx, ny, nz));
						if (neighbor != null) {
							continue;
						}

						ModelPlane modelPlane = null;

						// then this face is visible, generate plans
						// TOP OR BOT FACE
						if (faceID == Face.TOP || faceID == Face.BOT) {
							int width = 1;

							// generate the rectangle width;
							while (ix + width <= w && !visited[ix + width][iy][iz]
									&& (neighbor = blockDatas.get(pos.set(x + width, y, z))) != null
									&& blockData.fit(neighbor)) {
								visited[ix + width][iy][iz] = true;
								++width;
							}

							int depth = 1;

							// generate the rectangle depth
							depth_test: while (iy + depth <= d) {
								for (int dx = 0; dx < width; dx++) {
									if (visited[ix + dx][iy + depth][iz]
											|| (neighbor = blockDatas.get(pos.set(x + dx, y + depth, z))) == null
											|| !blockData.fit(neighbor)) {
										break depth_test;
									}
									visited[ix + dx][iy + depth][iz] = true;
								}
								++depth;
							}
							modelPlane = new ModelPlane(faceID, x, y, z, width, depth);
							x += width - 1;
						}
						// RIGHT OR LEFT FACE
						else if (faceID == Face.RIGHT || faceID == Face.LEFT) {
							int width = 1;

							// generate the rectangle width;
							while (ix + width <= w && !visited[ix + width][iy][iz]
									&& (neighbor = blockDatas.get(pos.set(x + width, y, z))) != null
									&& blockData.fit(neighbor)) {
								visited[ix + width][iy][iz] = true;
								++width;
							}

							// generate the rectangle depth
							int height = 1;

							height_test: while (iz + height <= h) {
								for (int dx = 0; dx < width; dx++) {
									if (visited[ix + dx][iy][iz + height]
											|| (neighbor = blockDatas.get(pos.set(x + dx, y, z + height))) == null
											|| !blockData.fit(neighbor)) {
										break height_test;
									}
									visited[ix + dx][iy][iz + height] = true;
								}
								++height;
							}
							modelPlane = new ModelPlane(faceID, x, y, z, width, height);
							x += width - 1;
						}
						// ELSE : FRONT OR BACK
						else {
							int depth = 1;

							// generate the rectangle width;
							while (iy + depth <= d && !visited[ix][iy + depth][iz]
									&& (neighbor = blockDatas.get(pos.set(x, y + depth, z))) != null
									&& blockData.fit(neighbor)) {
								visited[ix][iy + depth][iz] = true;
								++depth;
							}

							// generate the rectangle depth
							int height = 1;

							height_test: while (iz + height <= h) {
								for (int dy = 0; dy < depth; dy++) {
									if (visited[ix][iy + dy][iz + height]
											|| (neighbor = blockDatas.get(pos.set(x, y + dy, z + height))) == null
											|| !blockData.fit(neighbor)) {
										break height_test;
									}
									visited[ix][iy + dy][iz + height] = true;
								}
								++height;
							}
							modelPlane = new ModelPlane(faceID, x, y, z, depth, height);
						}
						modelPlanes.add(modelPlane);
					}
				}
			}
		}
	}

	private final ModelMeshVertex generateVertex(EditableModel editableModel, EditableModelLayer modelLayer, int faceID,
			float uvx, float uvy, ModelBlockData modelBlockData, int vertexID) {

		ModelMeshVertex vertex = new ModelMeshVertex();

		// ambiant occlusion
		int x = modelBlockData.getX();
		int y = modelBlockData.getY();
		int z = modelBlockData.getZ();
		float ao = 1.0f - getAmbiantOcclusion(modelLayer, x, y, z, BlockRenderer.getNeighboors(faceID, vertexID));

		float sizeUnit = modelLayer.getBlockSizeUnit();

		vertex.x = (x + BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[faceID][vertexID]].x) * sizeUnit;
		vertex.y = (y + BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[faceID][vertexID]].y) * sizeUnit;
		vertex.z = (z + BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[faceID][vertexID]].z) * sizeUnit;

		vertex.uvx = uvx;
		vertex.uvy = uvy;

		vertex.nx = Face.get(faceID).getNormal().x;
		vertex.ny = Face.get(faceID).getNormal().y;
		vertex.nz = Face.get(faceID).getNormal().z;

		vertex.b1 = this.getBoneID(editableModel, modelLayer, modelBlockData, 0);
		vertex.b2 = this.getBoneID(editableModel, modelLayer, modelBlockData, 1);
		vertex.b3 = this.getBoneID(editableModel, modelLayer, modelBlockData, 2);

		vertex.w1 = modelBlockData.getBoneWeight(0);
		vertex.w2 = modelBlockData.getBoneWeight(1);
		vertex.w3 = modelBlockData.getBoneWeight(2);

		vertex.ao = ao;

		return (vertex);
	}

	private int getBoneID(EditableModel editableModel, EditableModelLayer modelLayer, ModelBlockData modelBlockData,
			int i) {
		String boneName = modelBlockData.getBone(0);
		if (boneName == null) {
			return (0);
		}
		Bone bone = editableModel.getSkeleton().getBone(boneName);
		return (bone == null ? 0 : bone.getID());
	}

	private static final float AO_UNIT = 0.16f;

	public static final float getAmbiantOcclusion(EditableModelLayer m, int x, int y, int z, Vector3i... neighbr) {
		Vector3i p = new Vector3i();
		ModelBlockData side1 = m.getBlockData(p.set(x + neighbr[0].x, y + neighbr[0].y, z + neighbr[0].z));
		ModelBlockData side2 = m.getBlockData(p.set(x + neighbr[1].x, y + neighbr[1].y, z + neighbr[1].z));
		ModelBlockData corner = m.getBlockData(p.set(x + neighbr[2].x, y + neighbr[2].y, z + neighbr[2].z));

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