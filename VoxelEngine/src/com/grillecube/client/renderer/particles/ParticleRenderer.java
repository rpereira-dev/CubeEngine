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

package com.grillecube.client.renderer.particles;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

/** a simple cube rendering system for particles */
public class ParticleRenderer extends Renderer {
	/** program */
	private ProgramParticleBillboarded programBillboardedParticles;
	private ProgramParticleCube programCube;

	/** cube and quads vaos */
	private CubeMesh cubeMesh;

	// the vbo which contains every cubes instances informations
	private GLVertexBuffer cubeInstancesVBO;

	private static final int MAX_CUBE_PARTICLES = 100000;

	public ParticleRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
	}

	@Override
	public void initialize() {

		this.programBillboardedParticles = new ProgramParticleBillboarded();
		this.programCube = new ProgramParticleCube();

		// vao init
		this.cubeMesh = new CubeMesh();
		this.cubeMesh.initialize();

		this.cubeMesh.bind();
		this.cubeInstancesVBO = GLH.glhGenVBO();
		this.cubeInstancesVBO.bind(GL15.GL_ARRAY_BUFFER);
		this.cubeInstancesVBO.bufferSize(GL15.GL_ARRAY_BUFFER, 0, GL15.GL_STREAM_DRAW);
		this.cubeMesh.setAttributesInstanced();
	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this.programBillboardedParticles);
		this.programBillboardedParticles = null;

		GLH.glhDeleteObject(this.programCube);
		this.programCube = null;

		this.cubeMesh.deinitialize();

		GLH.glhDeleteObject(this.cubeInstancesVBO);
		this.cubeInstancesVBO = null;
	}

	/**
	 * render every given billoaded particles with the given camera (billboarded
	 * = textured quad facing the camera)
	 */
	public final void renderBillboardedParticles(CameraProjective camera, ArrayList<ParticleBillboarded> particles) {
		if (particles.size() == 0) {
			return;
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0); // Texture unit 0

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this.programBillboardedParticles.useStart();
		this.getMainRenderer().getDefaultVAO().bind();

		this.programBillboardedParticles.loadGlobalUniforms(camera);

		HashMap<ParticleBillboarded, Double> distances = new HashMap<ParticleBillboarded, Double>(particles.size() * 4);
		for (ParticleBillboarded particle : particles) {
			distances.put(particle, Vector3f.distanceSquare(particle.getPosition(), camera.getPosition()));
		}
		particles.sort(new Comparator<ParticleBillboarded>() {
			@Override
			public int compare(ParticleBillboarded a, ParticleBillboarded b) {
				double da = distances.get(a);
				double db = distances.get(b);
				if (da < db) {
					return (1);
				} else if (da > db) {
					return (-1);
				}
				return (0);
			}
		});

		int i = 0;
		while (i < particles.size()) {
			ParticleBillboarded particle = particles.get(i);
			float radius = Maths.max(Maths.max(particle.getSizeX(), particle.getSizeY()), particle.getSizeZ());
			if (particle != null && distances.get(particle) < camera.getSquaredRenderDistance()
					&& camera.isSphereInFrustum(particle.getPosition(), radius)) {
				this.programBillboardedParticles.loadInstanceUniforms(particle);
				this.getMainRenderer().getDefaultVAO().draw(GL11.GL_POINTS, 0, 1);
			}
			++i;
		}
	}

	/** render every cube particles */
	public final void renderCubeParticles(CameraProjective camera, ArrayList<ParticleCube> particles) {
		if (particles.size() == 0) {
			return;
		}

		// get the number of cube particle alive
		int cube_count = Maths.min(particles.size(), ParticleRenderer.MAX_CUBE_PARTICLES);

		if (cube_count == ParticleRenderer.MAX_CUBE_PARTICLES) {
			Logger.get().log(Logger.Level.WARNING, "Max number of cube particle reached! " + particles.size() + "/"
					+ ParticleRenderer.MAX_CUBE_PARTICLES);
		}

		// create a buffer to hold them all
		ByteBuffer floats = BufferUtils.createByteBuffer(cube_count * CubeMesh.FLOATS_PER_CUBE_INSTANCE * 4);
		int cubesInBuffer = 0;
		for (int i = 0; i < cube_count; i++) {
			ParticleCube particle = particles.get(i);

			// if not in frustum, do not render it
			if (!camera.isBoxInFrustum(particle, particle)) {
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

			++cubesInBuffer;
		}
		floats.flip();
		this.cubeInstancesVBO.bind(GL15.GL_ARRAY_BUFFER);
		int buffersize = cubesInBuffer * CubeMesh.FLOATS_PER_CUBE_INSTANCE * 4;
		this.cubeInstancesVBO.bufferDataUpdate(GL15.GL_ARRAY_BUFFER, floats, buffersize);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this.programCube.useStart();
		this.programCube.loadGlobalUniforms(camera);

		this.cubeMesh.bind();
		this.cubeMesh.drawInstanced(cubesInBuffer);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
	}
}