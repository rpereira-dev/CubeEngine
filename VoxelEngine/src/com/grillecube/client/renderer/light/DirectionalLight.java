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

package com.grillecube.client.renderer.light;

import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.common.maths.Vector3f;

public class DirectionalLight extends Light {
	public Camera _camera;

	public DirectionalLight(Vector3f pos, Vector3f color, float intensity) {
		super(pos, color, intensity);
	}

}
