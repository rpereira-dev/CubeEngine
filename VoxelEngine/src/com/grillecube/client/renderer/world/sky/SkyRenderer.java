/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.world.sky;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.geometry.GLGeometry;
import com.grillecube.client.renderer.geometry.Sphere;
import com.grillecube.client.renderer.world.RendererWorld;
import com.grillecube.client.renderer.world.ShadowCamera;
import com.grillecube.client.renderer.world.particles.ParticleCube;
import com.grillecube.client.renderer.world.particles.ParticleCubeBouncing;
import com.grillecube.client.renderer.world.particles.ParticleRenderer;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.Weather;
import com.grillecube.common.world.World;

public class SkyRenderer extends RendererWorld {
	private static final int SKYDOME_PRECISION = 4;
	private static final float SKYDOME_SIZE = 1.0f;

	/** program */
	private ProgramSky _sky_program;

	/** vao for icosphere */
	private GLVertexArray _vao;
	private GLVertexBuffer _vbo;

	public SkyRenderer(MainRenderer main_renderer) {
		super(main_renderer);
	}

	@Override
	public void initialize() {

		this._sky_program = new ProgramSky();

		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();

		this._vao.bind();
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		ByteBuffer floats = GLGeometry.generateSphere(SKYDOME_PRECISION, SKYDOME_SIZE);
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, floats, GL15.GL_STATIC_DRAW);
		this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, 4 * 3, 0);
		this._vao.enableAttribute(0);

	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this._sky_program);
		this._sky_program = null;

		GLH.glhDeleteObject(this._vao);
		this._vao = null;

		GLH.glhDeleteObject(this._vbo);
		this._vbo = null;
	}

	@Override
	public void onWorldSet(World world) {
	}

	@Override
	public void onWorldUnset(World world) {
	}

	@Override
	public void preRender() {
		World world = this.getWorld();

		this.ambientParticle(world);
		if (world.getWeather().isRaining()) {
			this.rainParticles(world, world.getWeather().getRainStrength());
		}
	}

	@Override
	public void render() {
		this.render(super.getCamera());
	}

	public void render(CameraProjectiveWorld camera) {
		World world = super.getWorld();

		// GL11.glEnable(GL11.GL_CULL_FACE);
		// GL11.glCullFace(GL11.GL_BACK);

		this._sky_program.useStart();
		this._sky_program.loadUniforms(world.getWeather(), camera);

		this._vao.bind();
		this._vao.draw(GL11.GL_TRIANGLES, 0, Sphere.getVertexCount(SKYDOME_PRECISION));

		this._sky_program.useStop();

		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void renderReflection(CameraProjectiveWorld camera, Vector4f clipplane) {
		// TODO : clip plane
		this.render(camera);
	}

	public void renderRefraction(CameraProjectiveWorld camera, Vector4f clipplane) {
		// TODO : cliplane
		this.render(camera);
	}

	/** testing particles system :D */
	private void ambientParticle(World world) {
		Random rng = this.getParent().getRNG();
		ParticleRenderer renderer = this.getParent().getWorldRenderer().getParticleRenderer();
		ParticleCube cube = new ParticleCube();
		Vector3f campos = this.getParent().getCamera().getPosition();

		float x = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		float y = rng.nextFloat();
		float z = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		cube.setPosition(campos.x + x * 16, campos.y + y * 16, campos.z + z * 16);
		float size = rng.nextFloat() * 0.1f;
		cube.setScale(size, size, size);
		cube.setHealth(120);
		Vector3f color = world.getWeather().getFogColor();
		cube.setColor(color.x, color.y, color.z, 0.5f);

		float velx = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		float vely = -rng.nextFloat();
		float velz = (rng.nextInt(2) == 0) ? -rng.nextFloat() : rng.nextFloat();
		cube.setPositionVel(velx / 32.0f, vely / 32.0f, velz / 32.0f);
		renderer.spawnParticle(cube);
	}

	/** testing particles system :D */
	private void rainParticles(World world, int strength) {
		float yfactor = strength / (3 * Weather.MID_RAIN_STRENGTH);
		for (int i = 0; i < strength; i++) {
			Random rng = this.getParent().getRNG();
			ParticleRenderer renderer = this.getParent().getWorldRenderer().getParticleRenderer();
			ParticleCubeBouncing cube = new ParticleCubeBouncing();
			Vector3f campos = this.getParent().getCamera().getPosition();

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
			renderer.spawnParticle(cube);
		}
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks, World world,
			CameraProjectiveWorld camera) {

	}
}