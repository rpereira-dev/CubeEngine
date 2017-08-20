package com.grillecube.client.renderer.world.factories;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.sky.SkyRenderer;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.world.Weather;

public class SkyRendererFactory extends WorldRendererFactory {

	public SkyRendererFactory(WorldRenderer worldRenderer) {
		super(worldRenderer);
	}

	@Override
	public void update() {
		// this.ambientParticle(world);
		// if (world.getWeather().isRaining()) {
		// this.rainParticles(world, world.getWeather().getRainStrength());
		// }
	}

	@Override
	public void render() {
		SkyRenderer skyRenderer = super.getMainRenderer().getSkyRenderer();
		CameraProjective camera = super.getCamera();
		Weather weather = super.getWorld().getWeather();
		skyRenderer.render(camera, weather);
	}
}
