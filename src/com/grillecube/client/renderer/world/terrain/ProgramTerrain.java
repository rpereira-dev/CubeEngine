package com.grillecube.client.renderer.world.terrain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.opengl.object.Program;
import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.client.world.Terrain;
import com.grillecube.client.world.Weather;

public class ProgramTerrain extends Program
{
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
	
	private int	_use_ao;
	
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
		super.bindAttribute(3, "ao");
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
		this._use_ao = super.getUniform("use_ao");
	}

	/** load global terrain uniform */
	public void	loadUniforms(Weather weather, Camera camera)
	{
		this.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		this.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
		
		this.loadUniformVec(this._fog_color, weather.getFogColor());
		this.loadUniformFloat(this._fog_gradient, weather.getFogGradient());
		this.loadUniformFloat(this._fog_density, weather.getFogDensity());
		
		Vector3f sun_color = new Vector3f(weather.getSunColor());
		sun_color.x *= weather.getSunIntensity();
		sun_color.y *= weather.getSunIntensity();
		sun_color.z *= weather.getSunIntensity();
		this.loadUniformVec(this._sun_color, sun_color);
		
		Vector3f sun_pos = new Vector3f(weather.getSunPos());
		sun_pos.scale(Weather.SUN_DISTANCE);
		sun_pos.x += camera.getPosition().x;
		sun_pos.y += camera.getPosition().y;
		sun_pos.z += camera.getPosition().z;
		this.loadUniformVec(this._sun_position, sun_pos);
		
		this.loadUniformInteger(this._use_ao, GLFW.glfwGetKey(camera.getWindow().getPointer(), GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS? 0 : 1);
	}

	/** load terrain instance uniforms variable */
	public void loadInstanceUniforms(Terrain terrain)
	{
		Matrix4f.createTransformationMatrix(this._matrix_buffer, terrain.getWorldLocation(), null, null);
		this.loadUniformMatrix(this._transf_matrix, this._matrix_buffer);
	}
}
