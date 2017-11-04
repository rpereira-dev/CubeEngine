package com.grillecube.client.renderer.world.flat;

import java.util.ArrayList;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMeshVertex;
import com.grillecube.client.renderer.world.TerrainMesher;
import com.grillecube.client.resources.BlockRendererManager;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.terrain.Terrain;

public class MarchingCubesTerrainMesher extends TerrainMesher {

	/**
	 * 256x5x5 UV for the given edgeFlags, triangleID, (faceID, u0, v0, u1, v1,
	 * u2, v2)
	 */
	// private static int[][][] UV_TABLE;

	private final int lodx;
	private final int lody;
	private final int lodz;

	public MarchingCubesTerrainMesher(int lod) {
		this(lod, lod, lod);
	}

	public MarchingCubesTerrainMesher(int lodx, int lody, int lodz) {
		this.lodx = Maths.clamp(lodx, 1, Terrain.DIMX);
		this.lody = Maths.clamp(lody, 1, Terrain.DIMY);
		this.lodz = Maths.clamp(lodz, 1, Terrain.DIMZ);
	}

	public MarchingCubesTerrainMesher() {
		this(1);
	}
	
	int EDGE_DEBUG = -1;
	T TRI_TABLE[][];
	@Override
	protected void fillVertexStacks(Terrain terrain, ArrayList<TerrainMeshTriangle> opaqueStack,
			ArrayList<TerrainMeshTriangle> transparentStack) {

		EDGE_DEBUG = -1;

		TRI_TABLE = new T[][]{
				{},

/*V*/				{new T(Face.LEFT, 0, 8, 3, 0.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0.0f)},
/*V*/				{new T(Face.BACK, 0, 1, 9, 1, 0, 3)},
/*V*/				{new T(Face.TOP, 1, 8, 3, 3, 1, 0), new T(Face.TOP, 9, 8, 1, 2, 1, 3)},
/*V*/				{new T(Face.TOP, 1, 2, 10, 0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f)},
/*V*/				{new T(Face.FRONT, 0, 8, 3, 3, 0, 2), new T(Face.TOP, 1, 2, 10, 2, 3, 0)},
/*V*/				{new T(Face.LEFT, 9, 2, 10, 2, 0, 3), new T(Face.LEFT, 0, 2, 9, 1, 0, 2)},
/*V*/				{new T(Face.LEFT, 2, 8, 3, 3, 2, 1), new T(Face.LEFT, 2, 10, 8, 0, 3, 1), new T(Face.LEFT, 10, 9, 8, 3, 2, 1)},
/*V*/				{new T(Face.BACK, 3, 11, 2, 2, 0, 1)},
/*V*/				{new T(Face.FRONT, 0, 11, 2, 2, 0, 3), new T(Face.FRONT, 8, 11, 0, 1, 0, 2)},
					{new T(Face.BOT, 1, 9, 0, 0, 2, 1), new T(Face.BOT, 2, 3, 11, 2, 0, 1)},
/*V*/				{new T(Face.TOP, 1, 11, 2, 0.0f, 0.0f, 0.0f, 1.0f, -0.5f, 0.5f), new T(Face.TOP, 1, 9, 11, 3, 2, 0), new T(Face.TOP, 9, 8, 11, 2, 1, 0)},
/*V*/				{new T(Face.TOP, 3, 10, 1, 1, 3, 2), new T(Face.TOP, 11, 10, 3, 0, 3, 1)},
/*V*/				{new T(Face.LEFT, 0, 10, 1, 0, 3, 2), new T(Face.LEFT, 0, 8, 10, 3, 0, 2), new T(Face.LEFT, 8, 11, 10, 0, 1, 2)},
/*V*/				{new T(Face.LEFT, 3, 9, 0, 2, 0, 3), new T(Face.LEFT, 3, 11, 9, 1, 0, 2), new T(Face.LEFT, 11, 10, 9, 0, 3, 2)},
/*V*/				{new T(Face.LEFT, 9, 8, 10, 2, 1, 3), new T(Face.LEFT, 10, 8, 11, 3, 1, 0)},
/*V*/				{new T(Face.LEFT, 4, 7, 8, 1, 0, 2)},				
/*V*/				{new T(Face.BACK, 4, 3, 0, 1, 3, 2), new T(Face.BACK, 7, 3, 4, 0, 3, 1)},
/*n*/				{new T(Face.TOP, 0, 1, 9, 0, 0, 0), new T(Face.TOP, 8, 4, 7, 0, 0, 0)},
/*V*/				{new T(Face.LEFT, 4, 1, 9, 0.0f, 1.0f, 1.5f, 0.0f, 1.0f, 1.0f), new T(Face.LEFT, 4, 7, 1, 0.0f, 1.0f, -0.5f, 0.0f, 1.5f, 0.0f), new T(Face.TOP, 7, 3, 1, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f)},
/*n*/				{new T(Face.TOP, 1, 2, 10, 0, 0, 0), new T(Face.TOP, 8, 4, 7, 0, 0, 0)},				
/*n*/				{new T(Face.TOP, 3, 4, 7, 0, 0, 0), new T(Face.TOP, 3, 0, 4, 0, 0, 0), new T(Face.TOP, 1, 2, 10, 0, 0, 0)},				
/*n*/				{new T(Face.TOP, 9, 2, 10, 0, 0, 0), new T(Face.TOP, 9, 0, 2, 0, 0, 0), new T(Face.TOP, 8, 4, 7, 0, 0, 0)},
/*V*/				{new T(Face.TOP, 2, 10, 9, 0, 3, 2), new T(Face.TOP, 2, 9, 7, 0, 2, 1), new T(Face.TOP, 2, 7, 3, 0, 3, 1), new T(Face.TOP, 7, 9, 4, 3, 2, 0)},
/*n*/				{new T(Face.TOP, 8, 4, 7, 0, 0, 0), new T(Face.TOP, 3, 11, 2, 0, 0, 0)},
/*V*/				{new T(Face.TOP, 11, 4, 7, 0.0f, 0.0f, 0.0f, 1.0f, -0.4f, 0.5f), new T(Face.TOP, 11, 2, 4, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f), new T(Face.TOP, 2, 0, 4, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)},
/*n*/				{new T(Face.TOP, 9, 0, 1, 0, 0, 0), new T(Face.TOP, 8, 4, 7, 0, 0, 0), new T(Face.TOP, 2, 3, 11, 0, 0, 0)},
/*V*/				{new T(Face.LEFT, 4, 7, 11, 0.0f, 1.0f, -0.5f, 0.5f, 0.0f, 0.0f), new T(Face.LEFT, 9, 4, 11, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f), new T(Face.LEFT, 9, 11, 2, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f), new T(Face.LEFT, 9, 2, 1, 1.0f, 1.0f, 1.0f, 0.0f, 1.5f, 0.5f)},				
/*n*/				{new T(Face.TOP, 3, 10, 1, 0, 0, 0), new T(Face.TOP, 3, 11, 10, 0, 0, 0), new T(Face.TOP, 7, 8, 4, 0, 0, 0)},
/*n*/				{new T(Face.TOP, 1, 11, 10, 0, 0, 0), new T(Face.TOP, 1, 4, 11, 0, 0, 0), new T(Face.TOP, 1, 0, 4, 0, 0, 0), new T(Face.TOP, 7, 11, 4, 0, 0, 0)},
/*n*/				{new T(Face.TOP, 4, 7, 8, 0, 0, 0), new T(Face.TOP, 9, 0, 11, 0, 0, 0), new T(Face.TOP, 9, 11, 10, 0, 0, 0), new T(Face.TOP, 11, 0, 3, 0, 0, 0)},
					
					
					
					{new T(Face.TOP, 4, 7, 11, 0, 0, 0), new T(Face.TOP, 4, 11, 9, 0, 0, 0), new T(Face.TOP, 9, 11, 10, 0, 0, 0)},					
					{new T(Face.TOP, 9, 5, 4, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 4, 0, 0, 0), new T(Face.TOP, 0, 8, 3, 0, 0, 0)},
					{new T(Face.TOP, 0, 5, 4, 0, 0, 0), new T(Face.TOP, 1, 5, 0, 0, 0, 0)},
					{new T(Face.TOP, 8, 5, 4, 0, 0, 0), new T(Face.TOP, 8, 3, 5, 0, 0, 0), new T(Face.TOP, 3, 1, 5, 0, 0, 0)},
					{new T(Face.TOP, 1, 2, 10, 0, 0, 0), new T(Face.TOP, 9, 5, 4, 0, 0, 0)},
					{new T(Face.TOP, 3, 0, 8, 0, 0, 0), new T(Face.TOP, 1, 2, 10, 0, 0, 0), new T(Face.TOP, 4, 9, 5, 0, 0, 0)},
					{new T(Face.TOP, 5, 2, 10, 0, 0, 0), new T(Face.TOP, 5, 4, 2, 0, 0, 0), new T(Face.TOP, 4, 0, 2, 0, 0, 0)},
					{new T(Face.TOP, 2, 10, 5, 0, 0, 0), new T(Face.TOP, 3, 2, 5, 0, 0, 0), new T(Face.TOP, 3, 5, 4, 0, 0, 0), new T(Face.TOP, 3, 4, 8, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 4, 0, 0, 0), new T(Face.TOP, 2, 3, 11, 0, 0, 0)},
					{new T(Face.TOP, 0, 11, 2, 0, 0, 0), new T(Face.TOP, 0, 8, 11, 0, 0, 0), new T(Face.TOP, 4, 9, 5, 0, 0, 0)},
					{new T(Face.TOP, 0, 5, 4, 0, 0, 0), new T(Face.TOP, 0, 1, 5, 0, 0, 0), new T(Face.TOP, 2, 3, 11, 0, 0, 0)},
					{new T(Face.TOP, 2, 1, 5, 0, 0, 0), new T(Face.TOP, 2, 5, 8, 0, 0, 0), new T(Face.TOP, 2, 8, 11, 0, 0, 0), new T(Face.TOP, 4, 8, 5, 0, 0, 0)},
					{new T(Face.TOP, 10, 3, 11, 0, 0, 0), new T(Face.TOP, 10, 1, 3, 0, 0, 0), new T(Face.TOP, 9, 5, 4, 0, 0, 0)},
					{new T(Face.TOP, 4, 9, 5, 0, 0, 0), new T(Face.TOP, 0, 8, 1, 0, 0, 0), new T(Face.TOP, 8, 10, 1, 0, 0, 0), new T(Face.TOP, 8, 11, 10, 0, 0, 0)},
					{new T(Face.TOP, 5, 4, 0, 0, 0, 0), new T(Face.TOP, 5, 0, 11, 0, 0, 0), new T(Face.TOP, 5, 11, 10, 0, 0, 0), new T(Face.TOP, 11, 0, 3, 0, 0, 0)},
					{new T(Face.TOP, 5, 4, 8, 0, 0, 0), new T(Face.TOP, 5, 8, 10, 0, 0, 0), new T(Face.TOP, 10, 8, 11, 0, 0, 0)},
					{new T(Face.TOP, 9, 7, 8, 0, 0, 0), new T(Face.TOP, 5, 7, 9, 0, 0, 0)},
					{new T(Face.TOP, 9, 3, 0, 0, 0, 0), new T(Face.TOP, 9, 5, 3, 0, 0, 0), new T(Face.TOP, 5, 7, 3, 0, 0, 0)},
					{new T(Face.TOP, 0, 7, 8, 0, 0, 0), new T(Face.TOP, 0, 1, 7, 0, 0, 0), new T(Face.TOP, 1, 5, 7, 0, 0, 0)},
					{new T(Face.TOP, 1, 5, 3, 0, 0, 0), new T(Face.TOP, 3, 5, 7, 0, 0, 0)},
					{new T(Face.TOP, 9, 7, 8, 0, 0, 0), new T(Face.TOP, 9, 5, 7, 0, 0, 0), new T(Face.TOP, 10, 1, 2, 0, 0, 0)},
					{new T(Face.TOP, 10, 1, 2, 0, 0, 0), new T(Face.TOP, 9, 5, 0, 0, 0, 0), new T(Face.TOP, 5, 3, 0, 0, 0, 0), new T(Face.TOP, 5, 7, 3, 0, 0, 0)},
					{new T(Face.TOP, 8, 0, 2, 0, 0, 0), new T(Face.TOP, 8, 2, 5, 0, 0, 0), new T(Face.TOP, 8, 5, 7, 0, 0, 0), new T(Face.TOP, 10, 5, 2, 0, 0, 0)},
					{new T(Face.TOP, 2, 10, 5, 0, 0, 0), new T(Face.TOP, 2, 5, 3, 0, 0, 0), new T(Face.TOP, 3, 5, 7, 0, 0, 0)},
					{new T(Face.TOP, 7, 9, 5, 0, 0, 0), new T(Face.TOP, 7, 8, 9, 0, 0, 0), new T(Face.TOP, 3, 11, 2, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 7, 0, 0, 0), new T(Face.TOP, 9, 7, 2, 0, 0, 0), new T(Face.TOP, 9, 2, 0, 0, 0, 0), new T(Face.TOP, 2, 7, 11, 0, 0, 0)},
					{new T(Face.TOP, 2, 3, 11, 0, 0, 0), new T(Face.TOP, 0, 1, 8, 0, 0, 0), new T(Face.TOP, 1, 7, 8, 0, 0, 0), new T(Face.TOP, 1, 5, 7, 0, 0, 0)},
					{new T(Face.TOP, 11, 2, 1, 0, 0, 0), new T(Face.TOP, 11, 1, 7, 0, 0, 0), new T(Face.TOP, 7, 1, 5, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 8, 0, 0, 0), new T(Face.TOP, 8, 5, 7, 0, 0, 0), new T(Face.TOP, 10, 1, 3, 0, 0, 0), new T(Face.TOP, 10, 3, 11, 0, 0, 0)},
					{new T(Face.TOP, 5, 7, 0, 0, 0, 0), new T(Face.TOP, 5, 0, 9, 0, 0, 0), new T(Face.TOP, 7, 11, 0, 0, 0, 0), new T(Face.TOP, 1, 0, 10, 0, 0, 0), new T(Face.TOP, 11, 10, 0, 0, 0, 0)},
					{new T(Face.TOP, 11, 10, 0, 0, 0, 0), new T(Face.TOP, 11, 0, 3, 0, 0, 0), new T(Face.TOP, 10, 5, 0, 0, 0, 0), new T(Face.TOP, 8, 0, 7, 0, 0, 0), new T(Face.TOP, 5, 7, 0, 0, 0, 0)},
					{new T(Face.TOP, 11, 10, 5, 0, 0, 0), new T(Face.TOP, 7, 11, 5, 0, 0, 0)},
					{new T(Face.TOP, 10, 6, 5, 0, 0, 0)},
					{new T(Face.TOP, 0, 8, 3, 0, 0, 0), new T(Face.TOP, 5, 10, 6, 0, 0, 0)},
					{new T(Face.TOP, 9, 0, 1, 0, 0, 0), new T(Face.TOP, 5, 10, 6, 0, 0, 0)},
					{new T(Face.TOP, 1, 8, 3, 0, 0, 0), new T(Face.TOP, 1, 9, 8, 0, 0, 0), new T(Face.TOP, 5, 10, 6, 0, 0, 0)},
					{new T(Face.TOP, 1, 6, 5, 0, 0, 0), new T(Face.TOP, 2, 6, 1, 0, 0, 0)},
					{new T(Face.TOP, 1, 6, 5, 0, 0, 0), new T(Face.TOP, 1, 2, 6, 0, 0, 0), new T(Face.TOP, 3, 0, 8, 0, 0, 0)},
					{new T(Face.TOP, 9, 6, 5, 0, 0, 0), new T(Face.TOP, 9, 0, 6, 0, 0, 0), new T(Face.TOP, 0, 2, 6, 0, 0, 0)},
					{new T(Face.TOP, 5, 9, 8, 0, 0, 0), new T(Face.TOP, 5, 8, 2, 0, 0, 0), new T(Face.TOP, 5, 2, 6, 0, 0, 0), new T(Face.TOP, 3, 2, 8, 0, 0, 0)},
					{new T(Face.TOP, 2, 3, 11, 0, 0, 0), new T(Face.TOP, 10, 6, 5, 0, 0, 0)},
					{new T(Face.TOP, 11, 0, 8, 0, 0, 0), new T(Face.TOP, 11, 2, 0, 0, 0, 0), new T(Face.TOP, 10, 6, 5, 0, 0, 0)},
					{new T(Face.TOP, 0, 1, 9, 0, 0, 0), new T(Face.TOP, 2, 3, 11, 0, 0, 0), new T(Face.TOP, 5, 10, 6, 0, 0, 0)},
					{new T(Face.TOP, 5, 10, 6, 0, 0, 0), new T(Face.TOP, 1, 9, 2, 0, 0, 0), new T(Face.TOP, 9, 11, 2, 0, 0, 0), new T(Face.TOP, 9, 8, 11, 0, 0, 0)},
					{new T(Face.TOP, 6, 3, 11, 0, 0, 0), new T(Face.TOP, 6, 5, 3, 0, 0, 0), new T(Face.TOP, 5, 1, 3, 0, 0, 0)},
					{new T(Face.TOP, 0, 8, 11, 0, 0, 0), new T(Face.TOP, 0, 11, 5, 0, 0, 0), new T(Face.TOP, 0, 5, 1, 0, 0, 0), new T(Face.TOP, 5, 11, 6, 0, 0, 0)},
					{new T(Face.TOP, 3, 11, 6, 0, 0, 0), new T(Face.TOP, 0, 3, 6, 0, 0, 0), new T(Face.TOP, 0, 6, 5, 0, 0, 0), new T(Face.TOP, 0, 5, 9, 0, 0, 0)},
					{new T(Face.TOP, 6, 5, 9, 0, 0, 0), new T(Face.TOP, 6, 9, 11, 0, 0, 0), new T(Face.TOP, 11, 9, 8, 0, 0, 0)},
					{new T(Face.TOP, 5, 10, 6, 0, 0, 0), new T(Face.TOP, 4, 7, 8, 0, 0, 0)},
					{new T(Face.TOP, 4, 3, 0, 0, 0, 0), new T(Face.TOP, 4, 7, 3, 0, 0, 0), new T(Face.TOP, 6, 5, 10, 0, 0, 0)},
					{new T(Face.TOP, 1, 9, 0, 0, 0, 0), new T(Face.TOP, 5, 10, 6, 0, 0, 0), new T(Face.TOP, 8, 4, 7, 0, 0, 0)},
					{new T(Face.TOP, 10, 6, 5, 0, 0, 0), new T(Face.TOP, 1, 9, 7, 0, 0, 0), new T(Face.TOP, 1, 7, 3, 0, 0, 0), new T(Face.TOP, 7, 9, 4, 0, 0, 0)},
					{new T(Face.TOP, 6, 1, 2, 0, 0, 0), new T(Face.TOP, 6, 5, 1, 0, 0, 0), new T(Face.TOP, 4, 7, 8, 0, 0, 0)},
					{new T(Face.TOP, 1, 2, 5, 0, 0, 0), new T(Face.TOP, 5, 2, 6, 0, 0, 0), new T(Face.TOP, 3, 0, 4, 0, 0, 0), new T(Face.TOP, 3, 4, 7, 0, 0, 0)},
					{new T(Face.TOP, 8, 4, 7, 0, 0, 0), new T(Face.TOP, 9, 0, 5, 0, 0, 0), new T(Face.TOP, 0, 6, 5, 0, 0, 0), new T(Face.TOP, 0, 2, 6, 0, 0, 0)},
					{new T(Face.TOP, 7, 3, 9, 0, 0, 0), new T(Face.TOP, 7, 9, 4, 0, 0, 0), new T(Face.TOP, 3, 2, 9, 0, 0, 0), new T(Face.TOP, 5, 9, 6, 0, 0, 0), new T(Face.TOP, 2, 6, 9, 0, 0, 0)},
					{new T(Face.TOP, 3, 11, 2, 0, 0, 0), new T(Face.TOP, 7, 8, 4, 0, 0, 0), new T(Face.TOP, 10, 6, 5, 0, 0, 0)},
					{new T(Face.TOP, 5, 10, 6, 0, 0, 0), new T(Face.TOP, 4, 7, 2, 0, 0, 0), new T(Face.TOP, 4, 2, 0, 0, 0, 0), new T(Face.TOP, 2, 7, 11, 0, 0, 0)},
					{new T(Face.TOP, 0, 1, 9, 0, 0, 0), new T(Face.TOP, 4, 7, 8, 0, 0, 0), new T(Face.TOP, 2, 3, 11, 0, 0, 0), new T(Face.TOP, 5, 10, 6, 0, 0, 0)},
					{new T(Face.TOP, 9, 2, 1, 0, 0, 0), new T(Face.TOP, 9, 11, 2, 0, 0, 0), new T(Face.TOP, 9, 4, 11, 0, 0, 0), new T(Face.TOP, 7, 11, 4, 0, 0, 0), new T(Face.TOP, 5, 10, 6, 0, 0, 0)},
					{new T(Face.TOP, 8, 4, 7, 0, 0, 0), new T(Face.TOP, 3, 11, 5, 0, 0, 0), new T(Face.TOP, 3, 5, 1, 0, 0, 0), new T(Face.TOP, 5, 11, 6, 0, 0, 0)},
					{new T(Face.TOP, 5, 1, 11, 0, 0, 0), new T(Face.TOP, 5, 11, 6, 0, 0, 0), new T(Face.TOP, 1, 0, 11, 0, 0, 0), new T(Face.TOP, 7, 11, 4, 0, 0, 0), new T(Face.TOP, 0, 4, 11, 0, 0, 0)},
					{new T(Face.TOP, 0, 5, 9, 0, 0, 0), new T(Face.TOP, 0, 6, 5, 0, 0, 0), new T(Face.TOP, 0, 3, 6, 0, 0, 0), new T(Face.TOP, 11, 6, 3, 0, 0, 0), new T(Face.TOP, 8, 4, 7, 0, 0, 0)},
					{new T(Face.TOP, 6, 5, 9, 0, 0, 0), new T(Face.TOP, 6, 9, 11, 0, 0, 0), new T(Face.TOP, 4, 7, 9, 0, 0, 0), new T(Face.TOP, 7, 11, 9, 0, 0, 0)},
					{new T(Face.TOP, 10, 4, 9, 0, 0, 0), new T(Face.TOP, 6, 4, 10, 0, 0, 0)},
					{new T(Face.TOP, 4, 10, 6, 0, 0, 0), new T(Face.TOP, 4, 9, 10, 0, 0, 0), new T(Face.TOP, 0, 8, 3, 0, 0, 0)},
					{new T(Face.TOP, 10, 0, 1, 0, 0, 0), new T(Face.TOP, 10, 6, 0, 0, 0, 0), new T(Face.TOP, 6, 4, 0, 0, 0, 0)},
					{new T(Face.TOP, 8, 3, 1, 0, 0, 0), new T(Face.TOP, 8, 1, 6, 0, 0, 0), new T(Face.TOP, 8, 6, 4, 0, 0, 0), new T(Face.TOP, 6, 1, 10, 0, 0, 0)},
					{new T(Face.TOP, 1, 4, 9, 0, 0, 0), new T(Face.TOP, 1, 2, 4, 0, 0, 0), new T(Face.TOP, 2, 6, 4, 0, 0, 0)},
					{new T(Face.TOP, 3, 0, 8, 0, 0, 0), new T(Face.TOP, 1, 2, 9, 0, 0, 0), new T(Face.TOP, 2, 4, 9, 0, 0, 0), new T(Face.TOP, 2, 6, 4, 0, 0, 0)},
					{new T(Face.TOP, 0, 2, 4, 0, 0, 0), new T(Face.TOP, 4, 2, 6, 0, 0, 0)},
					{new T(Face.TOP, 8, 3, 2, 0, 0, 0), new T(Face.TOP, 8, 2, 4, 0, 0, 0), new T(Face.TOP, 4, 2, 6, 0, 0, 0)},
					{new T(Face.TOP, 10, 4, 9, 0, 0, 0), new T(Face.TOP, 10, 6, 4, 0, 0, 0), new T(Face.TOP, 11, 2, 3, 0, 0, 0)},
					{new T(Face.TOP, 0, 8, 2, 0, 0, 0), new T(Face.TOP, 2, 8, 11, 0, 0, 0), new T(Face.TOP, 4, 9, 10, 0, 0, 0), new T(Face.TOP, 4, 10, 6, 0, 0, 0)},
					{new T(Face.TOP, 3, 11, 2, 0, 0, 0), new T(Face.TOP, 0, 1, 6, 0, 0, 0), new T(Face.TOP, 0, 6, 4, 0, 0, 0), new T(Face.TOP, 6, 1, 10, 0, 0, 0)},
					{new T(Face.TOP, 6, 4, 1, 0, 0, 0), new T(Face.TOP, 6, 1, 10, 0, 0, 0), new T(Face.TOP, 4, 8, 1, 0, 0, 0), new T(Face.TOP, 2, 1, 11, 0, 0, 0), new T(Face.TOP, 8, 11, 1, 0, 0, 0)},
					{new T(Face.TOP, 9, 6, 4, 0, 0, 0), new T(Face.TOP, 9, 3, 6, 0, 0, 0), new T(Face.TOP, 9, 1, 3, 0, 0, 0), new T(Face.TOP, 11, 6, 3, 0, 0, 0)},
					{new T(Face.TOP, 8, 11, 1, 0, 0, 0), new T(Face.TOP, 8, 1, 0, 0, 0, 0), new T(Face.TOP, 11, 6, 1, 0, 0, 0), new T(Face.TOP, 9, 1, 4, 0, 0, 0), new T(Face.TOP, 6, 4, 1, 0, 0, 0)},
					{new T(Face.TOP, 3, 11, 6, 0, 0, 0), new T(Face.TOP, 3, 6, 0, 0, 0, 0), new T(Face.TOP, 0, 6, 4, 0, 0, 0)},
					{new T(Face.TOP, 6, 4, 8, 0, 0, 0), new T(Face.TOP, 11, 6, 8, 0, 0, 0)},
					{new T(Face.TOP, 7, 10, 6, 0, 0, 0), new T(Face.TOP, 7, 8, 10, 0, 0, 0), new T(Face.TOP, 8, 9, 10, 0, 0, 0)},
					{new T(Face.TOP, 0, 7, 3, 0, 0, 0), new T(Face.TOP, 0, 10, 7, 0, 0, 0), new T(Face.TOP, 0, 9, 10, 0, 0, 0), new T(Face.TOP, 6, 7, 10, 0, 0, 0)},
					{new T(Face.TOP, 10, 6, 7, 0, 0, 0), new T(Face.TOP, 1, 10, 7, 0, 0, 0), new T(Face.TOP, 1, 7, 8, 0, 0, 0), new T(Face.TOP, 1, 8, 0, 0, 0, 0)},
					{new T(Face.TOP, 10, 6, 7, 0, 0, 0), new T(Face.TOP, 10, 7, 1, 0, 0, 0), new T(Face.TOP, 1, 7, 3, 0, 0, 0)},
					{new T(Face.TOP, 1, 2, 6, 0, 0, 0), new T(Face.TOP, 1, 6, 8, 0, 0, 0), new T(Face.TOP, 1, 8, 9, 0, 0, 0), new T(Face.TOP, 8, 6, 7, 0, 0, 0)},
					{new T(Face.TOP, 2, 6, 9, 0, 0, 0), new T(Face.TOP, 2, 9, 1, 0, 0, 0), new T(Face.TOP, 6, 7, 9, 0, 0, 0), new T(Face.TOP, 0, 9, 3, 0, 0, 0), new T(Face.TOP, 7, 3, 9, 0, 0, 0)},
					{new T(Face.TOP, 7, 8, 0, 0, 0, 0), new T(Face.TOP, 7, 0, 6, 0, 0, 0), new T(Face.TOP, 6, 0, 2, 0, 0, 0)},
					{new T(Face.TOP, 7, 3, 2, 0, 0, 0), new T(Face.TOP, 6, 7, 2, 0, 0, 0)},
					{new T(Face.TOP, 2, 3, 11, 0, 0, 0), new T(Face.TOP, 10, 6, 8, 0, 0, 0), new T(Face.TOP, 10, 8, 9, 0, 0, 0), new T(Face.TOP, 8, 6, 7, 0, 0, 0)},
					{new T(Face.TOP, 2, 0, 7, 0, 0, 0), new T(Face.TOP, 2, 7, 11, 0, 0, 0), new T(Face.TOP, 0, 9, 7, 0, 0, 0), new T(Face.TOP, 6, 7, 10, 0, 0, 0), new T(Face.TOP, 9, 10, 7, 0, 0, 0)},
					{new T(Face.TOP, 1, 8, 0, 0, 0, 0), new T(Face.TOP, 1, 7, 8, 0, 0, 0), new T(Face.TOP, 1, 10, 7, 0, 0, 0), new T(Face.TOP, 6, 7, 10, 0, 0, 0), new T(Face.TOP, 2, 3, 11, 0, 0, 0)},
					{new T(Face.TOP, 11, 2, 1, 0, 0, 0), new T(Face.TOP, 11, 1, 7, 0, 0, 0), new T(Face.TOP, 10, 6, 1, 0, 0, 0), new T(Face.TOP, 6, 7, 1, 0, 0, 0)},
					{new T(Face.TOP, 8, 9, 6, 0, 0, 0), new T(Face.TOP, 8, 6, 7, 0, 0, 0), new T(Face.TOP, 9, 1, 6, 0, 0, 0), new T(Face.TOP, 11, 6, 3, 0, 0, 0), new T(Face.TOP, 1, 3, 6, 0, 0, 0)},
					{new T(Face.TOP, 0, 9, 1, 0, 0, 0), new T(Face.TOP, 11, 6, 7, 0, 0, 0)},
					{new T(Face.TOP, 7, 8, 0, 0, 0, 0), new T(Face.TOP, 7, 0, 6, 0, 0, 0), new T(Face.TOP, 3, 11, 0, 0, 0, 0), new T(Face.TOP, 11, 6, 0, 0, 0, 0)},
					{new T(Face.TOP, 7, 11, 6, 0, 0, 0)},
					{new T(Face.TOP, 7, 6, 11, 0, 0, 0)},
					{new T(Face.TOP, 3, 0, 8, 0, 0, 0), new T(Face.TOP, 11, 7, 6, 0, 0, 0)},
					{new T(Face.TOP, 0, 1, 9, 0, 0, 0), new T(Face.TOP, 11, 7, 6, 0, 0, 0)},
					{new T(Face.TOP, 8, 1, 9, 0, 0, 0), new T(Face.TOP, 8, 3, 1, 0, 0, 0), new T(Face.TOP, 11, 7, 6, 0, 0, 0)},
					{new T(Face.TOP, 10, 1, 2, 0, 0, 0), new T(Face.TOP, 6, 11, 7, 0, 0, 0)},
					{new T(Face.TOP, 1, 2, 10, 0, 0, 0), new T(Face.TOP, 3, 0, 8, 0, 0, 0), new T(Face.TOP, 6, 11, 7, 0, 0, 0)},
					{new T(Face.TOP, 2, 9, 0, 0, 0, 0), new T(Face.TOP, 2, 10, 9, 0, 0, 0), new T(Face.TOP, 6, 11, 7, 0, 0, 0)},
					{new T(Face.TOP, 6, 11, 7, 0, 0, 0), new T(Face.TOP, 2, 10, 3, 0, 0, 0), new T(Face.TOP, 10, 8, 3, 0, 0, 0), new T(Face.TOP, 10, 9, 8, 0, 0, 0)},
					{new T(Face.TOP, 7, 2, 3, 0, 0, 0), new T(Face.TOP, 6, 2, 7, 0, 0, 0)},
					{new T(Face.TOP, 7, 0, 8, 0, 0, 0), new T(Face.TOP, 7, 6, 0, 0, 0, 0), new T(Face.TOP, 6, 2, 0, 0, 0, 0)},
					{new T(Face.TOP, 2, 7, 6, 0, 0, 0), new T(Face.TOP, 2, 3, 7, 0, 0, 0), new T(Face.TOP, 0, 1, 9, 0, 0, 0)},
					{new T(Face.TOP, 1, 6, 2, 0, 0, 0), new T(Face.TOP, 1, 8, 6, 0, 0, 0), new T(Face.TOP, 1, 9, 8, 0, 0, 0), new T(Face.TOP, 8, 7, 6, 0, 0, 0)},
					{new T(Face.TOP, 10, 7, 6, 0, 0, 0), new T(Face.TOP, 10, 1, 7, 0, 0, 0), new T(Face.TOP, 1, 3, 7, 0, 0, 0)},
					{new T(Face.TOP, 10, 7, 6, 0, 0, 0), new T(Face.TOP, 1, 7, 10, 0, 0, 0), new T(Face.TOP, 1, 8, 7, 0, 0, 0), new T(Face.TOP, 1, 0, 8, 0, 0, 0)},
					{new T(Face.TOP, 0, 3, 7, 0, 0, 0), new T(Face.TOP, 0, 7, 10, 0, 0, 0), new T(Face.TOP, 0, 10, 9, 0, 0, 0), new T(Face.TOP, 6, 10, 7, 0, 0, 0)},
					{new T(Face.TOP, 7, 6, 10, 0, 0, 0), new T(Face.TOP, 7, 10, 8, 0, 0, 0), new T(Face.TOP, 8, 10, 9, 0, 0, 0)},
					{new T(Face.TOP, 6, 8, 4, 0, 0, 0), new T(Face.TOP, 11, 8, 6, 0, 0, 0)},
					{new T(Face.TOP, 3, 6, 11, 0, 0, 0), new T(Face.TOP, 3, 0, 6, 0, 0, 0), new T(Face.TOP, 0, 4, 6, 0, 0, 0)},
					{new T(Face.TOP, 8, 6, 11, 0, 0, 0), new T(Face.TOP, 8, 4, 6, 0, 0, 0), new T(Face.TOP, 9, 0, 1, 0, 0, 0)},
					{new T(Face.TOP, 9, 4, 6, 0, 0, 0), new T(Face.TOP, 9, 6, 3, 0, 0, 0), new T(Face.TOP, 9, 3, 1, 0, 0, 0), new T(Face.TOP, 11, 3, 6, 0, 0, 0)},
					{new T(Face.TOP, 6, 8, 4, 0, 0, 0), new T(Face.TOP, 6, 11, 8, 0, 0, 0), new T(Face.TOP, 2, 10, 1, 0, 0, 0)},
					{new T(Face.TOP, 1, 2, 10, 0, 0, 0), new T(Face.TOP, 3, 0, 11, 0, 0, 0), new T(Face.TOP, 0, 6, 11, 0, 0, 0), new T(Face.TOP, 0, 4, 6, 0, 0, 0)},
					{new T(Face.TOP, 4, 11, 8, 0, 0, 0), new T(Face.TOP, 4, 6, 11, 0, 0, 0), new T(Face.TOP, 0, 2, 9, 0, 0, 0), new T(Face.TOP, 2, 10, 9, 0, 0, 0)},
					{new T(Face.TOP, 10, 9, 3, 0, 0, 0), new T(Face.TOP, 10, 3, 2, 0, 0, 0), new T(Face.TOP, 9, 4, 3, 0, 0, 0), new T(Face.TOP, 11, 3, 6, 0, 0, 0), new T(Face.TOP, 4, 6, 3, 0, 0, 0)},
					{new T(Face.TOP, 8, 2, 3, 0, 0, 0), new T(Face.TOP, 8, 4, 2, 0, 0, 0), new T(Face.TOP, 4, 6, 2, 0, 0, 0)},
					{new T(Face.TOP, 0, 4, 2, 0, 0, 0), new T(Face.TOP, 4, 6, 2, 0, 0, 0)},
					{new T(Face.TOP, 1, 9, 0, 0, 0, 0), new T(Face.TOP, 2, 3, 4, 0, 0, 0), new T(Face.TOP, 2, 4, 6, 0, 0, 0), new T(Face.TOP, 4, 3, 8, 0, 0, 0)},
					{new T(Face.TOP, 1, 9, 4, 0, 0, 0), new T(Face.TOP, 1, 4, 2, 0, 0, 0), new T(Face.TOP, 2, 4, 6, 0, 0, 0)},
					{new T(Face.TOP, 8, 1, 3, 0, 0, 0), new T(Face.TOP, 8, 6, 1, 0, 0, 0), new T(Face.TOP, 8, 4, 6, 0, 0, 0), new T(Face.TOP, 6, 10, 1, 0, 0, 0)},
					{new T(Face.TOP, 10, 1, 0, 0, 0, 0), new T(Face.TOP, 10, 0, 6, 0, 0, 0), new T(Face.TOP, 6, 0, 4, 0, 0, 0)},
					{new T(Face.TOP, 4, 6, 3, 0, 0, 0), new T(Face.TOP, 4, 3, 8, 0, 0, 0), new T(Face.TOP, 6, 10, 3, 0, 0, 0), new T(Face.TOP, 0, 3, 9, 0, 0, 0), new T(Face.TOP, 10, 9, 3, 0, 0, 0)},
					{new T(Face.TOP, 10, 9, 4, 0, 0, 0), new T(Face.TOP, 6, 10, 4, 0, 0, 0)},
					{new T(Face.TOP, 4, 9, 5, 0, 0, 0), new T(Face.TOP, 7, 6, 11, 0, 0, 0)},
					{new T(Face.TOP, 0, 8, 3, 0, 0, 0), new T(Face.TOP, 4, 9, 5, 0, 0, 0), new T(Face.TOP, 11, 7, 6, 0, 0, 0)},
					{new T(Face.TOP, 5, 0, 1, 0, 0, 0), new T(Face.TOP, 5, 4, 0, 0, 0, 0), new T(Face.TOP, 7, 6, 11, 0, 0, 0)},
					{new T(Face.TOP, 11, 7, 6, 0, 0, 0), new T(Face.TOP, 8, 3, 4, 0, 0, 0), new T(Face.TOP, 3, 5, 4, 0, 0, 0), new T(Face.TOP, 3, 1, 5, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 4, 0, 0, 0), new T(Face.TOP, 10, 1, 2, 0, 0, 0), new T(Face.TOP, 7, 6, 11, 0, 0, 0)},
					{new T(Face.TOP, 6, 11, 7, 0, 0, 0), new T(Face.TOP, 1, 2, 10, 0, 0, 0), new T(Face.TOP, 0, 8, 3, 0, 0, 0), new T(Face.TOP, 4, 9, 5, 0, 0, 0)},
					{new T(Face.TOP, 7, 6, 11, 0, 0, 0), new T(Face.TOP, 5, 4, 10, 0, 0, 0), new T(Face.TOP, 4, 2, 10, 0, 0, 0), new T(Face.TOP, 4, 0, 2, 0, 0, 0)},
					{new T(Face.TOP, 3, 4, 8, 0, 0, 0), new T(Face.TOP, 3, 5, 4, 0, 0, 0), new T(Face.TOP, 3, 2, 5, 0, 0, 0), new T(Face.TOP, 10, 5, 2, 0, 0, 0), new T(Face.TOP, 11, 7, 6, 0, 0, 0)},
					{new T(Face.TOP, 7, 2, 3, 0, 0, 0), new T(Face.TOP, 7, 6, 2, 0, 0, 0), new T(Face.TOP, 5, 4, 9, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 4, 0, 0, 0), new T(Face.TOP, 0, 8, 6, 0, 0, 0), new T(Face.TOP, 0, 6, 2, 0, 0, 0), new T(Face.TOP, 6, 8, 7, 0, 0, 0)},
					{new T(Face.TOP, 3, 6, 2, 0, 0, 0), new T(Face.TOP, 3, 7, 6, 0, 0, 0), new T(Face.TOP, 1, 5, 0, 0, 0, 0), new T(Face.TOP, 5, 4, 0, 0, 0, 0)},
					{new T(Face.TOP, 6, 2, 8, 0, 0, 0), new T(Face.TOP, 6, 8, 7, 0, 0, 0), new T(Face.TOP, 2, 1, 8, 0, 0, 0), new T(Face.TOP, 4, 8, 5, 0, 0, 0), new T(Face.TOP, 1, 5, 8, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 4, 0, 0, 0), new T(Face.TOP, 10, 1, 6, 0, 0, 0), new T(Face.TOP, 1, 7, 6, 0, 0, 0), new T(Face.TOP, 1, 3, 7, 0, 0, 0)},
					{new T(Face.TOP, 1, 6, 10, 0, 0, 0), new T(Face.TOP, 1, 7, 6, 0, 0, 0), new T(Face.TOP, 1, 0, 7, 0, 0, 0), new T(Face.TOP, 8, 7, 0, 0, 0, 0), new T(Face.TOP, 9, 5, 4, 0, 0, 0)},
					{new T(Face.TOP, 4, 0, 10, 0, 0, 0), new T(Face.TOP, 4, 10, 5, 0, 0, 0), new T(Face.TOP, 0, 3, 10, 0, 0, 0), new T(Face.TOP, 6, 10, 7, 0, 0, 0), new T(Face.TOP, 3, 7, 10, 0, 0, 0)},
					{new T(Face.TOP, 7, 6, 10, 0, 0, 0), new T(Face.TOP, 7, 10, 8, 0, 0, 0), new T(Face.TOP, 5, 4, 10, 0, 0, 0), new T(Face.TOP, 4, 8, 10, 0, 0, 0)},
					{new T(Face.TOP, 6, 9, 5, 0, 0, 0), new T(Face.TOP, 6, 11, 9, 0, 0, 0), new T(Face.TOP, 11, 8, 9, 0, 0, 0)},
					{new T(Face.TOP, 3, 6, 11, 0, 0, 0), new T(Face.TOP, 0, 6, 3, 0, 0, 0), new T(Face.TOP, 0, 5, 6, 0, 0, 0), new T(Face.TOP, 0, 9, 5, 0, 0, 0)},
					{new T(Face.TOP, 0, 11, 8, 0, 0, 0), new T(Face.TOP, 0, 5, 11, 0, 0, 0), new T(Face.TOP, 0, 1, 5, 0, 0, 0), new T(Face.TOP, 5, 6, 11, 0, 0, 0)},
					{new T(Face.TOP, 6, 11, 3, 0, 0, 0), new T(Face.TOP, 6, 3, 5, 0, 0, 0), new T(Face.TOP, 5, 3, 1, 0, 0, 0)},
					{new T(Face.TOP, 1, 2, 10, 0, 0, 0), new T(Face.TOP, 9, 5, 11, 0, 0, 0), new T(Face.TOP, 9, 11, 8, 0, 0, 0), new T(Face.TOP, 11, 5, 6, 0, 0, 0)},
					{new T(Face.TOP, 0, 11, 3, 0, 0, 0), new T(Face.TOP, 0, 6, 11, 0, 0, 0), new T(Face.TOP, 0, 9, 6, 0, 0, 0), new T(Face.TOP, 5, 6, 9, 0, 0, 0), new T(Face.TOP, 1, 2, 10, 0, 0, 0)},
					{new T(Face.TOP, 11, 8, 5, 0, 0, 0), new T(Face.TOP, 11, 5, 6, 0, 0, 0), new T(Face.TOP, 8, 0, 5, 0, 0, 0), new T(Face.TOP, 10, 5, 2, 0, 0, 0), new T(Face.TOP, 0, 2, 5, 0, 0, 0)},
					{new T(Face.TOP, 6, 11, 3, 0, 0, 0), new T(Face.TOP, 6, 3, 5, 0, 0, 0), new T(Face.TOP, 2, 10, 3, 0, 0, 0), new T(Face.TOP, 10, 5, 3, 0, 0, 0)},
					{new T(Face.TOP, 5, 8, 9, 0, 0, 0), new T(Face.TOP, 5, 2, 8, 0, 0, 0), new T(Face.TOP, 5, 6, 2, 0, 0, 0), new T(Face.TOP, 3, 8, 2, 0, 0, 0)},
					{new T(Face.TOP, 9, 5, 6, 0, 0, 0), new T(Face.TOP, 9, 6, 0, 0, 0, 0), new T(Face.TOP, 0, 6, 2, 0, 0, 0)},
					{new T(Face.TOP, 1, 5, 8, 0, 0, 0), new T(Face.TOP, 1, 8, 0, 0, 0, 0), new T(Face.TOP, 5, 6, 8, 0, 0, 0), new T(Face.TOP, 3, 8, 2, 0, 0, 0), new T(Face.TOP, 6, 2, 8, 0, 0, 0)},
					{new T(Face.TOP, 1, 5, 6, 0, 0, 0), new T(Face.TOP, 2, 1, 6, 0, 0, 0)},
					{new T(Face.TOP, 1, 3, 6, 0, 0, 0), new T(Face.TOP, 1, 6, 10, 0, 0, 0), new T(Face.TOP, 3, 8, 6, 0, 0, 0), new T(Face.TOP, 5, 6, 9, 0, 0, 0), new T(Face.TOP, 8, 9, 6, 0, 0, 0)},
					{new T(Face.TOP, 10, 1, 0, 0, 0, 0), new T(Face.TOP, 10, 0, 6, 0, 0, 0), new T(Face.TOP, 9, 5, 0, 0, 0, 0), new T(Face.TOP, 5, 6, 0, 0, 0, 0)},
					{new T(Face.TOP, 0, 3, 8, 0, 0, 0), new T(Face.TOP, 5, 6, 10, 0, 0, 0)},
					{new T(Face.TOP, 10, 5, 6, 0, 0, 0)},
					{new T(Face.TOP, 11, 5, 10, 0, 0, 0), new T(Face.TOP, 7, 5, 11, 0, 0, 0)},
					{new T(Face.TOP, 11, 5, 10, 0, 0, 0), new T(Face.TOP, 11, 7, 5, 0, 0, 0), new T(Face.TOP, 8, 3, 0, 0, 0, 0)},
					{new T(Face.TOP, 5, 11, 7, 0, 0, 0), new T(Face.TOP, 5, 10, 11, 0, 0, 0), new T(Face.TOP, 1, 9, 0, 0, 0, 0)},
					{new T(Face.TOP, 10, 7, 5, 0, 0, 0), new T(Face.TOP, 10, 11, 7, 0, 0, 0), new T(Face.TOP, 9, 8, 1, 0, 0, 0), new T(Face.TOP, 8, 3, 1, 0, 0, 0)},
					{new T(Face.TOP, 11, 1, 2, 0, 0, 0), new T(Face.TOP, 11, 7, 1, 0, 0, 0), new T(Face.TOP, 7, 5, 1, 0, 0, 0)},
					{new T(Face.TOP, 0, 8, 3, 0, 0, 0), new T(Face.TOP, 1, 2, 7, 0, 0, 0), new T(Face.TOP, 1, 7, 5, 0, 0, 0), new T(Face.TOP, 7, 2, 11, 0, 0, 0)},
					{new T(Face.TOP, 9, 7, 5, 0, 0, 0), new T(Face.TOP, 9, 2, 7, 0, 0, 0), new T(Face.TOP, 9, 0, 2, 0, 0, 0), new T(Face.TOP, 2, 11, 7, 0, 0, 0)},
					{new T(Face.TOP, 7, 5, 2, 0, 0, 0), new T(Face.TOP, 7, 2, 11, 0, 0, 0), new T(Face.TOP, 5, 9, 2, 0, 0, 0), new T(Face.TOP, 3, 2, 8, 0, 0, 0), new T(Face.TOP, 9, 8, 2, 0, 0, 0)},
					{new T(Face.TOP, 2, 5, 10, 0, 0, 0), new T(Face.TOP, 2, 3, 5, 0, 0, 0), new T(Face.TOP, 3, 7, 5, 0, 0, 0)},
					{new T(Face.TOP, 8, 2, 0, 0, 0, 0), new T(Face.TOP, 8, 5, 2, 0, 0, 0), new T(Face.TOP, 8, 7, 5, 0, 0, 0), new T(Face.TOP, 10, 2, 5, 0, 0, 0)},
					{new T(Face.TOP, 9, 0, 1, 0, 0, 0), new T(Face.TOP, 5, 10, 3, 0, 0, 0), new T(Face.TOP, 5, 3, 7, 0, 0, 0), new T(Face.TOP, 3, 10, 2, 0, 0, 0)},
					{new T(Face.TOP, 9, 8, 2, 0, 0, 0), new T(Face.TOP, 9, 2, 1, 0, 0, 0), new T(Face.TOP, 8, 7, 2, 0, 0, 0), new T(Face.TOP, 10, 2, 5, 0, 0, 0), new T(Face.TOP, 7, 5, 2, 0, 0, 0)},
					{new T(Face.TOP, 1, 3, 5, 0, 0, 0), new T(Face.TOP, 3, 7, 5, 0, 0, 0)},
					{new T(Face.TOP, 0, 8, 7, 0, 0, 0), new T(Face.TOP, 0, 7, 1, 0, 0, 0), new T(Face.TOP, 1, 7, 5, 0, 0, 0)},
					{new T(Face.TOP, 9, 0, 3, 0, 0, 0), new T(Face.TOP, 9, 3, 5, 0, 0, 0), new T(Face.TOP, 5, 3, 7, 0, 0, 0)},
					{new T(Face.TOP, 9, 8, 7, 0, 0, 0), new T(Face.TOP, 5, 9, 7, 0, 0, 0)},
					{new T(Face.TOP, 5, 8, 4, 0, 0, 0), new T(Face.TOP, 5, 10, 8, 0, 0, 0), new T(Face.TOP, 10, 11, 8, 0, 0, 0)},
					{new T(Face.TOP, 5, 0, 4, 0, 0, 0), new T(Face.TOP, 5, 11, 0, 0, 0, 0), new T(Face.TOP, 5, 10, 11, 0, 0, 0), new T(Face.TOP, 11, 3, 0, 0, 0, 0)},
					{new T(Face.TOP, 0, 1, 9, 0, 0, 0), new T(Face.TOP, 8, 4, 10, 0, 0, 0), new T(Face.TOP, 8, 10, 11, 0, 0, 0), new T(Face.TOP, 10, 4, 5, 0, 0, 0)},
					{new T(Face.TOP, 10, 11, 4, 0, 0, 0), new T(Face.TOP, 10, 4, 5, 0, 0, 0), new T(Face.TOP, 11, 3, 4, 0, 0, 0), new T(Face.TOP, 9, 4, 1, 0, 0, 0), new T(Face.TOP, 3, 1, 4, 0, 0, 0)},
					{new T(Face.TOP, 2, 5, 1, 0, 0, 0), new T(Face.TOP, 2, 8, 5, 0, 0, 0), new T(Face.TOP, 2, 11, 8, 0, 0, 0), new T(Face.TOP, 4, 5, 8, 0, 0, 0)},
					{new T(Face.TOP, 0, 4, 11, 0, 0, 0), new T(Face.TOP, 0, 11, 3, 0, 0, 0), new T(Face.TOP, 4, 5, 11, 0, 0, 0), new T(Face.TOP, 2, 11, 1, 0, 0, 0), new T(Face.TOP, 5, 1, 11, 0, 0, 0)},
					{new T(Face.TOP, 0, 2, 5, 0, 0, 0), new T(Face.TOP, 0, 5, 9, 0, 0, 0), new T(Face.TOP, 2, 11, 5, 0, 0, 0), new T(Face.TOP, 4, 5, 8, 0, 0, 0), new T(Face.TOP, 11, 8, 5, 0, 0, 0)},
					{new T(Face.TOP, 9, 4, 5, 0, 0, 0), new T(Face.TOP, 2, 11, 3, 0, 0, 0)},
					{new T(Face.TOP, 2, 5, 10, 0, 0, 0), new T(Face.TOP, 3, 5, 2, 0, 0, 0), new T(Face.TOP, 3, 4, 5, 0, 0, 0), new T(Face.TOP, 3, 8, 4, 0, 0, 0)},
					{new T(Face.TOP, 5, 10, 2, 0, 0, 0), new T(Face.TOP, 5, 2, 4, 0, 0, 0), new T(Face.TOP, 4, 2, 0, 0, 0, 0)},
					{new T(Face.TOP, 3, 10, 2, 0, 0, 0), new T(Face.TOP, 3, 5, 10, 0, 0, 0), new T(Face.TOP, 3, 8, 5, 0, 0, 0), new T(Face.TOP, 4, 5, 8, 0, 0, 0), new T(Face.TOP, 0, 1, 9, 0, 0, 0)},
					{new T(Face.TOP, 5, 10, 2, 0, 0, 0), new T(Face.TOP, 5, 2, 4, 0, 0, 0), new T(Face.TOP, 1, 9, 2, 0, 0, 0), new T(Face.TOP, 9, 4, 2, 0, 0, 0)},
					{new T(Face.TOP, 8, 4, 5, 0, 0, 0), new T(Face.TOP, 8, 5, 3, 0, 0, 0), new T(Face.TOP, 3, 5, 1, 0, 0, 0)},
					{new T(Face.TOP, 0, 4, 5, 0, 0, 0), new T(Face.TOP, 1, 0, 5, 0, 0, 0)},
					{new T(Face.TOP, 8, 4, 5, 0, 0, 0), new T(Face.TOP, 8, 5, 3, 0, 0, 0), new T(Face.TOP, 9, 0, 5, 0, 0, 0), new T(Face.TOP, 0, 3, 5, 0, 0, 0)},
					{new T(Face.TOP, 9, 4, 5, 0, 0, 0)},
					{new T(Face.TOP, 4, 11, 7, 0, 0, 0), new T(Face.TOP, 4, 9, 11, 0, 0, 0), new T(Face.TOP, 9, 10, 11, 0, 0, 0)},
					{new T(Face.TOP, 0, 8, 3, 0, 0, 0), new T(Face.TOP, 4, 9, 7, 0, 0, 0), new T(Face.TOP, 9, 11, 7, 0, 0, 0), new T(Face.TOP, 9, 10, 11, 0, 0, 0)},
					{new T(Face.TOP, 1, 10, 11, 0, 0, 0), new T(Face.TOP, 1, 11, 4, 0, 0, 0), new T(Face.TOP, 1, 4, 0, 0, 0, 0), new T(Face.TOP, 7, 4, 11, 0, 0, 0)},
					{new T(Face.TOP, 3, 1, 4, 0, 0, 0), new T(Face.TOP, 3, 4, 8, 0, 0, 0), new T(Face.TOP, 1, 10, 4, 0, 0, 0), new T(Face.TOP, 7, 4, 11, 0, 0, 0), new T(Face.TOP, 10, 11, 4, 0, 0, 0)},
					{new T(Face.TOP, 4, 11, 7, 0, 0, 0), new T(Face.TOP, 9, 11, 4, 0, 0, 0), new T(Face.TOP, 9, 2, 11, 0, 0, 0), new T(Face.TOP, 9, 1, 2, 0, 0, 0)},
					{new T(Face.TOP, 9, 7, 4, 0, 0, 0), new T(Face.TOP, 9, 11, 7, 0, 0, 0), new T(Face.TOP, 9, 1, 11, 0, 0, 0), new T(Face.TOP, 2, 11, 1, 0, 0, 0), new T(Face.TOP, 0, 8, 3, 0, 0, 0)},
					{new T(Face.TOP, 11, 7, 4, 0, 0, 0), new T(Face.TOP, 11, 4, 2, 0, 0, 0), new T(Face.TOP, 2, 4, 0, 0, 0, 0)},
					{new T(Face.TOP, 11, 7, 4, 0, 0, 0), new T(Face.TOP, 11, 4, 2, 0, 0, 0), new T(Face.TOP, 8, 3, 4, 0, 0, 0), new T(Face.TOP, 3, 2, 4, 0, 0, 0)},
					{new T(Face.TOP, 2, 9, 10, 0, 0, 0), new T(Face.TOP, 2, 7, 9, 0, 0, 0), new T(Face.TOP, 2, 3, 7, 0, 0, 0), new T(Face.TOP, 7, 4, 9, 0, 0, 0)},
					{new T(Face.TOP, 9, 10, 7, 0, 0, 0), new T(Face.TOP, 9, 7, 4, 0, 0, 0), new T(Face.TOP, 10, 2, 7, 0, 0, 0), new T(Face.TOP, 8, 7, 0, 0, 0, 0), new T(Face.TOP, 2, 0, 7, 0, 0, 0)},
					{new T(Face.TOP, 3, 7, 10, 0, 0, 0), new T(Face.TOP, 3, 10, 2, 0, 0, 0), new T(Face.TOP, 7, 4, 10, 0, 0, 0), new T(Face.TOP, 1, 10, 0, 0, 0, 0), new T(Face.TOP, 4, 0, 10, 0, 0, 0)},
					{new T(Face.TOP, 1, 10, 2, 0, 0, 0), new T(Face.TOP, 8, 7, 4, 0, 0, 0)},
					{new T(Face.TOP, 4, 9, 1, 0, 0, 0), new T(Face.TOP, 4, 1, 7, 0, 0, 0), new T(Face.TOP, 7, 1, 3, 0, 0, 0)},
					{new T(Face.TOP, 4, 9, 1, 0, 0, 0), new T(Face.TOP, 4, 1, 7, 0, 0, 0), new T(Face.TOP, 0, 8, 1, 0, 0, 0), new T(Face.TOP, 8, 7, 1, 0, 0, 0)},
					{new T(Face.TOP, 4, 0, 3, 0, 0, 0), new T(Face.TOP, 7, 4, 3, 0, 0, 0)},
					{new T(Face.TOP, 4, 8, 7, 0, 0, 0)},
					{new T(Face.TOP, 9, 10, 8, 0, 0, 0), new T(Face.TOP, 10, 11, 8, 0, 0, 0)},
					{new T(Face.TOP, 3, 0, 9, 0, 0, 0), new T(Face.TOP, 3, 9, 11, 0, 0, 0), new T(Face.TOP, 11, 9, 10, 0, 0, 0)},
					{new T(Face.TOP, 0, 1, 10, 0, 0, 0), new T(Face.TOP, 0, 10, 8, 0, 0, 0), new T(Face.TOP, 8, 10, 11, 0, 0, 0)},
					{new T(Face.TOP, 3, 1, 10, 0, 0, 0), new T(Face.TOP, 11, 3, 10, 0, 0, 0)},
					{new T(Face.TOP, 1, 2, 11, 0, 0, 0), new T(Face.TOP, 1, 11, 9, 0, 0, 0), new T(Face.TOP, 9, 11, 8, 0, 0, 0)},
					{new T(Face.TOP, 3, 0, 9, 0, 0, 0), new T(Face.TOP, 3, 9, 11, 0, 0, 0), new T(Face.TOP, 1, 2, 9, 0, 0, 0), new T(Face.TOP, 2, 11, 9, 0, 0, 0)},
					{new T(Face.TOP, 0, 2, 11, 0, 0, 0), new T(Face.TOP, 8, 0, 11, 0, 0, 0)},
					{new T(Face.TOP, 3, 2, 11, 0, 0, 0)},
					{new T(Face.TOP, 2, 3, 8, 0, 0, 0), new T(Face.TOP, 2, 8, 10, 0, 0, 0), new T(Face.TOP, 10, 8, 9, 0, 0, 0)},
					{new T(Face.TOP, 9, 10, 2, 0, 0, 0), new T(Face.TOP, 0, 9, 2, 0, 0, 0)},
					{new T(Face.TOP, 2, 3, 8, 0, 0, 0), new T(Face.TOP, 2, 8, 10, 0, 0, 0), new T(Face.TOP, 0, 1, 8, 0, 0, 0), new T(Face.TOP, 1, 10, 8, 0, 0, 0)},
					{new T(Face.TOP, 1, 10, 2, 0, 0, 0)},
					{new T(Face.TOP, 1, 3, 8, 0, 0, 0), new T(Face.TOP, 9, 1, 8, 0, 0, 0)},
					{new T(Face.TOP, 0, 9, 1, 0, 0, 0)},
					{new T(Face.TOP, 0, 3, 8, 0, 0, 0)},
					{},
			};

	
		for (int z = 0; z < Terrain.DIMZ; z += this.lodz) {
			for (int y = 0; y < Terrain.DIMY; y += this.lody) {
				for (int x = 0; x < Terrain.DIMX; x += this.lodx) {
					this.processBlock(opaqueStack, transparentStack, terrain, x, y, z);
				}
			}
		}
	}

	private void processBlock(ArrayList<TerrainMeshTriangle> opaqueStack, ArrayList<TerrainMeshTriangle> transparentStack,
			Terrain terrain, int x, int y, int z) {
		TerrainMeshVertex edgeVertices[] = new TerrainMeshVertex[12];

		Block block = terrain.getBlock(x, y, z);
		BlockRenderer blockRenderer = BlockRendererManager.instance().getBlockRenderer(block);
		if (blockRenderer == null) {
			for (Face face : Face.faces) {
				block = terrain.getBlock(x + face.getVector().x, y + face.getVector().y, z + face.getVector().z);
				blockRenderer = BlockRendererManager.instance().getBlockRenderer(block);
				if (blockRenderer != null) {
					break;
				}
			}
		}

		// Make a local copy of the values at the cube's corners
		// Find which vertices are inside of the surface and which are outside
		int indexFlags = 0;
		for (int vertexID = 0; vertexID < 8; vertexID++) {
			Block neighbor = terrain.getBlock(x + BlockRenderer.VERTICES[vertexID].x * this.lodx,
					y + BlockRenderer.VERTICES[vertexID].y * this.lody,
					z + BlockRenderer.VERTICES[vertexID].z * this.lodz);
			if (neighbor.isVisible()) {
				indexFlags |= 1 << vertexID;
			}
		}

		// Find which edges are intersected by the surface
		int edgeFlags = T.EDGE_TABLE[indexFlags];

		// If the cube is entirely inside or outside of the surface, then there
		// will be
		// no intersections
		if (edgeFlags == 0) {
			return;
		}

		// Find the point of intersection of the surface with each edge
		// Then find the normal to the surface at those points
		for (int edgeID = 0; edgeID < 12; edgeID++) {
			// if there is an intersection on this edge
			if ((edgeFlags & (1 << edgeID)) != 0) {
				int v0 = BlockRenderer.EDGES[edgeID][0];
				float offset = 0.5f;
				float offx = offset * BlockRenderer.EDGES_DIRECTIONS[edgeID].x;
				float offy = offset * BlockRenderer.EDGES_DIRECTIONS[edgeID].y;
				float offz = offset * BlockRenderer.EDGES_DIRECTIONS[edgeID].z;
				float dx = BlockRenderer.VERTICES[v0].x + offx;
				float dy = BlockRenderer.VERTICES[v0].y + offy;
				float dz = BlockRenderer.VERTICES[v0].z + offz;
				float posx = x + dx * this.lodx;
				float posy = y + dy * this.lody;
				float posz = z + dz * this.lodz;

				// brightness
				float sl = 0.0f;
				float bl = 0.0f;
				int faceID, vertexID;
				for (int i = 1; i < 2; i++) {
					// for (int i = 0; i < 3; i++) {
					faceID = BlockRenderer.VERTICES_FACES[v0][i];
					vertexID = BlockRenderer.VERTICES_FACES_ID[v0][faceID];
					sl += BlockRenderer.getSunLight(terrain, x, y, z, BlockRenderer.FACES_NEIGHBORS[faceID][vertexID]);
					bl += BlockRenderer.getBlockLight(terrain, x, y, z,
							BlockRenderer.FACES_NEIGHBORS[faceID][vertexID]);

					faceID = BlockRenderer.VERTICES_FACES[v0][i];
					vertexID = BlockRenderer.VERTICES_FACES_ID[v0][faceID];
					sl += BlockRenderer.getSunLight(terrain, x, y, z, BlockRenderer.FACES_NEIGHBORS[faceID][vertexID]);
					bl += BlockRenderer.getBlockLight(terrain, x, y, z,
							BlockRenderer.FACES_NEIGHBORS[faceID][vertexID]);
				}
				sl /= (float) 2.0f;
				sl = Maths.clamp(sl, 0.0f, 1.0f);
				bl /= (float) 2.0f;
				bl = Maths.clamp(bl, 0.0f, 1.0f);

				// brightness
				float b = Maths.clamp(sl + bl, 0.4f, 1.2f);

				// uv will be calculate later on depending on which triangle
				// configuration we are on

				// build vertex
				edgeVertices[edgeID] = new TerrainMeshVertex(posx, posy, posz, 0, 1, 0, 0, 0, 0, 0, 0xffffffff, b,
						0.0f);
			}
		}
		// draw the found triangle (5 max per cube)
		for (int triangleID = 0; triangleID < TRI_TABLE[indexFlags].length; triangleID++) {
			T t = TRI_TABLE[indexFlags][triangleID];

			int textureID = blockRenderer == null ? 1 : blockRenderer.getDefaultTextureID(t.faceID);
			float atlasX = BlockRenderer.getAtlasX(textureID);
			float atlasY = BlockRenderer.getAtlasY(textureID);
			TerrainMeshVertex v0 = this.generateVertex(t, 0, edgeVertices, blockRenderer, atlasX, atlasY);
			TerrainMeshVertex v1 = this.generateVertex(t, 1, edgeVertices, blockRenderer, atlasX, atlasY);
			TerrainMeshVertex v2 = this.generateVertex(t, 2, edgeVertices, blockRenderer, atlasX, atlasY);

			TerrainMeshTriangle triangle = new TerrainMeshTriangle(v0, v1, v2);
			if (block.isOpaque()) {
				opaqueStack.add(triangle);
			} else {
				transparentStack.add(triangle);
			}
		}
	}

	private TerrainMeshVertex generateVertex(T t, int edgeID, TerrainMeshVertex[] edgeVertices,
			BlockRenderer blockRenderer, float atlasX, float atlasY) {
		TerrainMeshVertex toCopy = edgeVertices[t.edges[edgeID]];
		TerrainMeshVertex vertex = (TerrainMeshVertex) toCopy.clone();
		vertex.atlasX = atlasX;
		vertex.atlasY = atlasY;
		vertex.u = t.uv[edgeID][0];
		vertex.v = t.uv[edgeID][1];
		if (t.faceID == Face.RIGHT || t.edges[edgeID] == EDGE_DEBUG) {
			vertex.brightness = 0;
		}
		return (vertex);
	}
}
