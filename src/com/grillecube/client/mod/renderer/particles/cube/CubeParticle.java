package com.grillecube.client.mod.renderer.particles.cube;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class CubeParticle
{
	private Matrix4f _transf_matrix;
	
	private Vector3f _pos;
	private Vector3f _rot;
	private Vector3f _scale;
	
	private Vector3f _pos_vel;
	private Vector3f _rot_vel;
	private Vector3f _scale_vel;
	
	public CubeParticle()
	{
		this._transf_matrix = new Matrix4f();
		this._pos = new Vector3f();
		this._rot = new Vector3f();
		this._scale = new Vector3f();
		
		this._pos_vel = new Vector3f();
		this._rot_vel = new Vector3f();
		this._scale_vel = new Vector3f();
	}
	
	public void update()
	{
		this._pos.add(this._pos_vel);
		this._rot.add(this._rot_vel);
		this._scale.add(this._scale_vel);
		
		Matrix4f.createTransformationMatrix(this._transf_matrix, this._pos, this._rot, this._scale);
	}
	
	public Matrix4f getTransfMatrix()
	{
		return (this._transf_matrix);
	}

}
