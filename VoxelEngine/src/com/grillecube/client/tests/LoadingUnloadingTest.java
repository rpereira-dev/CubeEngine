package com.grillecube.client.tests;

import com.grillecube.client.VoxelEngineClient;

public class LoadingUnloadingTest {

	public static void main(String[] args) {

		/* 1 */
		// initialize engine
		VoxelEngineClient engine = new VoxelEngineClient();
		engine.initialize();

		// load resources (mods)
		engine.load();

		try {
			engine.loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		engine.deinitialize();
	}
}