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

	public Class<? extends Packet> get_class() {
		return _class;
	}

	public List<PacketListener<? extends Packet>> get_listeners() {
		return _listeners;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onReceive(Packet packet) {
		for(PacketListener listener : _listeners) {
			listener.onReceive(packet);
		}
	}
	
}