package com.grillecube.editor.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.geometry.Sphere;
import com.grillecube.client.renderer.world.MVPObject;
import com.grillecube.client.renderer.world.ProgramMVP;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public class ModelPoint implements MVPObject {
	private GLVertexArray _vao;

	private Matrix4f _transf_matrix;
	private Vector3f _pos;
	private Vector4f _color;

	private Vector3f _scale = new Vector3f(1, 1, 1);

	private static final int DETAILS = 3;

	public ModelPoint() {
		this._vao = GLH.glhGenVAO();
		GLVertexBuffer vbo = GLH.glhGenVBO();
		vbo.bind(GL15.GL_ARRAY_BUFFER);
		vbo.bufferData(GL15.GL_ARRAY_BUFFER, Sphere.make(DETAILS, 0.5f), GL15.GL_STATIC_DRAW);
		this._vao.bind();
		this._vao.setAttribute(vbo, ProgramMVP.ATTR_POS, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
		this._vao.enableAttribute(ProgramMVP.ATTR_POS);
		this._vao.unbind();

		this._transf_matrix = new Matrix4f();
		this._pos = new Vector3f();
		this._color = new Vector4f();
		this.reset();
	}

	public void reset() {
		this.set(0.5f, 0.5f, 0.5f);
	}

	public Vector3f getPosition() {
		return (this._pos);
	}

	public void setColor(Vector4f color) {
		this._color.set(color);
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

	public void set(Vector3f vec) {
		this.setX(vec.x);
		this.setY(vec.y);
		this.setZ(vec.z);
	}

	@Override
	public Matrix4f getTransformationMatrix() {
		this._transf_matrix.setIdentity();
		this._transf_matrix.translate(this._pos);
		this._transf_matrix.scale(this._scale);
		return (this._transf_matrix);
	}

	public void setScale(Vector3f scale) {
		this._scale.set(scale);
	}

	@Override
	public Vector4f getColor() {
		return (this._color);
	}

	@Override
	public void render() {
		this._vao.bind();
		this._vao.draw(GL11.GL_TRIANGLES, 0, Sphere.getVertexCount(DETAILS));
		this._vao.unbind();
	}

	public void set(float x, float y, float z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
}
