package com.grillecube.engine.renderer.model;

import com.grillecube.engine.maths.Vector3f;

/**
 * represent a point, size and rotation on a ModelPart. A model can be attached
 * to this point and will be rotated / scaled when attached
 */
public class ModelPartAttachmentPoint {

	/** name */
	private final String _name;

	/** attachment position */
	private Vector3f _attachment_pos = new Vector3f();

	/** rotation */
	private Vector3f _attachment_rot = new Vector3f();

	/** scale */
	private Vector3f _attachment_scale = new Vector3f();

	public ModelPartAttachmentPoint(String name) {
		this._name = name;
	}

	public String getName() {
		return (this._name);
	}

	public Vector3f getPos() {
		return (this._attachment_pos);
	}

	public Vector3f getRot() {
		return (this._attachment_rot);
	}

	public Vector3f getScale() {
		return (this._attachment_scale);
	}
}