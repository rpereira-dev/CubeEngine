package com.grillecube.client.renderer;

import java.nio.ByteBuffer;

public class MeshTriangle<T extends MeshVertex> {

	public final T v0;
	public final T v1;
	public final T v2;

	public MeshTriangle(T v0, T v1, T v2) {
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
	}

	/** put this triangle vertices to the given buffer */
	public final void store(ByteBuffer buffer) {
		this.v0.store(buffer);
		this.v1.store(buffer);
		this.v2.store(buffer);
	}
}
