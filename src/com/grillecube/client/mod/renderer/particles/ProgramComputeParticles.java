package com.grillecube.client.mod.renderer.particles;

import com.grillecube.client.renderer.opengl.ProgramCompute;

public class ProgramComputeParticles extends ProgramCompute
{	
	public ProgramComputeParticles()
	{
		super("./assets/shaders/particle.compute");
	}
}
