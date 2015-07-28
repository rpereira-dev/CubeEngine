package com.grillecube.server;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.grillecube.common.network.NoSuchPacketException;
import com.grillecube.common.network.Packet;
import com.grillecube.common.network.WrongPacketFormatException;
import com.grillecube.server.RunnableMessageHandler.PacketClientDataWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class UserRegister
{
	private Map<SocketAddress, ClientData> _clientsConnected;
	private Thread[] workerThreads;
	private RunnableMessageHandler[] handlers;
	
	public UserRegister()
	{
		workerThreads = new Thread[Server.MAX_NB_THREADS];
		handlers = new RunnableMessageHandler[Server.MAX_NB_THREADS];
		for(int ite = 0; ite < Server.MAX_NB_THREADS; ++ite) {
			handlers[ite] = new RunnableMessageHandler();
			workerThreads[ite] = new Thread(handlers[ite]);
		}
		this._clientsConnected = new HashMap<SocketAddress, ClientData>();
	}
	
	public void clean() {
		for(RunnableMessageHandler handler : handlers) {
			handler.shouldStop();
		}
		for(Thread workerThread : workerThreads) {
			try {
				workerThread.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onUserConnect(ChannelHandlerContext ctx)
	{
		this._clientsConnected.putIfAbsent(ctx.channel().remoteAddress(), new ClientData(ctx));
	}
	
	public void onMessageReceived(ChannelHandlerContext ctx, ByteBuf byteBuffer)
	{
		try
		{
			Packet packet = Packet.fromByteBuffer(byteBuffer);
			ClientData clt = this._clientsConnected.get(ctx.channel().remoteAddress());
			
			/** Do whatever you want with Packet and Client :D **/
			RunnableMessageHandler lch = getLeastChargedHandler();
			lch.add(lch.new PacketClientDataWrapper(packet, clt));			
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
		RunnableMessageHandler lch = this.handlers[0];
		for(int ite = 1; ite < Server.MAX_NB_THREADS; ++ite) {
			if(this.handlers[ite].queueLength() < lch.queueLength()) {
				lch = this.handlers[ite];
			}
		}
		return lch;
	}

	public void onUserDisconnect(ChannelHandlerContext ctx)
	{
		SocketAddress key = ctx.channel().remoteAddress();
		
		if (this._clientsConnected.containsKey(key))
		{
			this._clientsConnected.remove(key);
		}
	}
}
