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

package com.grillecube.client.network;

import java.util.concurrent.TimeUnit;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Side;
import com.grillecube.common.network.INetwork;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientNetwork implements INetwork {
	private NioEventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private ChannelFuture channel;

	public void start(String host, int port) throws Exception {
		this.workerGroup = new NioEventLoopGroup();
		this.bootstrap = new Bootstrap(); // (1)
		this.bootstrap.group(this.workerGroup); // (2)
		this.bootstrap.channel(NioSocketChannel.class); // (3)
		this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
		this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new TimeClientHandler());
			}
		});

		// Start the client.
		try {
			this.channel = this.bootstrap.connect(host, port).sync(); // (5)
		} catch (Exception exception) {
			// args are: quiettime, timeout, time unit
			this.workerGroup.shutdownGracefully(0, 10, TimeUnit.SECONDS);
			Logger.get().log(Level.ERROR, "Couldnt connect to host: " + host + ":" + port);
			exception.printStackTrace(Logger.get().getPrintStream());

			this.bootstrap = null;
			this.channel = null;
			this.workerGroup = null;
		}
	}

	public void stop() {
		if (this.channel == null) {
			Logger.get().log(Level.ERROR, "Tried to stop network which wasnt started!");
			return;
		}
		// Wait until the connection is closed.
		try {
			this.channel.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			Logger.get().log(Level.WARNING, "Interupted while synchronizing client network threads...");
		}
		this.workerGroup.shutdownGracefully(0, 10, TimeUnit.SECONDS); // args
																		// are :
																		// quiettime,
																		// timeout,
																		// time
																		// unit
	}

	@Override
	public Side getSide() {
		return (VoxelEngine.Side.CLIENT);
	}
}

class TimeClientHandler extends ChannelInboundHandlerAdapter {

	/** on connect */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// onConnect
		// new PacketString("Hello World").send(ctx.channel());
	}

	/** on exception caught */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}