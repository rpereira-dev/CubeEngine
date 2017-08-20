package com.grillecube.client.renderer.lines;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

// line object
public class Line {

	public Vector3f posa, posb;
	public Vector4f colora, colorb;

	public Line(Vector3f posa, Vector4f colora, Vector3f posb, Vector4f colorb) {
		this.posa = posa;
		this.posb = posb;
		this.colora = colora;
		this.colorb = colorb;
	}
}