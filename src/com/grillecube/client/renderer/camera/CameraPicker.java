package com.grillecube.client.renderer.camera;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class CameraPicker
{
	private Camera	_camera;
	
	private ByteBuffer	_mouseX;
	private ByteBuffer	_mouseY;
	
	private Vector3f	_ray;	//world space ray
	
	public CameraPicker(Camera camera)
	{
		this._camera = camera;
		this._mouseX = BufferUtils.createByteBuffer(8);
		this._mouseY = BufferUtils.createByteBuffer(8);
		this._ray = new Vector3f();
	}
	
	public void	update()
	{
		this._mouseX.clear();
		this._mouseY.clear();
		GLFW.glfwGetCursorPos(this._camera.getWindow().getPointer(), this._mouseX, this._mouseY);
		this.calculateWorldSpaceRay();
	}
	
	private void calculateWorldSpaceRay()
	{
		float		mouseX;
		float		mouseY;
		Vector4f	clipcoords;
		Matrix4f	invertedprojection;
		Vector4f	eyecoords;
		Matrix4f	invertedview;
		Vector4f	rayworld;
		
//		mouseX  = (float) ((2 * this.getMouseX()) / this._camera.getWindow().getWidth() - 1);
//		mouseY = (float) (2 - (2 * this.getMouseY()) / this._camera.getWindow().getHeight() - 1);
		mouseX = this._camera.getWindow().getWidth() / 2;
		mouseY = this._camera.getWindow().getHeight() / 2;
		clipcoords = new Vector4f(mouseX, mouseY, -1.0f, 1.0f);
		invertedprojection = Matrix4f.invert(this._camera.getProjectionMatrix(), null);
		eyecoords = Matrix4f.transform(invertedprojection, clipcoords, null);
		eyecoords.setZ(-1);
		eyecoords.setW(0);
		invertedview = Matrix4f.invert(this._camera.getViewMatrix(), null);
		rayworld  = Matrix4f.transform(invertedview, eyecoords, null);
		this._ray.setX(rayworld.x);
		this._ray.setY(rayworld.y);
		this._ray.setZ(rayworld.z);
		this._ray.normalise();
	}
	
	public double getMouseX()
	{
		return (this._mouseX.getDouble());
	}
	
	public double getMouseY()
	{
		return (this._mouseY.getDouble());
	}
	
	public Vector3f	getRay()
	{
		return (this._ray);
	}
}
