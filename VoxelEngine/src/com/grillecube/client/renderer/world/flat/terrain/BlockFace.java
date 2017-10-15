package com.grillecube.client.renderer.world.flat.terrain;

import java.util.Stack;

import com.grillecube.client.renderer.world.TerrainMeshVertex;

/** represent a block face */
public class BlockFace {

	// the vertices
	public TerrainMeshVertex[] vertices;

	// the texture id
	public int textureID;

	public BlockFace(int textureID, TerrainMeshVertex... vertices) {
		this.textureID = textureID;
		this.vertices = vertices;
	}

	public boolean hasSameTexture(BlockFace other) {
		return (this.textureID == other.textureID);
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
	public void pushVertices(Stack<TerrainMeshVertex> stack) {

		TerrainMeshVertex v0 = this.vertices[0];
		TerrainMeshVertex v1 = this.vertices[1];
		TerrainMeshVertex v2 = this.vertices[2];
		TerrainMeshVertex v3 = this.vertices[3];

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