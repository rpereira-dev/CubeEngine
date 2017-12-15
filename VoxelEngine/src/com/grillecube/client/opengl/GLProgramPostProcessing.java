package com.grillecube.client.opengl;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.grillecube.common.resources.R;

public final class GLProgramPostProcessing extends GLProgram {

	private static GLShader vertex_shader;
	private static GLShader geometry_shader;
	private int _time;

	public GLProgramPostProcessing(String filepath) {
		super();

		if (vertex_shader == null) {
			vertex_shader = GLH.glhLoadShader(R.getResPath("shaders/post_process/post_processing.vs"),
					GL20.GL_VERTEX_SHADER);
			geometry_shader = GLH.glhLoadShader(R.getResPath("shaders/post_process/post_processing.gs"),
					GL32.GL_GEOMETRY_SHADER);
		}

		this.addShader(GLH.glhLoadShader(filepath, GL20.GL_FRAGMENT_SHADER));
		this.addShader(geometry_shader);
		this.addShader(vertex_shader);
		this.link();
	}

	@Override
	public void bindAttributes() {
	}

	@Override
	public void linkUniforms() {
		this._time = super.getUniform("time");
	}

	public void loadUniforms(float time) {
		this.loadUniformFloat(this._time, (float) time);
	}

}
