package com.pot.common.world.item;

import com.grillecube.engine.mod.Mod;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.resources.IModResource;
import com.grillecube.engine.resources.ResourceManager;
import com.grillecube.engine.world.items.Item;

public class POTItems implements IModResource {
	//items
	public static Item I_STICK;

	//items model
	public static Model M_STICK;
	
	//items textures id
	public static int T_STICK;
	
	@Override
	public void load(Mod mod, ResourceManager manager) {

	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
		// TODO Auto-generated method stub
	}

}
