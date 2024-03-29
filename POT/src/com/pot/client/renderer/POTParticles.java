package com.pot.client.renderer;

import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.event.renderer.EventPreWorldRender;
import com.grillecube.client.renderer.particles.ParticleBillboarded;
import com.grillecube.client.renderer.particles.ParticleRendererFactory;
import com.grillecube.client.renderer.particles.TextureSprite;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Side;
import com.grillecube.common.event.Listener;
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
			manager.getEventManager().addListener(new PostRenderCallback());

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

class PostRenderCallback extends Listener<EventPreWorldRender> {
	private static Vector3f pos = new Vector3f();

	@Override
	public void pre(EventPreWorldRender event) {
	}

	@Override
	public void post(EventPreWorldRender event) {
		if (true) {
			return;
		}
		ParticleRendererFactory renderer = event.getWorldRenderer().getParticleRendererFactory();
		Random rng = event.getRenderer().getRNG();

		if (event.getGLFWWindow().isKeyPressed(GLFW.GLFW_KEY_E)) {
			pos.set(event.getWorldRenderer().getCamera().getPosition());
		}

		ParticleBillboarded p = new ParticleBillboarded(400, POTParticles.SPRITE_MAGIC, true);
		p.setPositionVel(rng.nextInt(2) == 0 ? -rng.nextFloat() * 4 : rng.nextFloat() * 4,
				rng.nextInt(2) == 0 ? -rng.nextFloat() * 4 : rng.nextFloat() * 4,
				rng.nextInt(2) == 0 ? -rng.nextFloat() * 4 : rng.nextFloat() * 4);
		// p.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		p.setColor(rng.nextFloat(), rng.nextFloat(), rng.nextFloat(), 1.0f);
		// p.setColor(0.8f, 0.5f, 0.2f, 1.0f);
		p.setPosition(pos.x, pos.y, pos.z);

		renderer.spawnParticle(p);
	}

}
