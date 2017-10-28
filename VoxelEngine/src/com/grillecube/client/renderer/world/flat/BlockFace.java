package com.grillecube.client.renderer.world.flat;

import java.util.ArrayList;

import com.grillecube.client.renderer.world.TerrainMeshTriangle;
import com.grillecube.client.renderer.world.TerrainMeshVertex;
import com.grillecube.common.world.block.Block;

/** represent a block face */
public class BlockFace {

	// the block
	public final Block block;

	// the vertices
	public TerrainMeshVertex[] vertices;

	// the texture id
	public int textureID;

	public BlockFace(Block block, int textureID, TerrainMeshVertex... vertices) {
		this.block = block;
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
	public void pushVertices(ArrayList<TerrainMeshTriangle> stack) {

		TerrainMeshVertex v0 = this.vertices[0];
		TerrainMeshVertex v1 = this.vertices[1];
		TerrainMeshVertex v2 = this.vertices[2];
		TerrainMeshVertex v3 = this.vertices[3];

		if (v0.ao + v2.ao < v1.ao + v3.ao) {
			stack.add(new TerrainMeshTriangle(v0, v1, v2));
			stack.add(new TerrainMeshTriangle(v0, v2, v3));
		} else {
			// flip quad
			stack.add(new TerrainMeshTriangle(v1, v2, v3));
			stack.add(new TerrainMeshTriangle(v1, v3, v0));
		}
	}

	public final Block getBlock() {
		return (this.block);
	}
}