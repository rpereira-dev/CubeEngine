package com.grillecube.common.world.physic;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.extras.gimpact.GImpactCollisionAlgorithm;
import com.grillecube.common.world.Terrain;

public class WorldPhysicSystem {

	private final DiscreteDynamicsWorld dynamicsWorld;

	public WorldPhysicSystem() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
		DefaultCollisionConfiguration config = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(config);
		GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		this.dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, config);
		this.dynamicsWorld.setGravity(new Vector3f(0, 0, -9.81f * Terrain.BLOCKS_PER_METER));
	}

	public final void update(double dt) {
		this.dynamicsWorld.stepSimulation((float) dt);
	}
}
