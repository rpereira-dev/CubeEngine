package com.grillecube.client.renderer.particles;

import java.util.ArrayList;
import java.util.Random;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.RendererFactory;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Vector3f;

public class ParticleRendererFactory extends RendererFactory {

	// one array list is an array list of particles (one list for each sprite)
	private final ArrayList<ParticleBillboarded> billboardedParticles;
	private final ArrayList<ParticleCube> cubeParticles;
	private CameraProjective camera;

	public ParticleRendererFactory(MainRenderer mainRenderer) {
		super(mainRenderer);
		this.billboardedParticles = new ArrayList<ParticleBillboarded>();
		this.cubeParticles = new ArrayList<ParticleCube>();
	}

	public final CameraProjective getCamera() {
		return (this.camera);
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
	}

	@Override
	public void update(double dt) {

//		this.rainParticles(64);

		CameraProjective camera = this.getCamera();

		// update billboarded particles
		int i = 0;
		while (i < this.billboardedParticles.size()) {
			ParticleBillboarded particle = this.billboardedParticles.get(i);
			if (particle == null || particle.isDead()) {
				this.billboardedParticles.remove(i);
				continue;
			}
			particle.update(dt);
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
			particle.update(dt);
			++i;
		}
	}

	/** testing particles system :D */
	private void ambientParticle() {
		Random rng = new Random();
		ParticleCube cube = new ParticleCube();
		Vector3f campos = this.getCamera().getPosition();

		float x = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		float y = rng.nextFloat();
		float z = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		cube.setPosition(campos.x + x * 16, campos.y + y * 16, campos.z + z * 16);
		float size = rng.nextFloat() * 0.1f;
		cube.setSize(size, size, size);
		cube.setHealth(120);
		Vector3f color = new Vector3f(0.8f, 0.8f, 0.8f);
		cube.setColor(color.x, color.y, color.z, 0.5f);

		float velx = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		float vely = -rng.nextFloat();
		float velz = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		cube.setPositionVel(velx, vely, velz);
		this.spawnParticle(cube);
	}

	/** testing particles system :D */
	private void rainParticles(int strength) {
		Random rng = new Random();
		Vector3f campos = this.getCamera().getPosition();

		float yfactor = strength * 0.5f;

		for (int i = 0; i < strength; i++) {

			ParticleCube cube = new ParticleCube();

			float x = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
			float y = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
			float z = rng.nextFloat();
			cube.setPosition(campos.x + x * 16, campos.y + y * 16, campos.z + z * 16);
			float size = 0.05f;
			cube.setSize(size, size, size);
			cube.setHealth(120);
			cube.setRotationVelocity(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
			Vector3f color = new Vector3f(0.8f, 0.8f, 0.8f);
			cube.setColor(color.x, color.y, color.z, 0.5f);
			cube.setColor(0, 0.2f, 0.9f, 0.5f);
			cube.setPositionAccelerationZ(-1.0f * yfactor);

			// rng.nextFloat();
			cube.setPositionVel(0.0f, 0.0f, 0.0f);
			this.spawnParticle(cube);
		}
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

	public final ArrayList<ParticleBillboarded> getBillboadedParticles() {
		return (this.billboardedParticles);
	}

	public final ArrayList<ParticleCube> getCubeParticles() {
		return (this.cubeParticles);
	}

	@Override
	public final void render() {
		ParticleRenderer particleRenderer = this.getMainRenderer().getParticleRenderer();
		particleRenderer.renderBillboardedParticles(this.getCamera(), this.getBillboadedParticles());
		particleRenderer.renderCubeParticles(this.getCamera(), this.getCubeParticles());
	}

}
