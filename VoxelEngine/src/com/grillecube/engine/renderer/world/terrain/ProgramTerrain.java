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

import org.lwjgl.opengl.GL20;

import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLProgram;
import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.renderer.world.ShadowCamera;
import com.grillecube.engine.renderer.world.WorldRenderer;
import com.grillecube.engine.resources.R;
import com.grillecube.engine.world.World;

public class ProgramTerrain extends GLProgram {

	/** uniforms location */
	private int _view_matrix;
	private int _proj_matrix;
	private int _to_shadow_space_matrix;
	private int _transf_matrix;

	private int _shadow_map_size;

	private int _fog_color;
	private int _fog_density;
	private int _fog_gradient;

	private int _sun_color;
	private int _sun_position;
	private int _sun_intensity;

	private int _ambient_light;

	private int _tx_atlas;
	private int _tx_reflection;
	private int _tx_refraction;
	private int _tx_dudvmap;
	private int _tx_normalsmap;
	private int _tx_shadowmap;

	private int _camera_position;

	private int _move_factor;
	private float _move_factor_value;

	private int _shadow_distance;
	private int _shadow_pcf_count;

	public ProgramTerrain() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normal");
		super.bindAttribute(2, "uv");
		super.bindAttribute(3, "color");
		super.bindAttribute(4, "brightness");
	}

	@Override
	public void linkUniforms() {
		this._camera_position = super.getUniform("camera_position");

		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._to_shadow_space_matrix = super.getUniform("to_shadow_space_matrix");

		this._shadow_map_size = super.getUniform("shadow_map_size");
		this._shadow_distance = super.getUniform("shadow_distance");
		this._shadow_pcf_count = super.getUniform("shadow_pcf_count");

		this._fog_color = super.getUniform("fog_color");
		this._fog_gradient = super.getUniform("fog_gradient");
		this._fog_density = super.getUniform("fog_density");
		this._sun_color = super.getUniform("sun_color");
		this._sun_position = super.getUniform("sun_position");
		this._sun_intensity = super.getUniform("sun_intensity");
		this._ambient_light = super.getUniform("ambient_light");

		this._move_factor = super.getUniform("move_factor");

		this._tx_atlas = super.getUniform("tx_atlas");
		this._tx_reflection = super.getUniform("tx_reflection");
		this._tx_refraction = super.getUniform("tx_refraction");
		this._tx_dudvmap = super.getUniform("tx_dudvmap");
		this._tx_normalsmap = super.getUniform("tx_normalsmap");
		this._tx_shadowmap = super.getUniform("tx_shadowmap");
	}

	/** load global terrain uniform */
	private Matrix4f shadow_matrix_buffer = new Matrix4f();
	private Vector3f sunpos = new Vector3f();

	public void loadUniforms(CameraProjectiveWorld camera, World world, ShadowCamera shadow_camera) {

		this.loadUniformFloat(this._shadow_map_size, WorldRenderer.SHADOW_FBO_WIDTH);
		this.loadUniformFloat(this._shadow_distance, shadow_camera.getRenderDistance());
		this.loadUniformInteger(this._shadow_pcf_count, shadow_camera.getShadowPcfCount());

		this.loadUniformVec(this._camera_position, camera.getPosition());
		this.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		this.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
		this.loadUniformMatrix(this._to_shadow_space_matrix,
				shadow_camera.getToShadowSpaceMatrix(this.shadow_matrix_buffer));

		this.loadUniformVec(this._fog_color, world.getWeather().getFogColor());
		this.loadUniformFloat(this._fog_gradient, world.getWeather().getFogGradient());
		this.loadUniformFloat(this._fog_density, world.getWeather().getFogDensity());

		this.loadUniformVec(this._sun_color, world.getWeather().getSun().getColor());
		this.loadUniformFloat(this._sun_intensity, world.getWeather().getSun().getIntensity());
		this.loadUniformVec(this._sun_position, sunpos.set(world.getWeather().getSun().getPosition()));
		this.loadUniformFloat(this._ambient_light, world.getWeather().getAmbientLight());

		this.loadUniformInteger(this._tx_atlas, 0);
		this.loadUniformInteger(this._tx_reflection, 1);
		this.loadUniformInteger(this._tx_refraction, 2);
		this.loadUniformInteger(this._tx_dudvmap, 3);
		this.loadUniformInteger(this._tx_normalsmap, 4);
		this.loadUniformInteger(this._tx_shadowmap, 5);

		this.loadUniformFloat(this._move_factor, this._move_factor_value);
		this._move_factor_value += 0.0002f;
		if (this._move_factor_value >= 1.0f) {
			this._move_factor_value = 0.0f;
		}
	}

	/** load terrain instance uniforms variable */
	public void loadInstanceUniforms(TerrainMesh mesh) {
		this.loadUniformMatrix(this._transf_matrix, mesh.getTransformationMatrix());
	}
}
