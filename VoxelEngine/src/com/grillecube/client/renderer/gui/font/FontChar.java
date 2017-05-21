package com.grillecube.client.renderer.gui.font;

public class FontChar {
	public int ascii;
	public float uvx;
	public float uvy;
	public float uvwidth;
	public float uvheight;
	public float width;
	public float height;
	public float xoffset;
	public float yoffset;
	public float xadvance;

	public FontChar() {
	}
	
	public FontChar clone() {
		FontChar c = new FontChar();
		c.ascii = this.ascii;
		c.uvx = this.uvx;
		c.uvy = this.uvy;
		c.uvwidth = this.uvwidth;
		c.uvheight = this.uvheight;
		c.width = this.width;
		c.height = this.height;
		c.xoffset = this.xoffset;
		c.yoffset = this.yoffset;
		c.xadvance = this.xadvance;
		return (c);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("{");
		builder.append("ascii:");
		builder.append(ascii);
		builder.append("} ");

		builder.append("{");
		builder.append("uvx:");
		builder.append(uvx);
		builder.append("} ");

		builder.append("{");
		builder.append("uvy:");
		builder.append(uvy);
		builder.append("} ");

		builder.append("{");
		builder.append("uvwidth:");
		builder.append(uvwidth);
		builder.append("} ");

		builder.append("{");
		builder.append("uvheight:");
		builder.append(uvheight);
		builder.append("} ");

		builder.append("{");
		builder.append("width:");
		builder.append(width);
		builder.append("} ");

		builder.append("{");
		builder.append("height:");
		builder.append(height);
		builder.append("} ");

		builder.append("{");
		builder.append("xoffset:");
		builder.append(xoffset);
		builder.append("} ");

		builder.append("{");
		builder.append("yoffset:");
		builder.append(yoffset);
		builder.append("} ");

		builder.append("{");
		builder.append("xadvance:");
		builder.append(xadvance);
		builder.append("} ");

		return (builder.toString());
	}
}