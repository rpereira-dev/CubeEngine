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

package com.grillecube.client.renderer.world.terrain;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.common.world.terrain.Terrain;

public class TerrainMesh extends Mesh {

	// (x, y, z, nx, ny, nz, atlasx, atlasy, uvx, uvy, color, brightness)
	public static final int BYTES_PER_VERTEX = 12 * 4;

	/** the terrain */
	private final Terrain terrain;

	public TerrainMesh(Terrain terrain) {
		super();
		this.terrain = terrain;
	}

	@Override
	public void initialize() {
		super.initialize();
		super.getPosition().set(this.terrain.getWorldPos());
		super.updateTransformationMatrix();
	}

	public Terrain getTerrain() {
		return (this.terrain);
	}

	@Override
	public boolean update(TerrainMesher mesher, Camera camera) {
		super.update(mesher, camera);

		// if vertices need to be update, and lights are calculated
		if (!this.terrain.hasState(Terrain.STATE_VERTICES_UP_TO_DATE)) {

			// lock the update
			this.terrain.setState(Terrain.STATE_VERTICES_UP_TO_DATE);

			// set vertices

			VoxelEngineClient.instance().getRenderer().addGLTask(new GLTask() {

				@Override
				public void run() {
					setVertices(mesher.generateVertices(getTerrain()), BYTES_PER_VERTEX);
				}
			});
			return (true);
		}
		return (false);
	}

	@Override
	protected void setAttributes(GLVertexArray vao, GLVertexBuffer vbo) {
		vao.setAttribute(0, 3, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, 0); // xyz
		vao.setAttribute(1, 3, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, 3 * 4); // normal
		vao.setAttribute(2, 4, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, (3 + 3) * 4); // tx
		vao.setAttributei(3, 1, GL11.GL_INT, BYTES_PER_VERTEX, (3 + 3 + 4) * 4); // color
		vao.setAttribute(4, 1, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, (3 + 3 + 4 + 1) * 4); // brightness

		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
	}
}