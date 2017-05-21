package com.grillecube.common.world;

import java.util.Random;

/** http://stackoverflow.com/questions/18279456/any-simplex-noise-tutorials-or-resources */
public class SimplexNoise {

	SimplexNoiseOctave[] octaves;
	double[] frequencys;
	double[] amplitudes;

	int largestFeature;
	double persistence;
	int seed;

	public SimplexNoise(int largestFeature, double persistence, int seed) {
		this.largestFeature = largestFeature;
		this.persistence = persistence;
		this.seed = seed;

		// recieves a number (eg 128) and calculates what power of 2 it is (eg
		// 2^7)
		int numberOfOctaves = (int) Math.ceil(Math.log10(largestFeature) / Math.log10(2));

		octaves = new SimplexNoiseOctave[numberOfOctaves];
		frequencys = new double[numberOfOctaves];
		amplitudes = new double[numberOfOctaves];

		Random rnd = new Random(seed);

		for (int i = 0; i < numberOfOctaves; i++) {
			octaves[i] = new SimplexNoiseOctave(rnd.nextInt());

			frequencys[i] = Math.pow(2, i);
			amplitudes[i] = Math.pow(persistence, octaves.length - i);
		}
	}

	public SimplexNoise(int largestFeature, double persistence) {
		this(largestFeature, persistence, (int) System.currentTimeMillis());
	}

	public double getNoise(int x, int y) {

		double result = 0;

		for (int i = 0; i < octaves.length; i++) {
			
			result = result + octaves[i].noise(x / frequencys[i], y / frequencys[i]) * amplitudes[i];
		}

		return result;

	}

	public double getNoise(float x, float y, float z) {

		double result = 0;

		for (int i = 0; i < octaves.length; i++) {

			result = result + octaves[i].noise(x / frequencys[i], y / frequencys[i], z / frequencys[i]) * amplitudes[i];
		}

		return result;

	}
}