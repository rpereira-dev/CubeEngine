package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.gui.GuiRenderer;

public class GuiViewIngame extends GuiView {

	private GuiTexture _tx_world;

	public GuiViewIngame() {
		super();
	}

	@Override
	public void onAdded(GuiRenderer renderer) {
		this._tx_world = new GuiTexture(VoxelEngineClient.instance().getRenderer().getFBOTexture());
		this._tx_world.set(-1, -1, 2, 2);
		super.addGui(this._tx_world);
	}

	@Override
	public void onRemoved(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}
}
