package com.grillecube.server;

import com.grillecube.common.logger.Logger;
import com.grillecube.common.logger.Logger.Level;
import com.grillecube.common.network.PacketListener;
import com.grillecube.common.network.Packets;
import com.grillecube.server.network.packet.PacketString;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server implements PacketListener<PacketString> {

	public static final int MAX_NB_THREADS = 4;
    private int _port;

    public Server(int port)
    {
        this._port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        Packets.loadPackets();	//LOAD EVERY PACKETS TO A HASMAP
        Packets.addListenerToPacket(Packets.STRING, this);
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new MessageHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(this._port).sync(); // (7)
            Logger.get().log(Level.FINE,  "Listening on " + this._port);
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
    	new Logger(System.out);
        int port = 4242;
        new Server(port).run();
    }

	@Override
	public void onReceive(PacketString packet) {
		Logger.get().log(Level.FINE, "I have received " + packet.getString());
	}
}