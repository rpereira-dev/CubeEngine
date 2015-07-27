package com.grillecube.server;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.grillecube.common.network.NoSuchPacketException;
import com.grillecube.common.network.Packet;
import com.grillecube.common.network.WrongPacketFormatException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class UserRegister
{
	private Map<SocketAddress, ClientData> _clientsConnected;
	
	public UserRegister()
	{
		this._clientsConnected = new HashMap<SocketAddress, ClientData>();
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

	public void onUserDisconnect(ChannelHandlerContext ctx)
	{
		SocketAddress key = ctx.channel().remoteAddress();
		
		if (this._clientsConnected.containsKey(key))
		{
			this._clientsConnected.remove(key);
		}
	}
}
