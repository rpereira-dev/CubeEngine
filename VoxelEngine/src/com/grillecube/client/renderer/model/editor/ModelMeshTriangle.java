package com.grillecube.client.renderer.model.editor;

import com.grillecube.client.renderer.MeshTriangle;

public class ModelMeshTriangle extends MeshTriangle<ModelMeshVertex> {

	public ModelMeshTriangle(ModelMeshVertex v0, ModelMeshVertex v1, ModelMeshVertex v2) {
		super(v0, v1, v2);
	}

	@Override
	public MeshTriangle<ModelMeshVertex> clone() {
		return (new ModelMeshTriangle((ModelMeshVertex) this.v0.clone(), (ModelMeshVertex) this.v1.clone(),
				(ModelMeshVertex) this.v2.clone()));
	}

}
