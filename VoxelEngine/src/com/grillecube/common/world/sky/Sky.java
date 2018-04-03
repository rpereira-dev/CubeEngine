/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.common.world.sky;

import java.util.ArrayList;
import java.util.Random;

import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.WeatherState;

public class Sky implements Taskable {
	/** minutes for a full day / night cycle */
	private static final float MINUTE_PER_CYCLE = 15.0f;
	private static final long MILLIS_PER_CYCLE = (long) (1000 * 60 * MINUTE_PER_CYCLE);

	/** time constants */
	public static final float DAY_START = 0;
	public static final float DAY_END = 0.45f;
	public static final float NIGHT_START = 0.55f;
	public static final float NIGHT_END = 0.9f;

	/** state */
	private int state;

	private Random rng;

	/** time [0.0 ; 1.0] */
	private float cycleRatio;
	private int cycleCount;

	/** time in millis [0.0 ; MILLIS_PER_CYCLE] */
	private long _cycle_millis;
	private long _prev_millis;

	/** sky data */
	private static final Vector3f SKY_COLOR_DAY = new Vector3f(0.4f, 0.71f, 0.99f);
	private static final Vector3f SKY_COLOR_NIGHT = new Vector3f(0.01f, 0.14f, 0.19f);
	private Vector3f skyColor;

	/** sun color */
	private static final Vector3f SUN_RISE_COLOR = new Vector3f(1, 1, 1);

	/** sun light */
	private PointLight sun;

	private static final float DAY_AMBIENT = 0.6f;
	private static final float NIGHT_AMBIENT = 0.15f;
	private float ambientLight;

	/** fog data */
	private Vector3f fogColor;
	private float fogDensity;
	private float fogGradient;
	private int rainStrength;
	private int rainStrengthMax;
	private int rainTimer;

	public static final int MAX_RAIN_STRENGTH = 128;
	public static final int MIN_RAIN_STRENGTH = 8;

	/** weather factors */
	private float humidity;
	private float temperature;
	private float wind;

	/** Weather constructor */
	public Sky() {
		this.setCycleRatio(Sky.DAY_START);
		this.state = 0;
		this.ambientLight = 0;
		this.cycleCount = 0;
		this.rainTimer = 0;

		this.skyColor = new Vector3f();

		this.sun = new PointLight(new Vector3f(10000f, 10000f, 10000f), Sky.SUN_RISE_COLOR, 1.0f);

		this.fogColor = new Vector3f(1.0f, 1.0f, 1.0f);
		this.fogDensity = 0.04f;
		this.fogGradient = 2.5f;
	}

	public void setCycleRatio(float f) {
		this.cycleRatio = f;
		this._prev_millis = System.currentTimeMillis();
		this._cycle_millis = (long) (f * MILLIS_PER_CYCLE);
	}

	/** update the weather (mb use a 1D noise for rains, storm ...) */
	/** ACUALLY CALLED IN WORLD RENDERER! */
	public void update() {
		this.updateTime();
		this.calculateSunPosition();
		this.calculateFog();
		this.calculateSky();
		this.calculateRain();
		this.calculateLights();
	}

	private void calculateLights() {
		if (this.hasState(WeatherState.DAY_ENDING)) {
			float intensity = 1 - (this.cycleRatio - Sky.DAY_END) / (Sky.NIGHT_START - Sky.DAY_END);
			this.sun.setIntensity(intensity);
			this.ambientLight = intensity * Sky.DAY_AMBIENT + (1 - intensity) * Sky.NIGHT_AMBIENT;
		}

		if (this.hasState(WeatherState.NIGHT_ENDING)) {
			float intensity = (this.cycleRatio - Sky.NIGHT_END) / (1.0f - Sky.NIGHT_END);
			this.sun.setIntensity(intensity);
			this.ambientLight = intensity * Sky.DAY_AMBIENT + (1 - intensity) * Sky.NIGHT_AMBIENT;
		}

		if (this.hasState(WeatherState.DAY)) {
			this.sun.setIntensity(1.0f);
			this.ambientLight = Sky.DAY_AMBIENT;
		}

		if (this.hasState(WeatherState.NIGHT)) {
			this.sun.setIntensity(0.0f);
			this.ambientLight = Sky.NIGHT_AMBIENT;
		}
	}

	/**********************************************************************************/
	/*********************************
	 * TIMER STARTS
	 *********************************/
	/**********************************************************************************/
	private void updateTime() {
		long time = System.currentTimeMillis();
		this._cycle_millis += (time - this._prev_millis);
		if (this._cycle_millis >= MILLIS_PER_CYCLE) {
			this._cycle_millis = this._cycle_millis % MILLIS_PER_CYCLE;
			this.cycleCount++;
		}
		this.cycleRatio = this._cycle_millis / (float) MILLIS_PER_CYCLE;
		this._prev_millis = time;

		if (this.cycleRatio >= Sky.DAY_START && this.cycleRatio < Sky.DAY_END) {
			this.setCycleState(WeatherState.DAY);
		}

		if (this.cycleRatio >= Sky.DAY_END && this.cycleRatio < Sky.NIGHT_START) {
			this.setCycleState(WeatherState.DAY_ENDING);
		}

		if (this.cycleRatio > Sky.NIGHT_START && this.cycleRatio <= Sky.NIGHT_END) {
			this.setCycleState(WeatherState.NIGHT);
		}

		if (this.cycleRatio >= Sky.NIGHT_END) {
			this.setCycleState(WeatherState.NIGHT_ENDING);
		}
	}

	public int getDayCount() {
		return (this.cycleCount);
	}

	public float getCycleRatio() {
		return (this.cycleRatio);
	}

	/*********************************
	 * TIMER ENDS
	 *********************************/

	/**********************************************************************************/
	/*********************************
	 * STATES STARTS
	 *********************************/
	/**********************************************************************************/

	private void setCycleState(int state) {
		this.unsetState(WeatherState.DAY);
		this.unsetState(WeatherState.DAY_ENDING);
		this.unsetState(WeatherState.NIGHT);
		this.unsetState(WeatherState.NIGHT_ENDING);
		this.setState(state);
	}

	private void setState(int state) {
		this.state = this.state | state;
	}

	private void unsetState(int state) {
		this.state = this.state & ~(state);
	}

	public boolean hasState(int state) {
		return ((this.state & state) == state);
	}

	/*********************************
	 * STATES END
	 *********************************/

	/**********************************************************************************/
	/*********************************
	 * SKY STARTS
	 *********************************/
	/**********************************************************************************/
	private void calculateSky() {
		float ratio = 1.0f;

		if (this.hasState(WeatherState.NIGHT)) {
			ratio = 0;
		} else if (this.hasState(WeatherState.DAY_ENDING)) {
			ratio = 1 - (this.cycleRatio - Sky.DAY_END) / (Sky.NIGHT_START - Sky.DAY_END);
		} else if (this.hasState(WeatherState.NIGHT_ENDING)) {
			ratio = (this.cycleRatio - Sky.NIGHT_END) / (1.0f - Sky.NIGHT_END);
		}
		Vector3f.mix(this.skyColor, Sky.SKY_COLOR_DAY, Sky.SKY_COLOR_NIGHT, ratio);
	}

	public Vector3f getSkyColor() {
		return (this.skyColor);
	}

	public Vector3f getCloudColor() {
		return (new Vector3f(1, 1, 1));
	}

	/*********************************
	 * SKY END
	 *********************************/

	/**********************************************************************************/
	/*********************************
	 * AMBIENT STARTS
	 *********************************/
	/**********************************************************************************/

	protected void calculateFog() {
		this.fogGradient = 4.0f;
		this.fogDensity = 0.004f;

		float r = this.skyColor.x * 1.2f;
		float g = this.skyColor.y * 1.2f;
		float b = this.skyColor.z * 1.2f;
		this.fogColor.set(r, g, b);
		this.fogColor.set(1, 1, 1);
	}

	public Vector3f getFogColor() {
		return (this.fogColor);
	}

	public void setFogColor(Vector3f color) {
		this.fogColor = color;
	}

	public float getFogGradient() {
		return (this.fogGradient);
	}

	public float getFogDensity() {
		return (this.fogDensity);
	}

	public float getAmbientLight() {
		return (this.ambientLight);
	}

	/*********************************
	 * AMBIENT ENDS
	 *********************************/

	/**********************************************************************************/
	/*********************************
	 * SUN STARTS
	 *********************************/
	/**********************************************************************************/
	public PointLight getSun() {
		return (this.sun);
	}

	private void calculateSunPosition() {
		float x = (float) Math.cos(this.cycleRatio * Math.PI * 2);
		float y = (float) Math.sin(this.cycleRatio * Math.PI * 2);
		float z = 0.0f;
		this.sun.setPosition(x, y, z);
	}

	/*********************************
	 * SUN END
	 *********************************/

	/**********************************************************************************/
	/*********************************
	 * RAIN STARTS
	 *********************************/
	/**********************************************************************************/
	private void calculateRain() {
		if (this.hasState(WeatherState.RAIN_STARTING)) {
			if (this.rainStrength < this.rainStrengthMax) {
				this.rainStrength++;
			} else {
				this.unsetState(WeatherState.RAIN_STARTING);
				this.setState(WeatherState.RAINING);
			}
		} else if (this.hasState(WeatherState.RAINING)) {
			this.rainTimer--;
			if (this.rainTimer <= 0) {
				this.unsetState(WeatherState.RAINING);
				this.setState(WeatherState.RAIN_ENDING);
			}
		} else if (this.hasState(WeatherState.RAIN_ENDING)) {
			if (this.rainStrength > 0) {
				this.rainStrength--;
			} else {
				this.unsetState(WeatherState.RAIN_ENDING);
			}
		}
	}

	public void startRain() {
		this.startRain((int) (this.rng.nextFloat()));
	}

	/**
	 * indices between ]0 ; 1] which is interpolated between MIN_RAIN and
	 * MAX_RAIN
	 */
	public void startRain(float f) {
		f = Maths.clamp(f, 0.0f, 1.0f);
		this.startRain((int) (f * (Sky.MAX_RAIN_STRENGTH - Sky.MIN_RAIN_STRENGTH) + Sky.MIN_RAIN_STRENGTH));
	}

	/** numbre of particles spawned per frames */
	public void startRain(int particles) {
		this.rainStrengthMax = particles;
		this.rainStrength = 0;
		this.rainTimer = 60 * 16;
		this.setState(WeatherState.RAIN_STARTING);
	}

	public void setFogColor(float r, float g, float b) {
		this.fogColor.set(r, g, b);
	}

	public void setFogGradient(float gradient) {
		this.fogGradient = gradient;
	}

	public void setFogDensity(float density) {
		this.fogDensity = density;
	}

	public int getRainStrength() {
		return (this.rainStrength);
	}

	public boolean isRaining() {
		return (this.hasState(WeatherState.RAINING) || this.hasState(WeatherState.RAIN_STARTING)
				|| this.hasState(WeatherState.RAIN_ENDING));
	}

	/*********************************
	 * RAIN ENDS
	 *********************************/

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.common.VoxelEngine.Callable<Taskable>> tasks) {

		VoxelEngine.Callable<Taskable> call = engine.new Callable<Taskable>() {

			@Override
			public Sky call() {
				Sky.this.update();
				return (Sky.this);
			}

			@Override
			public String getName() {
				return ("Weather update");
			}
		};

		tasks.add(call);
	}

}
