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

package com.grillecube.client.renderer.world;

import org.lwjgl.opengl.GL20;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.GLProgram;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.WorldFlat;

public class ProgramTerrain extends GLProgram {

	public static final int MESH_TYPE_OPAQUE = 0;
	public static final int MESH_TYPE_TRANSPARENT = 1;

	/** uniforms location */
	private int blockAtlas;
	private int breakAtlas;

	private int viewMatrix;
	private int projMatrix;
	private int transfMatrix;
	private int meshType;

	private int fogColor;
	private int fogDensity;
	private int fogGradient;

	private int sunColor;
	private int sunPosition;
	private int sunIntensity;

	private int ambientLight;

	private int txAtlas;

	private int cameraPosition;

	public ProgramTerrain() {
		super();
		String header = "# define MESH_TYPE_OPAQUE (" + MESH_TYPE_OPAQUE + ")\n" + "# define MESH_TYPE_TRANSPARENT ("
				+ MESH_TYPE_TRANSPARENT + ")\n";
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain.fs"), GL20.GL_FRAGMENT_SHADER, header));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain.vs"), GL20.GL_VERTEX_SHADER, header));
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
		this.blockAtlas = super.getUniform("blockAtlas");
		this.breakAtlas = super.getUniform("breakAtlas");

		this.cameraPosition = super.getUniform("camera_position");

		this.projMatrix = super.getUniform("proj_matrix");
		this.viewMatrix = super.getUniform("view_matrix");
		this.transfMatrix = super.getUniform("transf_matrix");
		this.meshType = super.getUniform("mesh_type");
		this.fogColor = super.getUniform("fog_color");
		this.fogGradient = super.getUniform("fog_gradient");
		this.fogDensity = super.getUniform("fog_density");
		this.sunColor = super.getUniform("sun_color");
		this.sunPosition = super.getUniform("sun_position");
		this.sunIntensity = super.getUniform("sun_intensity");
		this.ambientLight = super.getUniform("ambient_light");

		this.txAtlas = super.getUniform("tx_atlas");
	}

	/** load global terrain uniform */
	private Vector3f sunpos = new Vector3f();

	public void loadUniforms(CameraProjective camera, WorldFlat world) {

		this.loadUniformInteger(this.blockAtlas, 0);
		this.loadUniformInteger(this.breakAtlas, 1);

		this.loadUniformVec(this.cameraPosition, camera.getPosition());
		this.loadUniformMatrix(this.projMatrix, camera.getProjectionMatrix());
		this.loadUniformMatrix(this.viewMatrix, camera.getViewMatrix());

		this.loadUniformVec(this.fogColor, world.getSky().getFogColor());
		this.loadUniformFloat(this.fogGradient, world.getSky().getFogGradient());
		this.loadUniformFloat(this.fogDensity, world.getSky().getFogDensity());

		this.loadUniformVec(this.sunColor, world.getSky().getSun().getColor());
		this.loadUniformFloat(this.sunIntensity, world.getSky().getSun().getIntensity());
		this.loadUniformVec(this.sunPosition, sunpos.set(world.getSky().getSun().getPosition()));
		this.loadUniformFloat(this.ambientLight, world.getSky().getAmbientLight());

		this.loadUniformInteger(this.txAtlas, 0);
	}

	/** load terrain instance uniforms variable */
	public void loadInstanceUniforms(TerrainMesh mesh) {
		this.loadUniformMatrix(this.transfMatrix, mesh.getTransformationMatrix());
	}

	public void loadTypeUniform(int meshType) {
		this.loadUniformInteger(this.meshType, meshType);
	}
}
