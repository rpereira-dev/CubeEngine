package com.grillecube.engine.resources;

import com.grillecube.engine.Logger;

public class Compress {

	/**
	 * on a terrain's blocks, the new compressed array takes 16 times less
	 * memory space
	 */
	public static final short[] compressShortArray(short[] data) {

		short[] array = new short[data.length * 2];
		int index = 0;
		int addr = 0;
		while (addr < data.length) {
			short value = data[addr++];
			short count = 1;
			while (count < Short.MAX_VALUE && addr < data.length && data[addr] == value) {
				++count;
				++addr;
			}
			array[index++] = count;
			array[index++] = value;
		}

		short[] narray = new short[index];
		System.arraycopy(array, 0, narray, 0, index);
		return (narray);

	}

	public static final short[] decompressShortArray(short[] data, int dstlen) {

		short[] dst = new short[dstlen];
		int index = 0;
		int start = 0;
		int end = 0;

		// for each integer of the compressed array
		while (index < data.length) {

			// get the number of value occurences
			short count = data[index++];
			// get the value
			short value = data[index++];

			// set next bounds to copy
			end += count;

			// do the copy
			for (int i = start; i < end; i++) {
				dst[i] = value;
			}

			// set next bounds to copy
			start += count;
		}
		return (dst);

	}

	/**
	 * on a terrain's blocks, the new compressed array takes 16 times less
	 * memory space
	 */
	public static final int[] compressIntArray(int[] data) {

		int[] array = new int[data.length * 2];
		int index = 0;
		int addr = 0;
		while (addr < data.length) {
			int value = data[addr++];
			int count = 1;
			while (count < Integer.MAX_VALUE && addr < data.length && data[addr] == value) {
				++count;
				++addr;
			}
			array[index++] = count;
			array[index++] = value;
		}

		int[] narray = new int[index];
		System.arraycopy(array, 0, narray, 0, index);
		return (narray);
	}

	public static final int[] decompressIntArray(int[] data, int dstlen) {

		int[] dst = new int[dstlen];
		int index = 0;
		int start = 0;
		int end = 0;

		// for each integer of the compressed array
		while (index < data.length) {

			// get the number of value occurences
			int count = data[index++];
			// get the value
			int value = data[index++];

			// set next bounds to copy
			end += count;

			// do the copy
			for (int i = start; i < end; i++) {
				dst[i] = value;
			}

			// set next bounds to copy
			start += count;
		}
		return (dst);
	}

	/**
	 * on a terrain's lights, the new compressed array takes 10 times less
	 * memory space
	 */
	public static final byte[] compressByteArray(byte[] data) {
		byte[] array = new byte[data.length * 2];
		int index = 0;
		int addr = 0;
		while (addr < data.length) {
			byte value = data[addr++];
			byte count = 1;
			while (count < Byte.MAX_VALUE && addr < data.length && data[addr] == value) {
				++count;
				++addr;
			}
			array[index++] = count;
			array[index++] = value;
		}

		byte[] narray = new byte[index];
		System.arraycopy(array, 0, narray, 0, index);
		return (narray);

	}

	public static final byte[] decompressByteArray(byte[] data, int dstlen) {

		byte[] dst = new byte[dstlen];
		int index = 0;
		int start = 0;
		int end = 0;

		// for each integer of the compressed array
		while (index < data.length) {

			// get the number of value occurences
			byte count = data[index++];
			// get the value
			byte value = data[index++];

			// set next bounds to copy
			end += count;

			// do the copy
			for (int i = start; i < end; i++) {
				dst[i] = value;
			}

			// set next bounds to copy
			start += count;
		}
		return (dst);

	}

	public static final byte[] compressBooleanArray(boolean[] data) {

		byte[] array = new byte[data.length * 2];
		int index = 0;
		int addr = 0;
		while (addr < data.length) {
			boolean value = data[addr++];
			byte count = 1;
			while (count < Byte.MAX_VALUE && addr < data.length && data[addr] == value) {
				++count;
				++addr;
			}
			array[index++] = (byte) (value ? -count : count);
		}

		byte[] narray = new byte[index];
		System.arraycopy(array, 0, narray, 0, index);
		return (narray);
	}

	public static final boolean[] decompressBooleanArray(byte[] data, int dstlen) {

		boolean[] dst = new boolean[dstlen];
		int index = 0;
		int start = 0;
		int end = 0;

		// for each integer of the compressed array
		while (index < data.length) {

			// get the number of value occurences
			int count = data[index++];
			// get the value
			boolean value = false;

			if (count < 0) {
				count = -count;
				value = true;
			}

			// set next bounds to copy
			end += count;

			// do the copy
			for (int i = start; i < end; i++) {
				dst[i] = value;
			}

			// set next bounds to copy
			start += count;
		}
		return (dst);

	}
}
