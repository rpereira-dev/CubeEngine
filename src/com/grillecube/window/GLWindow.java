package com.grillecube.window;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector4f;

public class GLWindow
{
	public static final Vector4f CLEAR_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	
	private static GLWindow	_instance;

	private long	_window;

	private GLWindowMouseButtonCallback	_callback_mouse_button;
	private GLWindowKeyCallback 		_callback_key;
	private GLWindowCursorPosCallback 	_callback_mouse_move;
	private GLWindowScrollCallback 		_callback_mouse_cursor;
	private GLWindowResizeCallback		_callback_window_size;
	
	private Camera	_camera;
	
	public GLWindow()
	{
		this._window = 0;
		this._camera = null;
		_instance = this;
	}
	
	public void start()
	{
		Dimension	screensize;
		int			width;
		int			height;

		GLFW.glfwSetErrorCallback(new GlfwErrorCallback());
		if (GLFW.glfwInit() == 0)
		{
			System.err.println("Couldnt initialize glfw");
			return ;
		}
		
		screensize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screensize.width / 2;
		height = screensize.height / 2;
		this._window = GLFW.glfwCreateWindow(width, height, "VoxelEngine", 0, 0);
		this._camera = new Camera(width / (float)height);
		if (this._window == 0)
		{
			System.err.println("Couldnt create glfw window");
			return ;
		}
		GLFW.glfwMakeContextCurrent(this._window);
		GLContext.createFromCurrent();	//finalize context creation depending on OS
		GLFW.glfwSwapInterval(1);
		
		this._callback_key 			= new GLWindowKeyCallback(this);
		this._callback_mouse_button = new GLWindowMouseButtonCallback(this);
		this._callback_mouse_move 	= new GLWindowCursorPosCallback(this);
		this._callback_mouse_cursor = new GLWindowScrollCallback(this);
		this._callback_window_size 	= new GLWindowResizeCallback(this);
		
		GLFW.glfwSetKeyCallback(this._window, this._callback_key);
		GLFW.glfwSetMouseButtonCallback(this._window, this._callback_mouse_button);
		GLFW.glfwSetCursorPosCallback(this._window, this._callback_mouse_move);
		GLFW.glfwSetScrollCallback(this._window, this._callback_mouse_cursor);
		GLFW.glfwSetWindowSizeCallback(this._window, this._callback_window_size);
		
		GLFW.glfwSetInputMode(this._window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		GLFW.glfwSetCursorPos(this._window, (int)(this.getWidth() / 2), (int)(this.getHeight() / 2));
	}

	public static void  glCheckError(String label)
	{
		String str[] = {
			"GL_INVALID_ENUM",
			"GL_INVALID_VALUE",
			"GL_INVALID_OPERATION",
			"GL_STACK_OVERFLOW",
			"GL_STACK_UNDERFLOW",
			"GL_OUT_OF_MEMORY"
		};
		int 	errs[] = {
			GL11.GL_INVALID_ENUM,
			GL11.GL_INVALID_VALUE,
			GL11.GL_INVALID_OPERATION,
			GL11.GL_STACK_OVERFLOW,
			GL11.GL_STACK_UNDERFLOW,
			GL11.GL_OUT_OF_MEMORY,	
		};
	    int err = GL11.glGetError();
	    
	    if (err != GL11.GL_NO_ERROR)
	    {
	    	for (int i = 0 ; i < 6 ; i++)
	    	{
	    		if (errs[i] == err)
	    		{
	    	    	System.err.println(label + ": GL error occured: " + str[i]);
	    			break ;
	    		}
	    	}
	    }
	}
	
	public void stop()
	{
		GLFW.glfwDestroyWindow(this._window);
	}

	public float getAspectRatio()
	{
		IntBuffer	width;
		IntBuffer	height;
		
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(this._window, width, height);
		return (width.get() / (float)height.get());
	}
	
	public int getWidth()
	{
		IntBuffer	width;
		IntBuffer	height;
		
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(this._window, width, height);
		return (width.get());
	}
	
	public int getHeight()
	{
		IntBuffer	width;
		IntBuffer	height;
		
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(this._window, width, height);
		return (height.get());
	}

	public void resize(int width, int height)
	{
        GL11.glViewport(0, 0, width, height);
	}

	public static GLWindow	instance()
	{
		return (_instance);
	}

	public Camera getCamera()
	{
		return (this._camera);
	}

	/** called after each rendered frame */
	public void update()
	{
		GLFW.glfwSwapBuffers(this._window);
		GLFW.glfwPollEvents();
		GLWindow.glCheckError("Rendering loop");		
	}

	public void clear()
	{
		GL11.glClearColor(CLEAR_COLOR.x, CLEAR_COLOR.y, CLEAR_COLOR.z, CLEAR_COLOR.w);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);		
	}

	public boolean shouldClose()
	{
		return (GLFW.glfwWindowShouldClose(this._window) != 0);
	}
}

class GlfwErrorCallback extends GLFWErrorCallback
{
	@Override
	public void invoke(int error, long description)
	{
		System.err.println("GL error occured: " + error);
	}
}