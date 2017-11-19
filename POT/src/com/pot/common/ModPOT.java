package com.pot.common;

import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.mod.ModInfo;
import com.grillecube.common.resources.AssetsPack;
import com.pot.common.world.POTWorlds;
import com.pot.common.world.blocks.POTBlocks;
import com.pot.common.world.entity.POTEntities;
import com.pot.common.world.item.POTItems;

@ModInfo(name = "Default Common 'People of Toss' mod", author = "toss-dev", version = "1.0.0.a", clientProxy = "com.pot.client.ModPOTClient")
public class ModPOT implements IMod {

	public static final String MOD_ID = "POT";

	@Override
	public void initialize(Mod mod) {
		mod.addResource(new POTBlocks());
		mod.addResource(new POTItems());
		mod.addResource(new POTEntities());
		mod.addResource(new POTWorlds());
	}

	@Override
	public void deinitialize(Mod mod) {
	}
}
