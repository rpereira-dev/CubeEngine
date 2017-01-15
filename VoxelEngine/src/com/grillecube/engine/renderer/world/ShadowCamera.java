package com.grillecube.engine.renderer.world;

import org.lwjgl.glfw.GLFW;

import com.grillecube.engine.VoxelEngineClient;
import com.grillecube.engine.maths.Maths;
import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.renderer.camera.CameraOrthographicWorld;
import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.renderer.world.lines.Line;
import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.World;

public class ShadowCamera extends CameraOrthographicWorld {

	private Vector3f lightdir;
	private int pcf_count;

	public ShadowCamera(GLFWWindow window) {
		super(window);
		this.pcf_count = 1;
		this.lightdir = new Vector3f(1, 1, 1);
		this.setRenderDistance(Terrain.SIZE_DIAGONAL * 3);
		this.setFar(this.getSquaredRenderDistance());
	}

	public void update(World world, CameraProjectiveWorld camera) {

		this.updateLightDir(world, camera);
		this.updatePosition(world, camera);
		this.updateRotation(world, camera);

		//TODO
//		Line line = new Line(this.getPosition(), new Vector4f(1, 0, 0, 1),
//				Vector3f.add(this.getPosition(), this.lightdir, null), new Vector4f(1, 0, 0, 1));
//		VoxelEngineClient.instance().getRenderer().getWorldRenderer().getLineRenderer().addLine(line);

		super.update();

	}

	private void updateRotation(World world, CameraProjectiveWorld camera) {
		float length = Maths.sqrt(this.lightdir.x * this.lightdir.x + this.lightdir.z * this.lightdir.z);
		float pitch = (float) Math.acos(length);
		float yaw = ((float) Math.atan(this.lightdir.x / this.lightdir.z));
		if (this.lightdir.z > 0) {
			yaw = (float) (yaw - Math.PI);
		}
		super.setPitch((float) Math.toDegrees(pitch));
		super.setYaw((float) Math.toDegrees(yaw));
	}

	private void updatePosition(World world, CameraProjectiveWorld camera) {
		float x = camera.getPosition().x + this.lightdir.x * this.getRenderDistance();
		float y = camera.getPosition().y + this.lightdir.y * this.getRenderDistance();
		float z = camera.getPosition().z + this.lightdir.z * this.getRenderDistance();
		super.setPosition(x, y, z);
		// Logger.get().log(Logger.Level.DEBUG, super.getPosition(),
		// camera.getPosition());
	}

	private void updateLightDir(World world, CameraProjectiveWorld camera) {
		if (this.getWindow().isKeyPressed(GLFW.GLFW_KEY_N)) {
			this.lightdir.set(camera.getViewVector());
		}

		this.lightdir.set(0.7f, 0, 0.7f);
		this.lightdir.normalise();
	}

	/** to shadow space offset */
	private static Matrix4f shadow_matrix_offset;
	static {
		shadow_matrix_offset = new Matrix4f();
		shadow_matrix_offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		shadow_matrix_offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
	}

	public Matrix4f getToShadowSpaceMatrix(Matrix4f dst) {
		if (dst == null) {
			dst = new Matrix4f();
		} else {
			dst.setIdentity();
		}
		return (Matrix4f.mul(shadow_matrix_offset, this.getMVPMatrix(), dst));
	}

	public int getShadowPcfCount() {
		return (this.pcf_count);
	}

	public void setPcfCount(int pcf) {
		this.pcf_count = pcf;
	}
}
