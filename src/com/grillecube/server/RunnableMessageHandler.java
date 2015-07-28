package com.grillecube.server;

import java.util.LinkedList;
import java.util.Queue;

import com.grillecube.common.network.Packet;

public class RunnableMessageHandler implements Runnable {
	public class PacketClientDataWrapper {
		private Packet _packet;
		private ClientData _clientData;
		
		public PacketClientDataWrapper(Packet _packet, ClientData _clientData) {
			super();
			this._packet = _packet;
			this._clientData = _clientData;
		}
		public Packet get_packet() {
			return _packet;
		}
		public void set_packet(Packet _packet) {
			this._packet = _packet;
		}
		public ClientData get_clientData() {
			return _clientData;
		}
		public void set_clientData(ClientData _clientData) {
			this._clientData = _clientData;
		}		
	}
	
	private Queue<PacketClientDataWrapper> _messageQueue;
	private Boolean _shouldStop;
	
	public RunnableMessageHandler() {
		_messageQueue = new LinkedList<RunnableMessageHandler.PacketClientDataWrapper>();
		_shouldStop = false;
	} 
	
	public int queueLength() {
		return _messageQueue.size();
	}

	public void add(PacketClientDataWrapper packetClientDataWrapper) {
		_messageQueue.add(packetClientDataWrapper);
	}
	
	public void stop() {
		this._shouldStop = true;
	}
	
	public Boolean shouldStop() {
		return _shouldStop;
	}
	
	@Override
	public void run() {
		while(!shouldStop()) {
			PacketClientDataWrapper packet = _messageQueue.poll();
			if(packet != null) {
				/** do something with packet **/
			}
		}
	}
	
}
