package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.world.WorldRenderer;

public class GuiViewWorld extends GuiView {

	private GuiTexture txWorld;
	private WorldRenderer worldRenderer;
	private int worldID;
	private CameraProjective camera;

	public GuiViewWorld(CameraProjective camera, int worldID) {
		super();
		this.camera = camera;
		this.worldID = worldID;
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {

		MainRenderer mainRenderer = renderer.getMainRenderer();

		this.camera.getPicker().setGuiRelative(this);

		this.worldRenderer = new WorldRenderer(mainRenderer);
		this.worldRenderer.initialize();
		this.worldRenderer.setCamera(this.camera);
		this.worldRenderer.setWorld(this.worldID);
		this.worldRenderer.matchGui(this);
		mainRenderer.addRenderer(this.worldRenderer);

		this.txWorld = new GuiTexture();
		this.txWorld.setTexture(this.worldRenderer.getFBOTexture(), 0.0f, 0.0f, 1.0f, 1.0f);
		this.addChild(this.txWorld);
	}

	@Override
	protected void onUpdate() {
		if (this.worldRenderer == null) {
			return;
		}
		this.worldRenderer.getCamera().update();

		// TODO update this by setting world renderer viewport
		float aspect = GLH.glhGetWindow().getAspectRatio() * super.getTotalAspectRatio();
		this.worldRenderer.getCamera().setAspect(aspect);
	}
}
