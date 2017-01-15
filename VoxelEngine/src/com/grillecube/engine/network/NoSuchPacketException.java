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

package com.grillecube.engine.network;

public class NoSuchPacketException extends Exception
{
	private static final long serialVersionUID = 1L;

	public NoSuchPacketException(int id)
	{
		super("Wrong No such packet! (" + id + ")");
	}
}
