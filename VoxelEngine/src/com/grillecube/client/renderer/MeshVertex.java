package com.grillecube.client.renderer;

import java.nio.ByteBuffer;

public abstract class MeshVertex {

	public MeshVertex() {
	}

	public abstract void store(ByteBuffer buffer);

}
