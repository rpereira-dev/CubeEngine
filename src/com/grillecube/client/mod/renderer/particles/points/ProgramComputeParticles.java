package com.grillecube.client.mod.renderer.particles.points;

import com.grillecube.client.opengl.object.ProgramCompute;

public class ProgramComputeParticles extends ProgramCompute
{	
	public ProgramComputeParticles()
	{
		super("./assets/shaders/particle_point.compute");
	}
}
