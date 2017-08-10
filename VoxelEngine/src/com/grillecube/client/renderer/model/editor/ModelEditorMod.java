package com.grillecube.client.renderer.model.editor;

import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.mod.ModInfo;
import com.grillecube.common.resources.ResourceManager;

@ModInfo(name = "Model Editor Module", author = "rpereira-dev", version = "0.0.0.a")
public class ModelEditorMod implements IMod, IModResource {

	@Override
	public void initialize(Mod mod) {
		mod.addResource(this);
	}

	@Override
	public void deinitialize(Mod mod) {
	}

	public static int WORLD_ID = 0;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		WORLD_ID = manager.getWorldManager().registerWorld(new ModelEditorWorld());
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {

	}
}
