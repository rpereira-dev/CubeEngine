package com.grillecube.client.renderer.gui.components;

public class GuiViewDefault extends GuiView {
	private GuiLabel label;

	public GuiViewDefault() {
		this.label = new GuiLabel();
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
}
