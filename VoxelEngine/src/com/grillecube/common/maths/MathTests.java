package com.grillecube.common.maths;

import org.junit.Test;

import junit.framework.Assert;

public class MathTests {
	public MathTests() {

	}

	@Test
	public void testQuaternion() {
		Vector3f toTest = new Vector3f(0.4f, 0.5f, 0.6f);
		Quaternion quaternion = Quaternion.toQuaternion(toTest);
		Vector3f eulerian = Quaternion.toEulerAngle(quaternion);
		float delta = 0.00001f;
		Assert.assertEquals(toTest.x, eulerian.x, delta);
		Assert.assertEquals(toTest.y, eulerian.y, delta);
		Assert.assertEquals(toTest.z, eulerian.z, delta);
	}
}
