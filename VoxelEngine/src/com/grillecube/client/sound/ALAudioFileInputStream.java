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

package com.grillecube.client.sound;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/** DataInputStream wrapper for endianess issues */
public class ALAudioFileInputStream extends DataInputStream
{
	public ALAudioFileInputStream(InputStream in)
	{
		super(in);
	}
	
	public int readLittleInt() throws IOException
	{
        int ch1 = super.read();
        int ch2 = super.read();
        int ch3 = super.read();
        int ch4 = super.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
        {
            throw new EOFException();        	
        }
        return ((ch1 << 0) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
	}
	
	public int readBigInt() throws IOException
	{
		return (super.readInt());
	}

	public short readLittleShort() throws IOException
	{
        int ch1 = super.read();
        int ch2 = super.read();
        if ((ch1 | ch2) < 0)
        {
            throw new EOFException();
        }
        return (short)((ch1 << 0) + (ch2 << 8));
    }
	
	public int readBigShort() throws IOException
	{
		return (super.readShort());
	}

}
