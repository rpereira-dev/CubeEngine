package com.grillecube.engine.renderer.model.items;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.renderer.model.Model;

/**
 * a class which represent item model (held in hand, wear on body, hat,
 * armor...)
 */
public class ModelItem extends Model {

	public ModelItem() {
		super();
	}

	/** the attach point for this model */
	private Vector3f _attachment_pos = new Vector3f();

	public Vector3f getAttachmentPos() {
		return (this._attachment_pos);
	}

	public void setAttachmentPos(float x, float y, float z) {
		this._attachment_pos.set(x, y, z);
	}

	public void setAttachmentPos(Vector3f vec) {
		this.setAttachmentPos(vec.x, vec.y, vec.z);
	}
}
