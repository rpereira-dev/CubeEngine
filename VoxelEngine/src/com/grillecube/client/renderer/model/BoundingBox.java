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

package com.grillecube.client.renderer.model;

import com.grillecube.client.renderer.model.animation.ModelPartAnimationInstance;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.maths.Vector4f;

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

	private Vector3f _center;
	private Vector3f _size;
	private Vector3f _min;
	private Vector3f _max;
	private Vector4f _color;

	public BoundingBox() {
		this._color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
		this._center = new Vector3f();
		this._min = new Vector3f();
		this._max = new Vector3f();
		this._size = new Vector3f();
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
		this._color.set(color);
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
		return (x >= this._min.x && x <= this._max.x && y >= this._min.y && y <= this._max.y && z >= this._min.z
				&& z <= this._max.z);
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

		if (center.x < this._min.x) {
			s = center.x - this._min.x;
			d = s * s;
		} else if (center.x > this._max.x) {
			s = center.x - this._max.x;
			d += s * s;
		}

		if (center.y < this._min.y) {
			s = center.y - this._min.y;
			d += s * s;
		} else if (center.y > this._max.y) {
			s = center.y - this._max.y;
			d += s * s;
		}

		if (center.z < this._min.z) {
			s = center.z - this._min.z;
			d += s * s;
		} else if (center.z > this._max.z) {
			s = center.z - this._max.z;
			d += s * s;
		}
		return (d <= radius * radius);
	}

	public Vector3f getMax() {
		return (this._max);
	}

	public Vector3f getMin() {
		return (this._min);
	}

	public Vector3f getSize() {
		return (this._size);
	}

	public Vector4f getColor() {
		return (this._color);
	}

	public void setColor(Vector4f vec) {
		this._color.set(vec);
	}

	public void setColor(float r, float g, float b, float a) {
		this._color.set(r, g, b, a);
	}

	public void setMinSize(Vector3f min, Vector3f size) {
		this.setMinSize(min, size.x, size.y, size.z);
	}

	public void setMinSize(Vector3f min, float size) {
		this.setMinSize(min, size, size, size);
	}

	public void setMinSize(Vector3f min, float sizex, float sizey, float sizez) {
		this._min.set(min);
		this._size.set(sizex, sizey, sizez);
		this._max.set(this._min.x + sizex, this._min.y + sizey, this._min.z + sizez);
		this._center.set(this._min.x + this._size.x / 2.0f, this._min.y + this._size.y / 2.0f,
				this._min.z + this._size.z / 2.0f);
	}

	public void setMinMax(Vector3f min, Vector3f max) {
		this.setMinMax(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public void setMinMax(Vector4f min, Vector4f max) {
		this.setMinMax(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public void setMinMax(float minx, float miny, float minz, float maxx, float maxy, float maxz) {
		this._min.set(minx, miny, minz);
		this._max.set(maxx, maxy, maxz);
		this._size.set(this._max.x - this._min.x, this._max.y - this._min.y, this._max.z - this._min.z);
		this._center.set(this._min.x + this._size.x / 2.0f, this._min.y + this._size.y / 2.0f,
				this._min.z + this._size.z / 2.0f);
	}

	public void setMin(Vector3f min) {
		this.setMinSize(min, this._size);
	}

	public void setSize(Vector3f size) {
		this.setMinSize(this._min, size);
	}

	public Vector3f getCenter() {
		return (this._center);
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
		this._min.translate(x, y, z);
		this._max.translate(x, y, z);
		this._center.set(this._min.x + this._size.x / 2.0f, this._min.y + this._size.y / 2.0f,
				this._min.z + this._size.z / 2.0f);
	}

	public void scale(float f) {
		this.scale(f, f, f);
	}

	public void scale(Vector3f vec) {
		this.scale(vec.x, vec.y, vec.z);
	}

	public void scale(float fx, float fy, float fz) {
		float sx = this._size.x;
		float sy = this._size.y;
		float sz = this._size.z;

		float scalex = sx * fx;
		float scaley = sy * fy;
		float scalez = sz * fz;

		float dx = sx - scalex;
		float dy = sy - scaley;
		float dz = sz - scalez;

		float ddx = dx / 2.0f;
		float ddy = dy / 2.0f;
		float ddz = dz / 2.0f;

		this._min.x += ddx;
		this._min.y += ddy;
		this._min.z += ddz;

		this._max.x -= ddx;
		this._max.y -= ddy;
		this._max.z -= ddz;

		this.setMinMax(this._min, this._max);
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
		vecs[0] = new Vector4f(this._min.x, this._min.y, this._min.z, 1.0f);
		vecs[1] = new Vector4f(this._min.x + this._size.x, this._min.y, this._min.z, 1.0f);
		vecs[2] = new Vector4f(this._min.x + this._size.x, this._min.y, this._min.z + this._size.z, 1.0f);
		vecs[3] = new Vector4f(this._min.x, this._min.y, this._min.z + this._size.z, 1.0f);
		vecs[4] = new Vector4f(this._min.x, this._min.y + this._size.y, this._min.z, 1.0f);
		vecs[5] = new Vector4f(this._min.x + this._size.x, this._min.y + this._size.y, this._min.z, 1.0f);
		vecs[6] = new Vector4f(this._min.x + this._size.x, this._min.y + this._size.y, this._min.z + this._size.z,
				1.0f);
		vecs[7] = new Vector4f(this._min.x, this._min.y + this._size.y, this._min.z + this._size.z, 1.0f);

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
		this._min.set(box.getMin());
		this._max.set(box.getMax());
		this._size.set(box.getSize());
		this._color.set(box.getColor());
		this._center.set(box.getCenter());
	}

	public void set(BoundingBox original, ModelPartAnimationInstance animation) {
		this.translate(animation.getInterpolatedTranslation());
	}

	public void join(BoundingBox box) {
		// min
		if (box._min.x < this._min.x) {
			this._min.x = box._min.x;
		}

		if (box._min.y < this._min.y) {
			this._min.y = box._min.y;
		}

		if (box._min.z < this._min.z) {
			this._min.z = box._min.z;
		}

		// max
		if (box._max.x > this._max.x) {
			this._max.x = box._max.x;
		}

		if (box._max.y > this._max.y) {
			this._max.y = box._max.y;
		}

		if (box._max.z > this._max.z) {
			this._max.z = box._max.z;
		}
		this._size.set(this._max.x - this._min.x, this._max.y - this._min.y, this._max.z - this._min.z);
		this._center.set(this._min.x + this._size.x / 2.0f, this._min.y + this._size.y / 2.0f,
				this._min.z + this._size.z / 2.0f);
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
		if (this._min.x > this._max.x) {
			float tmp = this._min.x;
			this._min.x = this._max.x;
			this._max.x = tmp;
		}

		if (this._min.y > this._max.y) {
			float tmp = this._min.y;
			this._min.y = this._max.y;
			this._max.y = tmp;
		}

		if (this._min.z > this._max.z) {
			float tmp = this._min.z;
			this._min.z = this._max.z;
			this._max.z = tmp;
		}
	}
}
