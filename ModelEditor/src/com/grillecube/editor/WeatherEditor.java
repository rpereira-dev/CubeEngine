package com.grillecube.editor;

import com.grillecube.engine.world.Weather;

public class WeatherEditor extends Weather {
	public WeatherEditor() {
		super();
		super.setCycleRatio(Weather.DAY_START);
	}

	@Override
	protected void calculateFog() {
		super.setFogGradient(2);
		super.setFogDensity(0.0008f);
		super.setFogColor(1, 1, 1);
	}
}
