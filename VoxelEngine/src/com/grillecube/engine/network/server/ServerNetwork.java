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

package com.grillecube.engine.network.server;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngine.Side;
import com.grillecube.engine.network.Network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerNetwork extends ChannelInitializer<SocketChannel> implements Network// ,
																						// PacketListener<PacketString>
{
	public static final int MAX_NB_THREADS = 4;
	private int _port;

	private EventLoopGroup _bossgroup;
	private EventLoopGroup _workergroup;
	private ServerBootstrap _bootstrap;
	private ChannelFuture _channel;

	public ServerNetwork() {
		this(4242);
	}

	public ServerNetwork(int port) {
		this._port = port;
	}

	public void start() throws Exception {
		this._bossgroup = new NioEventLoopGroup();
		this._workergroup = new NioEventLoopGroup();
		this._bootstrap = new ServerBootstrap();
		this._bootstrap.group(this._bossgroup, this._workergroup);
		this._bootstrap.channel(NioServerSocketChannel.class);
		this._bootstrap.childHandler(this);
		this._bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		this._bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

		// Bind and start to accept incoming connections.
		this._channel = this._bootstrap.bind(this._port).sync(); // (7)
		Logger.get().log(Level.FINE, "Listening on " + this._port);
		this.stop();
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		channel.pipeline().addLast(new MessageHandler());
	}

	// @Override
	// public void onReceive(PacketString packet)
	// {
	// Logger.get().log(Level.FINE, "I have received " + packet.getString());
	// }

	public void stop() {
		// Wait until the server socket is closed.
		// In this example, this does not happen, but you can do that to
		// gracefully
		// shut down your server.
		try {
			this._channel.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			Logger.get().log(Level.WARNING, "Interupted while synchronizing server threads...");
		}
		Logger.get().log(Level.FINE, "Stopping server");
		this._workergroup.shutdownGracefully();
		this._bossgroup.shutdownGracefully();
	}

	@Override
	public Side getSide() {
		return (VoxelEngine.Side.SERVER);
	}
}