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

package com.grillecube.client.renderer.world.flat;

import java.util.ArrayList;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMeshVertex;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.common.faces.Face;
import com.grillecube.common.world.terrain.Terrain;

/** an object which is used to generate terrain meshes dynamically */
public class FlatTerrainMesherGreedy extends TerrainMesher {

	/**
	 * fill an array of dimension
	 * [Terrain.BLOCK_SIZEIZE_X][Terrain.BLOCK_SIZEIZE_Y][Terrain.
	 * BLOCK_SIZEIZE_Z][6] of terrain faces visibility
	 */
	@Override
	protected void fillVertexStacks(Terrain terrain, ArrayList<TerrainMeshTriangle> opaqueStack,
			ArrayList<TerrainMeshTriangle> transparentStack) {
		// get visibile faces
		BlockFace[][][][] faces = this.getFacesVisibility(terrain, opaqueStack, transparentStack);
		if (faces == null) {
			return;
		}

		boolean[][][][] visited = new boolean[Face.faces.length][Terrain.DIMX][Terrain.DIMY][Terrain.DIMZ];

		// for each face
		for (int faceID = 0; faceID < Face.faces.length; faceID++) {
			for (int z = 0; z < Terrain.DIMZ; z++) {
				for (int y = 0; y < Terrain.DIMY; y++) {
					for (int x = 0; x < Terrain.DIMX; x++) {

						if (visited[faceID][x][y][z]) {
							continue;
						}

						BlockFace face = faces[faceID][x][y][z];

						// the face isnt visible, or has already been processed
						if (face == null) {
							continue;
						}

						ArrayList<TerrainMeshTriangle> stack = face.getBlock().isOpaque() ? opaqueStack
								: transparentStack;

						// TOP OR BOT FACE
						if (faceID == Face.TOP || faceID == Face.BOT) {

							int width = 1;
							int depth = 1;

							// generate the rectangle width
							while (x + width < Terrain.DIMX && !visited[faceID][x + width][y][z]
									&& face.equals(faces[faceID][x + width][y][z])) {
								++width;
							}

							// generate the rectangle depth
							depth_test: while (z + depth < Terrain.DIMZ) {
								for (int dx = 0; dx < width; dx++) {
									if (visited[faceID][x + dx][y][z + depth]
											|| !face.equals(faces[faceID][x + dx][y][z + depth])) {
										break depth_test;
									}
								}
								++depth;
							}

							// now we have the rectangle width / height
							for (int i = 0; i < 4; i++) {

								TerrainMeshVertex vertex = face.vertices[i];

								vertex.posx = x * Terrain.BLOCK_SIZE
										+ BlockRenderer.FACES_VERTICES[faceID][i].x * Terrain.BLOCK_SIZE * width;
								vertex.posz = z * Terrain.BLOCK_SIZE
										+ BlockRenderer.FACES_VERTICES[faceID][i].z * Terrain.BLOCK_SIZE * depth;

								vertex.uvx *= depth;
								vertex.uvy *= width;
							}
							face.pushVertices(stack);

							// set visited position
							this.setVisited(visited, faceID, x, y, z, width, 1, depth);

							// pass visited blocks
							x += width - 1;
						}

						// RIGHT OR LEFT FACE
						else if (faceID == Face.RIGHT || faceID == Face.LEFT) {
							int width = 1;
							int height = 1;

							// generate the rectangle width
							while (x + width < Terrain.DIMX && !visited[faceID][x + width][y][z]
									&& face.equals(faces[faceID][x + width][y][z])) {
								++width;
							}

							// generate the rectangle depth
							height_test: while (y + height < Terrain.DIMY) {
								for (int dx = 0; dx < width; dx++) {
									if (visited[faceID][x + dx][y + height][z]
											|| !face.equals(faces[faceID][x + dx][y + height][z])) {
										break height_test;
									}
								}
								++height;
							}

							// now we have the rectangle width / height
							for (int i = 0; i < 4; i++) {

								TerrainMeshVertex vertex = face.vertices[i];

								vertex.posx = x * Terrain.BLOCK_SIZE
										+ BlockRenderer.FACES_VERTICES[faceID][i].x * Terrain.BLOCK_SIZE * width;
								vertex.posy = y * Terrain.BLOCK_SIZE
										+ BlockRenderer.FACES_VERTICES[faceID][i].y * Terrain.BLOCK_SIZE * height;

								vertex.uvx *= width;
								vertex.uvy *= height;
							}
							face.pushVertices(stack);

							// set visited position
							this.setVisited(visited, faceID, x, y, z, width, height, 1);

							// pass visited blocks
							x += width - 1;
						}

						// ELSE : FRONT OR BACK
						else {

							int depth = 1;
							int height = 1;

							// generate the rectangle width
							while (z + depth < Terrain.DIMZ && !visited[faceID][x][y][z + depth]
									&& face.equals(faces[faceID][x][y][z + depth])) {
								++depth;
							}

							// generate the rectangle depth
							height_test: while (y + height < Terrain.DIMY) {
								for (int dz = 0; dz < depth; dz++) {
									if (visited[faceID][x][y + height][z + dz]
											|| !face.equals(faces[faceID][x][y + height][z + dz])) {
										break height_test;
									}
								}
								++height;
							}

							// now we have the rectangle width / height
							for (int i = 0; i < 4; i++) {

								TerrainMeshVertex vertex = face.vertices[i];

								vertex.posz = z * Terrain.BLOCK_SIZE
										+ BlockRenderer.FACES_VERTICES[faceID][i].z * Terrain.BLOCK_SIZE * depth;
								vertex.posy = y * Terrain.BLOCK_SIZE
										+ BlockRenderer.FACES_VERTICES[faceID][i].y * Terrain.BLOCK_SIZE * height;

								vertex.uvx *= depth;
								vertex.uvy *= height;
							}
							face.pushVertices(stack);

							// set visited position
							this.setVisited(visited, faceID, x, y, z, 1, height, depth);

							// pass visited blocks
						}
					}
				}
			}
		}
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
}