package com.grillecube.client.renderer.model.editor.gui.toolbox;

import java.util.ArrayList;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.gui.components.parameters.GuiParameter;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.utils.Color;
import com.grillecube.common.world.entity.Entity;

/** a view which handles model creation */
public class GuiToolboxModel extends GuiView {

	/** new model button */
	private final ModelInstance modelInstance;
	private final GuiButton prev;
	private final GuiLabel title;
	private final GuiButton next;

	private final ArrayList<GuiToolboxModelPanel> panels;

	private GuiToolboxModelPanelBuild buildPanel;
	private GuiToolboxModelPanelSkin skinPanel;
	private GuiToolboxModelPanelSkeleton skeletonPanel;
	private GuiToolboxModelPanelAnimation animationPanel;

	private int selected;

	public GuiToolboxModel(ModelInstance modelInstance) {
		super();

		this.setHoverable(false);

		this.modelInstance = modelInstance;

		GuiParameter<GuiText> center = new GuiTextParameterTextCenterBox();

		this.prev = new GuiButton();
		this.prev.setBox(0, 0.75f, 1 / 3.0f, 0.05f, 0);
		this.prev.addTextParameter(center);
		this.prev.setText("<-");
		this.addChild(this.prev);
		this.prev.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				event.getGui().addTask(new GuiTask() {
					@Override
					public final boolean run() {
						select(selected == 0 ? panels.size() - 1 : selected - 1);
						return (true);
					}
				});
			}
		});

		this.title = new GuiLabel();
		this.title.setBox(1 / 3.0f, 0.75f, 1 / 3.0f, 0.05f, 0);
		this.title.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.title.addTextParameter(new GuiTextParameterTextCenterBox());
		this.addChild(this.title);

		this.next = new GuiButton();
		this.next.setBox(2 / 3.0f, 0.75f, 1 / 3.0f, 0.05f, 0);
		this.next.addTextParameter(center);
		this.next.setText("->");
		this.next.addListener(new GuiListener<GuiEventClick<GuiButton>>() {
			@Override
			public void invoke(GuiEventClick<GuiButton> event) {
				event.getGui().addTask(new GuiTask() {
					@Override
					public final boolean run() {
						select((selected + 1) % panels.size());
						return (true);
					}
				});
			}
		});
		this.addChild(this.next);

		this.panels = new ArrayList<GuiToolboxModelPanel>();
		this.buildPanel = new GuiToolboxModelPanelBuild();
		this.skinPanel = new GuiToolboxModelPanelSkin();
		this.skeletonPanel = new GuiToolboxModelPanelSkeleton();
		this.animationPanel = new GuiToolboxModelPanelAnimation();

		this.panels.add(this.buildPanel);
		this.panels.add(this.skinPanel);
		this.panels.add(this.skeletonPanel);
		this.panels.add(this.animationPanel);

		for (GuiToolboxModelPanel panel : this.panels) {
			panel.setVisible(false);
			this.addChild(panel);
		}

		this.panels.get(0).fadeIn(this, 0.15d);

		this.refresh();
	}

	public final void refresh() {
		this.title.setText(this.panels.get(this.selected).getTitle());
		for (GuiToolboxModelPanel panel : this.panels) {
			panel.refresh();
		}
	}

	private final void select(int index) {
		GuiToolboxModelPanel prev = this.panels.get(this.selected);
		if (prev != null) {
			prev.fadeOut(0.15d);
		}
		this.selected = index;
		GuiToolboxModelPanel next = this.panels.get(this.selected);
		if (next != null) {
			next.fadeIn(this, 0.15d);
		}
		this.refresh();
	}

	public final EditableModel getModel() {
		return ((EditableModel) (this.modelInstance.getModel()));
	}

	public final ModelInstance getModelInstance() {
		return (this.modelInstance);
	}

	public final Entity getEntity() {
		return (this.modelInstance.getEntity());
	}

	public final int getSelectedTool() {
		return (this.buildPanel.getSelectedTool());
	}

	public final ModelSkin getSelectedSkin() {
		return (this.skinPanel.getSelectedSkin());
	}

	public final Color getSelectedColor() {
		return (this.skinPanel.getSelectedColor());
	}

	public final GuiToolboxModelPanelBuild getGuiToolboxModelPanelBuild() {
		return (this.buildPanel);
	}

	public final GuiToolboxModelPanelSkin getGuiToolboxModelPanelSkin() {
		return (this.skinPanel);
	}

	public final GuiToolboxModelPanelSkeleton getGuiToolboxModelPanelSkeleton() {
		return (this.skeletonPanel);
	}

	public final GuiToolboxModelPanelAnimation getGuiToolboxModelPanelAnimation() {
		return (this.animationPanel);
	}

	public final void selectNextPanel() {
		this.select((this.selected + 1) % this.panels.size());
	}

	public final void selectPreviousPanel() {
		int index = this.selected - 1;
		if (index < 0) {
			index = this.panels.size() - 1;
		}
		this.select(index);
	}
}
