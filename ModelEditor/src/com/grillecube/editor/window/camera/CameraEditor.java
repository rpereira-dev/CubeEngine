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

package com.grillecube.editor.window.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.editor.ModelEditor;
import com.grillecube.engine.Logger;
import com.grillecube.engine.maths.Maths;
import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector3i;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.opengl.GLFWListenerMousePress;
import com.grillecube.engine.opengl.GLFWListenerMouseRelease;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.ModelSkin;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.engine.renderer.world.lines.Line;
import com.grillecube.engine.renderer.world.lines.LineRenderer;
import com.grillecube.engine.world.Terrain;

public class CameraEditor extends CameraPerspectiveWorldCentered
		implements GLFWListenerMousePress, GLFWListenerMouseRelease {

	/** the bounding box lines renderer */
	private Line[] _lines = new Line[12];

	/** the two block selected */
	private Vector3i _block_one;
	private Vector3i _block_two;
	private int _y_selected;

	private CameraTools _tools;

	/** model editor instance */
	private ModelEditor _editor;

	private static final int STATE_ACTION = 1;
	private static final int STATE_REPEAT_ACTION = 2;
	private static final int STATE_SELECTING = 4;

	public CameraEditor(ModelEditor editor) {
		super(editor.getEngine().getGLFWWindow());
		this._editor = editor;
		this.getWindow().addMousePressListener(this);
		this.getWindow().addMouseReleaseListener(this);
		this.setCenter(0, 0, 0);

		super.setPosition(0.5f, 1, 2);
		super.setPositionVelocity(0, 0, 0);
		super.setRotationVelocity(0, 0, 0);
		super.setPitch(0);
		super.setYaw((float) Math.PI);
		super.setRoll(0);
		super.setSpeed(0.2f);
		super.setRotSpeed(1);
		super.setFarDistance(Float.MAX_VALUE);
		super.setRenderDistance(Float.MAX_VALUE);

		this._tools = new CameraTools(this);

		super.setDistanceFromCenter(16);
		super.setAngleAroundCenter(0);

		this._block_one = new Vector3i();
		this._block_two = new Vector3i();

		for (int i = 0; i < this._lines.length; i++) {
			this._lines[i] = new Line(new Vector3f(), this.getTool().getColor(), new Vector3f(),
					this.getTool().getColor());
		}
	}

	public ModelEditor getEditor() {
		return (this._editor);
	}

	@Override
	public void update() {
		super.update();
		this.updateLookBox();
	}

	@Override
	public void invokeKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {

		if (key == GLFW.GLFW_KEY_E) {
			this.unsetState(CameraEditor.STATE_ACTION);
		} else if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
			this.unsetState(CameraEditor.STATE_REPEAT_ACTION);
		}
	}

	public ModelPartBuilder getModelPart() {
		return (this._editor.getCurrentModelPart());
	}

	public ModelSkin getModelSkin() {
		return (this._editor.getCurrentModelSkin());
	}

	public ModelPartSkinBuilder getModelPartSkin() {
		return (this._editor.getCurrentModelPartSkin());
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_E) {
			this.setState(CameraEditor.STATE_ACTION);
			this._tools.doAction();
		} else if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
			this.setState(CameraEditor.STATE_REPEAT_ACTION);
		} else if (key == GLFW.GLFW_KEY_W && glfwWindow.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			this._tools.undoAction();
		}
	}

	/** modified raycasting function */
	private void raycast() {

		// if no model is set, return
		Model model = this.getModel();

		if (model == null) {
			return;
		}

		ModelPartBuilder part = this.getModelPart();
		if (part == null) {
			return;
		}

		// the position and the direction in world referential
		Vector4f origin = new Vector4f(this.getPosition(), Terrain.BLOCK_SIZE);
		Vector4f dir = new Vector4f(this.getPicker().getRay(), Terrain.BLOCK_SIZE);

		// rot matrix
		Matrix4f rotmat = Matrix4f.createTransformationMatrix(null, null, model.getAxis(), null);

		// transform ray casting
		Matrix4f.transform(rotmat, origin, origin);
		Matrix4f.transform(rotmat, dir, dir);

		// if the direction is 0, return (avoid infinite loop)
		if (dir.lengthSquared() < 0.001f) {
			return;
		}

		// the direction of the ray cast
		float bux = part.getBlockScale().x;
		float buy = part.getBlockScale().y;
		float buz = part.getBlockScale().z;

		float ibux = 1 / bux;
		float ibuy = 1 / buy;
		float ibuz = 1 / buz;

		float length = dir.length();
		float dirx = dir.x / length;
		float diry = dir.y / length;
		float dirz = dir.z / length;

		float stepX = Maths.sign(dirx) * bux;
		float stepY = Maths.sign(diry) * buy;
		float stepZ = Maths.sign(dirz) * buz;

		float maxX = Maths.intbound(origin.x, dirx);
		float maxY = Maths.intbound(origin.y, diry);
		float maxZ = Maths.intbound(origin.z, dirz);

		float dx = stepX / dirx;
		float dy = stepY / diry;
		float dz = stepZ / dirz;

		// transf matrix: from world to model referential

		// the radius to casts
		float range = 512.0f;
		Vector4f r = new Vector4f(range * ibux, range * ibuy, range * ibuz, 1);
		float x = Maths.floor(origin.x * ibux);
		float y = Maths.floor(origin.y * ibuy);
		float z = Maths.floor(origin.z * ibuz);

		// float x = (float) Math.floor(origin.x);
		// float y = (float) Math.floor(origin.y);
		// float z = (float) Math.floor(origin.z);

		// the block face
		Vector3f face = new Vector3f();

		while (true) {

			int bx = (int) x;
			int by = (int) y;
			int bz = (int) z;

			// if in entity building bounds
			if (bx >= ModelPartBuilder.MIN_X && by >= ModelPartBuilder.MIN_Y && bz >= ModelPartBuilder.MIN_Z
					&& bx < ModelPartBuilder.MAX_X && by < ModelPartBuilder.MAX_Y && bz < ModelPartBuilder.MAX_Z) {

				// if hitted a block
				if (this.getModelPart().isBlockSet(bx, by, bz)
						|| by < Maths.floor(this.getModel().getOrigin().getY())) {

					// hit block

					// update selection depending on tools
					bx = this._tools.adjustXCoordinate(bx, face);
					by = this._tools.adjustYCoordinate(by, face);
					bz = this._tools.adjustZCoordinate(bz, face);

					// update looked coords (world relatively)
					this.getLookCoords().set(bx * bux, by * buy, bz * buz);
					// update looked face
					this.getLookFace().set(face);

					// update selection
					if (this.getWindow().isMouseLeftPressed()) {
						this._block_two.set(bx, by + this._y_selected, bz);
					} else {
						// set looked block
						this._block_one.set(bx, by, bz);
						this._block_two.set(bx, by + this._y_selected, bz);
					}

					// update the box
					this.updateLookBox();
					break;
				}

			}

			if (maxX < maxY) {
				if (maxX < maxZ) {
					if (Maths.abs(maxX) > r.x)
						break;
					x += stepX;
					maxX += dx;
					face.x = -stepX;
					face.y = 0;
					face.z = 0;
				} else {
					if (Maths.abs(maxZ) > r.z)
						break;
					z += stepZ;
					maxZ += dz;
					face.x = 0;
					face.y = 0;
					face.z = -stepZ;
				}
			} else {
				if (maxY < maxZ) {
					if (Maths.abs(maxY) > r.y)
						break;
					y += stepY;
					maxY += dy;
					face.x = 0;
					face.y = -stepY;
					face.z = 0;
				} else {
					if (Maths.abs(maxZ) > r.z)
						break;
					z += stepZ;
					maxZ += dz;
					face.x = 0;
					face.y = 0;
					face.z = -stepZ;
				}
			}
		}
	}

	private void updateLookBox() {

		Model model = this.getModel();
		ModelPartBuilder part = this.getModelPart();

		if (model == null || part == null) {
			return;
		}

		// create rotation matrix
		Matrix4f transfmatrix = Matrix4f.createTransformationMatrix(null, null, model.getAxis(), null);

		// coordinates of the two selected blocks relatively to the model
		Vector4f one = new Vector4f(this._block_one.x, this._block_one.y, this._block_one.z, 1.0f);
		Vector4f two = new Vector4f(this._block_two.x, this._block_two.y, this._block_two.z, 1.0f);

		Matrix4f.transform(transfmatrix, one, one);
		Matrix4f.transform(transfmatrix, two, two);

		// Logger.get().log(Logger.Level.DEBUG, anglex, angley, anglez);
		// Logger.get().log(Logger.Level.DEBUG, this._block_one, one);
		// Logger.get().log(Logger.Level.DEBUG, this._block_two, two);
		// Logger.get().log(Logger.Level.DEBUG, "");

		// block unit

		float bux = part.getBlockScale().x;
		float buy = part.getBlockScale().y;
		float buz = part.getBlockScale().z;

		float mx = Maths.min(one.x, two.x) * bux;
		float my = Maths.min(one.y, two.y) * buy;
		float mz = Maths.min(one.z, two.z) * buz;
		float Mx = (Maths.max(one.x, two.x) + 1.0f) * bux;
		float My = (Maths.max(one.y, two.y) + 1.0f) * buy;
		float Mz = (Maths.max(one.z, two.z) + 1.0f) * buz;

		float sx = Mx - mx;
		float sy = My - my;
		float sz = Mz - mz;

		// bounding box corners
		Vector4f[] corners = new Vector4f[8];
		corners[0] = new Vector4f(mx, my, mz, 1);
		corners[1] = new Vector4f(mx + sx, my, mz, 1);
		corners[2] = new Vector4f(mx + sx, my, mz + sz, 1);
		corners[3] = new Vector4f(mx, my, mz + sz, 1);
		corners[4] = new Vector4f(mx, my + sy, mz, 1);
		corners[5] = new Vector4f(mx + sx, my + sy, mz, 1);
		corners[6] = new Vector4f(mx + sx, my + sy, mz + sz, 1);
		corners[7] = new Vector4f(mx, my + sy, mz + sz, 1);

		// set lines one by one
		// base lower
		this.setLine(0, corners, 0, 1);
		this.setLine(1, corners, 1, 2);
		this.setLine(2, corners, 2, 3);
		this.setLine(3, corners, 3, 0);

		// base upper
		this.setLine(4, corners, 4, 5);
		this.setLine(5, corners, 5, 6);
		this.setLine(6, corners, 6, 7);
		this.setLine(7, corners, 7, 4);

		// angles
		this.setLine(8, corners, 0, 4);
		this.setLine(9, corners, 1, 5);
		this.setLine(10, corners, 2, 6);
		this.setLine(11, corners, 3, 7);

		// add lines
		LineRenderer renderer = this._editor.getEngine().getRenderer().getWorldRenderer().getLineRenderer();
		for (int i = 0; i < this._lines.length; i++) {
			renderer.addLine(this._lines[i]);
		}
	}

	private void setLine(int index, Vector4f[] corners, int aindex, int bindex) {
		this._lines[index].posa.set(corners[aindex]);
		this._lines[index].posb.set(corners[bindex]);
	}

	@Override
	public void invokeCursorPos(GLFWWindow window, double xpos, double ypos) {

		// rotate
		if (window.isMouseRightPressed()) {

			float pitch = (float) ((window.getMouseDY()) * 0.1f);
			this.increasePitch(pitch);

			float angle = (float) ((window.getMouseDX()) * 0.3f);
			this.increaseAngleAroundCenter(-angle);
		} else {
			this.raycast();
		}
	}

	@Override
	public void invokeMouseRelease(GLFWWindow window, int button, int mods) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			this.getWindow().setCursor(true);
			this.getWindow().setCursorCenter();
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.unsetState(CameraEditor.STATE_SELECTING);
			this._y_selected = 0;
		}
	}

	@Override
	public void invokeMousePress(GLFWWindow window, int button, int mods) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			this.getWindow().setCursor(false);
			this.setCenter(this.getLookCoords());
			this.setDistanceFromCenter((float) Vector3f.distance(this.getCenter(), this.getPosition()));
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.setState(CameraEditor.STATE_SELECTING);
		}
	}

	@Override
	public void invokeMouseScroll(GLFWWindow window, double xpos, double ypos) {
		if (this.getWindow().isMouseRightPressed()) {
			float speed = this.getDistanceFromCenter() * 0.14f;
			super.increaseDistanceFromCenter((float) (-ypos * speed));
		} else if (this.getWindow().isMouseLeftPressed()) {
			if (ypos < 0) {
				this._y_selected += 1;
				this._block_two.y += 1;
			} else {
				this._y_selected -= 1;
				this._block_two.y -= 1;
			}
		} else {
			float scrollspeed = 4.0f;
			super.increaseDistanceFromCenter((float) (-ypos * scrollspeed));
		}
		this.updateLookBox();
	}

	public Model getModel() {
		return (this._editor.getModel());
	}

	public Vector3i getBlockOne() {
		return (this._block_one);
	}

	public Vector3i getBlockTwo() {
		return (this._block_two);
	}

	public void onModelSet(Model model) {
		this._tools.cleanHistory();
	}

	public int getBuildingColor() {
		return (this._editor.getToolbox().getBuildingColor().getRGB());
	}

	public void setBoxColor(Vector4f color) {
		for (Line line : this._lines) {
			line.colora = color;
			line.colorb = color;
		}
	}

	public CameraTool getTool() {
		return (this._tools.getTool());
	}

	public CameraTools getTools() {
		return (this._tools);
	}
}
