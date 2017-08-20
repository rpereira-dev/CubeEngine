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
	protected int _color;
	protected int _maxhealth;
	protected int _health;
	protected int _mvp_matrix;

	private int _cols;
	private int _lines;

	private int _position;
	private int _scale;
	private int _camera_pos;

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
		this._cols = super.getUniform("cols");
		this._lines = super.getUniform("lines");

		this._maxhealth = super.getUniform("maxhealth");
		this._health = super.getUniform("health");
		this._color = super.getUniform("color");

		this._position = super.getUniform("position");
		this._scale = super.getUniform("scale");

		this._mvp_matrix = super.getUniform("mvp_matrix");
		this._camera_pos = super.getUniform("camera_pos");
	}

	/** load particle instance uniforms */
	public void loadInstanceUniforms(ParticleBillboarded particle) {
		particle.getSprite().getTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		if (particle.isGlowing()) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		super.loadUniformInteger(this._lines, particle.getSprite().getLines());
		super.loadUniformInteger(this._cols, particle.getSprite().getCols());
		super.loadUniformInteger(this._maxhealth, particle.getMaxHealth());
		super.loadUniformInteger(this._health, particle.getHealth());

		super.loadUniformVec(this._color, particle.getColor());
		super.loadUniformVec(this._position, particle.getPosition());
		super.loadUniformVec(this._scale, particle.getScale());
	}

	public void loadGlobalUniforms(CameraProjective camera) {
		super.loadUniformVec(this._camera_pos, camera.getPosition());
		super.loadUniformMatrix(this._mvp_matrix, camera.getMVPMatrix());
	}
}
