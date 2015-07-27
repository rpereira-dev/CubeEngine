package com.grillecube.common.network;

public class WrongPacketFormatException extends Exception
{
	private static final long serialVersionUID = 1L;

	public WrongPacketFormatException(String str, Class<? extends Packet> packet_class)
	{
		super("Wrong Packet Format ! (for class: " + packet_class.getName() + ") : " + str);
	}
}
