package com.grillecube.editor.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.geometry.Grid;
import com.grillecube.client.renderer.world.MVPObject;
import com.grillecube.client.renderer.world.ProgramMVP;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public class ModelGrid implements MVPObject {
	private GLVertexArray _vao;
	private int _vertex_count;

	private Matrix4f _transf_matrix;
	private Vector3f _pos;
	private Vector4f _color;
	private float _line_width;
	private Vector3f _rot;
	private Vector3f _scale;

	public ModelGrid(int width, int height) {
		this._vertex_count = (width + height + 2) * 2;

		this._vao = GLH.glhGenVAO();
		GLVertexBuffer vbo = GLH.glhGenVBO();
		vbo.bind(GL15.GL_ARRAY_BUFFER);
		vbo.bufferData(GL15.GL_ARRAY_BUFFER, Grid.make(width, height, 1), GL15.GL_STATIC_DRAW);
		this._vao.bind();
		this._vao.setAttribute(vbo, ProgramMVP.ATTR_POS, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
		this._vao.enableAttribute(ProgramMVP.ATTR_POS);
		this._vao.unbind();

		this._transf_matrix = new Matrix4f();
		this._pos = new Vector3f(0, 0.01f, 0);
		this._rot = new Vector3f();
		this._scale = new Vector3f(1, 1, 1);

		this.setLineWidth(1.0f);
		this.setColor(new Vector4f(0.8f, 0.8f, 0.8f, 1.0f));
	}

	public void setRotation(Vector3f rotation) {
		this._rot = rotation;
	}

	public void setScale(Vector3f scale) {
		this._scale.set(scale);
	}

	public Vector3f getPosition() {
		return (this._pos);
	}

	private void setColor(Vector4f color) {
		this._color = color;
	}

	public void setX(float x) {
		this._pos.setX(x);
	}

	public void setY(float y) {
		this._pos.setY(y);
	}

	public void setZ(float z) {
		this._pos.setZ(z);
	}

	public void setLineWidth(float width) {
		this._line_width = width;
	}

	private Vector3f offset = new Vector3f(0, 0.01f, 0);

	@Override
	public Matrix4f getTransformationMatrix() {
		this._transf_matrix.setIdentity();

		this._transf_matrix.translate(this._pos);
		this._transf_matrix.translate(offset);
		this._transf_matrix.rotate((float) Math.toRadians(this._rot.x), Vector3f.AXIS_X);
		this._transf_matrix.rotate((float) Math.toRadians(this._rot.y), Vector3f.AXIS_Y);
		this._transf_matrix.rotate((float) Math.toRadians(this._rot.z), Vector3f.AXIS_Z);
		this._transf_matrix.scale(this._scale);

		return (this._transf_matrix);
	}

	@Override
	public Vector4f getColor() {
		return (this._color);
	}

	@Override
	public void render() {
		GL11.glLineWidth(this._line_width);
		this._vao.bind();
		this._vao.draw(GL11.GL_LINES, 0, this._vertex_count);
		this._vao.unbind();
	}

	public Vector3f getRotation() {
		return (this._rot);
	}

	public Vector3f getScale() {
		return (this._scale);
	}

	public void set(Vector3f origin, Vector3f axis, Vector3f unit) {
		this._pos.set(origin);
		this._rot.set(axis);
		this.setBlockUnit(unit);
	}

	public void setBlockUnit(Vector3f unit) {
		this._scale.set(1 / unit.x, 1 / unit.y, 1 / unit.z);
	}
}
