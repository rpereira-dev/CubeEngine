/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.client.opengl.GLFWListenerKeyPress;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.gui.font.Font;
import com.grillecube.client.renderer.gui.font.FontModel;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;
import com.grillecube.common.resources.R;

public class GuiRenderer extends Renderer {
	/** rendering program */
	private ProgramFont programFont;
	private ProgramQuad programQuad;

	/** fonts */
	public static Font DEFAULT_FONT;

	/** Fonts */
	private Map<String, Font> fonts;

	/** listener */
	private GLFWListenerKeyPress keyListeners;

	/** view */
	private ArrayList<GuiView> views;

	public GuiRenderer(MainRenderer renderer) {
		super(renderer);
	}

	@Override
	public void initialize() {
		this.fonts = new HashMap<String, Font>();
		this.programQuad = new ProgramQuad();
		this.programFont = new ProgramFont();
		this.views = new ArrayList<GuiView>();
		this.loadFonts();
		this.createListeners();
	}

	@Override
	public void deinitialize() {
		for (GuiView view : this.views) {
			view.delete(this);
		}

		this.getParent().getGLFWWindow().removeKeyPressListener(this.keyListeners);
	}

	private void createListeners() {

		this.keyListeners = new GLFWListenerKeyPress() {

			@Override
			public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
				getTopView().invokeKeyPress(glfwWindow, key, scancode, mods);
			}
		};
		this.getParent().getGLFWWindow().addKeyPressListener(this.keyListeners);
	}

	/** load every fonts */
	private void loadFonts() {

		// DEFAULT_FONT = this.getFont("Kirbyss");
		DEFAULT_FONT = this.getFont("Calibri");
	}

	public Font registerFont(String name) {
		Font font = new Font(R.getResPath("font/" + name));
		this.fonts.put(name, font);
		return (font);
	}

	public Font getFont(String fontname) {
		Font font = this.fonts.get(fontname);
		if (font == null) {
			return (this.registerFont(fontname));
		}
		return (font);
	}

	@Override
	public void preRender() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void postRender() {
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void render() {
		for (GuiView view : this.views) {
			view.render(this);
		}
	}

	public void renderFontModel(FontModel model) {
		this.programFont.useStart();
		this.programFont.bindFontModel(model);
		model.render();
	}

	/**
	 * render a rectangle:
	 * 
	 * @param texture
	 *            the texture to use
	 * @param uvxmin
	 *            the lower corner x texture coordinate
	 * @param uvymin
	 *            the lower corner y texture coordinate
	 * @param uvxmax
	 *            the upper corner x texture coordinate
	 * @param uvymax
	 *            the upper corner y texture coordinate
	 * @param x
	 *            position x on screen
	 * @param y
	 *            position y on screen
	 * @param width
	 *            rect size on screen
	 * @param height
	 *            rect size on screen
	 */
	public void renderQuad(GLTexture texture, float uvxmin, float uvymin, float uvxmax, float uvymax, float x, float y,
			float width, float height) {

		this.programQuad.useStart();
		texture.bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		this.programQuad.loadQuadTextured(x, y, width, height, uvxmin, uvymin, uvxmax, uvymax);
		GLH.glhDrawArrays(GL11.GL_POINTS, 0, 1);
	}

	private void updateViews() {
		for (GuiView view : this.views) {
			view.update();
		}
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
		tasks.add(engine.new Callable<Taskable>() {

			@Override
			public Taskable call() throws Exception {
				updateViews();
				return (GuiRenderer.this);
			}

			@Override
			public String getName() {
				return ("GuiRenderer guis update");
			}
		});
	}

	/** add a view to render */
	public void addView(GuiView view) {
		this.views.add(view);
		view.setGLFWWindow(this.getParent().getGLFWWindow());
		view.onAdded(this);
	}

	/** remove a view to render */
	public void removeView(GuiView view) {
		this.views.remove(view);
		view.onRemoved(this);
	}

	public GuiView getTopView() {
		return (this.views.size() > 0 ? this.views.get(0) : null);
	}

	// TODO fix toasts
	/** toast a message on the screen */
	public void toast(String text, Font font, float r, float g, float b, float a, int time) {

		GuiView view = new GuiView() {

			@Override
			public void onAdded(GuiRenderer renderer) {
			}

			@Override
			public void onRemoved(GuiRenderer renderer) {

			}

		};

		GuiLabel lbl = new GuiLabel() {
			int _timer = time;

			@Override
			public void render(GuiRenderer renderer) {
				super.render(renderer);
				_timer--;
				if (_timer <= 0) {
					getParent().addGLTask(new GLTask() {

						@Override
						public void run() {
							removeView(view);
						}

					});
				}
			}
		};
		lbl.setFontColor(r, g, b, a);
		lbl.setFontSize(1.0f, 1.0f);
		lbl.setText(text);
		lbl.setCenter(0.0f, 0.0f);
		lbl.addParameters(GuiLabel.PARAM_AUTO_ADJUST_RECT);
		lbl.addParameters(GuiLabel.PARAM_CENTER);
		// gui.startAnimation(new
		// GuiAnimationTextHoverScale<GuiLabel>(1.1f));
		view.addGui(lbl);
		this.addView(view);
	}

	public void toast(String str, float r, float g, float b, float a) {
		this.toast(str, r, g, b, a, 30);
	}

	public void toast(String str) {
		this.toast(str, 0, 1, 0, 1, 30);
	}

	public void toast(String str, int time) {
		this.toast(str, 0, 1, 0, 1, time);
	}

	public void toast(String str, boolean good) {
		if (good) {
			this.toast(str, 0, 1, 0, 1, 90);
		} else {
			this.toast(str, 1, 0, 0, 1, 90);
		}
	}

	public void toast(String str, float r, float g, float b, float a, int time) {
		this.toast(str, GuiRenderer.DEFAULT_FONT, r, g, b, a, time);
	}

}
