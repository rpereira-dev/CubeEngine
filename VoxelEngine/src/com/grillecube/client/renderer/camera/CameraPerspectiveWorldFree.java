
package com.grillecube.client.renderer.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.block.Blocks;

public class CameraPerspectiveWorldFree extends CameraPerspectiveWorld {

	public static final int STATE_MOVE_FORWARD = 1;
	public static final int STATE_MOVE_BACKWARD = 2;
	public static final int STATE_MOVE_LEFT = 4;
	public static final int STATE_MOVE_RIGHT = 8;

	public CameraPerspectiveWorldFree(GLFWWindow window) {
		super(window);
	}

	@Override
	public void update() {
		super.update();
		this.setSpeed(1.0f);
		this.updateMove();
	}

	protected void updateMove() {
		Vector3f vel = this.getPositionVelocity();
		if (this.hasState(STATE_MOVE_FORWARD)) {
			vel.setX(this.getViewVector().x);
			vel.setY(this.getViewVector().y);
			vel.setZ(this.getViewVector().z);
		} else if (this.hasState(STATE_MOVE_BACKWARD)) {
			vel.setX(-this.getViewVector().x);
			vel.setY(-this.getViewVector().y);
			vel.setZ(-this.getViewVector().z);
		} else if (this.hasState(STATE_MOVE_RIGHT)) {
			vel.setX(-this.getViewVector().z);
			vel.setY(0);
			vel.setZ(this.getViewVector().x);
		} else if (this.hasState(STATE_MOVE_LEFT)) {
			vel.setX(this.getViewVector().z);
			vel.setY(0);
			vel.setZ(-this.getViewVector().x);
		} else {
			vel.setX(0);
			vel.setY(0);
			vel.setZ(0);
		}

		this.move(vel.scale(2.0F));
	}

	@Override
	public void invokeKeyRelease(GLFWWindow window, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_W) {
			this.unsetState(STATE_MOVE_FORWARD);
		}
		if (key == GLFW.GLFW_KEY_S) {
			this.unsetState(STATE_MOVE_BACKWARD);
		}
		if (key == GLFW.GLFW_KEY_A) {
			this.unsetState(STATE_MOVE_LEFT);
		}
		if (key == GLFW.GLFW_KEY_D) {
			this.unsetState(STATE_MOVE_RIGHT);
		}
	}

	@Override
	public void invokeKeyPress(GLFWWindow window, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_W) {
			this.setState(STATE_MOVE_FORWARD);
		}
		if (key == GLFW.GLFW_KEY_A) {
			this.setState(STATE_MOVE_LEFT);
		}
		if (key == GLFW.GLFW_KEY_D) {
			this.setState(STATE_MOVE_RIGHT);
		}
		if (key == GLFW.GLFW_KEY_S) {
			this.setState(STATE_MOVE_BACKWARD);
		}
		if (key == GLFW.GLFW_KEY_R) {

			for (int x = -2; x < 2; x++) {
				for (int y = 4; y < 8; y++) {
					for (int z = -2; z < 2; z++) {
						super.setBlock(Blocks.LIQUID_WATER,
								Vector3f.add(this.getLookCoords(), new Vector3f(x, y, z), null));

					}
				}
			}
			//
			// super.setBlock(Blocks.PLANTS[(int) (System.currentTimeMillis() %
			// Blocks.PLANTS.length)],
			// this.getLookCoords());
			//
			// if (i++ % 4 != 0) {
//			 pos.set(this.getLookCoords());
//			 super.setBlock(Blocks.LIGHT, pos);
			// } else {
			// super.setBlock(Blocks.AIR, pos);
			// }

		}
	}

	static int i = 0;
	static Vector3f pos = new Vector3f();
	private double _prevx = 200;
	private double _prevy = 200;

	@Override
	public void invokeCursorPos(GLFWWindow window, double xpos, double ypos) {
		this.increaseYaw((float) ((xpos - _prevx) * 0.3f));
		this.increasePitch((float) ((ypos - _prevy) * 0.3f));

		_prevx = xpos;
		_prevy = ypos;
	}
}
