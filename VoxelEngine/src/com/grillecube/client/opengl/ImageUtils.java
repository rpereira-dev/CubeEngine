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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.grillecube.common.Logger;

public class ImageUtils {

	/** return an image for this file */
	public static BufferedImage readImage(String filepath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			Logger.get().log(Logger.Level.WARNING, "Cant get image file: " + filepath);
		}
		return (image);
	}

	/** return a byte array which contains texture pixels in RGBA format */
	public static byte[] getImagePixels(BufferedImage img) {
		byte[] pixels = new byte[img.getWidth() * img.getHeight() * 4];
		int[] buffer = new int[img.getWidth() * img.getHeight()];

		img.getRGB(0, 0, img.getWidth(), img.getHeight(), buffer, 0, img.getWidth());

		int i = 0;
		pixels = new byte[img.getWidth() * img.getHeight() * 4];
		for (int value : buffer) {
			pixels[i++] = (byte) (value >> 16 & 0xFF); // r
			pixels[i++] = (byte) (value >> 8 & 0xFF); // g
			pixels[i++] = (byte) (value >> 0 & 0xFF); // b
			pixels[i++] = (byte) (value >> 24 & 0xFF); // a
		}
		return (pixels);
	}

	/**
	 * export the given image to the given filepath in png file format
	 * 
	 * @param filepath
	 * @param atlas
	 */
	public static void exportPNGImage(String filepath, BufferedImage image) {
		exportPNGImage(new File(filepath), image);
	}

	public static void exportPNGImage(File file, BufferedImage image) {
		try {
			if (!file.exists()) {
				file.mkdirs();
				file.createNewFile();
			}
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			Logger.get().log(Logger.Level.WARNING, "Cant save texture: " + e.getMessage());
		}
	}
}
