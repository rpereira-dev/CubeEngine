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

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;

public abstract class ALSound extends ALObject {
	private ALBuffer _buffer;

	private String _filename;

	private ByteBuffer _data;
	private int _frequence;
	private int _format;

	public ALSound() {
		this._buffer = ALH.alhGenBuffer();
	}

	public ALSound(String filename) {
		this._buffer = ALH.alhGenBuffer();
		this.load(filename);
	}

	public void load(String filename) {
		this._filename = filename;

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(new File(filename));
			ALAudioFileInputStream ais = new ALAudioFileInputStream(fstream);
			this.onLoad(ais);
			ais.close();
			fstream.close();
		} catch (Exception exception) {
			Logger.get().log(Level.ERROR, "Error while loading: " + filename);
			exception.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		this._buffer.destroy();
	}

	public abstract void onLoad(ALAudioFileInputStream dis) throws Exception;

	public String getFilename() {
		return (this._filename);
	}

	public int getFormat() {
		return (this._format);
	}

	public ByteBuffer getData() {
		return (this._data);
	}

	public int getFrequence() {
		return (this._frequence);
	}

	public long getDataAdress() {
		return (MemoryUtil.memAddress(this._data));
	}

	public int getDataSize() {
		return (this._data.capacity());
	}

	public void setData(ByteBuffer data) {
		this._data = data;
		this._buffer.bufferData(this);
	}

	public void setFrequence(int frequence) {
		this._frequence = frequence;
	}

	public void setFormat(int format) {
		this._format = format;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("ALSound: ");
		builder.append(this.getFilename());
		builder.append(" {");
		builder.append("Format: ");
		builder.append(this.getFormat());
		builder.append(" ; Data size: ");
		builder.append(this.getDataSize());
		builder.append(" ; Frequence: ");
		builder.append(this.getFrequence());
		builder.append("}");
		return (builder.toString());
	}

	public ALBuffer getBuffer() {
		return (this._buffer);
	}
}
