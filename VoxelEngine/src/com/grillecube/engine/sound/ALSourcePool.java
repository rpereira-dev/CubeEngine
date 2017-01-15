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

package com.grillecube.engine.sound;

import java.util.ArrayList;

import com.grillecube.engine.maths.Vector3f;

/**
 * @author Romain find the next free source to play this sound if no source were
 *         available, it gets a random one and play on it
 */
public class ALSourcePool extends ALObject {
	private ArrayList<ALSource> _sources;
	private int _source_count;

	public ALSourcePool() {
		this(16);
	}

	public ALSourcePool(int count) {
		count = this.boundSourceCount(count);
		this._sources = new ArrayList<ALSource>(count);
		for (int i = 0; i < count; i++) {
			this._sources.add(ALH.alhGenSource());
		}
		this._source_count = count;
	}

	public void grow(int source_count) {
		source_count = this.boundSourceCount(source_count);
		if (source_count < this._source_count) {
			while (this._source_count != source_count) {
				ALSource source = this._sources.remove(0);
				source.destroy();
				--this._source_count;
			}
		} else if (source_count > this._source_count) {
			while (this._source_count != source_count) {
				this._sources.add(ALH.alhGenSource());
				++this._source_count;
			}
		}
	}

	private int boundSourceCount(int source_count) {
		if (source_count <= 0) {
			return (1);
		}
		if (source_count > 64) {
			return (64);
		}
		return (source_count);
	}

	public ALSource playAt(ALSound sound) {
		return (this.playAt(sound, new Vector3f(0, 0, 0)));
	}

	public ALSource playAt(ALSound sound, Vector3f pos) {
		return (this.playAt(sound, pos, new Vector3f(0, 0, 0)));
	}

	public ALSource playAt(ALSound sound, Vector3f pos, Vector3f velocity) {
		ALSource source = this.getNextSource();
		source.playAt(sound, pos, velocity);
		return (source);
	}

	public ALSource play(ALSound sound) {
		return (this.play(sound, 1.0f, 1.0f));
	}

	public ALSource play(ALSound sound, float gain, float pitch) {
		ALSource source = this.getNextSource();
		source.play(sound, gain, pitch);
		return (source);
	}

	public ALSource getNextSource() {
		for (ALSource source : this._sources) {
			if (!source.isPlaying()) {
				return (source);
			}
		}
		ALSource source = this._sources.get((int) (System.currentTimeMillis() % this._sources.size()));
		return (source);

	}

	public void stop(ALSource source) {
		source.stop();
	}

	public boolean isPlaying() {
		for (ALSource source : this._sources) {
			if (source.isPlaying()) {
				return (true);
			}
		}
		return (false);
	}

	@Override
	protected void onDestroy() {
		for (ALSource source : this._sources) {
			source.destroy();
		}
	}

	public void stopAll() {
		for (ALSource source : this._sources) {
			source.stop();
		}
	}
}
