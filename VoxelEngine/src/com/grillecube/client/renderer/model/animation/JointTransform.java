package com.grillecube.client.renderer.model.animation;

import com.grillecube.common.maths.Quaternion;
import com.grillecube.common.maths.Vector3f;

public class JointTransform {

	private Vector3f translation;
	private Quaternion rotation;

	public Vector3f getTranslation() {
		return translation;
	}

	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}

	public Quaternion getRotation() {
		return (this.rotation);
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}
}
