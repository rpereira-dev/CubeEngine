package com.grillecube.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles a server-side channel.
 */
public class MessageHandler extends ChannelInboundHandlerAdapter { 
	private UserRegister register;
	
	public MessageHandler() {
		register = new UserRegister();
	}

	/**
	 * OnConnect
	 */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) { 
        register.onUserConnect(ctx);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    		throws Exception {
    	/**
    	 * Message received
    	 */
    	register.onMessageReceived(ctx, (ByteBuf) msg); 
    }
    
    
    /**
     * OnDisconnect
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	register.onUserDisconnect(ctx);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}