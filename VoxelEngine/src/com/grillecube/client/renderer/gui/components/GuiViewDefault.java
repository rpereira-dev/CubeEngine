package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;

public class GuiViewDefault extends GuiView {
	private GuiLabel label;

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean mouse_in) {
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		this.label = new GuiLabel();
		this.label.setPosition(-1, 1);
		// this.label.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);

		this.label.addText("Hello you!");
		this.label.addText("\n");
		this.label.addText("It seems like everything is working properly! :D");
		this.label.addText("\n");
		this.label.addText("This is the default view, to set a different view, call:");
		this.label.addText("\n");
		this.label.addText("'VoxelEngineClient.getRenderer().setView(IView view)'");
		this.label.addText("\n");
		this.label.addText("\n");
		this.label.addText("\n");
		this.label.addText("Have fun using VoxelEngine 3D!");
		this.addChild(this.label);
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		// do not delete the texture, as it wasnt create by this object...
	}
}
