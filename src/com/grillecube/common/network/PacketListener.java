package com.grillecube.common.network;

/**
 * For a PacketListener : Packet is a subject
 * @author Primate
 *
 */
public interface PacketListener<T extends Packet> {
	/**
	 * This is to be called when packet is received.
	 * Every class that needs network handling should implement this
	 * @param packet
	 */
	public void onReceive(T packet);
}
