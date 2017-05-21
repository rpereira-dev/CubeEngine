package com.grillecube.client.renderer.model;

import java.util.Stack;

import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.json.ModelMeshVertex;

public class ModelMesherCull extends ModelMesher {

	@Override
	protected Stack<ModelMeshVertex> getVertexStack(ModelPartBuilder part) {

		Stack<ModelMeshVertex> stack = new Stack<ModelMeshVertex>();

		// for each block
		for (int x = ModelPartBuilder.MIN_X; x < ModelPartBuilder.MAX_X; x++) {
			for (int y = ModelPartBuilder.MIN_Y; y < ModelPartBuilder.MAX_Y; y++) {
				for (int z = ModelPartBuilder.MIN_Z; z < ModelPartBuilder.MAX_Z; z++) {
					super.pushVertices(part, stack, x, y, z);
				}
			}
		}
		return (stack);

	}
}
