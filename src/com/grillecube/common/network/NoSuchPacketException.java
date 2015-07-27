package com.grillecube.common.network;

public class NoSuchPacketException extends Exception
{
	private static final long serialVersionUID = 1L;

	public NoSuchPacketException(int id)
	{
		super("Wrong No such packet! (" + id + ")");
	}
}
