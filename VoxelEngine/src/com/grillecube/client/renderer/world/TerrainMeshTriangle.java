package com.grillecube.client.renderer.world;

import com.grillecube.client.renderer.MeshTriangle;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Vector3f;

public class TerrainMeshTriangle extends MeshTriangle<TerrainMeshVertex> {

	public float cameraDistance;

	public TerrainMeshTriangle(TerrainMeshVertex v0, TerrainMeshVertex v1, TerrainMeshVertex v2) {
		super(v0, v1, v2);
	}

	/**
	 * @param camera
	 * @return : the square dstance between the center of this triangle to the
	 *         camera
	 */
	public float calculateSquareCameraDistance(CameraProjective camera) {
		float cx = camera.getPosition().x;
		float cy = camera.getPosition().y;
		float cz = camera.getPosition().z;
		float x = (this.v0.posx + this.v1.posx + this.v2.posx) * 0.3333333f;
		float y = (this.v0.posy + this.v1.posy + this.v2.posy) * 0.3333333f;
		float z = (this.v0.posz + this.v1.posz + this.v2.posz) * 0.3333333f;
		return ((float) Vector3f.distanceSquare(cx, cy, cz, x, y, z));
	}

	@Override
	public MeshTriangle<TerrainMeshVertex> clone() {
		return (new TerrainMeshTriangle((TerrainMeshVertex) this.v0.clone(), (TerrainMeshVertex) this.v1.clone(),
				(TerrainMeshVertex) this.v2.clone()));
	}

}
