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

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.terrain.Terrain;

public class TerrainMesh {
	// terrain generator
	public static final int STATE_INITIALIZED = 1;
	public static final int STATEvbo_UP_TO_DATE = 2;

	// (x, y, z, nx, ny, nz, atlasx, atlasy, uvx, uvy, color, brightness)
	public static final int FLOAT_PER_VERTEX = 12;

	/** terrain meshes */
	private int vertexCount;

	/** opengl */
	private GLVertexArray vao;
	private GLVertexBuffer vbo;

	private int state;
	private Terrain terrain;

	private Vector3f rotation;
	private Vector3f scale;

	private Matrix4f transfMatrix;
	private ByteBuffer vertices;

	public TerrainMesh(Terrain terrain) {
		this.terrain = terrain;
		this.transfMatrix = new Matrix4f();
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
		this.unsetState(TerrainMesh.STATE_INITIALIZED);
		this.setState(TerrainMesh.STATEvbo_UP_TO_DATE);
		this.updateTransformationMatrix();
	}

	/** initialize opengl stuff (vao, vbo) */
	private void initializeMesh() {
		this.vao = GLH.glhGenVAO();
		this.vbo = GLH.glhGenVBO();

		this.vao.bind();
		this.vbo.bind(GL15.GL_ARRAY_BUFFER);

		int step = 4 * TerrainMesh.FLOAT_PER_VERTEX;
		this.vao.setAttribute(0, 3, GL11.GL_FLOAT, false, step, 0); // x, y, z
		this.vao.setAttribute(1, 3, GL11.GL_FLOAT, false, step, 3 * 4); // normal
		this.vao.setAttribute(2, 4, GL11.GL_FLOAT, false, step, (3 + 3) * 4); // tx
		this.vao.setAttributei(3, 1, GL11.GL_INT, step, (3 + 3 + 4) * 4); // color
		this.vao.setAttribute(4, 1, GL11.GL_FLOAT, false, step, (3 + 3 + 4 + 1) * 4); // brightness

		this.vbo.unbind(GL15.GL_ARRAY_BUFFER);

		this.vao.enableAttribute(0);
		this.vao.enableAttribute(1);
		this.vao.enableAttribute(2);
		this.vao.enableAttribute(3);
		this.vao.enableAttribute(4);

		this.vao.unbind();

		this.setState(TerrainMesh.STATE_INITIALIZED);
	}

	public void destroy() {
		if (this.hasState(TerrainMesh.STATE_INITIALIZED)) {
			GLH.glhDeleteObject(this.vao);
			GLH.glhDeleteObject(this.vbo);
			this.unsetState(TerrainMesh.STATE_INITIALIZED);
		}
	}

	public Terrain getTerrain() {
		return (this.terrain);
	}

	public boolean hasState(int state) {
		return ((this.state & state) == state);
	}

	public void setState(int state) {
		this.state = this.state | state;
	}

	public void unsetState(int state) {
		this.state = this.state & ~(state);
	}

	public void switchState(int state) {
		this.state = this.state ^ state;
	}

	/** called in the rendering thread */
	public void render() {
		this.vao.bind();
		this.vao.draw(GL11.GL_TRIANGLES, 0, this.vertexCount);
	}

	public void preRender() {

		if (!this.hasState(TerrainMesh.STATEvbo_UP_TO_DATE) && this.vertices != null
				&& this.terrain.hasState(Terrain.STATE_VERTICES_UP_TO_DATE)) {

			if (!this.hasState(TerrainMesh.STATE_INITIALIZED)) {
				this.initializeMesh();
			}

			this.setState(TerrainMesh.STATEvbo_UP_TO_DATE);
			this.vbo.bind(GL15.GL_ARRAY_BUFFER);
			this.vbo.bufferData(GL15.GL_ARRAY_BUFFER, this.vertices, GL15.GL_STATIC_DRAW);
			this.vertexCount = this.vertices.capacity() / (TerrainMesh.FLOAT_PER_VERTEX * 4);
			this.vertices = null; // no longerneed it
		}
	}

	public int getVertexCount() {
		return (this.vertexCount);
	}

	/** returnt rue if the mesh has been regenerated */
	public boolean update(TerrainMesher mesher, Camera camera) {

		// if vertices need to be update, and lights are calculated
		if (!this.terrain.hasState(Terrain.STATE_VERTICES_UP_TO_DATE)) {

			// lock the update
			this.terrain.setState(Terrain.STATE_VERTICES_UP_TO_DATE);
			// lock the vbo update
			this.setState(TerrainMesh.STATEvbo_UP_TO_DATE);

			// generate vertices
			this.vertices = mesher.generateVertices(this.getTerrain());

			// unlock vbo update
			this.unsetState(TerrainMesh.STATEvbo_UP_TO_DATE);
			return (true);
		}
		return (false);
	}

	private void updateTransformationMatrix() {
		Matrix4f.createTransformationMatrix(this.transfMatrix, this.getPosition(), this.getRotation(), this.getScale());
	}

	public Vector3f getPosition() {
		return (this.getTerrain().getWorldPosition());
	}

	public Vector3f getRotation() {
		return (this.rotation);
	}

	public Vector3f getScale() {
		return (this.scale);
	}

	public void setRotation(float x, float y, float z) {
		this.rotation.set(x, y, z);
		this.updateTransformationMatrix();
	}

	public void setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
		this.updateTransformationMatrix();
	}

	public Matrix4f getTransformationMatrix() {
		return (this.transfMatrix);
	}

}