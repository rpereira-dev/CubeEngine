/*
 * Copyright 1995-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.grillecube.common.utils;

import java.awt.image.ColorModel;

/**
 * color utilities, some functions are taken from java.awt
 * 
 * @author Romain PEREIRA
 */
public class Color {

	/**
	 * The color white. In the default sRGB space.
	 */
	public final static Color white = new Color(255, 255, 255);

	/**
	 * The color white. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color WHITE = white;

	/**
	 * The color light gray. In the default sRGB space.
	 */
	public final static Color lightGray = new Color(192, 192, 192);

	/**
	 * The color light gray. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color LIGHT_GRAY = lightGray;

	/**
	 * The color gray. In the default sRGB space.
	 */
	public final static Color gray = new Color(128, 128, 128);

	/**
	 * The color gray. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color GRAY = gray;

	/**
	 * The color dark gray. In the default sRGB space.
	 */
	public final static Color darkGray = new Color(64, 64, 64);

	/**
	 * The color dark gray. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color DARK_GRAY = darkGray;

	/**
	 * The color black. In the default sRGB space.
	 */
	public final static Color black = new Color(0, 0, 0);

	/**
	 * The color black. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color BLACK = black;

	/**
	 * The color red. In the default sRGB space.
	 */
	public final static Color red = new Color(255, 0, 0);

	/**
	 * The color red. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color RED = red;

	/**
	 * The color pink. In the default sRGB space.
	 */
	public final static Color pink = new Color(255, 175, 175);

	/**
	 * The color pink. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color PINK = pink;

	/**
	 * The color orange. In the default sRGB space.
	 */
	public final static Color orange = new Color(255, 200, 0);

	/**
	 * The color orange. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color ORANGE = orange;

	/**
	 * The color yellow. In the default sRGB space.
	 */
	public final static Color yellow = new Color(255, 255, 0);

	/**
	 * The color yellow. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color YELLOW = yellow;

	/**
	 * The color green. In the default sRGB space.
	 */
	public final static Color green = new Color(0, 255, 0);

	/**
	 * The color green. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color GREEN = green;

	/**
	 * The color magenta. In the default sRGB space.
	 */
	public final static Color magenta = new Color(255, 0, 255);

	/**
	 * The color magenta. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color MAGENTA = magenta;

	/**
	 * The color cyan. In the default sRGB space.
	 */
	public final static Color cyan = new Color(0, 255, 255);

	/**
	 * The color cyan. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color CYAN = cyan;

	/**
	 * The color blue. In the default sRGB space.
	 */
	public final static Color blue = new Color(0, 0, 255);

	/**
	 * The color blue. In the default sRGB space.
	 * 
	 * @since 1.4
	 */
	public final static Color BLUE = blue;

	/**
	 * The color argb.
	 * 
	 * @see #getRGB
	 */
	private int argb;

	public Color(int argb) {
		this.argb = argb;
	}

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, int a) {
		this(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0));
	}

	/**
	 * Creates an sRGB color with the specified combined RGBA argb consisting of
	 * the alpha component in bits 24-31, the red component in bits 16-23, the
	 * green component in bits 8-15, and the blue component in bits 0-7. If the
	 * <code>hasalpha</code> argument is <code>false</code>, alpha is defaulted
	 * to 255.
	 *
	 * @param rgba
	 *            the combined RGBA components
	 * @param hasalpha
	 *            <code>true</code> if the alpha bits are valid;
	 *            <code>false</code> otherwise
	 * @see java.awt.image.ColorModel#getRGBdefault
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getAlpha
	 * @see #getRGB
	 */
	public Color(int rgba, boolean hasalpha) {
		if (hasalpha) {
			argb = rgba;
		} else {
			argb = 0xff000000 | rgba;
		}
	}

	/**
	 * Creates an opaque sRGB color with the specified red, green, and blue
	 * argbs in the range (0.0 - 1.0). Alpha is defaulted to 1.0. The actual
	 * color used in rendering depends on finding the best match given the color
	 * space available for a particular output device.
	 *
	 * @throws IllegalArgumentException
	 *             if <code>r</code>, <code>g</code> or <code>b</code> are
	 *             outside of the range 0.0 to 1.0, inclusive
	 * @param r
	 *            the red component
	 * @param g
	 *            the green component
	 * @param b
	 *            the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGB
	 */
	public Color(float r, float g, float b) {
		this((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}

	/**
	 * Creates an sRGB color with the specified red, green, blue, and alpha
	 * argbs in the range (0.0 - 1.0). The actual color used in rendering
	 * depends on finding the best match given the color space available for a
	 * particular output device.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>r</code>, <code>g</code> <code>b</code> or
	 *             <code>a</code> are outside of the range 0.0 to 1.0, inclusive
	 * @param r
	 *            the red component
	 * @param g
	 *            the green component
	 * @param b
	 *            the blue component
	 * @param a
	 *            the alpha component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getAlpha
	 * @see #getRGB
	 */
	public Color(float r, float g, float b, float a) {
		this((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5), (int) (a * 255 + 0.5));
	}

	/**
	 * Returns the red component in the range 0-255 in the default sRGB space.
	 * 
	 * @return the red component.
	 * @see #getRGB
	 */
	public int getRed() {
		return (getARGB() >> 16) & 0xFF;
	}

	/**
	 * Returns the green component in the range 0-255 in the default sRGB space.
	 * 
	 * @return the green component.
	 * @see #getRGB
	 */
	public int getGreen() {
		return (getARGB() >> 8) & 0xFF;
	}

	/**
	 * Returns the blue component in the range 0-255 in the default sRGB space.
	 * 
	 * @return the blue component.
	 * @see #getRGB
	 */
	public int getBlue() {
		return (getARGB() >> 0) & 0xFF;
	}
	
	public final float getR() {
		return (this.getRed() / 255.0f);
	}
	
	public final float getG() {
		return (this.getGreen() / 255.0f);
	}
	
	public final float getB() {
		return (this.getBlue() / 255.0f);
	}
	
	public final float getA() {
		return (this.getAlpha() / 255.0f);
	}

	/**
	 * Returns the alpha component in the range 0-255.
	 * 
	 * @return the alpha component.
	 * @see #getRGB
	 */
	public int getAlpha() {
		return (getARGB() >> 24) & 0xff;
	}

	/**
	 * Returns the RGB argb representing the color in the default sRGB
	 * {@link ColorModel}. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green,
	 * 0-7 are blue).
	 * 
	 * @return the RGB argb of the color in the default sRGB
	 *         <code>ColorModel</code>.
	 * @see java.awt.image.ColorModel#getRGBdefault
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @since JDK1.0
	 */
	public int getARGB() {
		return argb;
	}

	private static final double FACTOR = 0.7;

	/**
	 * Creates a new <code>Color</code> that is a brighter version of this
	 * <code>Color</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of this <code>Color</code> to create a brighter version of
	 * this <code>Color</code>. Although <code>brighter</code> and
	 * <code>darker</code> are inverse operations, the results of a series of
	 * invocations of these two methods might be inconsistent because of
	 * rounding errors.
	 * 
	 * @return a new <code>Color</code> object that is a brighter version of
	 *         this <code>Color</code>.
	 * @see java.awt.Color#darker
	 * @since JDK1.0
	 */
	public Color brighter() {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();

		/*
		 * From 2D group: 1. black.brighter() should return grey 2. applying
		 * brighter to blue will always return blue, brighter 3. non pure color
		 * (non zero rgb) will eventually return white
		 */
		int i = (int) (1.0 / (1.0 - FACTOR));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i);
		}
		if (r > 0 && r < i)
			r = i;
		if (g > 0 && g < i)
			g = i;
		if (b > 0 && b < i)
			b = i;

		return new Color(Math.min((int) (r / FACTOR), 255), Math.min((int) (g / FACTOR), 255),
				Math.min((int) (b / FACTOR), 255));
	}

	/**
	 * Creates a new <code>Color</code> that is a darker version of this
	 * <code>Color</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of this <code>Color</code> to create a darker version of this
	 * <code>Color</code>. Although <code>brighter</code> and
	 * <code>darker</code> are inverse operations, the results of a series of
	 * invocations of these two methods might be inconsistent because of
	 * rounding errors.
	 * 
	 * @return a new <code>Color</code> object that is a darker version of this
	 *         <code>Color</code>.
	 * @see java.awt.Color#brighter
	 * @since JDK1.0
	 */
	public Color darker() {
		return new Color(Math.max((int) (getRed() * FACTOR), 0), Math.max((int) (getGreen() * FACTOR), 0),
				Math.max((int) (getBlue() * FACTOR), 0));
	}

	/**
	 * Computes the hash code for this <code>Color</code>.
	 * 
	 * @return a hash code argb for this object.
	 * @since JDK1.0
	 */
	public int hashCode() {
		return argb;
	}

	/**
	 * Determines whether another object is equal to this <code>Color</code>.
	 * <p>
	 * The result is <code>true</code> if and only if the argument is not
	 * <code>null</code> and is a <code>Color</code> object that has the same
	 * red, green, blue, and alpha argbs as this object.
	 * 
	 * @param obj
	 *            the object to test for equality with this <code>Color</code>
	 * @return <code>true</code> if the objects are the same; <code>false</code>
	 *         otherwise.
	 * @since JDK1.0
	 */
	public boolean equals(Object obj) {
		return obj instanceof Color && ((Color) obj).argb == this.argb;
	}

	/**
	 * Returns a string representation of this <code>Color</code>. This method
	 * is intended to be used only for debugging purposes. The content and
	 * format of the returned string might vary between implementations. The
	 * returned string might be empty but cannot be <code>null</code>.
	 *
	 * @return a string representation of this <code>Color</code>.
	 */
	public String toString() {
		return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
	}

	/**
	 * Converts a <code>String</code> to an integer and returns the specified
	 * opaque <code>Color</code>. This method handles string formats that are
	 * used to represent octal and hexadecimal numbers.
	 * 
	 * @param nm
	 *            a <code>String</code> that represents an opaque color as a
	 *            24-bit integer
	 * @return the new <code>Color</code> object.
	 * @see java.lang.Integer#decode
	 * @exception NumberFormatException
	 *                if the specified string cannot be interpreted as a
	 *                decimal, octal, or hexadecimal integer.
	 * @since JDK1.1
	 */
	public static Color decode(String nm) throws NumberFormatException {
		Integer intval = Integer.decode(nm);
		int i = intval.intValue();
		return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
	}

	/**
	 * Finds a color in the system properties.
	 * <p>
	 * The argument is treated as the name of a system property to be obtained.
	 * The string argb of this property is then interpreted as an integer which
	 * is then converted to a <code>Color</code> object.
	 * <p>
	 * If the specified property is not found or could not be parsed as an
	 * integer then <code>null</code> is returned.
	 * 
	 * @param nm
	 *            the name of the color property
	 * @return the <code>Color</code> converted from the system property.
	 * @see java.lang.System#getProperty(java.lang.String)
	 * @see java.lang.Integer#getInteger(java.lang.String)
	 * @see java.awt.Color#Color(int)
	 * @since JDK1.0
	 */
	public static Color getColor(String nm) {
		return getColor(nm, null);
	}

	/**
	 * Finds a color in the system properties.
	 * <p>
	 * The first argument is treated as the name of a system property to be
	 * obtained. The string argb of this property is then interpreted as an
	 * integer which is then converted to a <code>Color</code> object.
	 * <p>
	 * If the specified property is not found or cannot be parsed as an integer
	 * then the <code>Color</code> specified by the second argument is returned
	 * instead.
	 * 
	 * @param nm
	 *            the name of the color property
	 * @param v
	 *            the default <code>Color</code>
	 * @return the <code>Color</code> converted from the system property, or the
	 *         specified <code>Color</code>.
	 * @see java.lang.System#getProperty(java.lang.String)
	 * @see java.lang.Integer#getInteger(java.lang.String)
	 * @see java.awt.Color#Color(int)
	 * @since JDK1.0
	 */
	public static Color getColor(String nm, Color v) {
		Integer intval = Integer.getInteger(nm);
		if (intval == null) {
			return v;
		}
		int i = intval.intValue();
		return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
	}

	/**
	 * Finds a color in the system properties.
	 * <p>
	 * The first argument is treated as the name of a system property to be
	 * obtained. The string argb of this property is then interpreted as an
	 * integer which is then converted to a <code>Color</code> object.
	 * <p>
	 * If the specified property is not found or could not be parsed as an
	 * integer then the integer argb <code>v</code> is used instead, and is
	 * converted to a <code>Color</code> object.
	 * 
	 * @param nm
	 *            the name of the color property
	 * @param v
	 *            the default color argb, as an integer
	 * @return the <code>Color</code> converted from the system property or the
	 *         <code>Color</code> converted from the specified integer.
	 * @see java.lang.System#getProperty(java.lang.String)
	 * @see java.lang.Integer#getInteger(java.lang.String)
	 * @see java.awt.Color#Color(int)
	 * @since JDK1.0
	 */
	public static Color getColor(String nm, int v) {
		Integer intval = Integer.getInteger(nm);
		int i = (intval != null) ? intval.intValue() : v;
		return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, (i >> 0) & 0xFF);
	}
}