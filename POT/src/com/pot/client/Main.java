package com.pot.client;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldFree;
import com.grillecube.client.renderer.gui.components.GuiViewDebug;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.common.ModSample;
import com.grillecube.common.resources.AssetsPack;
import com.grillecube.common.world.World;
import com.pot.common.ModPOT;
import com.pot.common.world.POTWorlds;
import com.pot.common.world.entity.EntityBipedTest;

public class Main {

	public static void main(String[] args) {

		/* 1 */
		// initialize engine
		VoxelEngineClient engine = new VoxelEngineClient();
		engine.initialize();

		/* 2 */
		// inject resources to be loaded
		engine.getModLoader().injectMod(ModPOT.class);
		engine.getModLoader().injectMod(ModSample.class);
		engine.putAssets(new AssetsPack(ModPOT.MOD_ID, "./assets.zip"));

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
		engine.deinitialize();
	}

	private static void prepareEngine(VoxelEngineClient engine) {
		engine.loadWorld(POTWorlds.DEFAULT);
		engine.getGLFWWindow().swapInterval(1);
		engine.getGLFWWindow().setScreenPosition(100, 100);

		// CameraPerspectiveWorldEntity camera = new
		// CameraPerspectiveWorldEntity(engine.getGLFWWindow());
		// camera.setPosition(0.0f, 170.0f, -40.0f);
		World world = engine.getWorld(POTWorlds.DEFAULT);
		EntityBipedTest player = new EntityBipedTest(world);
		player.setPosition(16, 200, 16);
		world.spawnEntity(player);
		// camera.setWorld(world);
		// camera.setEntity(player);

		CameraPerspectiveWorldFree camera = new CameraPerspectiveWorldFree(engine.getGLFWWindow());

		engine.getRenderer().getGuiRenderer().addGui(new GuiViewWorld(camera, POTWorlds.DEFAULT));
		engine.getRenderer().getGuiRenderer().addGui(new GuiViewDebug(camera));
	}
}
