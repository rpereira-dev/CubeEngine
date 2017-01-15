package com.grillecube.common;

import com.grillecube.engine.mod.IMod;
import com.grillecube.engine.mod.Mod;
import com.grillecube.engine.mod.ModInfo;

@ModInfo(name = "Sample mod", author = "author goes here", version = "1.0.0.a")
public class ModSample implements IMod {
	@Override
	public void initialize(Mod mod) {
		// mod.addRessource(new Blocks());
		// mod.addRessource(new Events());
		// mod.addRessource(new Entities());
	}

	@Override
	public void deinitialize(Mod mod) {
	}

}
