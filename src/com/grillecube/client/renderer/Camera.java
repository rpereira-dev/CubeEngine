package com.grillecube.client.renderer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.window.GLWindow;
import com.grillecube.common.world.Terrain;

public class Camera
{
	public static final int STATE_MOVE_FORWARD	= 1;
	public static final int STATE_MOVE_BACKWARD = 2;
	public static final int STATE_MOVE_LEFT 	= 4;
	public static final int STATE_MOVE_RIGHT	= 8;
				
	private Vector3f	_pos;
	private Vector3f	_pos_vec;
	private Vector3f	_rot_vec;
	
	private float		_speed;
	private float		_rot_speed;
	
	private float		_pitch;
	private float		_yaw;
	private float		_roll;
	private float		_aspect;
	private float		_fov;
	private float		_near;
	private float		_far;
	
	private Vector3f	_look_vec;
	private Vector3f	_look_cube;
	private Vector3f	_look_face;
	private int			_state;

	private Matrix4f _view_matrix;
	private Matrix4f _projection_matrix;
	
	private CameraPicker	_picker;
	
	private GLWindow	_window;
	
	/** aspect_ratio is WIDTH / HEIGHT */
	public Camera(GLWindow window)
	{
		this._view_matrix = new Matrix4f();
		this._projection_matrix = new Matrix4f();
		this._look_vec = new Vector3f();
		this._pos_vec = new Vector3f();
		this._rot_vec = new Vector3f();
		this._look_cube = null;
		this._look_face = null;
		this._window = window;
		this.reset();
		this._picker = new CameraPicker(this);
		this._aspect = 1.0f;
	}
	
	public boolean	hasState(int state)
	{
		return ((this._state & state) == state);
	}
	
	public void		setState(int state)
	{
		this._state = this._state | state;
	}
	
	public void		unsetState(int state)
	{
		this._state = this._state & ~(state);
	}
	
	public float getPitch()
	{
		return (this._pitch);
	}
	
	public float getYaw()
	{
		return (this._yaw);
	}
	
	public float getRoll()
	{
		return (this._roll);
	}

	public Vector3f	getPosition()
	{
		return (this._pos);
	}
	
	public void	update()
	{
		this.rotateCamera();
		this.updateInput();
		this.updateLook();
		this.updateMove();
		this._picker.update();
		
		this.createViewMatrix();
		this.createProjectionMatrix();
	}

	/** rotate the camera depending on mouse cursor */
	private void rotateCamera()
	{
		float	mouse_speed = 0.004f;
		
		this.increaseYaw((float) ((this._window.getMouseX() - this._window.getPrevMouseX()) * mouse_speed));
		this.increasePitch((float) ((this._window.getMouseY() - this._window.getPrevMouseY()) * mouse_speed));
		GLFW.glfwSetInputMode(this._window.getPointer(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}
	
	private void	updateInput()
	{
		this.keyPressed();
		this.keyReleased();
	}
	
	private void	updateLook()
	{
		float	f;

		this._aspect = this._window.getAspectRatio();
		
		this._pitch += this._rot_vec.x * this._rot_speed;
		this._yaw 	+= this._rot_vec.y * this._rot_speed;
		this._roll 	+= this._rot_vec.z * this._rot_speed;

		f = (float) Math.cos(this.getPitch());
		this._look_vec.setX((float) (f * Math.sin(this.getYaw())));
		this._look_vec.setY((float) -Math.sin(this.getPitch()));
		this._look_vec.setZ((float) (-f * Math.cos(this.getYaw())));
		this._look_vec.normalise();
	}

	private void	updateMove()
	{
		if (this.hasState(STATE_MOVE_FORWARD))
		{
			this._pos_vec.setX(this._look_vec.x);
			this._pos_vec.setY(this._look_vec.y);
			this._pos_vec.setZ(this._look_vec.z);
		}
		else if (this.hasState(STATE_MOVE_BACKWARD))
		{
			this._pos_vec.setX(-this._look_vec.x);
			this._pos_vec.setY(-this._look_vec.y);
			this._pos_vec.setZ(-this._look_vec.z);
		}
		else if (this.hasState(STATE_MOVE_RIGHT))
		{			
			this._pos_vec.setX(-this._look_vec.z);
			this._pos_vec.setY(0);
			this._pos_vec.setZ(this._look_vec.x);
		}
		else if (this.hasState(STATE_MOVE_LEFT))
		{
			this._pos_vec.setX(this._look_vec.z);
			this._pos_vec.setY(0);
			this._pos_vec.setZ(-this._look_vec.x);
		}
		else
		{
			this._pos_vec.setX(0);
			this._pos_vec.setY(0);
			this._pos_vec.setZ(0);
		}
	
		this.move(this._pos_vec);
	}
	
	public void	createProjectionMatrix()
	{
		float	x_scale;
		float	y_scale;
		float	frustrum_length;
		
		y_scale = (float) (1.0f / Math.tan(Math.toRadians(this._fov / 2.0f)) * this._aspect);
		x_scale = y_scale / this._aspect;
		frustrum_length = this._far - this._near;

		this._projection_matrix.setIdentity();
		this._projection_matrix.m00 = x_scale;
		this._projection_matrix.m11 = y_scale;
		this._projection_matrix.m22 = -((this._far + this._near) / frustrum_length);
		this._projection_matrix.m23 = -1;
		this._projection_matrix.m32 = -((2 * this._near * this._far) / frustrum_length);
		this._projection_matrix.m33 = 0;
	}
	
	public Matrix4f	getProjectionMatrix()
	{
		return (this._projection_matrix);
	}
	
	private void	createViewMatrix()
	{
		this._view_matrix.setIdentity();
		this._view_matrix.rotate(this.getPitch(), new Vector3f(1, 0, 0));
		this._view_matrix.rotate(this.getYaw(), new Vector3f(0, 1, 0));
		this._view_matrix.rotate(this.getRoll(), new Vector3f(0, 0, 1));

		this._view_matrix.translate(this.getPosition().negate(null));
	}
	
	public Matrix4f getViewMatrix()
	{
		return (this._view_matrix);
	}

	public void setYaw(float yaw)
	{
		this._yaw = yaw;
	}
	
	public void	setPitch(float pitch)
	{
		this._pitch = pitch;
	}
	
	public void	setRoll(float roll)
	{
		this._roll = roll;
	}
	
	public void increaseYaw(float yaw)
	{
		this._yaw += yaw;
	}
	
	public void	increasePitch(float pitch)
	{
		this._pitch += pitch;
	}
	
	public void	increaseRoll(float roll)
	{
		this._roll += roll;
	}

	private boolean	isKeyPressed(int key)
	{
		return (GLFW.glfwGetKey(this._window.getPointer(), key) == GLFW.GLFW_PRESS);
	}
	
	private boolean	isKeyReleased(int key)
	{
		return (GLFW.glfwGetKey(this._window.getPointer(), key) == GLFW.GLFW_RELEASE);
	}
	
	/** check pressed key */
	public void keyPressed()
	{
		if (isKeyPressed(GLFW.GLFW_KEY_W))
		{
			this.setState(STATE_MOVE_FORWARD);
		}
		if (isKeyPressed(GLFW.GLFW_KEY_A))
		{
			this.setState(STATE_MOVE_LEFT);
		}
		if (isKeyPressed(GLFW.GLFW_KEY_D))
		{
			this.setState(STATE_MOVE_RIGHT);
		}
		if (isKeyPressed(GLFW.GLFW_KEY_S))
		{
			this.setState(STATE_MOVE_BACKWARD);
		}
		if (isKeyPressed(GLFW.GLFW_KEY_UP))
		{
			this._rot_vec.x = -1;
		}
		if (isKeyPressed(GLFW.GLFW_KEY_DOWN))
		{
			this._rot_vec.x = 1;
		}
		if (isKeyPressed(GLFW.GLFW_KEY_RIGHT))
		{
			this._rot_vec.y = 1;
		}
		if (isKeyPressed(GLFW.GLFW_KEY_LEFT))
		{
			this._rot_vec.y = -1;
		}
	}
	
	/** check released key */
	private void keyReleased()
	{
		if (isKeyReleased(GLFW.GLFW_KEY_W))
		{
			this.unsetState(STATE_MOVE_FORWARD);
		}
		if (isKeyReleased(GLFW.GLFW_KEY_S))
		{
			this.unsetState(STATE_MOVE_BACKWARD);
		}
		if (isKeyReleased(GLFW.GLFW_KEY_A))
		{
			this.unsetState(STATE_MOVE_LEFT);
		}
		if (isKeyReleased(GLFW.GLFW_KEY_D))
		{
			this.unsetState(STATE_MOVE_RIGHT);
		}
		if (isKeyReleased(GLFW.GLFW_KEY_UP) || isKeyReleased(GLFW.GLFW_KEY_DOWN))
		{
			this._rot_vec.setX(0);
		}
		if (isKeyReleased(GLFW.GLFW_KEY_RIGHT) || isKeyReleased(GLFW.GLFW_KEY_LEFT))
		{
			this._rot_vec.setY(0);
		}
	}

	/** reset camera datas */
	public void reset()
	{
		this.setPosition(new Vector3f(0, Terrain.SIZE_Y + 1, 0));
		this.setPositionVec(new Vector3f(0, 0, 0));
		this.setRotationVec(new Vector3f(0, 0, 0));
		this.setPitch(0);
		this.setYaw(0);
		this.setRoll(0);
		this.setFov(70);
		this.setSpeed(2);
		this.setRotSpeed(1);
		this.setNear(0.1f);
		this.setFar(2000);
		this._aspect = this._window.getAspectRatio();						
		this._state = 0;
	}
	
	public void	setFov(float f)
	{
		this._fov = f;
	}
	
	public void	setSpeed(float f)
	{
		this._speed = f;
	}
	
	public void	setRotSpeed(float f)
	{
		this._rot_speed = f;
	}
	
	public void	setNear(float f)
	{
		this._near = f;
	}

	public void	setFar(float f)
	{
		this._far = f;
	}

	public void setRotationVec(Vector3f rot)
	{
		this._rot_vec = rot;
	}

	public void setPositionVec(Vector3f pos_vec)
	{
		this._pos_vec = pos_vec;
	}

	public void setPosition(Vector3f pos)
	{
		this._pos = pos;
	}

	public Vector3f getLookVec()
	{
		return (this._look_vec);
	}

	public void move(Vector3f pos_vec)
	{
		this._pos.x += pos_vec.x * this._speed;
		this._pos.y += pos_vec.y * this._speed;
		this._pos.z += pos_vec.z * this._speed;
	}

	public Vector3f	getLookFace()
	{
		return (this._look_face);
	}
	
	public Vector3f	getLookCoords()
	{
		return (this._look_cube);
	}

	public void setLookingCube(int x, int y, int z)
	{
		this._look_cube = new Vector3f(x, y, z);
	}
	
	public void setLookingCubeFace(float x, float y, float z)
	{
		this._look_face = new Vector3f(x, y, z);
	}

	public GLWindow	getWindow()
	{
		return (this._window);
	}
	

/** my frustrum culling, check if not too far and if in field of view
 * 
	             \-------------/		
	              \  p        /
	               \         /
	                \       /	        p: point to test
	                 \     /
	                  \   /             imprecision: make field of view larger
	                   \ /
	                  / e \				e: eye
	                  \___/				
*/
	public boolean	isInFrustum(Vector3f point, float impresicion)
	{
		Vector3f	to_point_vector;
		double 		dot;
		double		angle;
	
		to_point_vector = new Vector3f(point.x - this._pos.x, point.y - this._pos.y, point.z - this._pos.z);
		to_point_vector.normalise(to_point_vector);
		dot = Vector3f.dot(to_point_vector, this._look_vec);
		angle = Math.toDegrees(Math.acos(dot));
		return (angle < this._fov + impresicion);
	}
	
	/** return true if the box of center "center" with dimension x, y, z is in camera frustum */
	public boolean	isInFrustum(Vector3f center, float x, float y, float z)
	{
		return (this.isInFrustum(new Vector3f(center.x + x, center.y + y, center.z), 0)
				|| this.isInFrustum(new Vector3f(center.x - x, center.y + y, center.z), 0)
				|| this.isInFrustum(new Vector3f(center.x + x, center.y - y, center.z), 0)
				|| this.isInFrustum(new Vector3f(center.x + x, center.y - y, center.z), 0)
				|| this.isInFrustum(new Vector3f(center.x, center.y + y, center.z + z), 0)
				|| this.isInFrustum(new Vector3f(center.x, center.y + y, center.z - z), 0)
				|| this.isInFrustum(new Vector3f(center.x, center.y - y, center.z + z), 0)
				|| this.isInFrustum(new Vector3f(center.x, center.y - y, center.z - z), 0)
				);
	}
}
