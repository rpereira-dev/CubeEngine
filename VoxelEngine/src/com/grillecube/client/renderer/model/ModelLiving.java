package com.grillecube.client.renderer.model;

import com.grillecube.client.renderer.model.animation.ModelAnimation;
import com.grillecube.common.resources.R;

/** a class which represent living models (mobs, players...) */
public class ModelLiving extends Model {

	public enum EnumAnimation {
		IDLE("anim_idle"),
		WALK("anim_walk"),
		RUN("anim_run"),
		JUMP("anim_jump"),
		GREET("anim_greet"),
		SIT_DOWN("anim_sit_down"),
		SIT_UP("anim_sit_up"),
		SIT("anim_sit"),
		LAY_DOWN("anim_lay_down"),
		LAY_UP("anim_lay_up"),
		EAT("anim_eat");
				
		String _name;

		EnumAnimation(String name) {
			this._name = name;
		}

		public String getUnlocalizedName() {
			return (this._name);
		}

		public int getID() {
			return (this.ordinal());
		}
	}

	/** prepare the model when initialized as a model builder */
	public void prepareModelBuilder() {
		super.prepareModelBuilder();
		for (EnumAnimation animation : EnumAnimation.values()) {
			super.addAnimation(new ModelAnimation(R.getWord(animation.getUnlocalizedName())));
		}
	}
}
