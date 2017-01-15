package com.grillecube.engine.renderer.model;

import com.grillecube.engine.renderer.model.ModelLiving.EnumAnimation;
import com.grillecube.engine.renderer.model.animation.ModelAnimation;
import com.grillecube.engine.resources.R;

/** a class which represents static models: plants, chairs, furnitures... */
public class ModelStatic extends Model {
	public enum EnumAnimation {
		INTERACT("anim_interact_name", "anim_interact_comment"), UPDATE("anim_update_name", "anim_update_comment");

		String _name;
		String _comment;

		EnumAnimation(String name, String comment) {
			this._name = name;
			this._comment = comment;
		}

		public String getUnlocalizedName() {
			return (this._name);
		}

		public int getID() {
			return (this.ordinal());
		}

		public String getComment() {
			return (this._comment);
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
