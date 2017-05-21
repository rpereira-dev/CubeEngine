package com.grillecube.client.opengl.object;

import org.lwjgl.opengl.GL30;

import com.grillecube.client.opengl.GLH;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;

public class GLRenderBuffer implements GLObject {
	private int _id;

	public GLRenderBuffer() {
		this(GL30.glGenRenderbuffers());
	}

	public GLRenderBuffer(int rbo) {
		this._id = rbo;
	}

	public void bind() {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.getID());
	}

	public void unbind() {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
	}

	public void storage(int internalformat, int width, int height) {
		if (GLH.glhGetBoundRBO() != this.getID()) {
			Logger.get().log(Level.ERROR, "Tried to GLRenderBuffer.storage() on an unbound RBO");
			return;
		}
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, internalformat, width, height);
	}

	/** attach this render buffer to the given attachment */
	public void attachToFBO(int target, int attachment) {
		if (GLH.glhGetBoundRBO() != this.getID()) {
			Logger.get().log(Level.ERROR, "Tried to GLRenderBuffer.attachTo() on an unbound RBO");
			return;
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, this.getID());
	}

	@Override
	public void delete() {
		GL30.glDeleteRenderbuffers(this.getID());
	}

	public int getID() {
		return (this._id);
	}
}
