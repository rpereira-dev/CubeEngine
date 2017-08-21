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

package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.animations.GuiAnimation;
import com.grillecube.client.renderer.gui.components.parameters.GuiParameter;
import com.grillecube.client.renderer.gui.event.GuiEvent;
import com.grillecube.client.renderer.gui.event.GuiEventAspectRatio;
import com.grillecube.client.renderer.gui.event.GuiEventMouseEnter;
import com.grillecube.client.renderer.gui.event.GuiEventMouseExit;
import com.grillecube.client.renderer.gui.event.GuiEventMouseHover;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftRelease;
import com.grillecube.client.renderer.gui.event.GuiEventMouseMove;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector2f;
import com.grillecube.common.maths.Vector4f;

/**
 * 
 * Main Gui class.
 *
 * A Gui can have parent / children.
 * 
 * It has a position, a rotation (relative to the axis poiting into the screen),
 * and a scaling. These value are relative to the Gui parent referential (or to
 * the screen referential if it has no parent)
 * 
 * 
 *
 * @author Romain
 *
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class Gui {

	/** default colors */
	public static final Vector4f COLOR_WHITE = new Vector4f(0.9f, 0.9f, 0.9f, 1.0f);
	public static final Vector4f COLOR_BLACK = new Vector4f(0.1f, 0.1f, 0.1f, 1.0f);
	public static final Vector4f COLOR_BLUE = new Vector4f(65 / 255.0f, 105 / 255.0f, 225 / 255.0f, 1.0f);
	public static final Vector4f COLOR_RED = new Vector4f(176 / 255.0f, 23 / 255.0f, 31 / 255.0f, 1.0f);
	public static final Vector4f COLOR_DARK_MAGENTA = new Vector4f(139 / 255.0f, 0 / 255.0f, 139 / 255.0f, 1.0f);

	/** mouse states (using bitwise operations) */
	public static final int STATE_INITIALIZED = (1 << 0);
	public static final int STATE_HOVERED = (1 << 1);
	public static final int STATE_FOCUSED = (1 << 2);
	public static final int STATE_PRESSED = (1 << 3);
	public static final int STATE_SELECTABLE = (1 << 4);
	public static final int STATE_VISIBLE = (1 << 5);
	private static final int STATE_ENABLED = (1 << 6);

	/** the transformation matrix, relative to the parent */
	private final Matrix4f guiToParentChangeOfBasis;
	private final Matrix4f guiToWindowChangeOfBasis;
	private final Matrix4f windowToGuiChangeOfBasis;
	private final Matrix4f guiToGLChangeOfBasis;

	/** gui custom attributes */
	private HashMap<String, Object> attributes;

	/** this rectangle relative to opengl axis (-1;-1) to (1;1) */
	private final Vector2f boxPos;
	private final Vector2f boxSize;
	private final Vector2f boxCenter;
	private float boxRot;

	/** guis of this view */
	private ArrayList<Gui> children;
	private final ArrayList<GuiTask> tasks;
	private Gui parent;

	private ArrayList<GuiParameter<Gui>> params;

	/** gui animations */
	private ArrayList<GuiAnimation<Gui>> animations;

	/** hashmap: class of 'GuiEvent', and list of GuiListener<GuiEvent> */
	private HashMap<Class<?>, ArrayList<GuiListener<?>>> listeners;
	private ArrayList<GuiEvent<?>> stackedEvents;

	public static final Comparator<? super Gui> WEIGHT_COMPARATOR = new Comparator<Gui>() {
		@Override
		public int compare(Gui left, Gui right) {
			return (-left.getWeight() + right.getWeight());
		}
	};

	/** gui state */
	private int state;
	private float localAspectRatio = 1.0f;
	private float totalAspectRatio = 1.0f;
	private int weight;

	/** the mouse coordinates relatively to the gui basis */
	private float mouseX;
	private float mouseY;
	private float prevMouseX;
	private float prevMouseY;

	public Gui() {
		this.children = null;
		this.guiToParentChangeOfBasis = new Matrix4f();
		this.guiToWindowChangeOfBasis = new Matrix4f();
		this.windowToGuiChangeOfBasis = new Matrix4f();
		this.guiToGLChangeOfBasis = new Matrix4f();
		this.boxPos = new Vector2f();
		this.boxCenter = new Vector2f();
		this.boxSize = new Vector2f();
		this.boxRot = 0.0f;
		this.setBox(0, 0, 1, 1, 0);
		this.params = null;
		this.animations = null;
		this.state = 0;
		this.tasks = new ArrayList<GuiTask>();
		this.setVisible(true);
		this.setPressed(false);
		this.setSelectable(false);
		this.setEnabled(true);
	}

	/** a listener to the mouse hovering the gui */
	@SuppressWarnings("unchecked")
	public void addListener(GuiListener<?> listener) {
		if (listener == null) {
			return;
		}
		if (this.listeners == null) {
			this.listeners = new HashMap<Class<?>, ArrayList<GuiListener<?>>>();
		}
		ArrayList<GuiListener<?>> lst = this.listeners.get(listener.getEventClass());
		if (lst == null) {
			lst = new ArrayList<GuiListener<?>>();
			this.listeners.put(listener.getEventClass(), lst);
		}

		lst.add(listener);
	}

	/** a listener to the mouse entering the gui */
	public void removeListener(GuiListener<?> listener) {
		ArrayList<GuiListener<?>> lst = this.listeners.get(listener.getEventClass());
		if (lst == null) {
			return;
		}
		lst.remove(listener);
		if (lst.size() == 0) {
			this.listeners.remove(listener.getEventClass());
		}
		if (this.listeners.size() == 0) {
			this.listeners = null;
		}
	}

	/**
	 * start an animation
	 * 
	 * @return the animation id
	 */
	@SuppressWarnings("unchecked")
	public int startAnimation(GuiAnimation<? extends Gui> animation) {
		if (this.animations == null) {
			this.animations = new ArrayList<GuiAnimation<Gui>>();
		}
		this.animations.add((GuiAnimation<Gui>) animation);
		((GuiAnimation<Gui>) animation).restart(this);
		return (this.animations.size() - 1);
	}

	/** stop an animation by it id */
	public void stopAnimation(int index) {
		if (this.animations != null && index >= 0 && index < this.animations.size()) {
			this.animations.remove(index);
			if (this.animations.size() == 0) {
				this.animations = null;
			}
		}
	}

	/** stop every animation with the given class */
	public void stopAnimation(Class<?> clazz) {
		if (this.animations == null) {
			return;
		}

		for (int i = 0; i < this.animations.size(); i++) {
			GuiAnimation<?> animation = this.animations.get(i);

			if (animation == null) {
				continue;
			}

			if (animation.getClass().equals(clazz)) {
				this.stopAnimation(i);
			}
		}
	}

	public void setBoxPosition(float x, float y) {
		this.setBoxPosition(x, y, true);
	}

	protected void setBoxPosition(float x, float y, boolean runParameters) {
		this.setBox(x, y, this.getBoxWidth(), this.getBoxHeight(), this.getBoxRotation(), runParameters);
	}

	/** set the gui text center, if using PARAM_CENTER */
	public final void setBoxCenterPosition(float xcenter, float ycenter) {
		this.setBoxCenter(xcenter, ycenter, this.getBoxWidth(), this.getBoxHeight(), this.getBoxRotation());
	}

	public final void setBoxCenter(float xcenter, float ycenter, float width, float height, float rot) {
		this.setBoxCenter(xcenter, ycenter, width, height, rot, true);
	}

	public final void setBoxCenter(float xcenter, float ycenter, float width, float height, float rot,
			boolean runParameters) {
		this.setBox(xcenter - width * 0.5f, ycenter - height * 0.5f, width, height, rot, runParameters);
	}

	/** set the x position, but do not run parameters to adjust it */
	public final void setBoxX(float x) {
		this.setBoxPosition(x, this.boxPos.y);
	}

	/** set the y position, but do not run parameters to adjust it */
	public final void setBoxY(float y) {
		this.setBoxPosition(this.boxPos.x, y);
	}

	/** set the width, but do not run parameters to adjust it */
	public void setBoxWidth(float width) {
		this.setBoxSize(width, this.boxSize.y);
	}

	/** set the height, but do not run parameters to adjust it */
	public void setBoxHeight(float height) {
		this.setBoxSize(this.boxSize.x, height);
	}

	public void setBoxSize(float width, float height) {
		this.setBoxSize(width, height, true);
	}

	public final void setBoxSize(float width, float height, boolean runParameters) {
		this.setBox(this.getBoxX(), this.getBoxY(), width, height, this.getBoxRotation(), runParameters);
	}

	/**
	 * MAIN SETTER FUNCTION.
	 * 
	 * Every setters end up calling this one.
	 * 
	 * It updates the transformation matrix
	 * 
	 * Coordinates and dimension follows (0, 0) (bot left) (1, 1) (top right)
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param rot
	 */
	public final void setBox(float x, float y, float width, float height, float rot) {
		this.setBox(x, y, width, height, rot, true);
	}

	public final void setBox(float x, float y, float width, float height, float rot, boolean runParameters) {

		if (width == 0.0f) {
			width = 0.000000000001f;
		}
		if (height == 0.0f) {
			height = 0.000000000001f;
		}

		// positions
		this.boxPos.set(x, y);
		this.boxSize.set(width, height);
		this.boxRot = rot;
		this.boxCenter.set(x + width * 0.5f, y + height * 0.5f);

		// matrices are relative to GL referential
		this.guiToParentChangeOfBasis.setIdentity();
		this.guiToParentChangeOfBasis.translate(this.boxCenter.x, this.boxCenter.y, 0.0f);
		this.guiToParentChangeOfBasis.rotateZ(rot);
		this.guiToParentChangeOfBasis.translate(-this.boxCenter.x, -this.boxCenter.y, 0.0f);
		this.guiToParentChangeOfBasis.translate(x, y, 0.0f);
		this.guiToParentChangeOfBasis.scale(width, height, 1.0f);

		Matrix4f parentTransform = this.parent == null ? Matrix4f.IDENTITY : this.parent.guiToWindowChangeOfBasis;
		this.updateTransformationMatrices(parentTransform);

		// aspect ratio
		float aspectRatio = this.getBoxWidth() / this.getBoxHeight();
		if (aspectRatio != this.localAspectRatio) {
			this.localAspectRatio = aspectRatio;
			float parentAspectRatio = this.getParent() == null ? 1.0f : this.getParent().getTotalAspectRatio();
			this.updateAspectRatio(parentAspectRatio, runParameters);
		}

		if (runParameters) {
			this.runParameters();
		}
	}

	private void updateAspectRatio(float parentAspectRatio, boolean runParameters) {
		float oldAspectRatio = this.totalAspectRatio;
		this.totalAspectRatio = parentAspectRatio * this.localAspectRatio;
		this.stackEvent(new GuiEventAspectRatio(this, oldAspectRatio, this.totalAspectRatio));
		if (this.children != null) {
			for (Gui gui : this.children) {
				gui.updateAspectRatio(this.totalAspectRatio, runParameters);
			}
		}
	}

	/** update transformation matrices */
	private final void updateTransformationMatrices(Matrix4f parentTransform) {

		// combine with parent transformation
		Matrix4f.mul(parentTransform, this.guiToParentChangeOfBasis, this.guiToWindowChangeOfBasis);
		Matrix4f.invert(this.guiToWindowChangeOfBasis, this.windowToGuiChangeOfBasis);
		Matrix4f.mul(MainRenderer.WINDOW_TO_GL_BASIS, this.guiToWindowChangeOfBasis, this.guiToGLChangeOfBasis);

		// finally, set it relative to opengl screen referential
		if (this.children != null) {
			for (Gui child : this.children) {
				child.updateTransformationMatrices(this.guiToWindowChangeOfBasis);
			}
		}
	}

	/**
	 * render the gui
	 * 
	 * @param program
	 */
	public final void render(GuiRenderer renderer) {

		if (!this.isInitialized()) {
			this.initialize(renderer);
		}

		this.onRender(renderer);
	}

	/** do the rendering of this gui */
	protected void onRender(GuiRenderer guiRenderer) {

	}

	/** initialize the gui */
	protected final void initialize(GuiRenderer renderer) {
		this.setState(STATE_INITIALIZED);
		this.onInitialized(renderer);

		if (this.children != null) {
			for (Gui gui : this.children) {
				gui.initialize(renderer);
			}
		}
	}

	/** initialize the gui: this function is call in opengl main thread */
	protected void onInitialized(GuiRenderer renderer) {

	}

	/** deinitialize the gui */
	public final void deinitialize(GuiRenderer renderer) {
		if (!this.hasState(STATE_INITIALIZED)) {
			return;
		}

		this.unsetState(STATE_INITIALIZED);
		this.onDeinitialized(renderer);

		if (this.children != null) {
			for (Gui gui : this.children) {
				gui.deinitialize(renderer);
			}
		}
	}

	public final void deinitialize() {
		this.deinitialize(null);
	}

	/** deinitialize the gui: this function is call in opengl main thread */
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	/**
	 * update this gui inputs, mouse (x, y) coordinates are relative to the
	 * window
	 * 
	 * @param x
	 *            : x
	 * @param y
	 *            : y
	 * @param pressed
	 *            : if mouse is left pressed
	 */
	public final void updateInput(float x, float y, boolean pressed) {

		// do not updateInput if tis gui is not enabled
		if (!this.isEnabled()) {
			return;
		}

		// get the coordinates relatively to the gui basis
		Vector4f mouse = new Vector4f(x, y, 0.0f, 1.0f);
		Matrix4f.transform(this.windowToGuiChangeOfBasis, mouse, mouse);

		// update gui states (stack events)
		this.updateMouseInputEvents(mouse.x, mouse.y, pressed);
		this.onInputUpdate();

		// unstack (raise events)
		this.unstackEvents();

		// do it recursively on each child
		if (this.children != null) {
			for (Gui child : this.children) {
				child.updateInput(x, y, pressed);
			}
		}
	}

	/** stack an event, it listeners will be caled using Gui#unstackEvents() */
	public final void stackEvent(GuiEvent<?> guiEvent) {
		if (this.stackedEvents == null) {
			this.stackedEvents = new ArrayList<GuiEvent<?>>();
		}
		this.stackedEvents.add(guiEvent);
	}

	/** release the stack events and call their listener */
	public final void unstackEvents() {
		if (this.stackedEvents == null) {
			return;
		}

		ArrayList<GuiEvent<?>> events = this.stackedEvents;
		this.stackedEvents = null; // little hack for concurrent modification

		for (GuiEvent<?> event : events) {
			this.invokeEvent(event);
		}
	}

	/** invoke the given event */
	protected final void invokeEvent(GuiEvent event) {
		if (this.listeners == null) {
			return;
		}
		ArrayList<GuiListener<?>> listeners = this.listeners.get(event.getClass());
		if (listeners == null) {
			return;
		}
		for (GuiListener listener : listeners) {
			listener.invoke(event);
		}
	}

	/** called right after the input has been updated */
	protected void onInputUpdate() {
	}

	/** update animations and gui tasks */
	public final void update() {

		if (!this.isEnabled()) {
			return;
		}

		for (int i = 0; i < this.tasks.size(); i++) {
			this.tasks.get(i).run();
		}
		this.tasks.clear();
		this.tasks.trimToSize();

		this.updateAnimations();
		this.onUpdate();
	}

	protected void onUpdate() {
	}

	/** a task to be run at the end of the gui update */
	public interface GuiTask {
		public void run();
	}

	/** add a task to be run at the end of the gui update */
	public final void addTask(GuiTask task) {
		this.tasks.add(task);
	}

	private void updateAnimations() {
		if (this.animations != null) {
			for (int i = 0; i < this.animations.size(); i++) {
				GuiAnimation<Gui> animation = this.animations.get(i);
				if (animation == null || animation.update(this)) {
					this.animations.remove(i);
					continue;
				}
				++i;
			}
		}
	}
	//
	// public final void onCharPressed(GLFWWindow window, int codepoint) {
	// this.invokeListeners(GuiEventChar.class, window, codepoint);
	// if (this.children != null) {
	// for (Gui child : this.children) {
	// child.onCharPressed(window, codepoint);
	// }
	// }
	// }
	//
	// public final void onKeyPressed(GLFWWindow glfwWindow, int key, int
	// scancode, int mods) {
	// this.invokeListeners(GuiEventKeyPress.class, glfwWindow, key, scancode,
	// mods);
	// if (this.children != null) {
	// for (Gui child : this.children) {
	// child.onKeyPressed(glfwWindow, key, scancode, mods);
	// }
	// }
	// }
	// TODO up here

	private void updateMouseInputEvents(float x, float y, boolean pressed) {

		this.prevMouseX = this.mouseX;
		this.prevMouseY = this.mouseY;
		this.mouseX = x;
		this.mouseY = y;

		this.stackEvent(new GuiEventMouseMove(this));

		boolean hovered = (x >= 0.0f && x < 1.0f && y >= 0.0f && y <= 1.0f);

		// if mouse is in the gui bounding box
		if (hovered) {
			// raise hover event
			this.stackEvent(new GuiEventMouseHover(this));
		}

		// if mouse is not in the gui bounding box, but used to be
		if (this.isHovered() && !hovered) {
			this.setHovered(false);
			// raise exit event
			this.stackEvent(new GuiEventMouseExit(this));
		} else if (!this.isHovered() && hovered) {
			// if mouse is in the gui bounding box, but wasnt earlier
			this.setHovered(true);
			this.stackEvent(new GuiEventMouseEnter(this));
		}

		// if mouse wasnt pressed, and now is pressed, and the gui is hovered
		if (!this.isPressed() && pressed && this.isHovered()) {
			// press this gui, is selectable, press/unpress it, if not
			// selectable, press it
			this.setPressed(this.isSelectable() ? !this.isPressed() : true);
			// mouse left press
			this.stackEvent(new GuiEventMouseLeftPress(this));
		} else if (this.isPressed() && (!pressed || !this.isHovered())) {
			if (!this.isSelectable()) {
				this.setPressed(false);
			}

			if (this.isHovered()) {
				this.stackEvent(new GuiEventMouseLeftRelease(this));
			}
		}
	}

	/**
	 * @return : the mouse X coordinate relatively to the gui basis
	 */
	public final float getMouseX() {
		return (this.mouseX);
	}

	/**
	 * @return : the mouse Y coordinate relatively to the gui basis
	 */
	public final float getMouseY() {
		return (this.mouseY);
	}

	/**
	 * @return : the previous mouse X coordinate relatively to the gui basis
	 */
	public final float getPrevMouseX() {
		return (this.prevMouseX);
	}

	/**
	 * @return : the previous mouse Y coordinate relatively to the gui basis
	 */
	public final float getPrevMouseY() {
		return (this.prevMouseY);
	}

	public void setVisible(boolean visible) {
		this.setState(STATE_VISIBLE, visible);
	}

	public final void setEnabled(boolean enabled) {
		this.setState(STATE_ENABLED, enabled);
	}

	public final void setSelectable(boolean isSelectable) {
		this.setState(STATE_SELECTABLE, isSelectable);
	}

	public final void setPressed(boolean isPressed) {
		this.setState(STATE_PRESSED, isPressed);
	}

	public final void setHovered(boolean isHovered) {
		this.setState(STATE_HOVERED, isHovered);
	}

	public final boolean isSelectable() {
		return (this.hasState(STATE_SELECTABLE));
	}

	public final boolean isEnabled() {
		return (this.hasState(STATE_ENABLED));
	}

	public final boolean isVisible() {
		return (this.hasState(STATE_VISIBLE));
	}

	/** return true if the mouse cursor is inside this gui */
	public final boolean isHovered() {
		return (this.hasState(STATE_HOVERED));
	}

	/** return true if the gui is pressed */
	public final boolean isPressed() {
		return (this.hasState(STATE_PRESSED));
	}

	@SuppressWarnings("unchecked")
	public void addParameter(GuiParameter<?> parameter) {
		if (this.params == null) {
			this.params = new ArrayList<GuiParameter<Gui>>();
		}
		this.params.add((GuiParameter<Gui>) parameter);
		((GuiParameter<Gui>) parameter).run(this);
	}

	public void addParameters(GuiParameter<?>... parameters) {
		for (GuiParameter<?> parameter : parameters) {
			this.addParameter(parameter);
		}
	}

	/**
	 * run the parameter to update the gui position and size depending on it
	 * parameters You shall not have to call it, as it is call when setting a
	 * gui's size and position automatically
	 */
	public final void runParameters() {
		if (this.params == null) {
			return;
		}
		for (GuiParameter<Gui> param : this.params) {
			param.run(this);
		}
		if (this.children != null) {
			for (Gui child : this.children) {
				child.runParameters(); // TODO keep this?
			}
		}
	}

	public boolean hasParameter(GuiParameter<?> param) {
		if (this.params == null) {
			return (false);
		}
		return (this.params.contains(param));
	}

	public float getBoxCenterX() {
		return (this.boxCenter.x);
	}

	public float getBoxCenterY() {
		return (this.boxCenter.y);
	}

	public float getBoxX() {
		return (this.boxPos.x);
	}

	public float getBoxY() {
		return (this.boxPos.y);
	}

	public float getBoxWidth() {
		return (this.boxSize.x);
	}

	public float getBoxHeight() {
		return (this.boxSize.y);
	}

	public boolean hasFocus() {
		return (this.hasState(STATE_FOCUSED));
	}

	public final float getBoxRotation() {
		return (this.boxRot);
	}

	public final void setBoxRotation(float rot) {
		this.setBoxRotation(rot, true);
	}

	public final void setBoxRotation(float rot, boolean runParameters) {
		this.setBox(this.getBoxX(), this.getBoxY(), this.getBoxWidth(), this.getBoxHeight(), rot, runParameters);
	}

	public final Gui getParent() {
		return (this.parent);
	}

	public final ArrayList<Gui> getChildren() {
		return (this.children);
	}

	/** add a child to this gui */
	public final void addChild(Gui gui) {
		this.addChild(this.children == null ? 0 : this.children.size(), gui);
	}

	public final void addChild(int position, Gui gui) {
		gui.parent = this;
		if (this.children == null) {
			this.children = new ArrayList<Gui>();
		}
		this.children.add(position, gui);
		gui.updateTransformationMatrices(this.guiToWindowChangeOfBasis);
		gui.updateAspectRatio(this.getTotalAspectRatio(), false);
		gui.setWeight(this.getWeight() + 1);
		gui.onAddedTo(this);
	}

	/** remove a child from this gui */
	public final void removeChild(Gui gui) {
		if (this.children == null) {
			return;
		}
		this.children.remove(gui);
		if (this.children.size() == 0) {
			this.children = null;
		}
		gui.onRemovedFrom(gui);
	}

	/** called when this gui is added to another gui */
	protected void onAddedTo(Gui gui) {
	}

	/** called when this gui is removed from another gui */
	protected void onRemovedFrom(Gui gui) {
	}

	/** return true if this Gui has been initialized */
	public final boolean isInitialized() {
		return (this.hasState(STATE_INITIALIZED));
	}

	private final boolean hasState(int state) {
		return ((this.state & state) == state);
	}

	private final void setState(int state) {
		this.state = this.state | state;
	}

	private final void setState(int state, boolean enabled) {
		if (enabled) {
			this.setState(state);
		} else {
			this.unsetState(state);
		}
	}

	private final void unsetState(int state) {
		this.state = this.state & ~state;
	}

	@SuppressWarnings("unused")
	private final void swapState(int state) {
		this.state = this.state ^ state;
	}

	public final Matrix4f getGuiToWindowChangeOfBasis() {
		return (this.guiToWindowChangeOfBasis);
	}

	public final Matrix4f getWindowToGuiChangeOfBasis() {
		return (this.windowToGuiChangeOfBasis);
	}

	public final Matrix4f getGuiToParentChangeOfBasis() {
		return (this.guiToParentChangeOfBasis);
	}

	public final Matrix4f getGuiToGLChangeOfBasis() {
		return (this.guiToGLChangeOfBasis);
	}

	/**
	 * @return the aspect ratio of this gui relative to his parent
	 */
	public float getLocalAspectRatio() {
		return (this.localAspectRatio);
	}

	/**
	 * @return the aspect ratio of this gui relative to the window
	 */
	public float getTotalAspectRatio() {
		return (this.totalAspectRatio);
	}

	public void onWindowResized(int width, int height) {
		if (this.children != null) {
			for (Gui gui : this.children) {
				gui.onWindowResized(width, height);
			}
		}
	}

	public final int getWeight() {
		return (this.weight);
	}

	/**
	 * gui weight (the greater the weight is, the most this gui will be placed
	 * in foreground layers). This weight is relative to the window
	 */
	public final void setWeight(int weight) {
		this.weight = weight;
	}

	public final Object getAttribute(String attrID) {
		return (this.attributes == null ? null : this.attributes.get(attrID));
	}

	public final void setAttribute(String attrID, Object attribute) {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, Object>();
			this.attributes.put(attrID, attribute);
		}
	}
}
