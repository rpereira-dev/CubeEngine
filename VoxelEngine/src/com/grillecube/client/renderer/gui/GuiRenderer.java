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

import com.grillecube.client.opengl.GLFWListenerKeyPress;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAdjustBox;
import com.grillecube.client.renderer.gui.font.Font;
import com.grillecube.client.renderer.gui.font.FontModel;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.resources.R;

public class GuiRenderer extends Renderer {
	/** rendering program */
	private ProgramFont programFont;
	private ProgramTexturedQuad programTexturedQuad;
	private ProgramColoredQuad programColoredQuad;

	/** fonts */
	public static Font DEFAULT_FONT;

	/** Fonts */
	private Map<String, Font> fonts;

	/** listener */
	private GLFWListenerKeyPress keyListeners;

	/** view */
	private ArrayList<Gui> guis;

	public GuiRenderer(MainRenderer renderer) {
		super(renderer);
	}

	@Override
	public void initialize() {
		this.fonts = new HashMap<String, Font>();
		this.programColoredQuad = new ProgramColoredQuad();
		this.programTexturedQuad = new ProgramTexturedQuad();
		this.programFont = new ProgramFont();
		this.guis = new ArrayList<Gui>();
		this.loadFonts();
		this.createListeners();
	}

	@Override
	public void deinitialize() {
		for (Gui gui : this.guis) {
			gui.deinitialize(this);
		}
		this.fonts.clear();
		this.programColoredQuad.delete();
		this.programTexturedQuad.delete();
		this.programFont.delete();
		this.getParent().getGLFWWindow().removeKeyPressListener(this.keyListeners);
	}

	private final void createListeners() {
		this.keyListeners = new GLFWListenerKeyPress() {
			@Override
			public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
				// TODO call key press in focused gui
			}
		};
		this.getParent().getGLFWWindow().addKeyPressListener(this.keyListeners);
	}

	/** load every fonts */
	private final void loadFonts() {

		// DEFAULT_FONT = this.getFont("Kirbyss");
		DEFAULT_FONT = this.getFont("Pokemon");
	}

	public final Font registerFont(String name) {
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
		for (Gui gui : this.guis) {
			gui.render(this);
		}
	}

	public void renderFontModel(FontModel model, Matrix4f transfMatrix) {
		this.programFont.useStart();
		this.programFont.bindFontModel(model, transfMatrix);
		model.render();
	}

	public void renderTexturedQuad(GLTexture glTexture, float ux, float uy, float vx, float vy,
			Matrix4f transformMatrix) {
		this.programTexturedQuad.useStart();
		this.programTexturedQuad.loadQuadTextured(glTexture, ux, uy, vx, vy, transformMatrix);
		GLH.glhDrawArrays(GL11.GL_POINTS, 0, 1);
	}

	public void renderColoredQuad(float r, float g, float b, float a, Matrix4f transformMatrix) {
		this.programColoredQuad.useStart();
		this.programColoredQuad.loadQuadColored(r, g, b, a, transformMatrix);
		GLH.glhDrawArrays(GL11.GL_POINTS, 0, 1);
	}

	private void updateViews() {

		GLFWWindow window = this.getParent().getGLFWWindow();
		float mx = (float) (window.getMouseX() / window.getWidth());
		float my = (float) (window.getMouseY() / window.getHeight());
		boolean pressed = window.isMouseLeftPressed();

		for (Gui gui : this.guis) {
			gui.update(mx, 1 - my, pressed);
		}

		// TODO
		// {
		// if (gui != this.guiFocused && gui.hasFocusRequest()) {
		// this.setGuiFocused(gui);
		// }
		// }
		//
		// if (this.guiFocused != null && !this.guiFocused.hasFocusRequest()) {
		// this.setGuiFocused(null);
		// }
		// this.onUpdate();
		// view.update();
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
	public final void addGui(Gui gui) {
		this.guis.add(gui);
		gui.onAddedTo(this);
	}

	/** remove a view to render */
	public final void removeGui(Gui gui) {
		this.guis.remove(gui);
		gui.onRemovedFrom(this);
	}

	public Gui getTopGui() {
		return (this.guis.size() > 0 ? this.guis.get(0) : null);
	}

	/** toast a message on the screen */
	public void toast(String text, Font font, float r, float g, float b, float a, int time) {

		GuiLabel lbl = new GuiLabel() {
			int timer = time;

			@Override
			protected void onRender(GuiRenderer renderer) {
				super.onRender(renderer);

				// weird trick, apparently it doesnt compile otherwise
				final GuiLabel thisGui = this;
				this.timer--;
				if (this.timer <= 0) {
					GuiRenderer.this.getParent().addGLTask(new GLTask() {
						@Override
						public void run() {
							GuiRenderer.this.removeGui(thisGui);
						}
					});
				}
			}
		};
		lbl.setFontColor(r, g, b, a);
		// lbl.setFontSize(1.0f, 1.0f);
		lbl.setText(text);
		lbl.setBoxCenterPosition(0.5f, 0.5f);
		lbl.addParameter(new GuiTextParameterTextAdjustBox());

		// gui.startAnimation(new GuiAnimationTextHoverScale<GuiLabel>(1.1f));
		this.addGui(lbl);
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
