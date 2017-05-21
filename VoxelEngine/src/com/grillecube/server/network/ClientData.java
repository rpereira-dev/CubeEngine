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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * This should contain all the data needed from clients
 * 
 * @author Primate
 *
 */
public class ClientData {
	private Channel channel;

	public ClientData(ChannelHandlerContext ctx) {
		this.channel = ctx.channel();
	}

	public Channel getChannel() {
		return (this.channel);
	}
}
