package com.grillecube.client.renderer.model;

import com.grillecube.common.maths.Vector3f;

public class ModelAttachmentPoint {

	private final String _name;
	private final Vector3f _point;

	public ModelAttachmentPoint(String name, Vector3f point) {
		this._name = name;
		this._point = point;
	}

	public String getName() {
		return (this._name);
	}

	public Vector3f getPoint() {
		return (this._point);
	}

	@Override
	public String toString() {
		// return ("ModelAttachmentPoint{" + this.getName() + ":" +
		// this.getPoint() + "}");
		return (this.getName());
	}
}
