package com.grillecube.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * This should contain all the data needed from clients
 * @author Primate
 *
 */
public class ClientData {
	private Channel channel;
	
	public ClientData(ChannelHandlerContext ctx) {
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}	
}
