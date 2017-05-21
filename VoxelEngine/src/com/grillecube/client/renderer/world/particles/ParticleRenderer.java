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

package com.grillecube.client.renderer.world.particles;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.geometry.Cube;
import com.grillecube.client.renderer.world.RendererWorld;
import com.grillecube.client.renderer.world.ShadowCamera;
import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.World;

/** a simple cube rendering system for particles */
public class ParticleRenderer extends RendererWorld {
	/** program */
	private ProgramParticleBillboarded _program_billboarded_particle;
	private ProgramParticleCube _program_cube;

	// one array list is an array list of particles (one list for each sprite)
	private ArrayList<ParticleBillboarded> billboardedParticles;
	private ArrayList<ParticleCube> cubeParticles;

	/** cube and quads vaos */
	private GLVertexArray _vao_cube;
	// the vbo which contains cube model data
	private GLVertexBuffer _vbo_cube;
	// the vbo which contains every cubes instances informations
	private GLVertexBuffer _vbo_cube_instances;
	private int _cubes_in_buffer;

	// transf matrix + color + health
	public static final int FLOATS_PER_CUBE_INSTANCE = 16 + 4 + 1;

	// TODO: increase it maybe
	private static final int MAX_CUBE_PARTICLES = 100000;

	private Comparator<ParticleBillboarded> _particle_comparator = new Comparator<ParticleBillboarded>() {
		@Override
		public int compare(ParticleBillboarded a, ParticleBillboarded b) {
			if (a.getCameraSquareDistance() < b.getCameraSquareDistance()) {
				return (1);
			} else if (a.getCameraSquareDistance() > b.getCameraSquareDistance()) {
				return (-1);
			}
			return (0);
		}

	};

	public ParticleRenderer(MainRenderer main_renderer) {
		super(main_renderer);
	}

	@Override
	public void initialize() {

		this._cubes_in_buffer = 0;

		this._program_billboarded_particle = new ProgramParticleBillboarded();
		this._program_cube = new ProgramParticleCube();

		this.billboardedParticles = new ArrayList<ParticleBillboarded>();
		this.cubeParticles = new ArrayList<ParticleCube>();

		this.initializeVAO();
	}

	/** create a new cube object */
	private void initializeVAO() {
		this._vao_cube = GLH.glhGenVAO();
		this._vbo_cube = GLH.glhGenVBO();

		this._vao_cube.bind();

		this._vbo_cube.bind(GL15.GL_ARRAY_BUFFER);
		this._vbo_cube.bufferData(GL15.GL_ARRAY_BUFFER, Cube.makeWithTrianglesAndFaces(1), GL15.GL_STATIC_DRAW);
		this._vao_cube.setAttribute(this._vbo_cube, 0, 4, GL11.GL_FLOAT, false, 4 * 4, 0);

		this._vbo_cube_instances = GLH.glhGenVBO();
		this._vbo_cube_instances.bind(GL15.GL_ARRAY_BUFFER);
		this._vbo_cube_instances.bufferSize(GL15.GL_ARRAY_BUFFER, 0, GL15.GL_STREAM_DRAW);
		this._vao_cube.setAttributeInstanced(1, 4, GL11.GL_FLOAT, false, FLOATS_PER_CUBE_INSTANCE * 4, 0 * 4);
		this._vao_cube.setAttributeInstanced(2, 4, GL11.GL_FLOAT, false, FLOATS_PER_CUBE_INSTANCE * 4, 4 * 4);
		this._vao_cube.setAttributeInstanced(3, 4, GL11.GL_FLOAT, false, FLOATS_PER_CUBE_INSTANCE * 4, 8 * 4);
		this._vao_cube.setAttributeInstanced(4, 4, GL11.GL_FLOAT, false, FLOATS_PER_CUBE_INSTANCE * 4, 12 * 4);
		this._vao_cube.setAttributeInstanced(5, 4, GL11.GL_FLOAT, false, FLOATS_PER_CUBE_INSTANCE * 4, 16 * 4);
		this._vao_cube.setAttributeInstanced(6, 1, GL11.GL_FLOAT, false, FLOATS_PER_CUBE_INSTANCE * 4, 20 * 4);

		this._vao_cube.enableAttribute(0);
		this._vao_cube.enableAttribute(1);
		this._vao_cube.enableAttribute(2);
		this._vao_cube.enableAttribute(3);
		this._vao_cube.enableAttribute(4);
		this._vao_cube.enableAttribute(5);
		this._vao_cube.enableAttribute(6);
	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this._program_billboarded_particle);
		this._program_billboarded_particle = null;

		GLH.glhDeleteObject(this._program_cube);
		this._program_cube = null;

		GLH.glhDeleteObject(this._vao_cube);
		this._vao_cube = null;

		GLH.glhDeleteObject(this._vbo_cube);
		this._vbo_cube = null;

		GLH.glhDeleteObject(this._vbo_cube_instances);
		this._vbo_cube_instances = null;

		this.billboardedParticles = null;
		this.cubeParticles = null;
	}

	@Override
	public void onWorldSet(World world) {
		this.removeAllParticles();
	}

	@Override
	public void onWorldUnset(World world) {
		this.removeAllParticles();
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks, World world,
			CameraProjectiveWorld camera) { // TODO move make this support
											// concurrency
		// tasks.add(new Callable<Taskable>() {
		// @Override
		// public Taskable call() throws Exception {
		// // TODO: split update in multiple tasks
		// updateParticles(world, camera);
		// return (ParticleRenderer.this);
		// }
		// });
	}

	@Override
	public void preRender() {
		this.updateParticles(this.getWorld(), this.getCamera());
		this.updateVBO(this.getCamera());
	}

	private void clearParticles() {
		this.billboardedParticles.clear();
		this.cubeParticles.clear();
	}

	private void updateParticles(World world, CameraProjectiveWorld camera) {

		int i;

		// update billboarded particles
		i = 0;
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

	/** update the cube instances vbo data */
	private void updateVBO(CameraProjectiveWorld camera) {

		if (this.cubeParticles.size() == 0) {
			return;
		}

		// get the number of cube particle alive
		int cube_count = Maths.min(this.cubeParticles.size(), ParticleRenderer.MAX_CUBE_PARTICLES);

		if (cube_count == ParticleRenderer.MAX_CUBE_PARTICLES) {
			Logger.get().log(Logger.Level.WARNING, "Max number of cube particle reached! " + this.cubeParticles.size()
					+ "/" + ParticleRenderer.MAX_CUBE_PARTICLES);
		}

		// create a buffer to hold them all
		ByteBuffer floats = BufferUtils.createByteBuffer(cube_count * ParticleRenderer.FLOATS_PER_CUBE_INSTANCE * 4);
		this._cubes_in_buffer = 0;
		for (int i = 0; i < cube_count; i++) {
			ParticleCube particle = this.cubeParticles.get(i);

			// if not in frustum, do not render it
			if (!camera.isBoxInFrustum(particle.getPosition(), particle.getScale())) {
				continue;
			}

			Matrix4f mat = particle.getTransfMatrix();
			Vector4f color = particle.getColor();
			float health = particle.getHealthRatio();

			floats.putFloat(mat.m00);
			floats.putFloat(mat.m01);
			floats.putFloat(mat.m02);
			floats.putFloat(mat.m03);

			floats.putFloat(mat.m10);
			floats.putFloat(mat.m11);
			floats.putFloat(mat.m12);
			floats.putFloat(mat.m13);

			floats.putFloat(mat.m20);
			floats.putFloat(mat.m21);
			floats.putFloat(mat.m22);
			floats.putFloat(mat.m23);

			floats.putFloat(mat.m30);
			floats.putFloat(mat.m31);
			floats.putFloat(mat.m32);
			floats.putFloat(mat.m33);

			floats.putFloat(color.x);
			floats.putFloat(color.y);
			floats.putFloat(color.z);
			floats.putFloat(color.w);

			floats.putFloat(health);

			++this._cubes_in_buffer;
		}

		floats.flip();
		this._vbo_cube_instances.bind(GL15.GL_ARRAY_BUFFER);
		int buffersize = this._cubes_in_buffer * FLOATS_PER_CUBE_INSTANCE * 4;
		this._vbo_cube_instances.bufferDataUpdate(GL15.GL_ARRAY_BUFFER, floats, buffersize);
	}

	@Override
	public void render() {
		this.render(this.getCamera());
	}

	public void render(CameraProjectiveWorld camera) {

		World world = super.getWorld();

		this.renderCubeParticles(world, camera);
		this.renderBillboardedParticles(world, camera);
	}

	public void renderReflection(CameraProjectiveWorld camera, Vector4f clipplane) {
		this.render(camera); // TODO optimize this, clip particles
	}

	public void renderRefraction(CameraProjectiveWorld camera, Vector4f clipplane) {
		this.render(camera); // TODO optimize this, clip particles
	}

	/** render every quad particles */
	private void renderBillboardedParticles(World world, CameraProjectiveWorld camera) {
		if (this.billboardedParticles.size() == 0) {
			return;
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0); // Texture unit 0

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this._program_billboarded_particle.useStart();
		this.getParent().getDefaultVAO().bind();

		this._program_billboarded_particle.loadGlobalUniforms(camera);
		this.billboardedParticles.sort(this._particle_comparator);

		int i = 0;
		while (i < this.billboardedParticles.size()) {

			ParticleBillboarded particle = this.billboardedParticles.get(i);
			float radius = Maths.max(particle.getScale().x, particle.getScale().y);
			if (particle != null && particle.getCameraSquareDistance() < camera.getSquaredRenderDistance()
					&& camera.isSphereInFrustum(particle.getPosition(), radius)) {
				this._program_billboarded_particle.loadInstanceUniforms(particle);
				this.getParent().getDefaultVAO().draw(GL11.GL_POINTS, 0, 1);
			}
			++i;
		}
	}

	/** render every cube particles */
	private void renderCubeParticles(World world, CameraProjectiveWorld camera) {
		if (this.cubeParticles.size() == 0) {
			return;
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this._program_cube.useStart();
		this._vao_cube.bind();
		this._program_cube.loadGlobalUniforms(camera);

		this._vao_cube.drawInstanced(GL11.GL_TRIANGLES, 0, 36, this._cubes_in_buffer);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void removeAllParticles() {
		this.clearParticles();
	}

	/** add a particule to the update functions */
	public void spawnParticle(ParticleBillboarded particle) {
		this.billboardedParticles.add(particle);
	}

	/** add a particule to the update functions */
	public void spawnParticle(ParticleCube particle) {
		this.cubeParticles.add(particle);
	}
}