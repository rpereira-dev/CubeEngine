package com.grillecube.client.renderer.world.factories;

import java.util.ArrayList;
import java.util.Random;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.particles.ParticleBillboarded;
import com.grillecube.client.renderer.particles.ParticleCube;
import com.grillecube.client.renderer.particles.ParticleCubeBouncing;
import com.grillecube.client.renderer.particles.ParticleRenderer;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.World;

public class ParticleRendererFactory extends WorldRendererFactory {

	// one array list is an array list of particles (one list for each sprite)
	private ArrayList<ParticleBillboarded> billboardedParticles;
	private ArrayList<ParticleCube> cubeParticles;

	public ParticleRendererFactory(WorldRenderer worldRenderer) {
		super(worldRenderer);

		this.billboardedParticles = new ArrayList<ParticleBillboarded>();
		this.cubeParticles = new ArrayList<ParticleCube>();
	}

	@Override
	public void update() {

		World world = super.getWorld();
		CameraProjective camera = super.getCamera();

		// update billboarded particles
		int i = 0;
		while (i < this.billboardedParticles.size()) {
			ParticleBillboarded particle = this.billboardedParticles.get(i);
			if (particle == null || particle.isDead()) {
				this.billboardedParticles.remove(i);
				continue;
			}
			particle.update(world, camera);
			++i;
		}

		// update cube particles
		i = 0;
		while (i < this.cubeParticles.size()) {
			ParticleCube particle = this.cubeParticles.get(i);
			if (particle == null || particle.isDead()) {
				this.cubeParticles.remove(i);
				continue;
			}
			particle.update(world, camera);
			++i;
		}
	}

	/** testing particles system :D */
	private void ambientParticle() {
		Random rng = super.getRNG();
		ParticleCube cube = new ParticleCube();
		Vector3f campos = super.getCamera().getPosition();

		float x = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		float y = rng.nextFloat();
		float z = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		cube.setPosition(campos.x + x * 16, campos.y + y * 16, campos.z + z * 16);
		float size = rng.nextFloat() * 0.1f;
		cube.setScale(size, size, size);
		cube.setHealth(120);
		Vector3f color = super.getWorld().getWeather().getFogColor();
		cube.setColor(color.x, color.y, color.z, 0.5f);

		float velx = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		float vely = -rng.nextFloat();
		float velz = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		cube.setPositionVel(velx / 32.0f, vely / 32.0f, velz / 32.0f);
		this.spawnParticle(cube);
	}

	/** testing particles system :D */
	private void rainParticles(World world, int strength) {
		Random rng = super.getRNG();
		Vector3f campos = super.getCamera().getPosition();

		float yfactor = 0.5f;

		for (int i = 0; i < strength; i++) {

			ParticleCubeBouncing cube = new ParticleCubeBouncing();

			float x = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
			float y = rng.nextFloat();
			float z = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
			cube.setPosition(campos.x + x * 16, campos.y + y * 16, campos.z + z * 16);
			float size = 0.05f;
			cube.setScale(size, size, size);
			cube.setHealth(120);
			cube.setRotationVel(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
			Vector3f color = world.getWeather().getFogColor();
			cube.setColor(color.x, color.y, color.z, 0.5f);
			cube.setColor(0, 0.2f, 0.9f, 0.5f);

			float velx = 0;// (rng.nextInt(2) == 0) ? -rng.nextFloat() :
			// rng.nextFloat();
			float vely = -rng.nextFloat() * yfactor;
			float velz = 0;// (rng.nextInt(2) == 0) ? -rng.nextFloat() :
			// rng.nextFloat();
			cube.setPositionVel(velx, vely, velz);
			this.spawnParticle(cube);
		}
	}

	@Override
	public void render() {
		ParticleRenderer renderer = super.getMainRenderer().getParticleRenderer();
		CameraProjective camera = super.getCamera();
		renderer.renderBillboardedParticles(camera, this.billboardedParticles);
		renderer.renderCubeParticles(camera, this.cubeParticles);
	}

	/** add a particule to the update functions */
	public final void spawnParticle(ParticleBillboarded particle) {
		this.billboardedParticles.add(particle);
	}

	/** add a particule to the update functions */
	public final void spawnParticle(ParticleCube particle) {
		this.cubeParticles.add(particle);
	}

	public final void removeAllParticles() {
		this.cubeParticles.clear();
		this.billboardedParticles.clear();
		// this.cubeParticles.trimToSize();
		// this.billboardedParticles.trimToSize();

	}

}
