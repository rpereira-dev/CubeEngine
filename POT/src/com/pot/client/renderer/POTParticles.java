package com.pot.client.renderer;

import com.grillecube.client.event.renderer.EventPreRender;
import com.grillecube.client.renderer.particles.TextureSprite;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Side;
import com.grillecube.common.event.EventCallback;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.pot.common.ModPOT;

/** client GUI resources main class */
public class POTParticles implements IModResource {
	public static TextureSprite SPRITE_EXPLOSION;
	public static TextureSprite SPRITE_FIRE;
	public static TextureSprite SPRITE_FIRE2;
	public static TextureSprite SPRITE_FLAME;
	public static TextureSprite SPRITE_FLAMEDROP;
	public static TextureSprite SPRITE_LEAVES;
	public static TextureSprite SPRITE_MAGIC;
	public static TextureSprite SPRITE_SNOW;
	public static TextureSprite SPRITE_CLOUDS;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		if (VoxelEngine.instance().getSide().equals(Side.CLIENT)) {
			manager.getEventManager().registerEventCallback(new PostRenderCallback());

			SPRITE_EXPLOSION = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/explosion.png"), 5, 5);
			SPRITE_FIRE = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/fire.png"), 2, 2);
			SPRITE_FIRE2 = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/fire2.png"), 4, 4);
			SPRITE_FLAME = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/flame.png"), 8, 4);
			SPRITE_FLAMEDROP = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/flamedrop.png"), 5, 2);
			SPRITE_LEAVES = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/leaves.png"), 1, 1);
			SPRITE_MAGIC = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/magic.png"), 2, 2);
			SPRITE_SNOW = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/snow.png"), 2, 2);
			SPRITE_CLOUDS = new TextureSprite(R.getResPath(ModPOT.MOD_ID, "textures/particles/clouds.png"), 2, 2);
		}
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}

class PostRenderCallback extends EventCallback<EventPreRender> {
	private static Vector3f pos = new Vector3f();

	@Override
	public void invoke(EventPreRender event) {
		//
		if (true) {
			return;
		}

		// ParticleRenderer renderer =
		// event.getRenderer().getWorldRenderer().getParticleRenderer();
		//
		// Random rng = event.getRenderer().getRNG();
		//
		// if (event.getGLFWWindow().isKeyPressed(GLFW.GLFW_KEY_E)) {
		// pos.set(event.getRenderer().getCamera().getPosition());
		// }
		//
		// if (rng.nextInt(4) != 3) {
		// return;
		// }
		//
		// ParticleBillboarded p = new ParticleBillboarded(200,
		// POTParticles.SPRITE_EXPLOSION, false);
		// p.setPositionVel(rng.nextInt(2) == 0 ? -rng.nextFloat() / 16 :
		// rng.nextFloat() / 16,
		// rng.nextInt(2) == 0 ? -rng.nextFloat() / 16 : rng.nextFloat() / 16,
		// rng.nextInt(2) == 0 ? -rng.nextFloat() / 16 : rng.nextFloat() / 162);
		// p.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		// p.setPosition(pos.x, pos.y, pos.z);
		// renderer.spawnParticle(p);
	}

}
