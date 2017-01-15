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

package com.grillecube.engine.renderer.model;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;

import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLVertexBuffer;

public class ModelPartSkin {

	private GLVertexBuffer _vbo;

	public ModelPartSkin() {
		this._vbo = null;
	}

	public void setVertices(float[] vertices) {
		if (this._vbo == null) {
			this._vbo = GLH.glhGenVBO();
		}

		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
	}

	public void setVertices(FloatBuffer buffer) {
		if (this._vbo == null) {
			this._vbo = GLH.glhGenVBO();
		}

		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	public GLVertexBuffer getVBO() {
		return (this._vbo);
	}

	public void delete() {
		GLH.glhDeleteObject(this._vbo);
		this._vbo = null;
	}
}
