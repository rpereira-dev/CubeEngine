package com.pot.common;

import com.grillecube.engine.mod.IMod;
import com.grillecube.engine.mod.Mod;
import com.grillecube.engine.mod.ModInfo;
import com.grillecube.engine.resources.AssetsPack;
import com.pot.client.renderer.POTParticles;
import com.pot.common.world.POTWorlds;
import com.pot.common.world.blocks.POTBlocks;
import com.pot.common.world.entity.POTEntities;
import com.pot.common.world.item.POTItems;

/** the POT is codded as a mod of the engine */
@ModInfo(name = "Default Common 'People of Toss' mod", author = "toss-dev", version = "1.0.0.a")
public class ModCommon implements IMod {

	public static final String MOD_ID = "POT";

	@Override
	public void initialize(Mod mod) {
		mod.addRessource(new AssetsPack(ModCommon.MOD_ID, "./assets_pot.zip"));
		mod.addRessource(new POTBlocks());
		mod.addRessource(new POTItems());
		mod.addRessource(new POTEntities());
		mod.addRessource(new POTParticles());
		mod.addRessource(new POTWorlds());
	}

	@Override
	public void deinitialize(Mod mod) {
	}

}
