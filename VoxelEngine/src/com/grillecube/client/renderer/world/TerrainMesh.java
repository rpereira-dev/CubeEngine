/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.world;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.GLVertexArray;
import com.grillecube.client.opengl.GLVertexBuffer;
import com.grillecube.client.renderer.Mesh;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class TerrainMesh extends Mesh {

	// TODO : optimize memory here
	// (x, y, z, nx, ny, nz, atlasx, atlasy, uvx, uvy, color, brightness,
	// durability)
	public static final int BYTES_PER_VERTEX = 13 * 4;

	/** the terrain */
	private final WorldObjectTerrain terrain;
	private boolean cull;

	public TerrainMesh(WorldObjectTerrain terrain) {
		super();
		this.terrain = terrain;
		super.getPosition().set(this.terrain.getWorldPosition());
		super.updateTransformationMatrix();
		this.cull = false;
	}

	/** return true if this terrain can be gl-culled (GL_BACK_FACE_CULLING) */
	public final boolean cull() {
		return (this.cull);
	}

	/** enable of disable culling for this terrain */
	public final void cull(boolean cull) { // TODO : enable culling on meshes
											// that are full of opaque cubes
		this.cull = cull;
	}

	@Override
	protected void preDraw() {
		if (this.cull()) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
		} else {
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
	}

	@Override
	protected void postDraw() {
		if (this.cull()) {
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
	}

	public WorldObjectTerrain getTerrain() {
		return (this.terrain);
	}

	@Override
	protected void setAttributes(GLVertexArray vao, GLVertexBuffer vbo) {
		vao.setAttribute(0, 3, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, 0); // xyz
		vao.setAttribute(1, 3, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, 3 * 4); // normal
		vao.setAttribute(2, 4, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, (3 + 3) * 4); // tx
		vao.setAttributei(3, 1, GL11.GL_INT, BYTES_PER_VERTEX, (3 + 3 + 4) * 4); // color
		vao.setAttribute(4, 1, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, (3 + 3 + 4 + 1) * 4); // brightness
		vao.setAttributei(5, 1, GL11.GL_INT, BYTES_PER_VERTEX, (3 + 3 + 4 + 1 + 1) * 4); // durability

		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
		vao.enableAttribute(5);
	}

	public void setVertices(ByteBuffer buffer) {
		super.setVertices(buffer, BYTES_PER_VERTEX);
	}
}