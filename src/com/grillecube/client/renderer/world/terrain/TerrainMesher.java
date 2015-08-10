package com.grillecube.client.renderer.world.terrain;

import java.util.Stack;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector3i;

import com.grillecube.client.world.Faces;
import com.grillecube.client.world.Terrain;
import com.grillecube.client.world.blocks.Block;


public class TerrainMesher
{
	/** constants */
	private static final float S = 1;	//block size unit

	private float _uuvx; //unit uvx
	private float _uuvy; //unit uvy
	
	/** number of block is need to know how to calculate UVs */
	public TerrainMesher(int block_count)
	{
		this._uuvx = 1;
		this._uuvy = 1 / (float)block_count;
	}
	
	/** set terrain mesh vertices depending on terrain block */
	public float[] generateVertices(Terrain terrain)
	{
		Stack<MeshVertex> stack = this.getVisibleVertexStack(terrain);
		float[] vertices = new float[stack.size() * 9]; //each vertex is 9 floats
		
		int ptr = 0; //'this._vertices' buffer addr for next face's vertices
		
		for (MeshVertex vertex : stack)
		{
			vertices[ptr++] = vertex.posx;
			vertices[ptr++] = vertex.posy;
			vertices[ptr++] = vertex.posz;
			vertices[ptr++] = vertex.normalx;
			vertices[ptr++] = vertex.normaly;
			vertices[ptr++] = vertex.normalz;
			vertices[ptr++] = vertex.uvx;
			vertices[ptr++] = vertex.uvy;
			vertices[ptr++] = vertex.ao;
		}
		
		return (vertices);
	}
	
	private static Vector3i[][] faces_vertices = new Vector3i[6][4];
	private static Vector3i[][][] faces_neighboors = new Vector3i[6][4][3]; //blocks offset which affect ao
	private static Vector3i[] vertices = new Vector3i[8];
	
/*
	  4-----7              ^ +            ^ +
	 /|    /|              |             /          -   Z   +
	0-----3 |              |  Y         /  X        -------->
	| 5___|_6              | -         / -
	|/    | /
	1-----2
*/
	static
	{
		vertices[0] = new Vector3i(0, 1, 0);
		vertices[1] = new Vector3i(0, 0, 0);
		vertices[2] = new Vector3i(0, 0, 1);
		vertices[3] = new Vector3i(0, 1, 1);
		vertices[4] = new Vector3i(1, 1, 0);
		vertices[5] = new Vector3i(1, 0, 0);
		vertices[6] = new Vector3i(1, 0, 1);
		vertices[7] = new Vector3i(1, 1, 1);


		/** left face */
		faces_vertices[Faces.LEFT][0] = vertices[4];
		faces_vertices[Faces.LEFT][1] = vertices[5];
		faces_vertices[Faces.LEFT][2] = vertices[1];
		faces_vertices[Faces.LEFT][3] = vertices[0];
		
		faces_neighboors[Faces.LEFT][0][0] = new Vector3i(0, 1, -1);
		faces_neighboors[Faces.LEFT][0][1] = new Vector3i(1, 0, -1);
		faces_neighboors[Faces.LEFT][0][2] = new Vector3i(1, 1, -1);
		
		faces_neighboors[Faces.LEFT][1][0] = new Vector3i(0, -1, -1);
		faces_neighboors[Faces.LEFT][1][1] = new Vector3i(1, 0, -1);
		faces_neighboors[Faces.LEFT][1][2] = new Vector3i(1, -1, -1);
		
		faces_neighboors[Faces.LEFT][2][0] = new Vector3i(0, -1, -1);
		faces_neighboors[Faces.LEFT][2][1] = new Vector3i(-1, 0, -1);
		faces_neighboors[Faces.LEFT][2][2] = new Vector3i(-1, -1, -1);
		
		faces_neighboors[Faces.LEFT][3][0] = new Vector3i(0, 1, -1);
		faces_neighboors[Faces.LEFT][3][1] = new Vector3i(-1, 0, -1);
		faces_neighboors[Faces.LEFT][3][2] = new Vector3i(-1, 1, -1);
		
		/** right face */
		faces_vertices[Faces.RIGHT][0] = vertices[3];
		faces_vertices[Faces.RIGHT][1] = vertices[2];
		faces_vertices[Faces.RIGHT][2] = vertices[6];
		faces_vertices[Faces.RIGHT][3] = vertices[7];
		
		faces_neighboors[Faces.RIGHT][0][0] = new Vector3i(0, 1, 1);
		faces_neighboors[Faces.RIGHT][0][1] = new Vector3i(-1, 0, 1);
		faces_neighboors[Faces.RIGHT][0][2] = new Vector3i(-1, 1, 1);
		
		faces_neighboors[Faces.RIGHT][1][0] = new Vector3i(0, -1, 1);
		faces_neighboors[Faces.RIGHT][1][1] = new Vector3i(-1, 0, 1);
		faces_neighboors[Faces.RIGHT][1][2] = new Vector3i(-1, -1, 1);
		
		faces_neighboors[Faces.RIGHT][2][0] = new Vector3i(0, -1, 1);
		faces_neighboors[Faces.RIGHT][2][1] = new Vector3i(1, 0, 1);
		faces_neighboors[Faces.RIGHT][2][2] = new Vector3i(1, -1, 1);
		
		faces_neighboors[Faces.RIGHT][3][0] = new Vector3i(0, 1, 1);
		faces_neighboors[Faces.RIGHT][3][1] = new Vector3i(1, 0, 1);
		faces_neighboors[Faces.RIGHT][3][2] = new Vector3i(1, 1, 1);
		
		/** back face */
		faces_vertices[Faces.BACK][0] = vertices[7];
		faces_vertices[Faces.BACK][1] = vertices[6];
		faces_vertices[Faces.BACK][2] = vertices[5];
		faces_vertices[Faces.BACK][3] = vertices[4];
		
		faces_neighboors[Faces.BACK][0][0] = new Vector3i(1, 1, 0);
		faces_neighboors[Faces.BACK][0][1] = new Vector3i(1, 0, 1);
		faces_neighboors[Faces.BACK][0][2] = new Vector3i(1, 1, 1);
		
		faces_neighboors[Faces.BACK][1][0] = new Vector3i(1, -1, 0);
		faces_neighboors[Faces.BACK][1][1] = new Vector3i(1, 0, 1);
		faces_neighboors[Faces.BACK][1][2] = new Vector3i(1, -1, 1);
		
		faces_neighboors[Faces.BACK][2][0] = new Vector3i(1, -1, 0);
		faces_neighboors[Faces.BACK][2][1] = new Vector3i(1, 0, -1);
		faces_neighboors[Faces.BACK][2][2] = new Vector3i(1, -1, -1);
		
		faces_neighboors[Faces.BACK][3][0] = new Vector3i(1, 1, 0);
		faces_neighboors[Faces.BACK][3][1] = new Vector3i(1, 0, -1);
		faces_neighboors[Faces.BACK][3][2] = new Vector3i(1, 1, -1);
		
		/** front face */
		faces_vertices[Faces.FRONT][0] = vertices[0];
		faces_vertices[Faces.FRONT][1] = vertices[1];
		faces_vertices[Faces.FRONT][2] = vertices[2];
		faces_vertices[Faces.FRONT][3] = vertices[3];
		
		faces_neighboors[Faces.FRONT][0][0] = new Vector3i(-1, 1, 0);
		faces_neighboors[Faces.FRONT][0][1] = new Vector3i(-1, 0, -1);
		faces_neighboors[Faces.FRONT][0][2] = new Vector3i(-1, 1, -1);
		
		faces_neighboors[Faces.FRONT][1][0] = new Vector3i(-1, -1, 0);
		faces_neighboors[Faces.FRONT][1][1] = new Vector3i(-1, 0, -1);
		faces_neighboors[Faces.FRONT][1][2] = new Vector3i(-1, -1, -1);
		
		faces_neighboors[Faces.FRONT][2][0] = new Vector3i(-1, -1, 0);
		faces_neighboors[Faces.FRONT][2][1] = new Vector3i(-1, 0, 1);
		faces_neighboors[Faces.FRONT][2][2] = new Vector3i(-1, -1, 1);
		
		faces_neighboors[Faces.FRONT][3][0] = new Vector3i(-1, 1, 0);
		faces_neighboors[Faces.FRONT][3][1] = new Vector3i(-1, 0, 1);
		faces_neighboors[Faces.FRONT][3][2] = new Vector3i(-1, 1, 1);

		
		/** bottom face */
		faces_vertices[Faces.BOT][0] = vertices[1];
		faces_vertices[Faces.BOT][1] = vertices[5];
		faces_vertices[Faces.BOT][2] = vertices[6];
		faces_vertices[Faces.BOT][3] = vertices[2];
		
		faces_neighboors[Faces.BOT][0][0] = new Vector3i(0, -1, -1);
		faces_neighboors[Faces.BOT][0][1] = new Vector3i(-1, -1, 0);
		faces_neighboors[Faces.BOT][0][2] = new Vector3i(-1, -1, -1);
		
		faces_neighboors[Faces.BOT][1][0] = new Vector3i(0, -1, -1);
		faces_neighboors[Faces.BOT][1][1] = new Vector3i(1, -1, 0);
		faces_neighboors[Faces.BOT][1][2] = new Vector3i(1, -1, -1);
		
		faces_neighboors[Faces.BOT][2][0] = new Vector3i(0, -1, 1);
		faces_neighboors[Faces.BOT][2][1] = new Vector3i(1, -1, 0);
		faces_neighboors[Faces.BOT][2][2] = new Vector3i(1, -1, 1);
		
		faces_neighboors[Faces.BOT][3][0] = new Vector3i(0, -1, 1);
		faces_neighboors[Faces.BOT][3][1] = new Vector3i(-1, -1, 0);
		faces_neighboors[Faces.BOT][3][2] = new Vector3i(-1, -1, 1);
		
		/** top face */
		faces_vertices[Faces.TOP][0] = vertices[4];
		faces_vertices[Faces.TOP][1] = vertices[0];
		faces_vertices[Faces.TOP][2] = vertices[3];
		faces_vertices[Faces.TOP][3] = vertices[7];
		
		faces_neighboors[Faces.TOP][0][0] = new Vector3i(0, 1, -1);
		faces_neighboors[Faces.TOP][0][1] = new Vector3i(1, 1, 0);
		faces_neighboors[Faces.TOP][0][2] = new Vector3i(1, 1, -1);
		
		faces_neighboors[Faces.TOP][1][0] = new Vector3i(0, 1, -1);
		faces_neighboors[Faces.TOP][1][1] = new Vector3i(-1, 1, 0);
		faces_neighboors[Faces.TOP][1][2] = new Vector3i(-1, 1, -1);
		
		faces_neighboors[Faces.TOP][2][0] = new Vector3i(0, 1, 1);
		faces_neighboors[Faces.TOP][2][1] = new Vector3i(-1, 1, 0);
		faces_neighboors[Faces.TOP][2][2] = new Vector3i(-1, 1, 1);
		
		faces_neighboors[Faces.TOP][3][0] = new Vector3i(0, 1, 1);
		faces_neighboors[Faces.TOP][3][1] = new Vector3i(1, 1, 0);
		faces_neighboors[Faces.TOP][3][2] = new Vector3i(1, 1, 1);

	};
	
	private static float[][] faces_uv = {
			{0, 0},
			{0, 1},
			{1, 1},
			{1, 0}
	};

	/** fill an array of dimension [Terrain.SIZE_X][Terrain.SIZE_Y][Terrain.SIZE_Z][6] of terrain faces visibility */
	private Stack<MeshVertex> getVisibleVertexStack(Terrain terrain)
	{
		Stack<MeshVertex> stack = new Stack<MeshVertex>();
		
		//for each block
		for (int x = 0 ; x < Terrain.SIZE_X ; x++)
		{
			for (int y = 0 ; y < Terrain.SIZE_Y ; y++)
			{
				for (int z = 0 ; z < Terrain.SIZE_Z ; z++)
				{
					Block block = terrain.getBlock(x, y, z);

					if (block.isVisible()) //if the block is visible
					{
						for (int faceID = 0 ; faceID < 6 ; faceID++) //for each of it face
						{
							Vector3i vec = Faces.getFaceVector(faceID);
							if (terrain.getBlock(x + vec.x, y + vec.y, z + vec.z).isVisible() == false) //if the face-neighboor block is invisible
							{
								this.pushFaceVertices(terrain, stack, x, y, z, faceID); //add the face
							}
						}
					}
				}
			}
		}
		return (stack);
	}
	
	private void pushFaceVertices(Terrain terrain, Stack<MeshVertex> stack, int x, int y, int z, int faceID)
	{
		Block block = terrain.getBlock(x, y, z);
		float uvx = 0;
		float uvy = block.getTextureIDForFace(faceID) * this._uuvy;
		
		Vector3f normal = Faces.getFaceNormal(faceID);
		
		MeshVertex v0 = this.getMeshVertex(terrain, faceID, 0, x, y, z, normal, uvx, uvy);
		MeshVertex v1 = this.getMeshVertex(terrain, faceID, 1, x, y, z, normal, uvx, uvy);
		MeshVertex v2 = this.getMeshVertex(terrain, faceID, 2, x, y, z, normal, uvx, uvy);
		MeshVertex v3 = this.getMeshVertex(terrain, faceID, 3, x, y, z, normal, uvx, uvy);
		
		if (v0.ao + v2.ao > v1.ao + v3.ao)
		{
			stack.push(v0);
			stack.push(v1);
			stack.push(v2);
			stack.push(v3);
		}
		else //flip quad
		{
			stack.push(v1);
			stack.push(v2);
			stack.push(v3);
			stack.push(v0);
		}
	}
	
	private MeshVertex getMeshVertex(Terrain terrain, int faceID, int vertexID, int x, int y, int z, Vector3f normal, float uvx, float uvy)
	{
		return (new MeshVertex(x + faces_vertices[faceID][vertexID].x * S,
								y + faces_vertices[faceID][vertexID].y * S,
								z + faces_vertices[faceID][vertexID].z * S,
								normal,
								uvx + faces_uv[vertexID][0] * this._uuvx,
								uvy + faces_uv[vertexID][1] * this._uuvy,
								this.getVertexAO(terrain, x, y, z, faceID, vertexID)));
	}
	
	//the float returned is the ratio of black which will be used for this vertex
	private static final float AO_UNIT	= 0.20f;
	private float getVertexAO(Terrain terrain, int x, int y, int z, int faceID, int vertexID)
	{
		Vector3i[] neighboors = faces_neighboors[faceID][vertexID];
		if (neighboors[0] == null)
		{
			return (0);
		}
		boolean side1 = terrain.getBlock(x + neighboors[0].x, y + neighboors[0].y, z + neighboors[0].z).isVisible();
		boolean side2 = terrain.getBlock(x + neighboors[1].x, y + neighboors[1].y, z + neighboors[1].z).isVisible();
		boolean corner = terrain.getBlock(x + neighboors[2].x, y + neighboors[2].y, z + neighboors[2].z).isVisible();
		
		if (side1 && side2)
		{
			return (AO_UNIT * 3);
		}
		
		float ao = 0;
		
		if (side1)
		{
			ao += AO_UNIT;
		}
		
		if (side2)
		{
			ao += AO_UNIT;
		}
		
		if (corner)
		{
			ao += AO_UNIT;
		}
		return (ao);
	}
}
