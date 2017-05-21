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

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

/**
 * 
 * @author Romain Load a wave file to an ALSound
 */
public class ALSoundWave extends ALSound {
	private static final int RIFF_INT = 0x52494646;
	private static final int WAVE_INT = 0x57415645;
	private static final int FMT_INT = 0x666D7420;
	private static final int DATA_INT = 0x64617461;

	public ALSoundWave(String filename) {
		super(filename);
	}

	@Override
	public void onLoad(ALAudioFileInputStream ais) throws Exception {
		// header telling this is a wave file
		if (ais.readInt() != RIFF_INT) {
			throw new Exception("Not a wave file");
		}
		@SuppressWarnings("unused")
		int file_size = ais.readInt() + 8;
		if (ais.readInt() != WAVE_INT) {
			throw new Exception("Not a wave file");
		}

		boolean fmt = false;
		boolean data = false;

		// read block by block
		while (ais.available() > 0) {
			int id = ais.readInt();
			int block_size = ais.readLittleInt();

			if (id == FMT_INT) {
				if (fmt) {
					throw new Exception("Multiple FMT blocks in WAV file");
				}
				fmt = true;

				@SuppressWarnings("unused")
				short audio_format = ais.readLittleShort();
				short channels = ais.readLittleShort();
				System.out.println(channels);
				int frequence = ais.readLittleInt();

				@SuppressWarnings("unused")
				int avg_bytes_per_sec = ais.readLittleInt();

				@SuppressWarnings("unused")
				short bytes_per_sample = ais.readLittleShort();
				short bits_per_sample = ais.readLittleShort();

				super.setFrequence(frequence);

				if (bits_per_sample == 8) {
					if (channels == 1) {
						super.setFormat(AL10.AL_FORMAT_MONO8);
					} else if (channels == 2) {
						super.setFormat(AL10.AL_FORMAT_STEREO8);
					} else {
						throw new Exception("Wrong number of channels (not supported): " + channels);
					}
				} else if (bits_per_sample == 16) {
					if (channels == 1) {
						super.setFormat(AL10.AL_FORMAT_MONO16);
					} else if (channels == 2) {
						super.setFormat(AL10.AL_FORMAT_STEREO16);
					} else {
						throw new Exception("Wrong number of channels (not supported): " + channels);
					}
				} else {
					throw new Exception("Wrong bits per sample: " + bits_per_sample);
				}
			} else if (id == DATA_INT) {
				if (data) {
					throw new Exception("Multiple data section in WAV file");
				}
				data = true;
				byte[] bytes = new byte[block_size];
				ais.read(bytes, 0, block_size);
				ByteBuffer buffer = BufferUtils.createByteBuffer(block_size);
				buffer.put(bytes);
				buffer.flip();
				super.setData(buffer);
			} else if (block_size >= 0 && block_size <= ais.available()) {
				ais.skip(block_size);
			} else {
				break;
			}
		}

		if (!fmt) {
			throw new Exception("No FMT");
		}

		if (!data) {
			throw new Exception("No data");
		}
	}
}
