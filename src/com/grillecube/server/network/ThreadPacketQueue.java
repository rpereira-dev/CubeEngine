package com.grillecube.server.network;

import java.io.IOException;
import java.net.DatagramPacket;

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
		
		packet = new DatagramPacket(null, 0);
		while (this._server.isRunning())
		{
			try
			{
				this._server.getSocket().receive(packet);
			}
			catch (IOException e)
			{
				Game.getLogger().log(Logger.LoggerLevel.WARNING, "Exception occured while received datagram packet: " + e.getMessage());
			}
			//read on the socket in packet
			
			//this._server.queuePacket(packet);
		}
	}
}
