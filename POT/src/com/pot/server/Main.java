package com.pot.server;

import com.grillecube.common.ModSample;
import com.grillecube.engine.VoxelEngineServer;
import com.grillecube.engine.opengl.GLH;
import com.pot.common.ModCommon;

public class Main {

	public static void main(String[] args) {
		/* 1 */
		// initialize engine
		VoxelEngineServer engine = new VoxelEngineServer();

		/* 2 */
		// inject resources to be loaded
		engine.injectInternalMod(new ModCommon());
		engine.injectInternalMod(new ModSample());

		// load resources (mods)
		engine.load();

		if (true) {
			GLH.glhInit();
			return;
		}

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
