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

package com.grillecube.client.renderer.world.terrain;

import org.lwjgl.opengl.GL20;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLProgram;
import com.grillecube.client.renderer.world.ShadowCamera;
import com.grillecube.common.resources.R;

public class ProgramTerrainShadow extends GLProgram {

	/** uniforms location */
	private int _mvp_matrix;
	private int _transf_matrix;
	private int _tx_atlas;

	public ProgramTerrainShadow() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain_shadow.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain_shadow.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	public void linkUniforms() {
		this._mvp_matrix = super.getUniform("mvp_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._tx_atlas = super.getUniform("tx_atlas");
	}

	/** load global terrain uniform */
	public void loadUniforms(ShadowCamera shadow_camera) {
		this.loadUniformMatrix(this._mvp_matrix, shadow_camera.getMVPMatrix());
		this.loadUniformInteger(this._tx_atlas, 0);
	}

	/** load terrain instance uniforms variable */
	public void loadInstanceUniforms(TerrainMesh mesh) {
		this.loadUniformMatrix(this._transf_matrix, mesh.getTransformationMatrix());
	}
}
