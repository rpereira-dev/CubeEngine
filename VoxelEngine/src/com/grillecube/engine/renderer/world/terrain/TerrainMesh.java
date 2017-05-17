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

package com.grillecube.engine.renderer.world.terrain;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLVertexArray;
import com.grillecube.engine.opengl.object.GLVertexBuffer;
import com.grillecube.engine.renderer.camera.Camera;
import com.grillecube.engine.world.Terrain;

public class TerrainMesh {
	// terrain generator
	public static final int STATE_INITIALIZED = 1;
	public static final int STATE_VBO_UP_TO_DATE = 2;

	// (x, y, z, nx, ny, nz, atlasx, atlasy, uvx, uvy, color, brightness)
	public static final int FLOAT_PER_VERTEX = 12;

	/** terrain meshes */
	private int _vertex_count;

	/** opengl */
	private GLVertexArray _vao;
	private GLVertexBuffer _vbo;

	private int _state;
	private Terrain terrain;

	private Vector3f _rotation;
	private Vector3f _scale;

	private Matrix4f _transf_matrix;
	private ByteBuffer vertices;

	public TerrainMesh(Terrain terrain) {
		this.terrain = terrain;
		this._transf_matrix = new Matrix4f();
		this._rotation = new Vector3f();
		this._scale = new Vector3f(1.0f, 1.0f, 1.0f);
		this.unsetState(TerrainMesh.STATE_INITIALIZED);
		this.setState(TerrainMesh.STATE_VBO_UP_TO_DATE);
		this.updateTransformationMatrix();
	}

	/** initialize opengl stuff (vao, vbo) */
	private void initializeMesh() {
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();

		this._vao.bind();
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);

		int step = 4 * TerrainMesh.FLOAT_PER_VERTEX;
		this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, step, 0); // x, y, z
		this._vao.setAttribute(1, 3, GL11.GL_FLOAT, false, step, 3 * 4); // normal
		this._vao.setAttribute(2, 4, GL11.GL_FLOAT, false, step, (3 + 3) * 4); // tx
		this._vao.setAttributei(3, 1, GL11.GL_INT, step, (3 + 3 + 4) * 4); // color
		this._vao.setAttribute(4, 1, GL11.GL_FLOAT, false, step, (3 + 3 + 4 + 1) * 4); // brightness

		this._vbo.unbind(GL15.GL_ARRAY_BUFFER);

		this._vao.enableAttribute(0);
		this._vao.enableAttribute(1);
		this._vao.enableAttribute(2);
		this._vao.enableAttribute(3);
		this._vao.enableAttribute(4);

		this._vao.unbind();

		this.setState(TerrainMesh.STATE_INITIALIZED);
	}

	public void destroy() {
		if (this.hasState(TerrainMesh.STATE_INITIALIZED)) {
			GLH.glhDeleteObject(this._vao);
			GLH.glhDeleteObject(this._vbo);
			this.unsetState(TerrainMesh.STATE_INITIALIZED);
		}
	}

	public Terrain getTerrain() {
		return (this.terrain);
	}

	public boolean hasState(int state) {
		return ((this._state & state) == state);
	}

	public void setState(int state) {
		this._state = this._state | state;
	}

	public void unsetState(int state) {
		this._state = this._state & ~(state);
	}

	public void switchState(int state) {
		this._state = this._state ^ state;
	}

	/** called in the rendering thread */
	public void render() {
		this._vao.bind();
		this._vao.draw(GL11.GL_TRIANGLES, 0, this._vertex_count);
	}

	public void preRender() {

		if (!this.hasState(TerrainMesh.STATE_VBO_UP_TO_DATE) && this.vertices != null
				&& this.terrain.hasState(Terrain.STATE_VERTICES_UP_TO_DATE)) {

			if (!this.hasState(TerrainMesh.STATE_INITIALIZED)) {
				this.initializeMesh();
			}

			this.setState(TerrainMesh.STATE_VBO_UP_TO_DATE);
			this._vbo.bind(GL15.GL_ARRAY_BUFFER);
			this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, this.vertices, GL15.GL_STATIC_DRAW);
			this._vertex_count = this.vertices.capacity() / TerrainMesh.FLOAT_PER_VERTEX;
			this.vertices = null; // no longerneed it
		}
	}

	public int getVertexCount() {
		return (this._vertex_count);
	}

	/** returnt rue if the mesh has been regenerated */
	public boolean update(TerrainMesher mesher, Camera camera) {

		// if vertices need to be update, and lights are calculated
		if (!this.terrain.hasState(Terrain.STATE_VERTICES_UP_TO_DATE)) {

			// lock the update
			this.terrain.setState(Terrain.STATE_VERTICES_UP_TO_DATE);
			// lock the vbo update
			this.setState(TerrainMesh.STATE_VBO_UP_TO_DATE);

			// generate vertices
			this.vertices = mesher.generateVertices(this.getTerrain());

			// unlock vbo update
			this.unsetState(TerrainMesh.STATE_VBO_UP_TO_DATE);
			return (true);
		}
		return (false);
	}

	private void updateTransformationMatrix() {
		Matrix4f.createTransformationMatrix(this._transf_matrix, this.getPosition(), this.getRotation(),
				this.getScale());
	}

	public Vector3f getPosition() {
		return (this.getTerrain().getWorldPosition());
	}

	public Vector3f getRotation() {
		return (this._rotation);
	}

	public Vector3f getScale() {
		return (this._scale);
	}

	public void setRotation(float x, float y, float z) {
		this._rotation.set(x, y, z);
		this.updateTransformationMatrix();
	}

	public void setScale(float x, float y, float z) {
		this._scale.set(x, y, z);
		this.updateTransformationMatrix();
	}

	public Matrix4f getTransformationMatrix() {
		return (this._transf_matrix);
	}

}