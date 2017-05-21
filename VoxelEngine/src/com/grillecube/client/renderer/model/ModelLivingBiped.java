package com.grillecube.client.renderer.model;

import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.common.resources.R;

public class ModelLivingBiped extends ModelLiving {

	/** part id */
	public static final int PART_LEFT_LEG = 0;
	public static final int PART_RIGHT_LEG = 1;
	public static final int PART_BODY = 2;
	public static final int PART_LEFT_ARM = 3;
	public static final int PART_RIGHT_ARM = 4;
	public static final int PART_HEAD = 5;

	public ModelLivingBiped() {
		super();
	}

	@Override
	public void prepareModelBuilder() {
		super.prepareModelBuilder();

		super.addPartBuilder(new ModelPartBuilder(R.getWord("left_leg")));
		super.addPartBuilder(new ModelPartBuilder(R.getWord("right_leg")));
		super.addPartBuilder(new ModelPartBuilder(R.getWord("body")));

		ModelPartBuilder left_arm = new ModelPartBuilder(R.getWord("left_arm"));
		left_arm.addAttachmentPoint(new ModelPartAttachmentPoint("Items"));
		super.addPartBuilder(left_arm);

		ModelPartBuilder right_arm = new ModelPartBuilder(R.getWord("right_arm"));
		right_arm.addAttachmentPoint(new ModelPartAttachmentPoint("Items"));
		super.addPartBuilder(right_arm);

		super.addPartBuilder(new ModelPartBuilder(R.getWord("head")));
	}
}
