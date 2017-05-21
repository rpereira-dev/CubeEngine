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

package com.grillecube.common.network;

/**
 * For a PacketListener : Packet is a subject
 * @author Primate
 *
 */
public interface PacketListener<T extends Packet> {
	/**
	 * This is to be called when packet is received.
	 * Every class that needs network handling should implement this
	 * @param packet
	 */
	public void onReceive(T packet);
}
