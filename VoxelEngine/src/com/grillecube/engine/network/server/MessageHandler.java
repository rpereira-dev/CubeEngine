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

import com.grillecube.engine.resources.ResourceManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles a server-side channel.
 */
public class MessageHandler extends ChannelInboundHandlerAdapter
{ 
	private UserRegister _user_register;
	
	public MessageHandler()
	{
		this._user_register = new UserRegister();
	}

	/** OnConnect */
    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    { 
        this._user_register.onUserConnect(ctx);
    }
    
    /** OnMessageReceived */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
    	this._user_register.onMessageReceived(ResourceManager.instance().getPacketManager(), ctx, (ByteBuf) msg); 
    }
    
    
    /** OnDisconnect */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
    	this._user_register.onUserDisconnect(ctx);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}