package com.grillecube.client.renderer.model.editor.gui.toolbox;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiColoredQuad;
import com.grillecube.client.renderer.gui.components.GuiView;

public class GuiToolbox extends Gui {

	/** the color back of the toolbox */
	private GuiColoredQuad bg;

	/** the front view of the toolbox (basically, menus for model edition) */
	private ArrayList<GuiView> guiViews;

	/** the index of the selected view */
	private int selectedView;

	public GuiToolbox() {
		super();
		this.guiViews = new ArrayList<GuiView>();

		// background
		this.bg = new GuiColoredQuad();
		this.bg.setBox(0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
		this.bg.setColor(0.95f, 0.95f, 0.95f, 1.0f);
		this.addChild(this.bg);

		// model creation
		this.guiViews.add(new GuiToolboxViewModels());
		this.selectView(0);
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	private void selectView(int i) {
		this.removeChild(this.guiViews.get(this.selectedView));
		this.selectedView = i;
		this.addChild(this.guiViews.get(this.selectedView));
	}

	public final void selectNextView() {
		this.selectView((this.selectedView + 1) % this.guiViews.size());
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

}
