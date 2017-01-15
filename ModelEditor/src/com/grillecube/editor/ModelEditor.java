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

package com.grillecube.editor;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

import com.grillecube.editor.model.ModelGrid;
import com.grillecube.editor.toolbox.Toolbox;
import com.grillecube.editor.window.camera.CameraEditor;
import com.grillecube.editor.window.camera.CameraTool;
import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.VoxelEngineClient;
import com.grillecube.engine.event.EventCallback;
import com.grillecube.engine.event.EventPostLoop;
import com.grillecube.engine.mod.ModInfo;
import com.grillecube.engine.opengl.GLFWListenerMouseEnter;
import com.grillecube.engine.opengl.GLFWListenerMouseExit;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.ModelSkin;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.engine.renderer.model.json.JSONHelper;
import com.grillecube.engine.renderer.model.json.ModelBuilderExporter;
import com.grillecube.engine.sound.ALSound;
import com.grillecube.engine.world.entity.EntityModeled;

@ModInfo(name = "Model Editor", author = "toss-dev", version = "1.0.0")
public class ModelEditor implements GLFWListenerMouseEnter, GLFWListenerMouseExit {

	public static final String VERSION = "1.0.0a";
	public static final URL RUNNING_DIRECTORY = ModelEditor.class.getProtectionDomain().getCodeSource().getLocation();

	private static ModelEditor _instance;

	/** current camera in use */
	private CameraEditor _camera;
	private ModelEditorWorld _world;

	/** toolboxes linked to this window */
	private Toolbox _toolbox;

	private HashMap<String, Integer> _config;

	private Robot _robot;

	private VoxelEngineClient _engine;

	public ModelEditor() {
		_instance = this;
	}

	public void loop() throws InterruptedException {
		this._engine = new VoxelEngineClient();
		this._engine.load();
		this.postLoad();
		this._engine.getResourceManager().getEventManager().registerEventCallback(new EventCallback<EventPostLoop>() {
			@Override
			public void invoke(EventPostLoop event) {
				stop();
			}
		});

		// this._engine.getRenderer().toggle(false);
		this._engine.loop();
		this.close();
	}

	private void postLoad() {

		// config
		this.checkConfig();
		this.loadConfig();

		// set the world
		this._world = new ModelEditorWorld();
		this._engine.getResourceManager().getWorldManager().registerWorld(this._world);
		this._engine.setWorld(this._world);

		// set the camera
		this._camera = new CameraEditor(this);
		this._engine.getRenderer().setCamera(this._camera);

		// toolbox
		this._toolbox = new Toolbox(this);

		// set the current view
		this._engine.getRenderer().getGuiRenderer().addView(new GuiViewModelEditor());
	}

	public void playSound(ALSound sound) {

		if (sound == null) {
			return;
		}

		if (this.getConfig(ModelEditor.Config.SOUND) == 1) {
			this._engine.getResourceManager().getSoundManager().playSound(sound);
		}
	}

	private void checkConfig() {
		Logger.get().log(Level.FINE, "Creating config");
		File file = new File(ModelEditor.Config.FILE_NAME);
		JSONObject json;

		if (file.exists()) {
			try {
				json = new JSONObject(JSONHelper.readFile(file));
			} catch (Exception e) {
				json = this.createDefaultJSON();
				Logger.get().log(Level.WARNING, "Config file is wrongly formatted.");
				e.printStackTrace(Logger.get().getPrintStream());
			}
		} else {
			json = this.createDefaultJSON();
		}
		this.fillConfig(json);
		this.writeConfig(file);
	}

	private JSONObject createDefaultJSON() {
		JSONObject json = new JSONObject();

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();

		width = width / 2;
		json.put(ModelEditor.Config.MODEL_WINDOW_WIDTH, width);
		json.put(ModelEditor.Config.MODEL_WINDOW_HEIGHT, width / 1.6f);
		json.put(ModelEditor.Config.VSYNC, 1);
		json.put(ModelEditor.Config.SOUND, 1);

		return (json);
	}

	private void writeConfig(File file) {
		try {
			String nl = System.getProperty("line.separator");
			FileWriter writer = new FileWriter(file);

			writer.write("{");
			writer.write(nl);

			for (String str : this._config.keySet()) {
				writer.write("\t\"");
				writer.write(str);
				writer.write("\":");
				writer.write(String.valueOf(this.getConfig(str)));
				writer.write(",");
				writer.write(nl);
			}
			writer.write("}");
			writer.write(nl);

			writer.flush();
			writer.close();
		} catch (Exception e) {
			Logger.get().log(Level.WARNING, "Couldnt create config file");
			e.printStackTrace(Logger.get().getPrintStream());
		}
	}

	private void loadConfig() {
		Logger.get().log(Level.FINE, "Loading config");

		this._engine.getRenderer().getGLFWWindow().setSize(this.getConfig(ModelEditor.Config.MODEL_WINDOW_WIDTH),
				this.getConfig(ModelEditor.Config.MODEL_WINDOW_HEIGHT));
		this._engine.getRenderer().getGLFWWindow().setScreenPosition(100, 100);
		this._engine.getRenderer().getGLFWWindow().swapInterval(this.getConfig(ModelEditor.Config.VSYNC));
	}

	private void useRobot() {
		try {
			this._robot = new Robot();
			this._engine.getRenderer().getGLFWWindow().addMouseEnterListener(this);
			this._engine.getRenderer().getGLFWWindow().addMouseExitListener(this);
		} catch (AWTException e) {
			Logger.get().log(Level.WARNING, "Tried to a use Robot but it failed: ModelWindow.useRobot()");
			e.printStackTrace(Logger.get().getPrintStream());
		}
	}

	@Override
	public void invokeMouseExit(GLFWWindow window, boolean cursor, double posx, double posy) {
		if (this._robot == null || !cursor) {
			return;
		}
		ModelEditor.instance().getToolbox().requestFocus();
	}

	@Override
	public void invokeMouseEnter(GLFWWindow window, boolean cursor, double posx, double posy) {

		if (this._robot == null || !cursor) {
			return;
		}
		this._robot.keyPress(KeyEvent.VK_ALT);
		this._robot.keyPress(KeyEvent.VK_TAB);
		this._robot.delay(10);
		this._robot.keyRelease(KeyEvent.VK_ALT);
		this._robot.keyRelease(KeyEvent.VK_TAB);

	}

	private void fillConfig(JSONObject json) {
		this._config = new HashMap<String, Integer>();
		String[] keys = JSONObject.getNames(json);

		for (String key : keys) {
			try {
				this._config.put(key, json.getInt(key));
			} catch (Exception e) {
				Logger.get().log(Level.ERROR, "Config value can only be integer!");
			}
		}
	}

	public int getConfig(String key) {
		return (this.getConfig(key, 0));
	}

	public int getConfig(String key, int defvalue) {
		Integer value = this._config.get(key);
		return (value == null ? defvalue : (int) value);
	}

	private void stop() {
		this._config.put(ModelEditor.Config.MODEL_WINDOW_WIDTH, this._engine.getRenderer().getGLFWWindow().getWidth());
		this._config.put(ModelEditor.Config.MODEL_WINDOW_HEIGHT,
				this._engine.getRenderer().getGLFWWindow().getHeight());
		this.writeConfig(new File(ModelEditor.Config.FILE_NAME));

		ModelBuilderExporter.exportModelBuilder(this.getModel(), ModelEditor.Config.TMP_MODEL_BUILDER_FILE);
	}

	public EntityModeled getEntity() {
		return (this._toolbox.getEntity());
	}

	public ModelEditorWorld getWorld() {
		return (this._world);
	}

	public Model getModel() {
		return (this._toolbox.getModel());
	}

	public ModelPartBuilder getCurrentModelPart() {
		Model model = this.getModel();
		if (model == null || model.getPartsCount() == 0) {
			return (null);
		}
		Toolbox toolbox = this.getToolbox();
		if (toolbox == null) {
			return ((ModelPartBuilder) model.getPartAt(0));
		}
		return (toolbox.getModelPart());
	}

	public ModelSkin getCurrentModelSkin() {
		Toolbox toolbox = this.getToolbox();
		if (toolbox == null) {
			return (null);
		}
		return (toolbox.getModelSkin());
	}

	public ModelPartSkinBuilder getCurrentModelPartSkin() {
		ModelSkin skin = this.getCurrentModelSkin();
		if (skin == null) {
			return (null);
		}
		int id = this.getModel().getPartID(this.getCurrentModelPart());
		return (ModelPartSkinBuilder) (skin.getPart(id));
	}

	/** return the main toolbox */
	public Toolbox getToolbox() {
		return (this._toolbox);
	}

	public static ModelEditor instance() {
		return (_instance);
	}

	public ModelGrid getGrid() {
		return (this._world.getGrid());
	}

	public void close() {
		this._toolbox.close();
	}

	public class Config {
		public static final float DEFAULT_SIZE_UNIT = 8.0f;

		public static final String TMP_MODEL_BUILDER_FILE = ".tmp.json";
		public static final String FILE_NAME = "./config.json";
		public static final String MODEL_WINDOW_WIDTH = "screen_width";
		public static final String MODEL_WINDOW_HEIGHT = "screen_height";
		public static final String VSYNC = "vsync";
		public static final String SOUND = "sound";
	}

	public VoxelEngineClient getEngine() {
		return (this._engine);
	}

	public void toast(String str, boolean good) {
		if (good) {
			this._engine.getRenderer().getGuiRenderer().toast(str, 0, 1, 0, 1, 90);
		} else {
			this._engine.getRenderer().getGuiRenderer().toast(str, 1, 0, 0, 1, 90);
		}

	}

	public void addModel(Model model) {

		EntityModeled entity = new EntityModeled(this.getWorld(), model) {
			@Override
			protected void onUpdate() {
			}

			@Override
			public boolean move(float dx, float dy, float dz) {
				return (false);
			}
		};

		this.getToolbox().addModel(entity);
	}

	public CameraTool getTool() {
		return (this._camera.getTool());
	}

	public CameraEditor getCamera() {
		return (this._camera);
	}
}