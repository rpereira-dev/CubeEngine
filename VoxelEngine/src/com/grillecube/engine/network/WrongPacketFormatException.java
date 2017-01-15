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

public class WrongPacketFormatException extends Exception
{
	private static final long serialVersionUID = 1L;

	public WrongPacketFormatException(String str, Class<? extends Packet> packet_class)
	{
		super("Wrong Packet Format ! (for class: " + packet_class.getName() + ") : " + str);
	}
}
