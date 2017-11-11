package com.grillecube.client.renderer.model.editor.mesher;

import com.grillecube.client.renderer.model.editor.ModelMeshVertex;

public class ModelBlockFace {

	// the vertices
	public final ModelMeshVertex[] vertices;

	public ModelBlockFace(ModelMeshVertex... vertices) {
		this.vertices = vertices;
	}
}