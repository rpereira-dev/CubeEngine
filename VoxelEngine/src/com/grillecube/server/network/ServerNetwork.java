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

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.VoxelEngine.Side;
import com.grillecube.common.network.INetwork;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//,
// PacketListener<PacketString>
public class ServerNetwork extends ChannelInitializer<SocketChannel> implements INetwork {
	public static final int MAX_NB_THREADS = 4;
	private int port;

	private EventLoopGroup bossgroup;
	private EventLoopGroup workergroup;
	private ServerBootstrap bootstrap;
	private ChannelFuture channel;

	public ServerNetwork() {
		this(4242);
	}

	public ServerNetwork(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		this.bossgroup = new NioEventLoopGroup();
		this.workergroup = new NioEventLoopGroup();
		this.bootstrap = new ServerBootstrap();
		this.bootstrap.group(this.bossgroup, this.workergroup);
		this.bootstrap.channel(NioServerSocketChannel.class);
		this.bootstrap.childHandler(this);
		this.bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		this.bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

		// Bind and start to accept incoming connections.
		this.channel = this.bootstrap.bind(this.port).sync(); // (7)
		Logger.get().log(Level.FINE, "Listening on " + this.port);
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
			this.channel.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			Logger.get().log(Level.WARNING, "Interupted while synchronizing server threads...");
		}
		Logger.get().log(Level.FINE, "Stopping server");
		this.workergroup.shutdownGracefully();
		this.bossgroup.shutdownGracefully();
	}

	@Override
	public Side getSide() {
		return (Side.SERVER);
	}
}