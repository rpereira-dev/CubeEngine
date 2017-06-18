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

package com.grillecube.client.renderer;

public class ColorInt {
	public static int get(int r, int g, int b, int a) {
		return ((a << 24) | (r << 16) | (g << 8) | (b << 0));
	}

	public static int get(float r, float g, float b, float a) {
		return (get((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f), (int) (a * 255.0f)));
	}

	public static int getAlpha(int value) {
		return ((value >> 24) & 0xFF);
	}

	public static int getRed(int value) {
		return ((value >> 16) & 0xFF);
	}

	public static int getGreen(int value) {
		return ((value >> 8) & 0xFF);
	}

	public static int getBlue(int value) {
		return ((value >> 0) & 0xFF);
	}
}
