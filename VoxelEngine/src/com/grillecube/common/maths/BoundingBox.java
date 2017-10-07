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

package com.grillecube.common.maths;

/*
   o-----------------MAX
  /|       ^          /|
 o-------------------o |
 | |       |       ^ | |
 | |       |      /  | |
 | |     s |     /   | |
 | |     i |    /    | |
 | |     z |   /     | |
 | |     e |  /      | |
 | |     . | / size.x| |
 | |     y |/        | |
 | |       C-------->| |
 | |         size.z  | |
 | |                 | |
 | |                 | |
 | |                 | |
 | |                 | |
 | |                 | |
 | o_______________ _|_|
 |/                  | /
 MIN-----------------o
 
 
*/

public class BoundingBox {

	public static final BoundingBox EMPTY_BOX = new BoundingBox();

	private Vector3f center;
	private Vector3f size;
	private Vector3f min;
	private Vector3f max;
	private Vector4f color;

	public BoundingBox() {
		this.color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
		this.center = new Vector3f();
		this.min = new Vector3f();
		this.max = new Vector3f();
		this.size = new Vector3f();
	}

	public BoundingBox(BoundingBox box) {
		this();

		if (box == null) {
			return;
		}

		this.set(box);
	}

	public BoundingBox(Vector3f min, Vector3f size, Vector4f color) {
		this();
		this.color.set(color);
		this.setMinSize(min, size);
	}

	public float getArea() {
		return (this.getSize().x * this.getSize().y * this.getSize().z);
	}

	/**
	 * @param point:
	 *            point to test
	 * @return true if the point is in the box
	 */
	public boolean contains(Vector3f point) {
		return (this.contains(point.x, point.y, point.z));
	}

	public boolean contains(float x, float y, float z) {
		return (x >= this.min.x && x <= this.max.x && y >= this.min.y && y <= this.max.y && z >= this.min.z
				&& z <= this.max.z);
	}

	public boolean contains(BoundingBox box) {
		return (this.getMin().x < box.getMin().x && this.getMin().y < box.getMin().y && this.getMin().z < box.getMin().z
				&& this.getMax().x > box.getMax().x && this.getMax().y > box.getMax().y
				&& this.getMax().z > box.getMax().z);

	}

	/**
	 * return true if this boundingbox intersect with the given one
	 * 
	 * @return
	 */
	public boolean intersect(BoundingBox box) {
		return ((this.getMin().x <= box.getMax().x && this.getMax().x >= box.getMin().x)
				&& (this.getMin().y <= box.getMax().y && this.getMax().y >= box.getMin().y)
				&& (this.getMin().z <= box.getMax().z && this.getMax().z >= box.getMin().z));
	}

	/** contains version for a sphere */
	public boolean contains(Vector3f center, float radius) {
		float s = 0;
		float d = 0;

		if (center.x < this.min.x) {
			s = center.x - this.min.x;
			d = s * s;
		} else if (center.x > this.max.x) {
			s = center.x - this.max.x;
			d += s * s;
		}

		if (center.y < this.min.y) {
			s = center.y - this.min.y;
			d += s * s;
		} else if (center.y > this.max.y) {
			s = center.y - this.max.y;
			d += s * s;
		}

		if (center.z < this.min.z) {
			s = center.z - this.min.z;
			d += s * s;
		} else if (center.z > this.max.z) {
			s = center.z - this.max.z;
			d += s * s;
		}
		return (d <= radius * radius);
	}

	public Vector3f getMax() {
		return (this.max);
	}

	public Vector3f getMin() {
		return (this.min);
	}

	public Vector3f getSize() {
		return (this.size);
	}

	public Vector4f getColor() {
		return (this.color);
	}

	public void setColor(Vector4f vec) {
		this.color.set(vec);
	}

	public void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

	public void setMinSize(Vector3f min, Vector3f size) {
		this.setMinSize(min, size.x, size.y, size.z);
	}

	public void setMinSize(Vector3f min, float size) {
		this.setMinSize(min, size, size, size);
	}

	public void setMinSize(Vector3f min, float sizex, float sizey, float sizez) {
		this.setMinSize(min.x, min.y, min.z, sizex, sizey, sizez);
	}

	public void setMinSize(float minx, float miny, float minz, float sizex, float sizey, float sizez) {
		this.min.set(minx, miny, minz);
		this.size.set(sizex, sizey, sizez);
		this.max.set(this.min.x + sizex, this.min.y + sizey, this.min.z + sizez);
		this.center.set(this.min.x + this.size.x / 2.0f, this.min.y + this.size.y / 2.0f,
				this.min.z + this.size.z / 2.0f);
	}

	public void setMinMax(Vector3f min, Vector3f max) {
		this.setMinMax(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public void setMinMax(Vector4f min, Vector4f max) {
		this.setMinMax(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public void setMinMax(float minx, float miny, float minz, float maxx, float maxy, float maxz) {
		this.min.set(minx, miny, minz);
		this.max.set(maxx, maxy, maxz);
		this.size.set(this.max.x - this.min.x, this.max.y - this.min.y, this.max.z - this.min.z);
		this.center.set(this.min.x + this.size.x / 2.0f, this.min.y + this.size.y / 2.0f,
				this.min.z + this.size.z / 2.0f);
	}

	public void setMin(Vector3f min) {
		this.setMinSize(min, this.size);
	}

	public void setSize(Vector3f size) {
		this.setMinSize(this.min, size);
	}

	public Vector3f getCenter() {
		return (this.center);
	}

	/** set bounding box by a center and it dimensions */
	public void setCenterSize(Vector3f center, float width, float height, float depth) {
		float w = width * 0.5f;
		float h = height * 0.5f;
		float d = depth * 0.5f;
		this.center.set(center);
		this.min.set(center.x - w, center.y - h, center.z - d);
		this.max.set(center.x + w, center.y + h, center.z + d);
		this.size.set(width, height, depth);
	}

	@Override
	public String toString() {
		return ("Bounding Box(" + this.getMin() + ";" + this.getMax() + ";" + this.getSize() + ")");
	}

	public void translate(Vector3f translation) {
		this.translate(translation.x, translation.y, translation.z);
	}

	public void translate(Vector3i translation) {
		this.translate(translation.x, translation.y, translation.z);
	}

	public void translate(float x, float y, float z) {
		this.min.translate(x, y, z);
		this.max.translate(x, y, z);
		this.center.set(this.min.x + this.size.x / 2.0f, this.min.y + this.size.y / 2.0f,
				this.min.z + this.size.z / 2.0f);
	}

	public void scale(float f) {
		this.scale(f, f, f);
	}

	public void scale(Vector3f vec) {
		this.scale(vec.x, vec.y, vec.z);
	}

	public void scale(float fx, float fy, float fz) {
		float sx = this.size.x;
		float sy = this.size.y;
		float sz = this.size.z;

		float scalex = sx * fx;
		float scaley = sy * fy;
		float scalez = sz * fz;

		float dx = sx - scalex;
		float dy = sy - scaley;
		float dz = sz - scalez;

		float ddx = dx / 2.0f;
		float ddy = dy / 2.0f;
		float ddz = dz / 2.0f;

		this.min.x += ddx;
		this.min.y += ddy;
		this.min.z += ddz;

		this.max.x -= ddx;
		this.max.y -= ddy;
		this.max.z -= ddz;

		this.setMinMax(this.min, this.max);
	}

	public void rotate(Vector3f rot, Vector3f center) {

		// TODO : optimize memory usage here (much new instanciations are done
		// here)

		Matrix4f matrix = new Matrix4f();
		matrix.translate(center);
		matrix.rotate((float) Math.toRadians(rot.x), Vector3f.AXIS_X);
		matrix.rotate((float) Math.toRadians(rot.y), Vector3f.AXIS_Y);
		matrix.rotate((float) Math.toRadians(rot.z), Vector3f.AXIS_Z);
		matrix.translate(center.negate(null));

		Vector4f[] vecs = new Vector4f[8];
		vecs[0] = new Vector4f(this.min.x, this.min.y, this.min.z, 1.0f);
		vecs[1] = new Vector4f(this.min.x + this.size.x, this.min.y, this.min.z, 1.0f);
		vecs[2] = new Vector4f(this.min.x + this.size.x, this.min.y, this.min.z + this.size.z, 1.0f);
		vecs[3] = new Vector4f(this.min.x, this.min.y, this.min.z + this.size.z, 1.0f);
		vecs[4] = new Vector4f(this.min.x, this.min.y + this.size.y, this.min.z, 1.0f);
		vecs[5] = new Vector4f(this.min.x + this.size.x, this.min.y + this.size.y, this.min.z, 1.0f);
		vecs[6] = new Vector4f(this.min.x + this.size.x, this.min.y + this.size.y, this.min.z + this.size.z, 1.0f);
		vecs[7] = new Vector4f(this.min.x, this.min.y + this.size.y, this.min.z + this.size.z, 1.0f);

		Vector3f min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

		for (Vector4f vec : vecs) {
			Matrix4f.transform(matrix, vec, vec);
			if (vec.x < min.x) {
				min.x = vec.x;
			} else if (vec.x > max.x) {
				max.x = vec.x;
			}

			if (vec.y < min.y) {
				min.y = vec.y;
			} else if (vec.y > max.y) {
				max.y = vec.y;
			}

			if (vec.z < min.z) {
				min.z = vec.z;
			} else if (vec.z > max.z) {
				max.z = vec.z;
			}
		}

		this.setMinMax(min, max);
	}

	public void set(BoundingBox box) {
		this.min.set(box.getMin());
		this.max.set(box.getMax());
		this.size.set(box.getSize());
		this.color.set(box.getColor());
		this.center.set(box.getCenter());
	}

	public void join(BoundingBox box) {
		// min
		if (box.min.x < this.min.x) {
			this.min.x = box.min.x;
		}

		if (box.min.y < this.min.y) {
			this.min.y = box.min.y;
		}

		if (box.min.z < this.min.z) {
			this.min.z = box.min.z;
		}

		// max
		if (box.max.x > this.max.x) {
			this.max.x = box.max.x;
		}

		if (box.max.y > this.max.y) {
			this.max.y = box.max.y;
		}

		if (box.max.z > this.max.z) {
			this.max.z = box.max.z;
		}
		this.size.set(this.max.x - this.min.x, this.max.y - this.min.y, this.max.z - this.min.z);
		this.center.set(this.min.x + this.size.x / 2.0f, this.min.y + this.size.y / 2.0f,
				this.min.z + this.size.z / 2.0f);
	}

	public void join(Vector4f vec) {
		Vector3f min = this.getMin();
		Vector3f max = this.getMax();
		if (vec.x > max.x) {
			max.x = vec.x;
		} else if (vec.x < min.x) {
			min.x = vec.x;
		}
		if (vec.y > max.y) {
			max.y = vec.y;
		} else if (vec.y < min.y) {
			min.y = vec.y;
		}
		if (vec.z > max.z) {
			max.z = vec.z;
		} else if (vec.z < min.z) {
			min.z = vec.z;
		}
		this.setMinMax(min, max);
	}

	/**
	 * reequilibrate minimum and maximum, so each component of minimum vector is
	 * inferior to respective componnent of maximum (mx < Mx, my < mY, mz < Mz)
	 */
	public void reequilibrateMinMax() {
		if (this.min.x > this.max.x) {
			float tmp = this.min.x;
			this.min.x = this.max.x;
			this.max.x = tmp;
		}

		if (this.min.y > this.max.y) {
			float tmp = this.min.y;
			this.min.y = this.max.y;
			this.max.y = tmp;
		}

		if (this.min.z > this.max.z) {
			float tmp = this.min.z;
			this.min.z = this.max.z;
			this.max.z = tmp;
		}
	}
}
