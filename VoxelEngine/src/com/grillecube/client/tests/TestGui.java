package com.grillecube.client.tests;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiSliderBar;
import com.grillecube.client.renderer.gui.components.GuiSliderBarValues;
import com.grillecube.client.renderer.gui.components.GuiTextPrompt2;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAlignLeft;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterYBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseEnter;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseExit;
import com.grillecube.client.renderer.model.editor.gui.components.GuiSliderBarEditor;
import com.grillecube.common.Logger;
import com.grillecube.common.resources.R;

public class TestGui {

	public static void main(String[] args) {

		VoxelEngineClient engine = new VoxelEngineClient();

		engine.load();

		GuiView guiView = new GuiView() {

			@Override
			protected void onInitialized(GuiRenderer renderer) {
				this.addGuiTexture();
				this.addGuiLabel();
				this.addGuiTextPrompt();
				this.addGuiSliderBar();
			}

			@Override
			protected void onDeinitialized(GuiRenderer renderer) {
			}

			@Override
			protected void onUpdate(float x, float y, boolean pressed) {
			}

			private void addGuiSliderBar() {
				GuiSliderBar<Float> guiSliderBar = new GuiSliderBarEditor<Float>();
				guiSliderBar.setBox(0.25f, 0.75f, 0.5f, 0.10f, 0.0f);
				Float[] floats = GuiSliderBarValues.floatRange(0, 1000, 1001);
				guiSliderBar.setHolder(new GuiSliderBarValues<Float>(floats));
				this.addChild(guiSliderBar);
			}

			private void addGuiTexture() {
				GuiTexture t = new GuiTexture();
				float ux = 144.0f / 256.0f;
				float uy = 16.0f / 256.0f;
				t.setBox(0.1f, 0.1f, 0.8f, 0.16f, 0.5f);
				t.setTexture(GLH.glhGenTexture(R.getResPath("textures/block_atlas/256x256.png")), 0.0f, 0.0f, ux, uy);
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
				lbl.setPosition(0.0f, 0.0f);
				lbl.setText("Hello world");
				lbl.setFontColor(1, 1, 1, 1.0f);
				lbl.addParameter(new GuiTextParameterTextFillBox(0.75f));
				lbl.addParameter(new GuiTextParameterTextCenterYBox());
				lbl.addParameter(new GuiTextParameterTextAlignLeft(0.1f));
				this.addChild(lbl);
			}

			private void addGuiLabel() {
				GuiLabel lbl = new GuiLabel();
				lbl.setPosition(0.0f, 0.0f);
				lbl.setText("Hello world");
				lbl.setFontColor(1, 1, 1, 1.0f);
				lbl.addParameter(new GuiTextParameterTextFillBox(0.75f));
				lbl.addParameter(new GuiTextParameterTextCenterYBox());
				lbl.addParameter(new GuiTextParameterTextAlignLeft(0.1f));
				lbl.addListener(new GuiListenerMouseEnter<GuiLabel>() {
					@Override
					public void invokeMouseEnter(GuiLabel gui, double mousex, double mousey) {
						Logger.get().log(Logger.Level.DEBUG, "in");
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
