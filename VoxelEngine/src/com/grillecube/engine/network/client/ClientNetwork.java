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

package com.grillecube.engine.network.client;

import java.util.concurrent.TimeUnit;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngine.Side;
import com.grillecube.engine.network.Network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientNetwork implements Network {
	private NioEventLoopGroup _workergroup;
	private Bootstrap _bootstrap;
	private ChannelFuture _channel;

	public void start(String host, int port) throws Exception {
		this._workergroup = new NioEventLoopGroup();
		this._bootstrap = new Bootstrap(); // (1)
		this._bootstrap.group(this._workergroup); // (2)
		this._bootstrap.channel(NioSocketChannel.class); // (3)
		this._bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
		this._bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new TimeClientHandler());
			}
		});

		// Start the client.
		try {
			this._channel = this._bootstrap.connect(host, port).sync(); // (5)
		} catch (Exception exception) {
			// args are: quiettime, timeout, time unit
			this._workergroup.shutdownGracefully(0, 10, TimeUnit.SECONDS);
			Logger.get().log(Level.ERROR, "Couldnt connect to host: " + host + ":" + port);
			exception.printStackTrace(Logger.get().getPrintStream());

			this._bootstrap = null;
			this._channel = null;
			this._workergroup = null;
		}
	}

	public void stop() {
		if (this._channel == null) {
			Logger.get().log(Level.ERROR, "Tried to stop network which wasnt started!");
			return;
		}
		// Wait until the connection is closed.
		try {
			this._channel.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			Logger.get().log(Level.WARNING, "Interupted while synchronizing client network threads...");
		}
		this._workergroup.shutdownGracefully(0, 10, TimeUnit.SECONDS); // args
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
	// onConnect
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// onConnect
		// new PacketString("Hello World").send(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}