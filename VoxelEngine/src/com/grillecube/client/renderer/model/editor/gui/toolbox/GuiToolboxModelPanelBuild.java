package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiSpinner;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.event.GuiPromptEventHeldTextChanged;
import com.grillecube.client.renderer.model.editor.gui.GuiPromptEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiSpinnerEditor;

public class GuiToolboxModelPanelBuild extends GuiToolboxModelPanel {

	/** the building tools list */
	private final GuiSpinner tools;

	/** the model block size unit slider bar */
	private final GuiPromptEditor modelBlockSizeUnit;

	private final GuiButton tx;
	private final GuiButton ty;
	private final GuiButton tz;

	private final GuiButton txm;
	private final GuiButton tym;
	private final GuiButton tzm;

	private final GuiButton rx;
	private final GuiButton ry;
	private final GuiButton rz;
	// private final GuiButton translate;

	public GuiToolboxModelPanelBuild() {
		super();
		this.tools = new GuiSpinnerEditor();
		this.tools.setHint("Tools...");
		this.tools.add(0, "Place");
		this.tools.add(1, "Paint");
		this.tools.add(2, "Remove");
		this.tools.add(3, "Extrude");
		this.tools.add(4, "Rigging");
		this.tools.pick(0);
		this.tools.setBox(0, 0.70f, 1.0f, 0.05f, 0);
		this.addChild(this.tools);

		// block size unit slider bar
		this.modelBlockSizeUnit = new GuiPromptEditor("Size-unit:", "block size");
		this.modelBlockSizeUnit.setBox(0, 0.65f, 1.0f, 0.05f, 0);
		this.modelBlockSizeUnit.getPrompt().addListener(new GuiListener<GuiPromptEventHeldTextChanged<GuiPrompt>>() {
			@Override
			public void invoke(GuiPromptEventHeldTextChanged<GuiPrompt> event) {
				onBlockSizeUnitChanged();
			}
		});
		this.addChild(this.modelBlockSizeUnit);

		float w = 1.0f / 3.0f;

		// translate
		this.tx = new GuiButton();
		this.tx.setText("+X");
		this.tx.setBox(0.0f, 0.60f, w, 0.05f, 0.0f);
		this.tx.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.tx.addTextParameter(new GuiTextParameterTextCenterBox());
		this.tx.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().translate(1, 0, 0);
				getModel().generate();
			}
		});
		this.addChild(this.tx);

		this.ty = new GuiButton();
		this.ty.setText("+Y");
		this.ty.setBox(w, 0.60f, w, 0.05f, 0.0f);
		this.ty.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.ty.addTextParameter(new GuiTextParameterTextCenterBox());
		this.ty.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().translate(0, 1, 0);
				getModel().generate();
			}
		});
		this.addChild(this.ty);

		this.tz = new GuiButton();
		this.tz.setText("+Z");
		this.tz.setBox(2.0f * w, 0.60f, w, 0.05f, 0.0f);
		this.tz.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.tz.addTextParameter(new GuiTextParameterTextCenterBox());
		this.tz.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().translate(0, 0, 1);
				getModel().generate();
			}
		});
		this.addChild(this.tz);

		// translate
		this.txm = new GuiButton();
		this.txm.setText("-X");
		this.txm.setBox(0.0f, 0.55f, w, 0.05f, 0.0f);
		this.txm.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.txm.addTextParameter(new GuiTextParameterTextCenterBox());
		this.txm.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().translate(-1, 0, 0);
				getModel().generate();
			}
		});
		this.addChild(this.txm);

		this.tym = new GuiButton();
		this.tym.setText("-Y");
		this.tym.setBox(w, 0.55f, w, 0.05f, 0.0f);
		this.tym.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.tym.addTextParameter(new GuiTextParameterTextCenterBox());
		this.tym.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().translate(0, -1, 0);
				getModel().generate();
			}
		});
		this.addChild(this.tym);

		this.tzm = new GuiButton();
		this.tzm.setText("-Z");
		this.tzm.setBox(2.0f * w, 0.55f, w, 0.05f, 0.0f);
		this.tzm.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.tzm.addTextParameter(new GuiTextParameterTextCenterBox());
		this.tzm.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().translate(0, 0, -1);
				getModel().generate();
			}
		});
		this.addChild(this.tzm);

		// rotate
		this.rx = new GuiButton();
		this.rx.setText("RX");
		this.rx.setBox(0.0f, 0.50f, w, 0.05f, 0.0f);
		this.rx.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.rx.addTextParameter(new GuiTextParameterTextCenterBox());
		this.rx.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().rotateX();
				getModel().generate();
			}
		});
		this.addChild(this.rx);

		this.ry = new GuiButton();
		this.ry.setText("RY");
		this.ry.setBox(w, 0.50f, w, 0.05f, 0.0f);
		this.ry.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.ry.addTextParameter(new GuiTextParameterTextCenterBox());
		this.ry.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().rotateY();
				getModel().generate();
			}
		});
		this.addChild(this.ry);

		this.rz = new GuiButton();
		this.rz.setText("RZ");
		this.rz.setBox(2.0f * w, 0.50f, w, 0.05f, 0.0f);
		this.rz.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.rz.addTextParameter(new GuiTextParameterTextCenterBox());
		this.rz.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				getModel().rotateZ();
				getModel().generate();
			}
		});
		this.addChild(this.rz);
	}

	@Override
	public void refresh() {
		this.modelBlockSizeUnit.getPrompt().setHeldText(String.valueOf(this.getModel().getBlockSizeUnit()));
	}

	private final void onBlockSizeUnitChanged() {
		float sizeUnit = 0.0f;
		try {
			sizeUnit = this.modelBlockSizeUnit.getPrompt().asFloat();
		} catch (Exception e) {
		}
		if (sizeUnit == this.getModel().getBlockSizeUnit()) {
			return;
		}
		this.getModel().setBlockSizeUnit(sizeUnit);
		this.getModel().requestMeshUpdate();
	}

	@Override
	public String getTitle() {
		return ("Build");
	}

	public final int getSelectedTool() {
		return ((Integer) this.tools.getPickedObject());
	}

	public final int selectNextTool() {
		this.tools.pick((this.tools.getPickedIndex() + 1) % this.tools.count());
		return (this.tools.getPickedIndex());
	}

	public final int selectPreviousTool() {
		int index = this.tools.getPickedIndex() - 1;
		if (index < 0) {
			index = this.tools.count() - 1;
		}
		this.tools.pick(index);
		return (index);
	}
}
