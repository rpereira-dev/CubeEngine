package com.grillecube.client.renderer.world;

public class TerrainMeshTriangle {

	public final TerrainMeshVertex v0;
	public final TerrainMeshVertex v1;
	public final TerrainMeshVertex v2;

	public TerrainMeshTriangle(TerrainMeshVertex v0, TerrainMeshVertex v1, TerrainMeshVertex v2) {
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
	}
}
