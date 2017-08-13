package com.pot.client;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldFree;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.components.GuiTexture;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.gui.components.GuiViewDebug;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseEnter;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseExit;
import com.grillecube.common.Logger;
import com.grillecube.common.ModSample;
import com.grillecube.common.resources.R;
import com.pot.common.ModPOT;
import com.pot.common.world.POTWorlds;

public class Main {

	public static void main(String[] args) {

		/* 1 */
		// initialize engine
		VoxelEngineClient engine = new VoxelEngineClient();

		/* 2 */
		// inject resources to be loaded
		engine.getModLoader().injectMod(ModPOT.class);
		engine.getModLoader().injectMod(ModSample.class);

		// load resources (mods)
		engine.load();

		/* prepare engine before looping */
		prepareEngine(engine);

		/* 3 */
		// loop, every allocated memory will be released properly on program
		// termination */
		try {
			engine.loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		engine.stopAll();
	}

	private static void prepareEngine(VoxelEngineClient engine) {
		// engine.getGLFWWindow().setCursor(false);

		// int s = 2;
		// for (int i = 0; i < s; i++) {
		// for (int j = 0; j < s; j++) {
		// Entity entity = new EntityShroom(Worlds.DEFAULT);
		// Worlds.DEFAULT.spawnEntity(entity, i, 100, j);
		// entity.toggleSkin(i % 2);
		// }
		// }

		// Entity entity = new EntityShroom(POTWorlds.DEFAULT);
		// POTWorlds.DEFAULT.getEntityStorage().add(entity, 64, 256, 64);
		// CameraPerspectiveWorldEntity cam = new
		// CameraPerspectiveWorldEntity(engine.getGLFWWindow());
		// cam.setEntity(entity);
		// engine.getRenderer().setCamera(cam);

		engine.getRenderer().setCamera(new CameraPerspectiveWorldFree(engine.getGLFWWindow()));
		engine.getRenderer().getCamera().setPosition(0.0f, 170.0f, -40.0f);
		engine.setWorld(POTWorlds.DEFAULT);
		engine.getGLFWWindow().swapInterval(1);
		engine.getGLFWWindow().setScreenPosition(100, 100);
		engine.getRenderer().getGuiRenderer().addGui(new GuiViewDebug());
	}
}
