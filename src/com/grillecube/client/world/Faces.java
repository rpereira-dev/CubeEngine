package com.grillecube.client.world;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector3i;

public class Faces
{
	//index for the 'touch_by_flood' array
	public static final int LEFT 	= 0;
	public static final int RIGHT	= 1;
	public static final int TOP		= 2;
	public static final int BOT		= 3;
	public static final int FRONT	= 4;
	public static final int BACK	= 5;
	
	private static final int[] 		_opposite_faces = new int[6];
	private static final Vector3i[] _face_vector = new Vector3i[6];
	private static final Vector3f[] _face_normal = new Vector3f[6];
	private static final String[] 	_faces_name = new String[6];

	static
	{
		_opposite_faces[LEFT]	= RIGHT;
		_opposite_faces[RIGHT]	= LEFT;
		_opposite_faces[TOP]	= BOT;
		_opposite_faces[BOT]	= TOP;
		_opposite_faces[FRONT]	= BACK;
		_opposite_faces[BACK] 	= FRONT;
		
		_face_vector[LEFT]	= new Vector3i(-1, 0, 0);
		_face_vector[RIGHT]	= new Vector3i(1, 0, 0);
		_face_vector[TOP]	= new Vector3i(0, 1, 0);
		_face_vector[BOT]	= new Vector3i(0, -1, 0);
		_face_vector[FRONT]	= new Vector3i(0, 0, -1);
		_face_vector[BACK] 	= new Vector3i(0, 0, 1);
		
		_face_normal[LEFT]	= new Vector3f(-1, 0, 0);
		_face_normal[RIGHT]	= new Vector3f(1, 0, 0);
		_face_normal[TOP]	= new Vector3f(0, 1, 0);
		_face_normal[BOT]	= new Vector3f(0, -1, 0);
		_face_normal[FRONT]	= new Vector3f(0, 0, -1);
		_face_normal[BACK] 	= new Vector3f(0, 0, 1);
		
		_faces_name[LEFT]	= new String("LEFT");
		_faces_name[RIGHT]	= new String("RIGHT");
		_faces_name[TOP]	= new String("TOP");
		_faces_name[BOT]	= new String("BOT");
		_faces_name[FRONT]	= new String("FRONT");
		_faces_name[BACK] 	= new String("BACK");
	}
	
	public static int getOppositeFace(int faceID)
	{
		return (_opposite_faces[faceID]);
	}

	public static Vector3i getFaceVector(int faceID)
	{
		return (_face_vector[faceID]);
	}

	public static String toString(int faceID)
	{
		return (_faces_name[faceID]);
	}
	
	public static Vector3f getFaceNormal(int faceID)
	{
		return (_face_normal[faceID]);
	}
}
