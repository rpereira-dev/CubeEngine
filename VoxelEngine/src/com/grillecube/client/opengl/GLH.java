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

package com.grillecube.client.opengl;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;

/** OpenGL helper class */
public class GLH {

	/** currently bounded window */

	/** if we use debug */
	public static boolean DEBUG = false;

	private static GLFWContext theContext;

	/** called to init opengl */
	public static void glhInit() {
		// GLFWErrorCallback.createPrint(Logger.get().getPrintStream()).set();
		if (!GLFW.glfwInit()) {
			System.err.println("Couldnt initialize glfw");
			return;
		}
		Logger.get().log(Level.FINE, "OpenGL initialized.");
	}

	/** set the opengl context to the given window */
	public static void glhSetContext(GLFWContext context) {
		Logger.get().log(Level.FINE, "OpenGL context set: " + context);
		// set current context
		GLFW.glfwMakeContextCurrent(context.getWindow().getPointer());
		// create context capa
		context.createCapabilities();
		// set current capa to use
		GL.setCapabilities(context.getCapabilities());

		// singleton update
		theContext = context;

		// add the window to GLH objects so it is clean properly on program
		// termination
		GLH.glhAddObject(context.getWindow());
	}

	/** get the last set context */
	public static GLFWContext glhGetContext() {
		return (theContext);
	}

	public static GLFWWindow glhGetWindow() {
		if (glhGetContext() == null) {
			return (null);
		}
		return (glhGetContext().getWindow());
	}

	/** generate a new frame buffer */
	public static GLFrameBuffer glhGenFBO() {
		GLFrameBuffer fbo = new GLFrameBuffer();
		GLH.glhAddObject(fbo);
		return (fbo);
	}

	/** generate a new render buffer */
	public static GLRenderBuffer glhGenRBO() {
		GLRenderBuffer rbo = new GLRenderBuffer();
		GLH.glhAddObject(rbo);
		return (rbo);
	}

	/** generate new buffer object */
	public static GLVertexBuffer glhGenVBO() {
		GLVertexBuffer vbo = new GLVertexBuffer();
		GLH.glhAddObject(vbo);
		return (vbo);
	}

	public static void glhAddObject(GLVertexBuffer buffer) {
		GLH.glhGetContext().addObject(buffer);
	}

	public static void glhRemoveObject(GLVertexBuffer buffer) {
		GLH.glhGetContext().removeObject(buffer);
	}

	/** generate new buffer object */
	public static GLVertexArray glhGenVAO() {
		GLVertexArray vao = new GLVertexArray();
		GLH.glhAddObject(vao);
		return (vao);
	}

	/** clean all generated data */
	public static void glhStop() {
		Logger.get().log(Logger.Level.FINE, "Cleaning GLObjects...");
		theContext.destroy();
	}

	/**
	 * add the program to the GLOBject list so it is delete when GLH.clean() is
	 * called
	 */
	public static void glhAddObject(GLObject object) {
		if (DEBUG) {
			Logger.get().log(Level.DEBUG, "GLH: adding", object.getClass().getSimpleName());
		}
		theContext.addObject(object);
	}

	/** remove the object */
	public static void glhDeleteObject(GLObject object) {

		if (object == null) {
			return;
		}

		if (DEBUG) {
			Logger.get().log(Level.DEBUG, "GLH: removing", object.getClass().getSimpleName());
		}
		theContext.removeObject(object);
		object.delete();
	}

	/***************************
	 * TEXTURES HELPER STARTS HERE:
	 ************************************/

	/** create opengl textures ID */
	public static GLTexture glhGenTexture() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL11.glGenTextures(buffer);
		GLTexture texture = new GLTexture(buffer.get());
		GLH.glhAddObject(texture);
		return (texture);
	}

	/**
	 * create opengl textures ID and fill it data with the given bufferedimage (rgba
	 * format)
	 */
	public static GLTexture glhGenTexture(BufferedImage image) {
		GLTexture texture = GLH.glhGenTexture();
		texture.setData(image);
		return (texture);
	}

	/** create opengl textures ID and fill it data with the given file */
	public static GLTexture glhGenTexture(String filepath) {
		return (GLH.glhGenTexture(ImageUtils.readImage(filepath)));
	}

	/***************************
	 * TEXTURES HELPER ENDS HERE:
	 ************************************/

	/** program helper */
	public static GLShader glhLoadShader(String filepath, int type) {
		return (glhLoadShader(filepath, type, ""));
	}

	public static GLShader glhLoadShader(String filepath, int type, String header) {
		GLShader shader = GLShader.load(filepath, type, header);
		GLH.glhAddObject(shader);
		return (shader);
	}

	public static String glhGetErrorString(int err) {
		String str[] = { "GL_INVALID_ENUM", "GL_INVALID_VALUE", "GL_INVALID_OPERATION", "GL_STACK_OVERFLOW",
				"GL_STACK_UNDERFLOW", "GL_OUT_OF_MEMORY" };
		int errs[] = { GL11.GL_INVALID_ENUM, GL11.GL_INVALID_VALUE, GL11.GL_INVALID_OPERATION, GL11.GL_STACK_OVERFLOW,
				GL11.GL_STACK_UNDERFLOW, GL11.GL_OUT_OF_MEMORY, };

		if (err != GL11.GL_NO_ERROR) {
			for (int i = 0; i < 6; i++) {
				if (errs[i] == err) {
					return (str[i]);
				}
			}
		}
		return (null);
	}

	/** call it to check openGL error after a gl call */
	public static void glhCheckError(String label) {
		int err = GL11.glGetError();

		String str = GLH.glhGetErrorString(err);
		if (str == null) {
			return;
		}
		Logger.get().log(Level.ERROR, label + " : GLH error check: " + str);
	}

	public static int glhGetBoundVertexArray() {
		return (glhGetBinding(GL30.GL_VERTEX_ARRAY_BINDING));
	}

	public static void glhDrawBuffer(int buf) {
		GL11.glDrawBuffer(buf);
	}

	public static int glhCheckFrameBufferStatus(int target) {
		return (GL30.glCheckFramebufferStatus(target));
	}

	public static int glhGetBoundRBO() {
		return (glhGetBinding(GL30.GL_RENDERBUFFER_BINDING));
	}

	public static int glhGetBoundFBO() {
		return (glhGetBinding(GL30.GL_FRAMEBUFFER_BINDING));
	}

	public static int glhGetBinding(int binding) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL11.glGetIntegerv(binding, buffer);
		return (buffer.get());
	}

	public static void glhDrawArrays(int dst, int begin, int vertex_count) {
		GL11.glDrawArrays(dst, begin, vertex_count);
		theContext.incrementDrawCalls();
		theContext.increaseVerticesDrawn(vertex_count);
	}

	public static void glhDrawElements(int glTriangles, int indexCount, int indiceType, long indices) {
		GL11.glDrawElements(glTriangles, indexCount, indiceType, indices);
		theContext.incrementDrawCalls();
		theContext.increaseVerticesDrawn(indexCount / 3);
	}

	public static void glhDrawArraysInstanced(int mode, int first, int count, int primcount) {
		GL31.glDrawArraysInstanced(mode, first, count, primcount);
		theContext.incrementDrawCalls();
		theContext.increaseVerticesDrawn(3 * count);
	}

	/** MAIN TESTS */
	/** MAIN TESTS */
	/** MAIN TESTS */
	/** MAIN TESTS */

	/** constants */
	public static final float f = 1.0f;
	public static final int DEFAULT_SCREEN_WIDTH = (int) (1280 * f);
	public static final int DEFAULT_SCREEN_HEIGHT = (int) (720 * f);

	public static GLFWWindow glhCreateWindow() {
		GLFWWindow window = new GLFWWindow();
		window = new GLFWWindow();
		window.create(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT, "");
		window.setCursor(false);
		return (window);
	}

	public static GLFWContext glhCreateContext(GLFWWindow window) {
		GLFWContext context = new GLFWContext(window);
		return (context);
	}

	public static void main(String[] args) throws InterruptedException {

		GLH.glhInit();
		GLFWWindow window = GLH.glhCreateWindow();
		GLFWContext context = GLH.glhCreateContext(window);
		GLH.glhSetContext(context);
		Logger.get().log(Logger.Level.FINE, "Context set properly. Sleeping 5 seconds");
		Thread.sleep(5000);
		GLH.glhStop();
	}

	/** create a new GLFW image */
	public static GLIcon glhCreateIcon(String imagePath) {
		GLIcon glIcon = new GLIcon(imagePath);
		return (glIcon);
	}
}
