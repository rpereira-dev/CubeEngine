package com.grillecube.server.network;

import java.io.IOException;
import java.net.DatagramPacket;

import com.grillecube.common.packet.Packet;
import com.grillecube.server.Game;

import fr.toss.lib.Logger;

public class ThreadPacketQueue extends Thread
{
	private Server	_server;
	
	public ThreadPacketQueue(Server server)
	{
		this._server = server;
	}
	
	@Override
	public void run()
	{
		DatagramPacket	packet;
		
		packet = new DatagramPacket(new byte[Packet.MAX_SIZE], Packet.MAX_SIZE);

		while (this._server.isRunning())
		{
			try
			{
				this._server.getSocket().receive(packet);
				
				this._server.queuePacket(packet);
			}
			catch (IOException e)
			{
				Game.getLogger().log(Logger.Level.WARNING, "Exception occured while received datagram packet: " + e.getMessage());
			}
		}
	}
}
