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

import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.common.faces.Face;
import com.grillecube.common.world.terrain.Terrain;

/** the simplest terrain mesher (cull colliding faces) */
public class FlatTerrainMesherCull extends TerrainMesher {

	/**
	 * fill an array of dimension
	 * [Terrain.BLOCK_SIZEIZE_X][Terrain.BLOCK_SIZEIZE_Y][Terrain.
	 * BLOCK_SIZEIZE_Z][6] of terrain faces visibility
	 */
	protected void fillVertexStacks(Terrain terrain, ArrayList<TerrainMeshTriangle> opaqueStack,
			ArrayList<TerrainMeshTriangle> transparentStack) {
		// get visibile faces
		BlockFace[][][][] faces = super.getFacesVisibility(terrain, opaqueStack, transparentStack);
		if (faces == null) {
			return;
		}

		// for each face
		for (Face face : Face.faces) {
			for (int z = 0; z < Terrain.DIMZ; z++) {
				for (int y = 0; y < Terrain.DIMY; y++) {
					for (int x = 0; x < Terrain.DIMX; x++) {
						BlockFace blockFace = faces[face.getID()][x][y][z];
						if (blockFace == null) {
							continue;
						}
						// Vector3i n = face.getVector();
						// if (faces[face.getOpposite().getID()][x + n.x][y +
						// n.y][z + n.z] != null) {
						// continue;
						// }
						blockFace.pushVertices(opaqueStack);
					}
				}
			}
		}
	}
}
