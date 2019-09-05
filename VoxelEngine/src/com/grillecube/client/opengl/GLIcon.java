package com.grillecube.client.opengl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;

public class GLIcon implements GLObject {

	private final GLFWImage image;
	private final Buffer buffer;

	protected GLIcon(String imagePath) {
		this.image = GLFWImage.malloc();
		this.buffer = GLFWImage.malloc(1);

		BufferedImage bufferedImage = ImageUtils.readImage(imagePath);
		int imwidth = bufferedImage.getWidth();
		int imheight = bufferedImage.getHeight();
		ByteBuffer pixels = BufferUtils.createByteBuffer(imwidth * imheight * 4);
		for (int y = 0; y < imheight; y++) {
			for (int x = 0; x < imwidth; x++) {
				Color color = new Color(bufferedImage.getRGB(x, y), true);
				pixels.put((byte) color.getRed());
				pixels.put((byte) color.getGreen());
				pixels.put((byte) color.getBlue());
				pixels.put((byte) color.getAlpha());
			}
		}
		pixels.flip();
		this.image.set(imwidth, imheight, pixels);
		this.buffer.put(0, this.image);
	}

	@Override
	public void delete() {
		this.image.free();
		this.buffer.free();
	}

	public final GLFWImage getGLFWImage() {
		return (this.image);
	}

	public final Buffer getBuffer() {
		return (this.buffer);
	}

}
