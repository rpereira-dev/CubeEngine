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

package com.grillecube.engine.faces;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector3i;

public class FaceLeft implements Face {
	private static Vector3i _vec = new Vector3i(0, 0, -1);
	private static Vector3f _normal = new Vector3f(0, 0, -1);
	private static Vector3i _movement = new Vector3i(1, 1, 0);

	@Override
	public String getName() {
		return ("LEFT");
	}

	@Override
	public Vector3f getNormal() {
		return (_normal);
	}

	@Override
	public Vector3i getVector() {
		return (_vec);
	}

	@Override
	public Face getOpposite() {
		return (Face.get(Face.RIGHT));
	}

	@Override
	public int getID() {
		return (Face.LEFT);
	}

	@Override
	public float getFaceFactor() {
		return (0.70f);
	}
	
	@Override
	public Vector3i getAllowedTranslation() {
		return (_movement);
	}
}
