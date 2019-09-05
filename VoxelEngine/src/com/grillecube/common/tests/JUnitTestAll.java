package com.grillecube.common.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.grillecube.client.tests.WorldTests;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.MathTests;
import com.grillecube.common.world.entity.collision.CollisionUnitTests;

public class JUnitTestAll {

	public static void main(String[] args) {
		Logger.get().log(Logger.Level.FINE, "running tests...");
		Class<?> classes[] = { CollisionUnitTests.class, WorldTests.class, MathTests.class };
		for (Class<?> classToTest : classes) {
			Logger.get().log(Logger.Level.FINE, "running test for class: " + classToTest.getSimpleName());
			Result result = JUnitCore.runClasses(classToTest);
			for (Failure failure : result.getFailures()) {
				Logger.get().log(Logger.Level.ERROR, failure.toString());
			}
		}
		Logger.get().log(Logger.Level.FINE, "tests done succesfully");
	}
}
