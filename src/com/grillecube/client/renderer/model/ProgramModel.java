package com.grillecube.client.renderer.model;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.Program;
import com.grillecube.client.world.entity.EntityModeled;
import com.grillecube.common.world.entity.Entity;

public class ProgramModel extends Program
{
	private int	_projection_matrix;
	private int	_view_matrix;
	private int	_transformation_matrix;

	public ProgramModel()
	{
		super("model", "model");
	}
	
	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "color");
	}

	@Override
	public void linkUniforms()
	{
		this._projection_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
		this._transformation_matrix = super.getUniform("transf_matrix");
	}

	public void loadUniforms(Camera camera)
	{
		this.loadUniformMatrix(this._projection_matrix, camera.getProjectionMatrix());
		this.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
	}

	public void loadAnimationUniforms(EntityModeled entity, AnimationInstance animation)
	{
		Matrix4f	matrix;
		Vector3f	translation;
		Vector3f	offset;
		Vector3f	rotation;
		Vector3f	scaling;
		
		//TODO : add entity position
		
		matrix = new Matrix4f();
				
		if (animation.isPlaying())
		{
			translation = animation.getTranslation();
			offset = animation.getOffset();
			rotation = animation.getRotation();
			scaling = animation.getScaling();			
		}
		else
		{
			translation = new Vector3f(0, 0, 0);
			offset = new Vector3f(0, 0, 0);
			rotation = new Vector3f(0, 0, 0);
			scaling = new Vector3f(1, 1, 1);
		}

		matrix.translate(entity.getPosition());

		matrix.translate(translation);
		offset = new Vector3f(offset.x + 0.5f, offset.y + 0.5f, offset.z + 0.5f);
		matrix.translate(offset);	//set rotation point
		matrix.rotate(rotation.x, new Vector3f(1, 0, 0));
		matrix.rotate(rotation.y, new Vector3f(0, 1, 0));
		matrix.rotate(rotation.z, new Vector3f(0, 0, 1));
		matrix.translate(offset.negate(offset));	//unset rotation point
		
		scaling.x /= 16.0f;
		scaling.y /= 16.0f;
		scaling.z /= 16.0f;
		matrix.scale(scaling);
		
		this.loadUniformMatrix(this._transformation_matrix, matrix);
	}
}
