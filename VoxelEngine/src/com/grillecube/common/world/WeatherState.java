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

package com.grillecube.common.world;

public class WeatherState
{
	/** weather states */
	public static final int DAY = (1 << 0);
	public static final int NIGHT = (1 << 1);
	public static final int DAY_ENDING = (1 << 2);
	public static final int NIGHT_ENDING = (1 << 3);
	public static final int RAIN_STARTING = (1 << 4);
	public static final int RAINING = (1 << 5);
	public static final int RAIN_ENDING = (1 << 6);
}
