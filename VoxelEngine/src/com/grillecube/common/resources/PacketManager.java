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

package com.grillecube.common.resources;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.network.NoSuchPacketException;
import com.grillecube.common.network.Packet;
import com.grillecube.common.network.PacketData;
import com.grillecube.common.network.PacketListener;
import com.grillecube.common.network.WrongPacketFormatException;

import io.netty.buffer.ByteBuf;

public class PacketManager extends GenericManager<PacketData> {
	
	public PacketManager(ResourceManager manager) {
		super(manager);
	}

	/**
	 * add a packet class to the map
	 * 
	 * @return the packet ID or -1 on error
	 * @throws WrongPacketFormatException
	 */
	public int registerPacket(Class<? extends Packet> packet_class) {
		Logger.get().log(Level.FINE, "Registering packet: " + packet_class);
		try {
			packet_class.getDeclaredConstructor(ByteBuf.class); //try to get the constructor, if it doesnt exists, dont register the packet!			
			return (super.registerObject(new PacketData(packet_class)));
		} catch (NoSuchMethodException e) {
			Logger.get().log(Level.ERROR, "Byte buffer constructor not found! " + packet_class);
		} catch (SecurityException e) {
			Logger.get().log(Level.ERROR,
					"Cant register packet: " + packet_class + "Exception received:\n" + e.getLocalizedMessage());
		}
		return (-1);
	}

	/**
	 * register a listener to the given packet (which will be called when the packet is received)
	 * 
	 * @param packetID : the packet
	 * @param listener : the listener
	 */
	public void addListenerToPacket(int packetID, PacketListener<? extends Packet> listener) {
		if (listener == null) {
			Logger.get().log(Level.WARNING, "Tried to add a null listener (see PacketManager.addListenerToPacket())");
			return;
		}
		
		PacketData data = super.getObjectByID(packetID);
		if (data == null) {
			Logger.get().log(Level.WARNING,
					"Tried to add listener to an un-existed packet (see PacketManager.addListenerToPacket())");
			return;
		}
		data.addListener(listener);
	}

	/** called whenever a packet is received */
	public void onPacketReceived(Packet packet) {
		PacketData data = super.getObjectByID(packet.getPacketID());
		if (data == null) {
			Logger.get().log(Level.WARNING, "PacketManager.onPacketReceived()");
			return;
		}
		data.onReceive(packet);
	}

	/** generate a new packet from it ID and the given bytebuffer */
	public Packet getFromPacketID(int packetID, ByteBuf byteBuffer) throws NoSuchPacketException, WrongPacketFormatException {
		Class<? extends Packet> packet_class;
		PacketData data = super.getObjectByID(packetID);
		if (data == null) {
			throw new NoSuchPacketException(packetID);
		}
		packet_class = data.getPacketClass();
		try {
			return (packet_class.getDeclaredConstructor(ByteBuf.class).newInstance(byteBuffer));
		} catch (Exception exception) // should never occured because inserted
										// packets are checked on initialiaztion
		{
			Logger.get().log(Level.ERROR, "Packets.getFromPacketID()");
			throw new WrongPacketFormatException(exception.getMessage(), packet_class);
		}
	}

	/** Returns an instance of a packet from byteBuffer */
	public Packet fromByteBuffer(ByteBuf byteBuffer) throws NoSuchPacketException, WrongPacketFormatException {
		return (this.getFromPacketID(byteBuffer.readInt(), byteBuffer));
	}

	@Override
	protected void onObjectRegistered(PacketData object) {}

	@Override
	protected void onInitialized() {}

	@Override
	protected void onStopped() {}

	@Override
	protected void onCleaned() {}

	@Override
	protected void onLoaded() {}
}
