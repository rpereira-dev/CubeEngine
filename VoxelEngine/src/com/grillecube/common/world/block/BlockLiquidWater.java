package com.grillecube.common.world.block;

import com.grillecube.common.world.terrain.Terrain;

public class BlockLiquidWater extends BlockLiquid {

	public BlockLiquidWater(int blockID) {
		super(blockID);
	}

	@Override
	public String getName() {
		return ("Liquid water");
	}

	@Override
	public boolean isOpaque() {
		return (false);
	}

	@Override
	public void update(Terrain terrain, int x, int y, int z) {
		// if (terrain.getWorld().getRNG().nextInt(128) == 0) {
		// this.spawnWaterParticles(terrain, x, y, z, 1);
		// }
	}

	@Override
	public void onSet(Terrain terrain, int x, int y, int z) {
		// this.spawnWaterParticles(terrain, x, y, z, 8);
	}

	// private void spawnWaterParticles(Terrain terrain, int x, int y, int z,
	// int count) {
	//
	//
	// float px = terrain.getLocation().toWorldLocation().x + x;
	// float py = terrain.getLocation().toWorldLocation().y + y;
	// float pz = terrain.getLocation().toWorldLocation().z + z;
	//
	// Random rng = terrain.getWorld().getRNG();
	// ParticleRenderer renderer =
	// VoxelEngineClient.instance().getRenderer().getWorldRenderer().getParticleRenderer();
	// ParticleCubeBouncing cube = new ParticleCubeBouncing();
	//
	// for (int i = 0 ; i < count ; i++) {
	//
	// float rx = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
	// float ry = rng.nextFloat();
	// float rz = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
	// cube.setPosition(px + rx, py + ry, pz + rz);
	// cube.setScale(0.1f, 0.1f, 0.1f);
	// cube.setHealth(500);
	// cube.setColor(0.2f, 0.5f, 1.0f, 1.0f);
	//
	// float velx = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
	// float vely = rng.nextFloat();
	// float velz = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
	// cube.setPositionVel(velx / 256.0f, vely / 64.0f, velz / 256.0f);
	// renderer.spawnParticle(cube);
	// }
	//
	// }
}
