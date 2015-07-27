package com.grillecube.client.renderer.program;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.WorldClient;

public class ProgramTerrain extends Program
{
	/** const */
	private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	/** Matrix buffer */
	private Matrix4f	_matrix_buffer;
	
	/** uniforms location */
	private int	_proj_matrix;
	private int	_view_matrix;
	private int	_transf_matrix;
	
	private int	_fog_color;	
	private int	_fog_density;
	private int	_fog_gradient;

	private int	_sun_color;
	private int	_sun_position;
	
	public ProgramTerrain()
	{
		super("terrain", "terrain");
		this._matrix_buffer = new Matrix4f();
	}

	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normal");
		super.bindAttribute(2, "uv");
		super.bindAttribute(3, "shade");
	}

	@Override
	public void linkUniforms()
	{
		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._fog_color = super.getUniform("fog_color");
		this._fog_gradient = super.getUniform("fog_gradient");
		this._fog_density = super.getUniform("fog_density");
		this._sun_color = super.getUniform("sun_color");
		this._sun_position = super.getUniform("sun_position");
	}

	public void	loadUniforms(WorldClient world, Camera camera)
	{
		this.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		this.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
		
		this.loadUniformVec(this._fog_color, world.getWeather().getFogColor());
		this.loadUniformFloat(this._fog_gradient, world.getWeather().getFogGradient());
		this.loadUniformFloat(this._fog_density, world.getWeather().getFogDensity());
		
		this.loadUniformVec(this._sun_color, world.getWeather().getSunColor());
		this.loadUniformVec(this._sun_position, world.getWeather().getSunPos());
	}
	
	public void loadInstanceUniforms(TerrainClient terrain)
	{
		this._matrix_buffer.setIdentity();
		this._matrix_buffer.translate(terrain.getWorldPosition());
		this._matrix_buffer.rotate(0, X_AXIS);
		this._matrix_buffer.rotate(0, Y_AXIS);
		this._matrix_buffer.rotate(0, Z_AXIS);
		this._matrix_buffer.scale(new Vector3f(1, 1, 1));

		this.loadUniformMatrix(this._transf_matrix, this._matrix_buffer);
	}
}
