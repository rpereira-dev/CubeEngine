/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.model;

import java.nio.ByteBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;

import com.grillecube.client.renderer.model.builder.ColorInt;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.client.renderer.model.json.ModelMeshVertex;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;

/** an object which is used to generate terrain meshes dynamically */
public abstract class ModelMesher {

	/** mesher stuff starts here */

	public static Vector3i[][] FACES_VERTICES = new Vector3i[6][4];
	// blocks offset which affect ao
	public static Vector3i[][][] FACES_NEIGHBORS = new Vector3i[6][4][3];
	private static Vector3i[] VERTICES = new Vector3i[8];

	/*
	 * 7---------6 /| | 3---------2 | | | | | | | | | | 4______|__5 | / | /
	 * 0---------1
	 */

	static {
		VERTICES[0] = new Vector3i(0, 1, 0);
		VERTICES[1] = new Vector3i(0, 0, 0);
		VERTICES[2] = new Vector3i(0, 0, 1);
		VERTICES[3] = new Vector3i(0, 1, 1);
		VERTICES[4] = new Vector3i(1, 1, 0);
		VERTICES[5] = new Vector3i(1, 0, 0);
		VERTICES[6] = new Vector3i(1, 0, 1);
		VERTICES[7] = new Vector3i(1, 1, 1);

		/** left face */
		FACES_VERTICES[Face.LEFT][0] = VERTICES[4];
		FACES_VERTICES[Face.LEFT][1] = VERTICES[5];
		FACES_VERTICES[Face.LEFT][2] = VERTICES[1];
		FACES_VERTICES[Face.LEFT][3] = VERTICES[0];

		FACES_NEIGHBORS[Face.LEFT][0][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.LEFT][0][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][0][2] = new Vector3i(1, 1, -1);

		FACES_NEIGHBORS[Face.LEFT][1][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.LEFT][1][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][1][2] = new Vector3i(1, -1, -1);

		FACES_NEIGHBORS[Face.LEFT][2][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.LEFT][2][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][2][2] = new Vector3i(-1, -1, -1);

		FACES_NEIGHBORS[Face.LEFT][3][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.LEFT][3][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.LEFT][3][2] = new Vector3i(-1, 1, -1);

		/** right face */
		FACES_VERTICES[Face.RIGHT][0] = VERTICES[3];
		FACES_VERTICES[Face.RIGHT][1] = VERTICES[2];
		FACES_VERTICES[Face.RIGHT][2] = VERTICES[6];
		FACES_VERTICES[Face.RIGHT][3] = VERTICES[7];

		FACES_NEIGHBORS[Face.RIGHT][0][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.RIGHT][0][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][0][2] = new Vector3i(-1, 1, 1);

		FACES_NEIGHBORS[Face.RIGHT][1][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.RIGHT][1][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][1][2] = new Vector3i(-1, -1, 1);

		FACES_NEIGHBORS[Face.RIGHT][2][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.RIGHT][2][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][2][2] = new Vector3i(1, -1, 1);

		FACES_NEIGHBORS[Face.RIGHT][3][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.RIGHT][3][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.RIGHT][3][2] = new Vector3i(1, 1, 1);

		/** back face */
		FACES_VERTICES[Face.BACK][0] = VERTICES[7];
		FACES_VERTICES[Face.BACK][1] = VERTICES[6];
		FACES_VERTICES[Face.BACK][2] = VERTICES[5];
		FACES_VERTICES[Face.BACK][3] = VERTICES[4];

		FACES_NEIGHBORS[Face.BACK][0][0] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.BACK][0][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.BACK][0][2] = new Vector3i(1, 1, 1);

		FACES_NEIGHBORS[Face.BACK][1][0] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BACK][1][1] = new Vector3i(1, 0, 1);
		FACES_NEIGHBORS[Face.BACK][1][2] = new Vector3i(1, -1, 1);

		FACES_NEIGHBORS[Face.BACK][2][0] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BACK][2][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.BACK][2][2] = new Vector3i(1, -1, -1);

		FACES_NEIGHBORS[Face.BACK][3][0] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.BACK][3][1] = new Vector3i(1, 0, -1);
		FACES_NEIGHBORS[Face.BACK][3][2] = new Vector3i(1, 1, -1);

		/** front face */
		FACES_VERTICES[Face.FRONT][0] = VERTICES[0];
		FACES_VERTICES[Face.FRONT][1] = VERTICES[1];
		FACES_VERTICES[Face.FRONT][2] = VERTICES[2];
		FACES_VERTICES[Face.FRONT][3] = VERTICES[3];

		FACES_NEIGHBORS[Face.FRONT][0][0] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.FRONT][0][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.FRONT][0][2] = new Vector3i(-1, 1, -1);

		FACES_NEIGHBORS[Face.FRONT][1][0] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.FRONT][1][1] = new Vector3i(-1, 0, -1);
		FACES_NEIGHBORS[Face.FRONT][1][2] = new Vector3i(-1, -1, -1);

		FACES_NEIGHBORS[Face.FRONT][2][0] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.FRONT][2][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.FRONT][2][2] = new Vector3i(-1, -1, 1);

		FACES_NEIGHBORS[Face.FRONT][3][0] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.FRONT][3][1] = new Vector3i(-1, 0, 1);
		FACES_NEIGHBORS[Face.FRONT][3][2] = new Vector3i(-1, 1, 1);

		/** bottom face */
		FACES_VERTICES[Face.BOT][0] = VERTICES[1];
		FACES_VERTICES[Face.BOT][1] = VERTICES[5];
		FACES_VERTICES[Face.BOT][2] = VERTICES[6];
		FACES_VERTICES[Face.BOT][3] = VERTICES[2];

		FACES_NEIGHBORS[Face.BOT][0][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.BOT][0][1] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][0][2] = new Vector3i(-1, -1, -1);

		FACES_NEIGHBORS[Face.BOT][1][0] = new Vector3i(0, -1, -1);
		FACES_NEIGHBORS[Face.BOT][1][1] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][1][2] = new Vector3i(1, -1, -1);

		FACES_NEIGHBORS[Face.BOT][2][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.BOT][2][1] = new Vector3i(1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][2][2] = new Vector3i(1, -1, 1);

		FACES_NEIGHBORS[Face.BOT][3][0] = new Vector3i(0, -1, 1);
		FACES_NEIGHBORS[Face.BOT][3][1] = new Vector3i(-1, -1, 0);
		FACES_NEIGHBORS[Face.BOT][3][2] = new Vector3i(-1, -1, 1);

		/** top face */
		FACES_VERTICES[Face.TOP][0] = VERTICES[4];
		FACES_VERTICES[Face.TOP][1] = VERTICES[0];
		FACES_VERTICES[Face.TOP][2] = VERTICES[3];
		FACES_VERTICES[Face.TOP][3] = VERTICES[7];

		FACES_NEIGHBORS[Face.TOP][0][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.TOP][0][1] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][0][2] = new Vector3i(1, 1, -1);

		FACES_NEIGHBORS[Face.TOP][1][0] = new Vector3i(0, 1, -1);
		FACES_NEIGHBORS[Face.TOP][1][1] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][1][2] = new Vector3i(-1, 1, -1);

		FACES_NEIGHBORS[Face.TOP][2][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.TOP][2][1] = new Vector3i(-1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][2][2] = new Vector3i(-1, 1, 1);

		FACES_NEIGHBORS[Face.TOP][3][0] = new Vector3i(0, 1, 1);
		FACES_NEIGHBORS[Face.TOP][3][1] = new Vector3i(1, 1, 0);
		FACES_NEIGHBORS[Face.TOP][3][2] = new Vector3i(1, 1, 1);

	};

	public static float[][] FACES_UV = { { 0, 0 }, { 0, 1 }, { 1, 1 }, { 1, 0 } };
	private Stack<ModelMeshVertex> _vertex_stack;

	/** number of block is need to know how to calculate UVs */
	public ModelMesher() {
	}

	public final void updateVertexStack(ModelPartBuilder part) {
		this._vertex_stack = this.getVertexStack(part);
	}

	/** generate the position vertices for the given model part */
	public final ByteBuffer generateModelPartVertices() {

		int length = this._vertex_stack.size() * ModelPartBuilder.FLOAT_PER_MODEL_VERTEX;
		ByteBuffer buffer = BufferUtils.createByteBuffer(length * 4);

		for (ModelMeshVertex vertex : this._vertex_stack) {
			buffer.putFloat(vertex.posx);
			buffer.putFloat(vertex.posy);
			buffer.putFloat(vertex.posz);
			buffer.putFloat(vertex.normalx);
			buffer.putFloat(vertex.normaly);
			buffer.putFloat(vertex.normalz);
		}
		buffer.flip();
		return (buffer);
	}

	/** generate the vertices for the given skin */
	public final ByteBuffer generateSkinPartVertices(ModelPartSkinBuilder skin) {

		int length = this._vertex_stack.size() * ModelPartBuilder.FLOAT_PER_SKIN_VERTEX;
		ByteBuffer buffer = BufferUtils.createByteBuffer(length * 4);

		for (ModelMeshVertex vertex : this._vertex_stack) {

			int x = vertex.blockx;
			int y = vertex.blocky;
			int z = vertex.blockz;
			int color = skin.getColor(x, y, z);

			float r = ColorInt.getRed(color) / 255.0f * vertex.face.getFaceFactor() - vertex.ao;
			float g = ColorInt.getGreen(color) / 255.0f * vertex.face.getFaceFactor() - vertex.ao;
			float b = ColorInt.getBlue(color) / 255.0f * vertex.face.getFaceFactor() - vertex.ao;
			float a = ColorInt.getAlpha(color) / 255.0f;

			buffer.putFloat(r);
			buffer.putFloat(g);
			buffer.putFloat(b);
			buffer.putFloat(a);

		}
		buffer.flip();

		return (buffer);
	}

	/**
	 * generate a stack which contains every vertices ordered to render back
	 * face culled triangles
	 */
	protected abstract Stack<ModelMeshVertex> getVertexStack(ModelPartBuilder part);

	/** push the vertices to the stack for model positions vertices */
	public void pushVertices(ModelPartBuilder part, Stack<ModelMeshVertex> stack, int x, int y, int z) {

		if (!part.isBlockSet(x, y, z)) {
			return;
		}

		// for each of it face
		for (Face face : Face.values()) {
			Vector3i vec = face.getVector();
			// get the neighbor of this face
			boolean neighbor = part.isBlockSet(x + vec.x, y + vec.y, z + vec.z);

			// if the face-neighboor block is invisible or non opaque
			if (!neighbor) {
				// then add the face
				this.pushFaceVertices(part, stack, x, y, z, face);
			}
		}
	}

	public void pushFaceVertices(ModelPartBuilder part, Stack<ModelMeshVertex> stack, int x, int y, int z, Face face) {

		ModelMeshVertex v0 = this.getBlockFaceVertex(part, face, 0, x, y, z);
		ModelMeshVertex v1 = this.getBlockFaceVertex(part, face, 1, x, y, z);
		ModelMeshVertex v2 = this.getBlockFaceVertex(part, face, 2, x, y, z);
		ModelMeshVertex v3 = this.getBlockFaceVertex(part, face, 3, x, y, z);

		// test light to swap the quad vertices to create a nice looking effect
		if (v0.ao + v2.ao < v1.ao + v3.ao) {
			stack.push(v0);
			stack.push(v1);
			stack.push(v2);
			stack.push(v0);
			stack.push(v2);
			stack.push(v3);
		} else {
			// flip quad
			stack.push(v1);
			stack.push(v2);
			stack.push(v3);
			stack.push(v1);
			stack.push(v3);
			stack.push(v0);
		}
	}

	/**
	 * return the vertex for the given face at the given coordinates, for it
	 * given id
	 */
	public ModelMeshVertex getBlockFaceVertex(ModelPartBuilder part, Face face, int vertexID, int x, int y, int z) {

		float unit = 1.0f;
		float px = x * unit + FACES_VERTICES[face.getID()][vertexID].x * unit;
		float py = y * unit + FACES_VERTICES[face.getID()][vertexID].y * unit;
		float pz = z * unit + FACES_VERTICES[face.getID()][vertexID].z * unit;

		float nx = face.getNormal().x;
		float ny = face.getNormal().y;
		float nz = face.getNormal().z;

		float ao = this.getVertexAO(part, face, vertexID, x, y, z);

		return (new ModelMeshVertex(face, x, y, z, px, py, pz, nx, ny, nz, ao));
	}

	// the float returned is the ratio of black which will be used for this
	// vertex
	private static final float AO_0 = 0.00f;
	private static final float AO_1 = 0.16f;
	private static final float AO_2 = 0.32f;
	private static final float AO_3 = 0.42f;

	public float getVertexAO(ModelPartBuilder part, Face face, int vertexID, int x, int y, int z) {

		Vector3i[] neighboors = FACES_NEIGHBORS[face.getID()][vertexID];
		boolean s1 = part.isBlockSet(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z);
		boolean s2 = part.isBlockSet(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z);
		boolean c = part.isBlockSet(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z);

		if (s1 && s2) {
			return (AO_3);
		}

		if (s1 || s2) {
			return (c ? AO_2 : AO_1);
		}

		return (c ? AO_1 : AO_0);
	}

	/** represent a block face */
	protected class BlockFace {

		// the vertices
		public ModelMeshVertex[] vertices;

		// allowed movement for this face
		public Face face;

		public BlockFace(Face face, ModelMeshVertex... vertices) {
			this.face = face;
			this.vertices = vertices;
		}

		@Override
		public boolean equals(Object object) {
			if (object == null || !(object instanceof BlockFace)) {
				return (false);
			}

			BlockFace other = (BlockFace) object;

			// if (other.color != this.color) {
			// return (false);
			// }

			// if the block face doesnt have the same lighting, return
			for (ModelMeshVertex vertex : this.vertices) {
				for (ModelMeshVertex vertex_other : other.vertices) {
					if (vertex_other.ao != vertex.ao) {
						return (false);
					}
				}
			}
			return (true);
		}

		/** push this face vertices to the stack */
		public void pushVertices(Stack<ModelMeshVertex> stack) {

			ModelMeshVertex v0 = this.vertices[0];
			ModelMeshVertex v1 = this.vertices[1];
			ModelMeshVertex v2 = this.vertices[2];
			ModelMeshVertex v3 = this.vertices[3];

			if (v0.ao + v2.ao > v1.ao + v3.ao) {
				stack.push(v0);
				stack.push(v1);
				stack.push(v2);
				stack.push(v0);
				stack.push(v2);
				stack.push(v3);
			} else {
				// flip quad
				stack.push(v1);
				stack.push(v2);
				stack.push(v3);
				stack.push(v1);
				stack.push(v3);
				stack.push(v0);
			}
		}
	}

	/** return the given block face data informations */
	public BlockFace getBlockFace(ModelPartBuilder part, int x, int y, int z, Face face) {

		Vector3i vec = face.getVector();

		// if the face-neighboor block is visible and opaque
		if (part.isBlockSet(x + vec.x, y + vec.y, z + vec.z)) {
			// the face isnt visible
			return (null);
		}

		// else the face is visible, create it!
		ModelMeshVertex v0 = this.getBlockFaceVertex(part, face, 0, x, y, z);
		ModelMeshVertex v1 = this.getBlockFaceVertex(part, face, 1, x, y, z);
		ModelMeshVertex v2 = this.getBlockFaceVertex(part, face, 2, x, y, z);
		ModelMeshVertex v3 = this.getBlockFaceVertex(part, face, 3, x, y, z);
		BlockFace blockface = new BlockFace(face, v0, v1, v2, v3);
		return (blockface);
	}
}
