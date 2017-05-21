package com.grillecube.client.renderer.world.terrain;

import java.util.Stack;

import com.grillecube.common.faces.Face;
import com.grillecube.common.world.block.Block;

/** represent a block face */
public class BlockFace {

	// the vertices
	public MeshVertex[] vertices;

	// allowed movement for this face
	public Face face;

	// the texture
	public int texture;

	public BlockFace(Block block, Face face, MeshVertex... vertices) {
		this.face = face;
		this.texture = block.getTextureIDForFace(face);
		this.vertices = vertices;
	}

	public boolean hasSameTexture(BlockFace other) {
		return (other.texture == this.texture);
	}

	public boolean hasSameBrightness(BlockFace other) {
		return (this.vertices[0].brightness == other.vertices[0].brightness
				&& this.vertices[1].brightness == other.vertices[1].brightness
				&& this.vertices[2].brightness == other.vertices[2].brightness
				&& this.vertices[3].brightness == other.vertices[3].brightness);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof BlockFace)) {
			return (false);
		}

		BlockFace other = (BlockFace) object;

		return (this.hasSameTexture(other) && this.hasSameBrightness(other));
	}

	/** push this face vertices to the stack */
	public void pushVertices(Stack<MeshVertex> stack) {

		MeshVertex v0 = this.vertices[0];
		MeshVertex v1 = this.vertices[1];
		MeshVertex v2 = this.vertices[2];
		MeshVertex v3 = this.vertices[3];

		if (v0.getAO() + v2.getAO() < v1.getAO() + v3.getAO()) {
			stack.push(v0);
			stack.push(v1);
			stack.push(v2);
			stack.push(v0);
			stack.push(v2);
			stack.push(v3);
		} else {
			// flip quad
			stack.push(v1);
			stack.push(v2);
			stack.push(v3);
			stack.push(v1);
			stack.push(v3);
			stack.push(v0);
		}
	}
}