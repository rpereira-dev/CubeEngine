package org.lwjgl.util.vector;

//http://www.java-gaming.org/index.php?PHPSESSID=tfhkmlmcmci9kfk5l61u9jf766&topic=14647.0
	
public class FastMath
{
	  private static final int ATAN2_BITS = 7;

	   private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
	   private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	   private static final int ATAN2_COUNT = ATAN2_MASK + 1;
	   private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);

	   private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);
	   private static final float DEG = 180.0f / (float) Math.PI;

	   private static final float[] atan2 = new float[ATAN2_COUNT];



	   static
	   {
	      for (int i = 0; i < ATAN2_DIM; i++)
	      {
	         for (int j = 0; j < ATAN2_DIM; j++)
	         {
	            float x0 = (float) i / ATAN2_DIM;
	            float y0 = (float) j / ATAN2_DIM;

	            atan2[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
	         }
	      }
	   }


	   /**
	    * ATAN2
	    */

	   public static final float atan2Deg(float y, float x)
	   {
	      return FastMath.atan2(y, x) * DEG;
	   }

	   public static final float atan2DegStrict(float y, float x)
	   {
	      return (float) Math.atan2(y, x) * DEG;
	   }

	   static boolean sign;
	   static float add;
	   static int xi;
	   static int yi;
	   static float result;
	   
	   public static final float atan2(float y, float x)
	   {
		   if (x < 0.0f)
		   {
			   if (y < 0.0f)
			   {
				   x = -x;
				   y = -y;
				   sign = true;
			   }
			   else
			   {
				   x = -x;
				   sign = false;
			   }
			   add = -3.141592653f;
		   }
		   else
		   {
			   if (y < 0.0f)
			   {
				   y = -y;
				   sign = false;
			   }
			   else
			   {
				   sign = true;
			   }
			   add = 0.0f;
	      }
		   
		   float invDiv = 1.0f / (((x < y) ? y : x) * INV_ATAN2_DIM_MINUS_1);
		   
		   xi = (int) (x * invDiv);
		   yi = (int) (y * invDiv);
		   
		   result = atan2[yi * ATAN2_DIM + xi] + add;
		   return (sign ? result : -result);
	   }
}
