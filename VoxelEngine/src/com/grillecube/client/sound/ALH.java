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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.maths.Vector3f;

/**
 * 
 * @author Romain OpenAL Helper wrapper
 */
public class ALH {

	/** context stuff */
	private static ALContext _context;
	private static ArrayList<ALObject> _objects;
	private static HashMap<String, ALSound> _sounds;

	/** initialize openal */
	public static void alhInit() {
		Logger.get().log(Level.FINE, "Initializing OpenAL.");

		_objects = new ArrayList<ALObject>();
		_sounds = new HashMap<String, ALSound>(128);
		_context = ALContext.create().makeCurrent();

		String version = ALH.alhGetString(AL10.AL_VERSION);
		String vendor = ALH.alhGetString(AL10.AL_VENDOR);
		String renderer = ALH.alhGetString(AL10.AL_RENDERER);
		Logger.get().log(Level.FINE, "OpenAL Version: " + version);
		Logger.get().log(Level.FINE, "OpenAL Vendor: " + vendor);
		Logger.get().log(Level.FINE, "OpenAL Renderer: " + renderer);
		ALH.alhCheckError("Initialization");
	}

	public static ALListener alhGetListener() {
		return (_context.getListener());
	}

	public static ALContext alhGetContext() {
		return (_context);
	}

	public static String alhGetString(int stringID) {
		return (AL10.alGetString(stringID));
	}

	/** get the device with this specifier */
	public static ALDevice alhGetDefaultDevice() {
		ALDevice device = new ALDevice(ALC10.alcOpenDevice((ByteBuffer) null));
		_objects.add(device);
		return (device);
	}

	/** generate a new al buffer */
	public static ALBuffer alhGenBuffer() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		AL10.alGenBuffers(buffer);
		ALBuffer albuffer = new ALBuffer(buffer.get());
		_objects.add(albuffer);
		return (albuffer);
	}

	/** set the buffer data from the given sound */
	public static void alhBufferData(ALBuffer buffer, ALSound sound) {
		AL10.alBufferData(buffer.getID(), sound.getFormat(), sound.getData(), sound.getFrequence());
	}

	/** generate a sound */
	public static ALSound alhLoadSound(String filepath) {
		ALSound sound = _sounds.get(filepath);
		if (sound == null) {
			sound = new ALSoundWave(filepath);
			_objects.add(sound);
			_sounds.put(filepath, sound);
		}
		return (sound);
	}

	/** create a new source */
	public static ALSource alhGenSource() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(buffer);
		ALSource alsource = new ALSource(buffer.get());
		_objects.add(alsource);
		return (alsource);
	}

	/** generate a new sound pool */
	public static ALSourcePool alhGenSourcePool(int size) {
		ALSourcePool pool = new ALSourcePool(size);
		_objects.add(pool);
		return (pool);
	}

	/** play a sound */
	public static void alhPlaySound(ALSourcePool pool, ALSound sound) {
		pool.play(sound);
	}

	/** play a sound */
	public static void alhPlaySound(ALSource source, ALSound sound) {
		source.setFloat(AL10.AL_PITCH, 1.0f);
		source.setFloat(AL10.AL_GAIN, 1.0f);
		source.play(sound);
	}

	/** stop open al */
	public static void alhStop() {
		Logger.get().log(Level.FINE, "Stopping OpenAL");

		if (_objects == null) {
			Logger.get().log(Level.WARNING, "Tried to stop OpenAL, but wasnt started!");
			return;
		}

		for (ALObject object : _objects) {
			object.destroy();
		}
		Logger.get().log(Level.FINE, "OpenAL stopped. " + _objects.size() + " object destroyed.");

		_objects = null;
		_context.destroy();
	}

	public static void alhSourcePlay(ALSource source) {
		AL10.alSourcePlay(source.getID());
	}

	public static void alhSourceStop(ALSource source) {
		AL10.alSourceStop(source.getID());
	}

	/** set listener orientation in 3D */
	public static void alhSetListenerPosition(Vector3f pos) {
		AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
	}

	/** set listener orientation in 3D */
	public static void alhSetListenerOrientation(Vector3f lookvec) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(6);
		buffer.put(lookvec.x);
		buffer.put(lookvec.y);
		buffer.put(lookvec.z);
		buffer.put(0);
		buffer.put(1);
		buffer.put(0);
		buffer.flip();
		AL10.alListenerfv(AL10.AL_ORIENTATION, buffer);
	}

	/** check the open al error in the current context : print and returns it */
	public static int alhCheckError(String label) {
		int err = AL10.alGetError();
		if (err != AL10.AL_NO_ERROR) {
			Logger.get().log(Level.WARNING, label + " : OpenAL error occured : " + AL10.alGetString(err));
		}
		return (err);
	}

	/** get an integer value with the given id frm the given source */
	public static int alhGetSourcei(ALSource source, int name) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		AL10.alGetSourcei(source.getID(), name, buffer);
		return (buffer.get());
	}

	static final String TEST_SOUND = "C:/Users/Romain/AppData/Roaming/VoxelEngine/assets/VoxelEngine/sounds/pop.wav";

	// test openal
	public static void main(String[] args) throws InterruptedException {
		ALH.alhInit();
		ALH.alhCheckError("post init");

		ALH.alhGetListener().setPosition(new Vector3f(0, 0, 0));
		ALH.alhGetListener().setOrientation(new Vector3f(1, 0, 0));

		testOneSound();
		testSourcePool();

		ALH.alhCheckError("pre stop");
		ALH.alhStop();
	}

	private static void testSourcePool() throws InterruptedException {
		ALH.alhSetListenerPosition(new Vector3f(0, 0, 0));
		ALH.alhSetListenerOrientation(new Vector3f(1, 0, 0));

		ALSound file = ALH.alhLoadSound(TEST_SOUND);
		ALSourcePool pool = ALH.alhGenSourcePool(16);
		pool.play(file);

		int i = 0;
		while (pool.isPlaying() && i < 5) {
			Thread.sleep(1000);
			pool.play(file);
			++i;
		}

		if (i != 5) {
			Logger.get().log(Level.ERROR, "SOMETHING WRONG HAPPENED ON 'testSourcePool()' test");
		}

		while (pool.isPlaying()) {
			Thread.sleep(1000);
		}
	}

	private static void testOneSound() throws InterruptedException {
		ALSound file = ALH.alhLoadSound(TEST_SOUND);
		ALSource source = ALH.alhGenSource();
		ALH.alhPlaySound(source, file);

		while (source.isPlaying()) {
			Thread.sleep(100);
		}

	}
}
