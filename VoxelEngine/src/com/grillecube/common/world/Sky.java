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

package com.grillecube.common.world;

import java.util.ArrayList;
import java.util.Random;

import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;

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
	private int _state;

	private Random _rng;

	/** time [0.0 ; 1.0] */
	private float _cycle_ratio;
	private int _cycle_count;

	/** time in millis [0.0 ; MILLIS_PER_CYCLE] */
	private long _cycle_millis;
	private long _prev_millis;

	/** sky data */
	private static final Vector3f SKY_COLOR_DAY = new Vector3f(0.4f, 0.71f, 0.99f);
	private static final Vector3f SKY_COLOR_NIGHT = new Vector3f(0.01f, 0.14f, 0.19f);
	private Vector3f _sky_color;

	/** sun color */
	private static final Vector3f SUN_RISE_COLOR = new Vector3f(1, 1, 1);

	/** sun light */
	private PointLight _sun;

	private static final float DAY_AMBIENT = 0.6f;
	private static final float NIGHT_AMBIENT = 0.15f;
	private float _ambient_light;

	/** fog data */
	private Vector3f _fog_color;
	private float _fog_density;
	private float _fog_gradient;
	private int _rain_strength;
	private int _rain_strength_max;
	private int _rain_timer;

	public static final int MAX_RAIN_STRENGTH = 128;
	public static final int MIN_RAIN_STRENGTH = 8;

	/** weather factors */
	private float _humidity;
	private float _temperature;
	private float _wind;

	/** Weather constructor */
	public Sky() {
		this.setCycleRatio(Sky.DAY_START);
		this._state = 0;
		this._ambient_light = 0;
		this._cycle_count = 0;
		this._rain_timer = 0;

		this._sky_color = new Vector3f();

		this._sun = new PointLight(new Vector3f(10000f, 10000f, 10000f), Sky.SUN_RISE_COLOR, 1.0f);

		this._fog_color = new Vector3f(1.0f, 1.0f, 1.0f);
		this._fog_density = 0.04f;
		this._fog_gradient = 2.5f;
	}

	public void setCycleRatio(float f) {
		this._cycle_ratio = f;
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
			float intensity = 1 - (this._cycle_ratio - Sky.DAY_END) / (Sky.NIGHT_START - Sky.DAY_END);
			this._sun.setIntensity(intensity);
			this._ambient_light = intensity * Sky.DAY_AMBIENT + (1 - intensity) * Sky.NIGHT_AMBIENT;
		}

		if (this.hasState(WeatherState.NIGHT_ENDING)) {
			float intensity = (this._cycle_ratio - Sky.NIGHT_END) / (1.0f - Sky.NIGHT_END);
			this._sun.setIntensity(intensity);
			this._ambient_light = intensity * Sky.DAY_AMBIENT + (1 - intensity) * Sky.NIGHT_AMBIENT;
		}

		if (this.hasState(WeatherState.DAY)) {
			this._sun.setIntensity(1.0f);
			this._ambient_light = Sky.DAY_AMBIENT;
		}

		if (this.hasState(WeatherState.NIGHT)) {
			this._sun.setIntensity(0.0f);
			this._ambient_light = Sky.NIGHT_AMBIENT;
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
			this._cycle_count++;
		}
		this._cycle_ratio = this._cycle_millis / (float) MILLIS_PER_CYCLE;
		this._prev_millis = time;

		if (this._cycle_ratio >= Sky.DAY_START && this._cycle_ratio < Sky.DAY_END) {
			this.setCycleState(WeatherState.DAY);
		}

		if (this._cycle_ratio >= Sky.DAY_END && this._cycle_ratio < Sky.NIGHT_START) {
			this.setCycleState(WeatherState.DAY_ENDING);
		}

		if (this._cycle_ratio > Sky.NIGHT_START && this._cycle_ratio <= Sky.NIGHT_END) {
			this.setCycleState(WeatherState.NIGHT);
		}

		if (this._cycle_ratio >= Sky.NIGHT_END) {
			this.setCycleState(WeatherState.NIGHT_ENDING);
		}
	}

	public int getDayCount() {
		return (this._cycle_count);
	}

	public float getCycleRatio() {
		return (this._cycle_ratio);
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
		this._state = this._state | state;
	}

	private void unsetState(int state) {
		this._state = this._state & ~(state);
	}

	public boolean hasState(int state) {
		return ((this._state & state) == state);
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
			ratio = 1 - (this._cycle_ratio - Sky.DAY_END) / (Sky.NIGHT_START - Sky.DAY_END);
		} else if (this.hasState(WeatherState.NIGHT_ENDING)) {
			ratio = (this._cycle_ratio - Sky.NIGHT_END) / (1.0f - Sky.NIGHT_END);
		}
		Vector3f.mix(this._sky_color, Sky.SKY_COLOR_DAY, Sky.SKY_COLOR_NIGHT, ratio);
	}

	public Vector3f getSkyColor() {
		return (this._sky_color);
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
		this._fog_gradient = 4.0f;
		this._fog_density = 0.004f;

		float r = this._sky_color.x * 1.2f;
		float g = this._sky_color.y * 1.2f;
		float b = this._sky_color.z * 1.2f;
		this._fog_color.set(r, g, b);
		this._fog_color.set(1, 1, 1);
	}

	public Vector3f getFogColor() {
		return (this._fog_color);
	}

	public void setFogColor(Vector3f color) {
		this._fog_color = color;
	}

	public float getFogGradient() {
		return (this._fog_gradient);
	}

	public float getFogDensity() {
		return (this._fog_density);
	}

	public float getAmbientLight() {
		return (this._ambient_light);
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
		return (this._sun);
	}

	private void calculateSunPosition() {
		float x = (float) Math.cos(this._cycle_ratio * Math.PI * 2);
		float y = (float) Math.sin(this._cycle_ratio * Math.PI * 2);
		float z = 0.0f;
		this._sun.setPosition(x, y, z);
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
			if (this._rain_strength < this._rain_strength_max) {
				this._rain_strength++;
			} else {
				this.unsetState(WeatherState.RAIN_STARTING);
				this.setState(WeatherState.RAINING);
			}
		} else if (this.hasState(WeatherState.RAINING)) {
			this._rain_timer--;
			if (this._rain_timer <= 0) {
				this.unsetState(WeatherState.RAINING);
				this.setState(WeatherState.RAIN_ENDING);
			}
		} else if (this.hasState(WeatherState.RAIN_ENDING)) {
			if (this._rain_strength > 0) {
				this._rain_strength--;
			} else {
				this.unsetState(WeatherState.RAIN_ENDING);
			}
		}
	}

	public void startRain() {
		this.startRain((int) (this._rng.nextFloat()));
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
		this._rain_strength_max = particles;
		this._rain_strength = 0;
		this._rain_timer = 60 * 16;
		this.setState(WeatherState.RAIN_STARTING);
	}

	public void setFogColor(float r, float g, float b) {
		this._fog_color.set(r, g, b);
	}

	public void setFogGradient(float gradient) {
		this._fog_gradient = gradient;
	}

	public void setFogDensity(float density) {
		this._fog_density = density;
	}

	public int getRainStrength() {
		return (this._rain_strength);
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
