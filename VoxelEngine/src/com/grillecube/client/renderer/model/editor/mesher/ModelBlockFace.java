package com.grillecube.client.renderer.model.editor.mesher;

public class ModelBlockFace {

	// the vertices
	public final ModelMeshVertex[] vertices;

	public ModelBlockFace(ModelMeshVertex... vertices) {
		this.vertices = vertices;
	}
}
