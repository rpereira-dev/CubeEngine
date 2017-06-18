package com.pot.client;

import com.grillecube.common.mod.Mod;
import com.pot.client.renderer.POTParticles;
import com.pot.client.renderer.model.POTModels;
import com.pot.common.ModPOT;

public class ModPOTClient extends ModPOT {

	@Override
	public void initialize(Mod mod) {
		super.initialize(mod);
		mod.addResource(new POTModels());
		mod.addResource(new POTParticles());
	}

	@Override
	public void deinitialize(Mod mod) {
		super.deinitialize(mod);
	}

}
