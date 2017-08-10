package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.gui.GuiRenderer;

public class GuiViewIngame extends GuiView {

	private GuiTexture txWorld;

	public GuiViewIngame() {
		super();
	}

	@Override
	public void onAddedTo(GuiRenderer renderer) {
		this.txWorld = new GuiTexture();
		this.txWorld.setTexture(VoxelEngineClient.instance().getRenderer().getFBOTexture(), -1, -1, 2, 2);
		this.addChild(this.txWorld);
	}

	@Override
	public void onRemovedFrom(GuiRenderer renderer) {
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
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
	}
}
