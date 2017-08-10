package com.grillecube.client.tests;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseEnter;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseExit;
import com.grillecube.common.Logger;
import com.grillecube.common.resources.R;

public class TestGui {

	public static void main(String[] args) {

		VoxelEngineClient engine = new VoxelEngineClient();

		engine.load();

		GuiView guiView = new GuiView() {

			@Override
			protected void onInitialized(GuiRenderer renderer) {
			}

			@Override
			protected void onDeinitialized(GuiRenderer renderer) {
			}

			@Override
			protected void onUpdate(float x, float y, boolean pressed) {
			}

			@Override
			public void onAddedTo(GuiRenderer guiRenderer) {
				this.addGuiTexture();
				// this.addGuiLabel();
			}

			private void addGuiTexture() {
				GuiTexture t = new GuiTexture();
				float ux = 144.0f / 256.0f;
				float uy = 16.0f / 256.0f;
				t.set(0.1f, 0.1f, 0.8f, 0.16f, 0.5f);
				t.setTexture(GLH.glhGenTexture(R.getResPath("textures/blocks/atlas_256x256.png")), 0.0f, 0.0f, ux, uy);
				t.addListener(new GuiListenerMouseEnter<GuiTexture>() {
					@Override
					public void invokeMouseEnter(GuiTexture gui, double mousex, double mousey) {
						Logger.get().log(Logger.Level.DEBUG, "in");
					}
				});

				t.addListener(new GuiListenerMouseExit<GuiTexture>() {
					@Override
					public void invokeMouseExit(GuiTexture gui, double mousex, double mousey) {
						Logger.get().log(Logger.Level.DEBUG, "out");
					}
				});
				this.addGui(t);
			}

			private void addGuiLabel() {
				GuiLabel lbl = new GuiLabel();
				lbl.setFontColor(Gui.COLOR_DARK_MAGENTA);
				lbl.setText("abcdefg");
				lbl.addParameter(GuiLabel.PARAM_AUTO_ADJUST_RECT);
				lbl.setFontSize(1.0f, 1.0f);
				lbl.setPosition(0.0f, 0.0f);
				this.addGui(lbl);
			}

			@Override
			public void onRemovedFrom(GuiRenderer guiRenderer) {
			}

			@Override
			public void onAddedTo(Gui gui) {
			}

			@Override
			public void onRemovedFrom(Gui gui) {
			}

		};
		guiView.set(0.f, 0.0f, 1.0f, 1.0f, 0.0f);
		engine.getRenderer().getGuiRenderer().addGui(guiView);

		try {
			engine.loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		engine.stopAll();
	}
}
