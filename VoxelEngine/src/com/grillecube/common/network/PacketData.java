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

import java.util.ArrayList;
import java.util.List;

public class PacketData {
	private Class<? extends Packet> _class;
	private List<PacketListener<? extends Packet>> _listeners;

	public PacketData(Class<? extends Packet> _class) {
		this._class = _class;
		this._listeners = new ArrayList<PacketListener<? extends Packet>>();
	}

	public Class<? extends Packet> getPacketClass() {
		return (this._class);
	}

	public List<PacketListener<? extends Packet>> getListeners() {
		return (this._listeners);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onReceive(Packet packet) {
		for (PacketListener listener : _listeners) {
			listener.onReceive(packet);
		}
	}

	public void addListener(PacketListener<? extends Packet> listener) {
		this._listeners.add(listener);
	}

}