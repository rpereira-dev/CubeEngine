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

package com.grillecube.server.network;

import java.util.LinkedList;
import java.util.Queue;

import com.grillecube.common.network.Packet;

public class RunnableMessageHandler implements Runnable {

	public class PacketClientDataWrapper {
		private Packet packet;
		private ClientData clientData;

		public PacketClientDataWrapper(Packet packet, ClientData clientData) {
			super();
			this.packet = packet;
			this.clientData = clientData;
		}

		public Packet getpacket() {
			return packet;
		}

		public void setpacket(Packet packet) {
			this.packet = packet;
		}

		public ClientData getclientData() {
			return clientData;
		}

		public void setclientData(ClientData clientData) {
			this.clientData = clientData;
		}
	}

	private Queue<PacketClientDataWrapper> messageQueue;
	private boolean shouldStop;

	public RunnableMessageHandler() {
		this.messageQueue = new LinkedList<RunnableMessageHandler.PacketClientDataWrapper>();
		this.shouldStop = false;
	}

	public int queueLength() {
		return messageQueue.size();
	}

	public void add(PacketClientDataWrapper packetClientDataWrapper) {
		messageQueue.add(packetClientDataWrapper);
	}

	public void stop() {
		this.shouldStop = true;
	}

	public boolean shouldStop() {
		return (this.shouldStop);
	}

	@Override
	public void run() {
		while (!shouldStop()) {
			PacketClientDataWrapper packet = this.messageQueue.poll();
			if (packet != null) {
				/** do something with packet **/
			}
		}
	}

}
