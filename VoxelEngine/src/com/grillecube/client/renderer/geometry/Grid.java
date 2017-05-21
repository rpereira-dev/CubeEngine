package com.grillecube.client.renderer.geometry;

/** GL_LINES format 3D */
public class Grid {
	/** GL11.glDrawArrays(GL11.GL_LINES, 0, (width + height + 2) * 2) */
	public static float[] make(int width, int height, float sizeunit) {
		float[] vertices = new float[(width + height + 2) * 3 * 2];
		float maxx = width * sizeunit;
		float maxz = height * sizeunit;

		float offsetx = maxx / 2;
		float offsetz = maxz / 2;

		int i = 0;
		for (int j = 0; j <= width; j++) {
			vertices[i++] = j * sizeunit - offsetz;
			vertices[i++] = 0;
			vertices[i++] = -offsetx;

			vertices[i++] = j * sizeunit - offsetz;
			vertices[i++] = 0;
			vertices[i++] = maxx - offsetx;
		}

		for (int j = 0; j <= height; j++) {
			vertices[i++] = -offsetz;
			vertices[i++] = 0;
			vertices[i++] = j * sizeunit - offsetx;

			vertices[i++] = maxz - offsetz;
			vertices[i++] = 0;
			vertices[i++] = j * sizeunit - offsetx;
		}

		return (vertices);
	}

}
