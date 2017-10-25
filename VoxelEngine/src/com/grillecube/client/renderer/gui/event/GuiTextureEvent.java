package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.gui.components.GuiTexture;

public abstract class GuiTextureEvent<T extends GuiTexture> extends GuiEvent<T> {

	/** on event values */
	private GLTexture texture;
	private float ux;
	private float uy;
	private float vx;
	private float vy;

	public GuiTextureEvent(T gui) {
		super(gui);
		this.texture = gui.getGLTexture();
		this.ux = gui.getUx();
		this.uy = gui.getUy();
		this.vx = gui.getVx();
		this.vy = gui.getVy();
	}

	/** get the texture before this event was raised */
	public final GLTexture getGLTexture() {
		return (this.texture);
	}

	/** get the ux before this event was raised */
	public final float getUx() {
		return (this.ux);
	}

	/** get the uy before this event was raised */
	public final float getUy() {
		return (this.uy);
	}

	/** get the vx before this event was raised */
	public final float getVx() {
		return (this.vx);
	}

	/** get the vy before this event was raised */
	public final float getVy() {
		return (this.vy);
	}
}
