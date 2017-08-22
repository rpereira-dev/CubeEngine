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
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.opengl.window.event.GLFWEventChar;
import com.grillecube.client.opengl.window.event.GLFWEventKeyPress;
import com.grillecube.client.opengl.window.event.GLFWListener;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextAdjustBox;
import com.grillecube.client.renderer.gui.font.Font;
import com.grillecube.client.renderer.gui.font.FontModel;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
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

	/** the main gui, parent of every other guis */
	private Gui mainGui;
	private GuiInputManager guiInputManager;

	/** gui rendering list (sorted by layers) */
	private ArrayList<Gui> renderingList;

	public GuiRenderer(MainRenderer renderer) {
		super(renderer);
	}

	@Override
	public void initialize() {
		Logger.get().log(Level.FINE, "Initializing " + this.getClass().getSimpleName());
		this.fonts = new HashMap<String, Font>();
		this.programColoredQuad = new ProgramColoredQuad();
		this.programTexturedQuad = new ProgramTexturedQuad();
		this.programFont = new ProgramFont();
		this.mainGui = new GuiView();
		this.guiInputManager = new GuiInputManager();
		this.guiInputManager.initialize(this.getMainRenderer().getGLFWWindow(), this.mainGui);
		this.renderingList = new ArrayList<Gui>();
		this.loadFonts();
	}

	@Override
	public void deinitialize() {
		this.mainGui.deinitialize(this);
		this.guiInputManager.deinitialize();
		this.fonts.clear();
		this.programColoredQuad.delete();
		this.programTexturedQuad.delete();
		this.programFont.delete();
	}

	/** load every fonts */
	private final void loadFonts() {

		DEFAULT_FONT = this.getFont("Calibri");
		// DEFAULT_FONT = this.getFont("Pokemon");
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
	public void render() {

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// render them in the correct order
		for (Gui gui : this.renderingList) {
			gui.render(this);
		}

		GL11.glDisable(GL11.GL_BLEND);

	}

	/** a recursive helper to generate rendering list */
	private final void addGuisToRenderingList(Gui parent) {
		if (parent.getChildren() != null) {
			for (Gui child : parent.getChildren()) {
				if (child.isVisible()) {
					this.renderingList.add(child);
					this.addGuisToRenderingList(child);
				}
			}
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

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
		updateGuis();
		// tasks.add(engine.new Callable<Taskable>() {
		//
		// @Override
		// public Taskable call() throws Exception {
		// updateGuis();
		// return (GuiRenderer.this);
		// }
		//
		// @Override
		// public String getName() {
		// return ("GuiRenderer guis update");
		// }
		// });
	}

	private final void updateGuis() {
		this.renderingList.clear();
		this.addGuisToRenderingList(this.mainGui);
		this.renderingList.sort(Gui.WEIGHT_COMPARATOR);

		this.guiInputManager.update();
		for (Gui gui : this.renderingList) {
			gui.update();
		}
	}

	/** add a view to render */
	public final void addGui(Gui gui) {
		this.mainGui.addChild(gui);
	}

	/** remove a view to render */
	public final void removeGui(Gui gui) {
		this.mainGui.removeChild(gui);
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
					GuiRenderer.this.getMainRenderer().addGLTask(new GLTask() {
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

	/** a callback when the window is resized */
	public void onWindowResize(GLFWWindow window, int width, int height) {
		this.mainGui.onWindowResized(width, height);
	}

	/**
	 * wrappers for tinyfd library
	 * 
	 * @param title
	 *            : dialog title
	 * @param defaultPath
	 *            : dialog default path
	 * @return
	 */
	public static final String dialogSelectFolder(String title, String defaultPath) {
		return (TinyFileDialogs.tinyfd_selectFolderDialog(title, defaultPath));
	}

	// TODO : more dialogs
}
