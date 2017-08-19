package com.pot.server;

import com.grillecube.common.ModSample;
import com.grillecube.server.VoxelEngineServer;
import com.pot.common.ModPOT;

public class Main {

	public static void main(String[] args) {
		/* 1 */
		// initialize engine
		VoxelEngineServer engine = new VoxelEngineServer();

		/* 2 */
		// inject resources to be loaded
		engine.getModLoader().injectMod(ModPOT.class);
		engine.getModLoader().injectMod(ModSample.class);

		// load resources (mods)
		engine.load();

		/* prepare engine before looping */

		/* 3 */
		// loop, every allocated memory will be release properly on program
		// termination */
		try {
			engine.loop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		engine.stopAll();
	}
}
