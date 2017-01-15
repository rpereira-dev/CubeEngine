package com.grillecube.engine.tests.client;

import com.grillecube.engine.Logger;
import com.grillecube.engine.VoxelEngineClient;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLTexture;
import com.grillecube.engine.renderer.gui.Gui;
import com.grillecube.engine.renderer.gui.GuiListenerMouseEnter;
import com.grillecube.engine.renderer.gui.GuiListenerMouseLeftRelease;
import com.grillecube.engine.renderer.gui.GuiRenderer;
import com.grillecube.engine.renderer.gui.animations.GuiAnimationTextHoverScale;
import com.grillecube.engine.renderer.gui.components.GuiButton;
import com.grillecube.engine.renderer.gui.components.GuiButtonTextured;
import com.grillecube.engine.renderer.gui.components.GuiLabel;
import com.grillecube.engine.renderer.gui.components.GuiText;
import com.grillecube.engine.renderer.gui.components.GuiTextPrompt;
import com.grillecube.engine.renderer.gui.components.GuiTexture;
import com.grillecube.engine.renderer.gui.components.GuiView;
import com.grillecube.engine.resources.R;

public class TestGui {

	public static void main(String[] args) {

		VoxelEngineClient engine = new VoxelEngineClient();

		engine.load();
		engine.getRenderer().getGuiRenderer().addView(new GuiViewTest());
		engine.getRenderer().getGLFWWindow().setClearColor(1, 1, 1, 1);

		try {
			engine.loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		engine.stopAll();
	}
}

class GuiViewTest extends GuiView {

	@Override
	public void onRemoved(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdded(GuiRenderer renderer) {
		this.addGuiLabel(renderer);
		this.addGuiTexture(renderer);
		this.addGuiButton(renderer);
		this.addGuiTextPrompt(renderer);
	}

	private void addGuiLabel(GuiRenderer renderer) {
		GuiLabel lbl = new GuiLabel();
		lbl.setFontColor(Gui.COLOR_BLACK);
		lbl.setText("This is a GuiLabel and a GuiTexture under it");
		lbl.setPosition(-1.0f, 1.0f);
		lbl.setSize(1.0f, 0.2f);
		lbl.setFontSize(1.0f, 1.0f);
		lbl.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		lbl.startAnimation(new GuiAnimationTextHoverScale<GuiLabel>(1.1f));
		this.addGui(lbl);
	}

	private void addGuiTexture(GuiRenderer renderer) {
		GuiTexture t = new GuiTexture(GLH.glhGenTexture(R.getResPath("textures/blocks/atlas_256x256.png")));
		t.setPosition(-1.0f, 0.8f);
		t.setSize(0.5f, 0.5f);
		this.addGui(t);
	}

	private void addGuiTextPrompt(GuiRenderer renderer) {
		GLTexture texture = GLH.glhGenTexture(R.getResPath("textures/gui/button_default.png"));
		GuiTextPrompt prompt = new GuiTextPrompt();
		prompt.setBackgroundTexture(texture, 0, 0, 1, 1 / 3.0f);
		prompt.setPosition(-1.0f, 0.0f);
		prompt.setFontSize(1.0f, 1.0f);
		prompt.setSize(1.0f, 0.2f);
		prompt.setHint("hello world i'm a hint");
		prompt.setHintColor(0.5f, 0.5f, 0.5f, 0.5f);
		prompt.setFontColor(1.0f, 0.0f, 0.0f, 1.0f);
		prompt.setHintAsText();
		// prompt.addParameter(GuiTextPrompt.PARAM_TEXT_DONT_OVERFLOW);
		// prompt.addParameter(GuiTextPrompt.PARAM_TEXT_CENTER_X);
		prompt.addParameter(GuiTextPrompt.PARAM_TEXT_CENTER_Y);
		prompt.setMaxChars(16);
		this.addGui(prompt);
	}

	private void addGuiButton(GuiRenderer renderer) {
		GLTexture texture = GLH.glhGenTexture(R.getResPath("textures/gui/button_default.png"));
		GuiButtonTextured b = new GuiButtonTextured(texture);
		b.setPosition(-1.0f, 0.7f);
		b.setSize(0.5f, 0.1f);
		b.setText("I'm a GuiButton (FIX ME)");
		b.addParameter(GuiButton.PARAM_TEXT_FILL_BUTTON);
		b.addParameter(GuiText.PARAM_TEXT_CENTER);
		b.addListener(new GuiListenerMouseEnter<GuiButton>() {
			@Override
			public void invokeMouseEnter(GuiButton gui, double mousex, double mousey) {
				Logger.get().log(Logger.Level.DEBUG, "entered GuiButton hitbox at: " + mousex + " : " + mousey);
			}
		});
		b.addListener(new GuiListenerMouseLeftRelease<GuiButton>() {
			@Override
			public void invokeMouseLeftRelease(GuiButton gui, double mousex, double mousey) {
				Logger.get().log(Logger.Level.DEBUG, "GuiButton clicked");
			}
		});
		this.addGui(b);
	}
}
