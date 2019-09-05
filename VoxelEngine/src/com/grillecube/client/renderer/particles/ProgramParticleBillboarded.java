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

package com.grillecube.client.renderer.particles;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLProgram;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.resources.R;

public class ProgramParticleBillboarded extends GLProgram {
	protected int color;
	protected int maxhealth;
	protected int health;
	protected int mvp_matrix;

	private int cols;
	private int lines;

	private int position;
	private int scale;
	private int camerapos;

	public ProgramParticleBillboarded() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/billboards.gs"), GL32.GL_GEOMETRY_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/billboards.vs"), GL20.GL_VERTEX_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/billboards.fs"), GL20.GL_FRAGMENT_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
	}

	@Override
	public void linkUniforms() {
		this.cols = super.getUniform("cols");
		this.lines = super.getUniform("lines");

		this.maxhealth = super.getUniform("maxhealth");
		this.health = super.getUniform("health");
		this.color = super.getUniform("color");

		this.position = super.getUniform("position");
		this.scale = super.getUniform("scale");

		this.mvp_matrix = super.getUniform("mvp_matrix");
		this.camerapos = super.getUniform("camera_pos");
	}

	/** load particle instance uniforms */
	public void loadInstanceUniforms(ParticleBillboarded particle) {
		particle.getSprite().getTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		if (particle.isGlowing()) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		super.loadUniformInteger(this.lines, particle.getSprite().getLines());
		super.loadUniformInteger(this.cols, particle.getSprite().getCols());
		super.loadUniformInteger(this.maxhealth, particle.getMaxHealth());
		super.loadUniformInteger(this.health, particle.getHealth());

		super.loadUniformVec(this.color, particle.getColor());
		super.loadUniformVec(this.position, particle.getPosition());
		super.loadUniformVec(this.scale, particle.getSize());
	}

	public void loadGlobalUniforms(CameraProjective camera) {
		super.loadUniformVec(this.camerapos, camera.getPosition());
		super.loadUniformMatrix(this.mvp_matrix, camera.getMVPMatrix());
	}
}
