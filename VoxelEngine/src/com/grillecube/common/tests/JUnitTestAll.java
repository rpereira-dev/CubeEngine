package com.grillecube.common.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.grillecube.common.world.entity.collision.Collision;

public class JUnitTestAll {

	public static void main(String[] args) {
		Class<?> classes[] = { Collision.class };
		for (Class<?> classToTest : classes) {
			Result result = JUnitCore.runClasses(classToTest);
			for (Failure failure : result.getFailures()) {
				System.out.println(failure.toString());
			}
		}
	}
}
