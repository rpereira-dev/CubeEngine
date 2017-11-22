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

package com.grillecube.common.faces;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;

public interface Face {

	public static final int NULL = -1;

	/** z - */
	public static final int LEFT = 0;

	/** z + */
	public static final int RIGHT = 1;

	/** y + */
	public static final int TOP = 2;

	/** y - */
	public static final int BOT = 3;

	/** x - */
	public static final int FRONT = 4;

	/** x + */
	public static final int BACK = 5;

	static final Face[] faces = { new FaceLeft(), new FaceRight(), new FaceTop(), new FaceBot(), new FaceFront(),
			new FaceBack(), };

	public static final Face F_LEFT = Face.get(LEFT);
	public static final Face F_RIGHT = Face.get(RIGHT);
	public static final Face F_TOP = Face.get(TOP);
	public static final Face F_BOT = Face.get(BOT);
	public static final Face F_FRONT = Face.get(FRONT);
	public static final Face F_BACK = Face.get(BACK);

	public static Face get(int index) {
		return (faces[index]);
	}

	public static Face[] values() {
		return (faces);
	}

	public String getName();

	public Vector3f getNormal();

	public Vector3i getVector();

	public Face getOpposite();

	public int getID();

	public abstract float getFaceFactor();

	public static Face fromVec(Vector3f vec) {
		if (vec.x > 0 && vec.x > vec.y && vec.x > vec.z) {
			return (Face.get(Face.BACK));
		}
		if (vec.x < 0 && vec.x < vec.y && vec.x < vec.z) {
			return (Face.get(Face.FRONT));
		}
		if (vec.y > 0 && vec.y > vec.x && vec.y > vec.z) {
			return (Face.get(Face.TOP));
		}
		if (vec.y < 0 && vec.y < vec.x && vec.y < vec.z) {
			return (Face.get(Face.BOT));
		}
		if (vec.z > 0 && vec.z > vec.x && vec.z > vec.y) {
			return (Face.get(Face.RIGHT));
		}
		return (Face.get(Face.LEFT));
	}

	public static Face fromVec(Vector3i vec) {
		if (vec.x > 0 && vec.x > vec.y && vec.x > vec.z) {
			return (Face.get(Face.BACK));
		}
		if (vec.x < 0 && vec.x < vec.y && vec.x < vec.z) {
			return (Face.get(Face.FRONT));
		}
		if (vec.y > 0 && vec.y > vec.x && vec.y > vec.z) {
			return (Face.get(Face.TOP));
		}
		if (vec.y < 0 && vec.y < vec.x && vec.y < vec.z) {
			return (Face.get(Face.BOT));
		}
		if (vec.z > 0 && vec.z > vec.x && vec.z > vec.y) {
			return (Face.get(Face.RIGHT));
		}
		return (Face.get(Face.LEFT));
	}

	public Vector3i getAllowedTranslation();
}
