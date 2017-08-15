package com.grillecube.client.opengl.object;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.grillecube.client.opengl.GLH;

public class GLFrameBuffer implements GLObject {
	private int _id;

	public GLFrameBuffer() {
		this(GL30.glGenFramebuffers());
	}

	public GLFrameBuffer(int fbo) {
		this._id = fbo;
	}

	@Override
	public void delete() {
		GL30.glDeleteFramebuffers(this.getID());
	}

	public int getID() {
		return (this._id);
	}

	/** bind this fbo */
	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.getID());
	}

	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void texture(int target, int attachment, GLTexture texture) {
		GL32.glFramebufferTexture(target, attachment, texture.getID(), 0);
	}

	public void clear(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	/** create a new draw buffer for the given attachment for this fbo */
	public void createDrawBuffer(int attachment) {
		GL11.glDrawBuffer(attachment);
	}

	/**
	 * create a texture for the attachment, with the given format for this fbo
	 */
	public void createTextureAttachment(GLTexture texture, int attachment) {
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, attachment, texture.getID(), 0);
	}

	public void viewport(int x, int y, int width, int height) {
		GL11.glViewport(x, y, width, height);
	}
}
