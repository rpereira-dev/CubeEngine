package com.grillecube.common.maths;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Romain
 *
 *         A small maths library based on my C implementation
 *
 *         https://github.com/rpereira-dev/C_maths/
 */
public class Maths {

	public static float[] SIN_TABLE = new float[180];
	public static float DEG_TO_RAD = 0.01745329251f;
	public static float RAD_TO_DEG = 57.2957795131f;
	public static float PI = (float) Math.PI;
	public static float PI_2 = (float) (Math.PI / 2);
	public static float PI_4 = (float) (Math.PI / 4);
	public static final float ESPILON = 0.000000000000000000000000000000000000000000001f;

	static {

		int i, j;
		for (i = 0, j = 0; i < 180; i++) {
			SIN_TABLE[j++] = (float) Math.sin((double) DEG_TO_RAD * i);
		}
	}

	public static float atan(float x) {
		float xabs = abs(x);
		return (PI_4 * x - x * (xabs - 1) * (0.2447f + 0.0663f * xabs));
	}

	public static float acos(float x) {
		return ((-0.69813170079773212f * x * x - 0.87266462599716477f) * x + PI_2);
	}

	public static float asin(float x) {
		return (-acos(x) + PI_2);
	}

	public static float tan(float x) {
		return (sin(x) / cos(x));
	}

	public static float sin(float x) {
		int index = ((int) (RAD_TO_DEG * abs(x)) % 360);
		return (index > 180 ? -SIN_TABLE[index - 180] : SIN_TABLE[index]);
	}

	public static float cos(float x) {
		return (sin(x + PI_2));
	}

	public static float sqrtFast(float x) {
		int i = Float.floatToIntBits(x);
		i += 127 << 23;
		i >>= 1;
		return (Float.intBitsToFloat(i));
	}

	public static float sqrt(float f) {
		return ((float) Math.sqrt((double) f));
	}

	public static float sqrt(double d) {
		return ((float) Math.sqrt(d));
	}

	public static int floor(float f) {
		int i = (int) f;
		return (f < (float) i ? i - 1 : i);
	}

	/**
	 * 
	 * examples: floor(1.5f, 1.0f) -> 1.0f ; floor(1.6f, 0.25f) -> 1.5f;
	 * floor(1.21f, 0.1f) -> 1.2f
	 * 
	 * @param f
	 *            : the number to floor
	 * @param unit
	 *            : the size unit
	 * @return
	 */
	public static float floor(float f, float unit) {
		return ((float) Maths.floor(f) + ((int) ((f % 1.0f) / unit)) * unit);
	}

	public static int floor(double d) {
		int i = (int) d;
		return (d < (double) i ? i - 1 : i);
	}

	public static int abs(int i) {
		return ((i >= 0) ? i : -i);
	}

	public static float abs(float f) {
		return (f == 0.0f ? 0.0f : (f > 0.0f) ? f : -f);
	}

	public static long abs(long l) {
		return ((l >= 0) ? l : -l);
	}

	public static double abs(double d) {
		return ((d >= 0) ? d : -d);
	}

	public static int ceil(float f) {
		int i = (int) f;
		return ((f > (float) i) ? i + 1 : i);
	}

	public static int ceil(double d) {
		int i = (int) d;
		return ((d > (double) i) ? i + 1 : i);
	}

	public static int clamp(int x, int a, int b) {
		return ((x < a) ? a : (x > b) ? b : x);
	}

	public static float clamp(float x, float a, float b) {
		return ((x < a) ? a : (x > b) ? b : x);
	}

	public static double clamp(double x, double a, double b) {
		return ((x < a) ? a : (x > b) ? b : x);
	}

	/** e.g : Maths.approximate(1.41689, 10.0f) returns '1.4f' */
	public static float approximatate(float f, float decimal) {
		return (Math.round(f * decimal) / decimal);
	}

	public static int sign(float f) {
		return (f < 0 ? -1 : 1);
	}

	public static int sign(double d) {
		return (d < 0 ? -1 : 1);
	}

	public static int sign(int i) {
		return (i < 0 ? -1 : 1);
	}

	public static int min(int a, int b) {
		return (a < b ? a : b);
	}

	public static float min(float a, float b) {
		return (a < b ? a : b);
	}

	public static double min(double a, double b) {
		return (a < b ? a : b);
	}

	public static int max(int a, int b) {
		return (a > b ? a : b);
	}

	public static float max(float a, float b) {
		return (a > b ? a : b);
	}

	public static double max(double a, double b) {
		return (a > b ? a : b);
	}

	public static int intbound(float s, float ds) {
		// Find the smallest positive t such that s+t*ds is an integer.
		if (ds < 0) {
			return intbound(-s, -ds);
		} else {
			s = mod(s, 1);
			// problem is now s+t*ds = 1
			return (int) ((1 - s) / ds);
		}
	}

	public static int signum(float x) {
		return (x < 0 ? -1 : x > 0 ? 1 : 0);
	}

	public static float mod(float s, int modulus) {
		return (s % modulus + modulus) % modulus;
	}

	public Maths() {

	}

	@Test
	public final void processTests() {
		float e = 0.000000000000001f;
		Assert.assertEquals(0.0f, Maths.approximatate(0.123456789f, 1.0f), e);
		Assert.assertEquals(0.1f, Maths.approximatate(0.123456789f, 10.0f), e);
		Assert.assertEquals(0.12f, Maths.approximatate(0.123456789f, 100.0f), e);
		Assert.assertEquals(0.123f, Maths.approximatate(0.123456789f, 1000.0f), e);

		Assert.assertEquals(4.25f, Maths.floor(4.26f, 0.25f), e);
		Assert.assertEquals(4.25f, Maths.floor(4.49f, 0.25f), e);
		Assert.assertEquals(4.50f, Maths.floor(4.50f, 0.25f), e);

	}

	/** return the given angle normalize in [-PI, PI] */
	public static float angleModPI(float angle) {
		return (float) Math.atan2(Math.sin(angle), Math.cos(angle));
	}

}
