package com.grillecube.common.world.entity.collision;

import java.util.ArrayList;

import org.junit.Test;

import com.grillecube.common.world.entity.WorldEntity;
import com.grillecube.common.world.physic.WorldObject;
import com.grillecube.common.world.physic.Positioneable;

import junit.framework.Assert;

public class CollisionUnitTests {

	@Test
	public void noCollision() {
		// moving entity
		WorldEntity entity = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		entity.setSize(1.0f, 1.0f, 1.0f);
		entity.setPosition(0.0f, 0.0f, 0.0f);
		entity.setPositionVelocity(0.0f, 1.0f, 0.0f);

		// walls
		ArrayList<WorldObject> objects = new ArrayList<WorldObject>();
		// for (int i = -3; i < 3; i++) {
		int i = 1;
		WorldEntity block = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		block.setSize(1.0f, 1.0f, 1.0f);
		block.setPosition(10000.0f, i * 1.0f, 0.0f);
		objects.add(block);
		// }

		// process collision
		CollisionDetection detection = CollisionDetection.detect(entity, objects, 100.0f);
		Assert.assertEquals(detection, null);
	}

	@Test
	public void collisionFalling() {
		WorldEntity entity = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		entity.setSize(1.0f, 1.0f, 1.0f);
		entity.setPosition(0.0f, 0.0f, 6.0f);
		entity.setPositionVelocity(0.0f, 0.0f, -1.0f);

		WorldEntity block = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		block.setSize(1.0f, 1.0f, 1.0f);
		block.setPosition(0.0f, 0.0f, 0.0f);

		CollisionDetection collisionResponse = CollisionDetection.detectAABB(entity, block, 100.0f);
		Assert.assertEquals(collisionResponse, new CollisionDetection(entity, block, 0.0f, 0.0f, 1.0f, 5.0f));
	}

	@Test
	public void collisionRounding() {

		WorldEntity entity = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		entity.setSize(1.0f, 2.0f, 1.0f);
		entity.setPosition(0.0f, 6.0f, 0.0f);
		entity.setPositionVelocity(0.0f, -1.0f, 0.0f);

		WorldEntity block = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		block.setSize(1.0f, 1.0f, 1.0f);
		block.setPosition(0.0f, 0.0f, 0.0f);

		// falling
		{
			CollisionDetection collisionResponse = CollisionDetection.detectAABB(entity, block, 100.0f);
			Assert.assertEquals(collisionResponse, new CollisionDetection(entity, block, 0.0f, 1.0f, 0.0f, 5.0f));

			// move to collision
			Positioneable.position(entity, collisionResponse.dt);
			Assert.assertEquals(entity.getPositionX(), 0.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionY(), 1.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionZ(), 0.0f, CollisionDetection.MARGIN);

			// stick to collision
			CollisionResponse.stick(entity, collisionResponse);
			Assert.assertEquals(entity.getPositionVelocityX(), 0.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionVelocityY(), 0.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionVelocityZ(), 0.0f, CollisionDetection.MARGIN);
		}

		// try falling more
		{
			entity.setPosition(0.0f, 1.0f, 0.0f);
			entity.setPositionVelocity(0.0f, -1.0f, 0.0f);
			CollisionDetection collisionResponse = CollisionDetection.detectAABB(entity, block, 100.0f);

			Assert.assertEquals(collisionResponse, new CollisionDetection(entity, block, 0.0f, 1.0f, 0.0f, 0.0f));

			// if collision, move just before it collides
			Positioneable.position(entity, collisionResponse.dt);
			Assert.assertEquals(entity.getPositionX(), 0.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionY(), 1.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionZ(), 0.0f, CollisionDetection.MARGIN);

			CollisionResponse.stick(entity, collisionResponse);
			Assert.assertEquals(entity.getPositionVelocityX(), 0.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionVelocityY(), 0.0f, CollisionDetection.MARGIN);
			Assert.assertEquals(entity.getPositionVelocityZ(), 0.0f, CollisionDetection.MARGIN);
		}
	}

	@Test
	public void entityToWallCollision() {
		WorldEntity entity = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		entity.setSize(1.0f, 1.0f, 1.0f);
		entity.setPosition(0.0f, 0.0f, 0.0f);
		entity.setPositionVelocity(1.0f, 0.0f, 0.0f);

		ArrayList<WorldObject> objects = new ArrayList<WorldObject>();
		for (int i = -3; i < 3; i++) {
			WorldEntity block = new WorldEntity() {
				@Override
				protected void onUpdate(double dt) {
				}
			};
			block.setSize(1.0f, 1.0f, 1.0f);
			block.setPosition(2.0f, i * 1.0f, 0.0f);
			objects.add(block);
		}

		CollisionDetection collisionResponse = CollisionDetection.detect(entity, objects, 100.0f);
		Assert.assertEquals(collisionResponse, new CollisionDetection(entity, objects.get(0), -1.0f, 0.0f, 0.0f, 1.0f));
	}

	@Test
	public void entitySlideWall() {
		WorldEntity entity = new WorldEntity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		entity.setSize(1.0f, 1.0f, 1.0f);
		entity.setPosition(0.0f, 0.0f, 0.0f);
		entity.setPositionVelocity(0.707f, 0.707f, 0.0f);

		ArrayList<WorldObject> objects = new ArrayList<WorldObject>();
		for (int i = -3; i < 4; i++) {
			WorldEntity block = new WorldEntity() {
				@Override
				protected void onUpdate(double dt) {
				}
			};
			block.setSize(1.0f, 1.0f, 1.0f);
			block.setPosition(2.0f, i * 1.0f, 0.0f);
			objects.add(block);
		}

		/** stick to wall */
		CollisionDetection collisionResponse = CollisionDetection.detect(entity, objects, 100.0f);
		Assert.assertEquals(collisionResponse,
				new CollisionDetection(entity, objects.get(0), -1.0f, 0.0f, 0.0f, 1.4144272f));

		Positioneable.position(entity, collisionResponse.dt);

		Assert.assertEquals(entity.getPositionX(), 1.0f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionY(), 1.0f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionZ(), 0.0f, CollisionDetection.MARGIN);

		Assert.assertEquals(entity.getPositionVelocityX(), 0.707f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionVelocityY(), 0.707f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionVelocityZ(), 0.0f, CollisionDetection.MARGIN);

		/** push alongside the wall */
		CollisionResponse.push(entity, collisionResponse);

		Assert.assertEquals(entity.getPositionX(), 1.0f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionY(), 1.0f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionZ(), 0.0f, CollisionDetection.MARGIN);

		Assert.assertEquals(entity.getPositionVelocityX(), 0.0f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionVelocityY(), 0.707f, CollisionDetection.MARGIN);
		Assert.assertEquals(entity.getPositionVelocityZ(), 0.0f, CollisionDetection.MARGIN);

		/** push */
		collisionResponse = CollisionDetection.detect(entity, objects, 100.0f);
		Assert.assertEquals(collisionResponse, null);
	}
}
