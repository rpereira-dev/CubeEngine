package com.grillecube.client.renderer.model.dae.datastructures;

/**
 * Contains the extracted data for an animated model, which includes the mesh
 * data, and skeleton (joints heirarchy) data.
 * 
 * @author Karl
 *
 */
public class ModelData {

	private final SkeletonData skeleton;
	private final MeshData mesh;

	public ModelData(MeshData mesh, SkeletonData skeleton) {
		this.skeleton = skeleton;
		this.mesh = mesh;
	}

	public SkeletonData getSkeletonData() {
		return skeleton;
	}

	public MeshData getMeshData() {
		return mesh;
	}

}
