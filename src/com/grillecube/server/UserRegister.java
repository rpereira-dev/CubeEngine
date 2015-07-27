package com.grillecube.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;
import java.util.Map;

import com.grillecube.common.network.Packet;

public class UserRegister {
	private Map<SocketAddress, ClientData> clientsConnected;
	
	public UserRegister() {
		
	}
	
	public void onUserConnect(ChannelHandlerContext ctx) {
		clientsConnected.putIfAbsent(ctx.channel().remoteAddress(), new ClientData(ctx));
	}
	
	public void onMessageReceived(ChannelHandlerContext ctx, ByteBuf byteBuffer) {
		Packet packet = Packet.fromByteBuffer(byteBuffer); 
		/** Do whatever you want with Packet **/
	}

	public void onUserDisconnect(ChannelHandlerContext ctx) {
		SocketAddress key = ctx.channel().remoteAddress();
		if(clientsConnected.containsKey(key)) {
			clientsConnected.remove(key);
		}
	}
}
