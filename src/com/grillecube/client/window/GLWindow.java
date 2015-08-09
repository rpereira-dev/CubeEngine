package com.grillecube.client.window;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.grillecube.common.logger.Logger;

/**
 * 
 * HOW TO USE:
 * 
 * create a new instance (GLWindow window = new GLWindow())
 * 
 * call 'window.start()' to set GLContext + resize and error callback
 * 
 * 	window.prepareScreen(); //clear screen
 * 	...                     // rendering stuff goes here
 *	window.flushScreen(); //swap buffer + update fps counter
 */
public class GLWindow
{	
	/** window pointer */
	private long _window;
	
	/** window size (in pixels) */
	private int	_width;
	private int	_height;
	
	/** window size (in pixels) */
	private ByteBuffer	_bufferX;
	private ByteBuffer	_bufferY;
	
	private double	_mouseX;
	private double	_mouseY;
	private double	_prev_mouseX;
	private double	_prev_mouseY;

	/** window event instances */
	private GLWindowResizeCallback		_callback_window_size;
	
	/** frames data */
	private long _prev_frame; //previous frame timer
	private long _frames; //total frames flushed
	private long _fps_counter; //frame per second counter
	private long _fps; //last frame per second calculated
	
	public GLWindow()
	{
		this._window = 0;
	}
	
	public void start()
	{
		GLFW.glfwSetErrorCallback(new GlfwErrorCallback());
		if (GLFW.glfwInit() == 0)
		{
			System.err.println("Couldnt initialize glfw");
			return ;
		}
		
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		this._width = screensize.width / 2;
		this._height = screensize.height / 2;
		this._window = GLFW.glfwCreateWindow(this._width, this._height, "VoxelEngine", 0, 0);
		if (this._window == 0)
		{
			System.err.println("Couldnt create glfw window");
			return ;
		}
		GLFW.glfwMakeContextCurrent(this._window);
		GLContext.createFromCurrent();	//finalize context creation depending on OS
		GLFW.glfwSwapInterval(1);
		
		this._callback_window_size 	= new GLWindowResizeCallback(this);

		GLFW.glfwSetWindowSizeCallback(this._window, this._callback_window_size);
		
		GLFW.glfwSetInputMode(this._window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		GLFW.glfwSetCursorPos(this._window, (int)(this.getWidth() / 2), (int)(this.getHeight() / 2));

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this._mouseX = this.getWidth()/ 2;
		this._mouseY = this.getHeight() / 2;
		
		this._prev_mouseX = this.getWidth()/ 2;
		this._prev_mouseY = this.getHeight() / 2;
		
		this._bufferX = BufferUtils.createByteBuffer(8);
		this._bufferY = BufferUtils.createByteBuffer(8);
		
		this._prev_frame = System.currentTimeMillis();
		this._frames = 0;
		this._fps_counter = 0;
		this._fps = 0;
	}
	
	/** enable or disable vsync (0 == disable, 1 == enable) */
	public void	useVSync(int v)
	{
		GLFW.glfwSwapInterval(v);
	}

	/** call it to check openGL error after a gl call */
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

	/** stop the window */
	public void stop()
	{
		GLFW.glfwDestroyWindow(this._window);
	}

	public float getAspectRatio()
	{
		return (this._width / (float)this._height);
	}

	public void resize(int width, int height)
	{
        GL11.glViewport(0, 0, width, height);
        this._width = width;
        this._height = height;
	}
	
	public int	getWidth()
	{
		return (this._width);
	}
	
	public int	getHeight()
	{
		return (this._height);
	}

	/** should be call before rendering */
	public void prepareScreen()
	{
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);		
		
		GLFW.glfwGetCursorPos(this._window, this._bufferX, this._bufferY);
		this._mouseX = this._bufferX.getDouble();
		this._mouseY = this._bufferY.getDouble();
		this._bufferX.clear();
		this._bufferY.clear();
	}
	
	/** should be call after rendering */
	public void	flushScreen()
	{
		GLFW.glfwSwapBuffers(this._window);
		GLFW.glfwPollEvents();
		
		if (GLFW.glfwGetKey(this._window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS)
		{
			GLFW.glfwSetWindowShouldClose(this._window, 1);
		}
	
		this._prev_mouseX = this._mouseX;
		this._prev_mouseY = this._mouseY;
	
		this.updateFpsCounter();
		
		GLWindow.glCheckError("GLWindow.flushScreen()");
	}
	
	private void updateFpsCounter()
	{
		if (System.currentTimeMillis() - this._prev_frame >= 1000)
		{
			this._fps = this._fps_counter;
			Logger.get().log(Logger.Level.DEBUG, "fps: " + this._fps);
			this._fps_counter = 0;
			this._prev_frame = System.currentTimeMillis();
		}
		else
		{
			this._fps_counter++;
		}
		
		this._frames++;
	}

	/** return true if glfw was close-requested */
	public boolean shouldClose()
	{
		return (GLFW.glfwWindowShouldClose(this._window) != 0);
	}

	/** get window GLFW pointer */
	public long getPointer()
	{
		return (this._window);
	}
	
	/** get mouse X coordinate */
	public double getMouseX()
	{
		return (this._mouseX);
	}
	
	/** get mouse Y coordinate */
	public double getMouseY()
	{
		return (this._mouseY);
	}
	
	public double getPrevMouseX()
	{
		return (this._prev_mouseX);
	}
	
	public double getPrevMouseY()
	{
		return (this._prev_mouseY);
	}
	
	/** get total frames flushed */
	public long getTotalFramesFlushed()
	{
		return (this._frames);
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