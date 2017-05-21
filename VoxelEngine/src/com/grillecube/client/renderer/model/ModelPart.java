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

package com.grillecube.client.renderer.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.terrain.Terrain;

public class ModelPart {
	public static final int FLOAT_PER_MODEL_VERTEX = 6;
	public static final int FLOAT_PER_SKIN_VERTEX = 4;

	/** model part name */
	private String _name;

	/** opengl */
	private GLVertexArray _vao;
	private GLVertexBuffer _vbo;
	private int _primitive_count;
	private int _vertex_count;

	/** animations */
	private BoundingBox _box;

	/** skin used for rendering */
	private ModelPartSkin _current_skin_ptr;

	/** position and rotation for the item attachment */
	private ArrayList<ModelPartAttachmentPoint> _attachment_points;

	/** the block unit size */
	public static final float DEFAULT_BLOCK_SCALE = 8.0f * Terrain.BLOCK_SIZE;
	public static final float DEFAULT_SCALE = 1.0f / DEFAULT_BLOCK_SCALE;
	private Vector3f _block_scale;

	public ModelPart() {
		this("unknown");
	}

	public ModelPart(String name) {
		this._name = name;
		this._primitive_count = 0;
		this._vertex_count = 0;
		this._box = new BoundingBox();
		this._attachment_points = new ArrayList<ModelPartAttachmentPoint>();
		this._block_scale = new Vector3f(DEFAULT_SCALE, DEFAULT_SCALE, DEFAULT_SCALE);
		this.prepare();
	}

	/** return the block unit size */
	public Vector3f getBlockScale() {
		return (this._block_scale);
	}

	public void setBlockScale(float x, float y, float z) {
		this._block_scale.set(x, y, z);
	}

	/** initalize opengl vao / vbo */
	private void prepare() {
		if (this._vao != null) {
			Logger.get().log(Level.ERROR, "Tried to prepare an already prepared model part: " + this.toString());
			return;
		}

		this._primitive_count = 0;
		this._vertex_count = 0;
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();
		this._vao.bind();
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, FLOAT_PER_MODEL_VERTEX * 4, 0); // pos
		this._vao.setAttribute(1, 3, GL11.GL_FLOAT, false, FLOAT_PER_MODEL_VERTEX * 4, 3 * 4); // normal
		this._vao.enableAttribute(0);
		this._vao.enableAttribute(1);
		this._vbo.unbind(GL15.GL_ARRAY_BUFFER);
		this._vao.unbind();
	}

	/** set modelaprt vertices, should only be called once ! */
	public void setVertices(float[] vertices) {
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		this._vertex_count = vertices.length / ModelPart.FLOAT_PER_MODEL_VERTEX;
		this._primitive_count = this._vertex_count / 3;
	}

	/** set modelaprt vertices, should only be called once ! */
	public void setVertices(ByteBuffer buffer) {
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		this._vertex_count = buffer.capacity() / ModelPart.FLOAT_PER_MODEL_VERTEX;
		this._primitive_count = this._vertex_count / 3;
	}

	public float[] getPositionVertices() {
		return (this._vbo.getContent(0));
	}

	/** unable a skin for rendering: the part has to be bound */
	public void toggleSkin(ModelPartSkin skin) {

		if (skin == null || skin.getVBO() == null || this._current_skin_ptr == skin) {
			return;
		}

		this._vao.setAttribute(skin.getVBO(), 2, FLOAT_PER_SKIN_VERTEX, GL11.GL_FLOAT, false, FLOAT_PER_SKIN_VERTEX * 4,
				0);
		this._vao.enableAttribute(2);
		this._current_skin_ptr = skin;
	}

	public void bind() {
		this._vao.bind();
	}

	/** render the model part */
	public void render() {
		this._vao.draw(GL11.GL_TRIANGLES, 0, this._vertex_count);
	}

	/** get model part name */
	public String getName() {
		return (this._name);
	}

	/** return the number of vertex for this model part */
	public int getVertexCount() {
		return (this._vertex_count);
	}

	public int getPrimitiveCount() {
		return (this._primitive_count);
	}

	/** get bouding boxes for this modelpart */
	public BoundingBox getBoundingBox() {
		return (this._box);
	}

	/** delete the model */
	public void delete() {
		GLH.glhDeleteObject(this._vao);
		GLH.glhDeleteObject(this._vbo);
	}

	public GLVertexBuffer getVBO() {
		return (this._vbo);
	}

	public void addAttachmentPoint(ModelPartAttachmentPoint point) {
		this._attachment_points.add(point);
	}

	public void addAttachmentPoint() {
		this.addAttachmentPoint(new ModelPartAttachmentPoint("AttachmentPoint" + this._attachment_points.size()));
	}

	public ModelPartAttachmentPoint getAttachmentPoint(int pointID) {
		if (pointID < 0 || pointID >= this._attachment_points.size()) {
			return (null);
		}
		return (this._attachment_points.get(pointID));
	}

	public ArrayList<ModelPartAttachmentPoint> getAttachmentPoints() {
		return (this._attachment_points);
	}

	public static float blockScaleToUnit(float blockscale) {
		return ((float) (Math.log(1 / blockscale) / Math.log(2)));
	}

	public static float unitToBlockScale(float unit) {
		return (1 / (float) Math.pow(2, unit));
	}
}
