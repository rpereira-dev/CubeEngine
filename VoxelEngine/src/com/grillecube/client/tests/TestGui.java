package com.grillecube.client.tests;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.components.GuiTextPrompt2;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAdjustBox;
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
				// this.addGuiTexture();
				// this.addGuiLabel();
				this.addGuiTextPrompt();
			}

			private void addGuiTexture() {
				GuiTexture t = new GuiTexture();
				float ux = 144.0f / 256.0f;
				float uy = 16.0f / 256.0f;
				t.setBox(0.1f, 0.1f, 0.8f, 0.16f, 0.5f);
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
				this.addChild(t);
			}

			private void addGuiTextPrompt() {
				GuiTextPrompt2 lbl = new GuiTextPrompt2();
				lbl.setPosition(0.0f, 0.5f);
				lbl.setText("Hello world this is a test\nyou know what i mean right????????");
				this.addChild(lbl);
			}

			private void addGuiLabel() {
				GuiLabel lbl = new GuiLabel();
				lbl.setText("hello worlaaaaaaaaaad\nthis is atesaaaaaaaaaaaaaaaaaaat");
				lbl.setFontColor(Gui.COLOR_DARK_MAGENTA);
				lbl.setFontSize(0.5f, 0.5f);
				lbl.setPosition(0.0f, 1.0f - lbl.getTextHeight());
				lbl.addParameter(new GuiTextParameterTextAdjustBox());

				lbl.addListener(new GuiListenerMouseEnter<GuiLabel>() {
					@Override
					public void invokeMouseEnter(GuiLabel gui, double mousex, double mousey) {
						Logger.get().log(Logger.Level.DEBUG, "in");
						if (gui.getText().length() > 0) {
							gui.setBox(0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
							gui.setText(gui.getText().substring(0, gui.getText().length() - 1));
						}
					}
				});

				lbl.addListener(new GuiListenerMouseExit<GuiLabel>() {
					@Override
					public void invokeMouseExit(GuiLabel gui, double mousex, double mousey) {
						Logger.get().log(Logger.Level.DEBUG, "out");
					}
				});

				this.addChild(lbl);
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
		// guiView.set(0.5f, 0.5f, 0.5f, 0.5f, 0.0f);
		guiView.setBox(0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
		engine.getRenderer().getGuiRenderer().addGui(guiView);

		try {
			engine.loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		engine.stopAll();
	}
}
