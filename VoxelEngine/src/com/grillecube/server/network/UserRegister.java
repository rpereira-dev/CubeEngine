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

import java.net.SocketAddress;
import java.util.HashMap;

import com.grillecube.common.network.NoSuchPacketException;
import com.grillecube.common.network.Packet;
import com.grillecube.common.network.WrongPacketFormatException;
import com.grillecube.common.resources.PacketManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class UserRegister
{
	private HashMap<SocketAddress, ClientData> _connected_clients;
	private Thread[] _worker_threads;
	private RunnableMessageHandler[] _handlers;
	
	public UserRegister()
	{
		this._worker_threads = new Thread[ServerNetwork.MAX_NB_THREADS];
		this._handlers = new RunnableMessageHandler[ServerNetwork.MAX_NB_THREADS];
		for (int t = 0; t < ServerNetwork.MAX_NB_THREADS; t++)
		{
			this._handlers[t] = new RunnableMessageHandler();
			this._worker_threads[t] = new Thread(_handlers[t]);
		}
		this._connected_clients = new HashMap<SocketAddress, ClientData>();
	}
	
	public void clean()
	{
		for (RunnableMessageHandler handler : this._handlers)
		{
			handler.stop();
		}
		
		for (Thread worker_thrd : this._worker_threads)
		{
			try
			{
				worker_thrd.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void onUserConnect(ChannelHandlerContext ctx)
	{
		this._connected_clients.putIfAbsent(ctx.channel().remoteAddress(), new ClientData(ctx));
	}
	
	public void onMessageReceived(PacketManager manager, ChannelHandlerContext ctx, ByteBuf byteBuffer)
	{
		try
		{
			Packet packet = manager.fromByteBuffer(byteBuffer);
			ClientData clt = this._connected_clients.get(ctx.channel().remoteAddress());
			
			/** Do whatever you want with Packet and Client :D **/
			RunnableMessageHandler lch = getLeastChargedHandler();
			lch.add(lch.new PacketClientDataWrapper(packet, clt));
			manager.onPacketReceived(packet);
		}
		catch (NoSuchPacketException e)
		{
			e.printStackTrace();
		}
		catch (WrongPacketFormatException e)
		{
			e.printStackTrace();
		} 
	}

	private RunnableMessageHandler getLeastChargedHandler() {
		RunnableMessageHandler lch = this._handlers[0];
		for(int ite = 1; ite < ServerNetwork.MAX_NB_THREADS; ++ite) {
			if (this._handlers[ite].queueLength() < lch.queueLength()) {
				lch = this._handlers[ite];
			}
		}
		return lch;
	}

	public void onUserDisconnect(ChannelHandlerContext ctx)
	{
		SocketAddress key = ctx.channel().remoteAddress();
		
		if (this._connected_clients.containsKey(key))
		{
			this._connected_clients.remove(key);
		}
	}
}
