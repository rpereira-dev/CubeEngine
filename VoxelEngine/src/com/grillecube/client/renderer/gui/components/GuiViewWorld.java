package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.gui.GuiRenderer;

public class GuiViewWorld extends GuiView {

	private GuiTexture txWorld;

	public GuiViewWorld() {
		super();
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		this.txWorld = new GuiTexture();
		this.txWorld.setTexture(VoxelEngineClient.instance().getRenderer().getFBOTexture(), 0.0f, 0.0f, 1.0f, 1.0f);
		this.addChild(this.txWorld);
	}

	@Override
	public void onAddedTo(Gui gui) {

	}

	@Override
	public void onRemovedFrom(Gui gui) {

	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		float aspect = GLH.glhGetWindow().getAspectRatio() * super.getTotalAspectRatio();
		VoxelEngineClient.instance().getRenderer().getCamera().setAspect(aspect);
	}
}
