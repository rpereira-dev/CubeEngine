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
import com.grillecube.client.renderer.model.EditableModel;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.world.entity.Entity;

/** a view which handles model creation */
public class GuiToolboxModel extends GuiView {

	/** new model button */
	private final ModelInstance modelInstance;
	private final GuiButton prev;
	private final GuiLabel title;
	private final GuiButton next;

	private final ArrayList<GuiToolboxModelPanel> panels;

	private int selected;

	public GuiToolboxModel(ModelInstance modelInstance) {
		super();

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
					public void run() {
						select(selected == 0 ? panels.size() - 1 : selected - 1);
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
					public void run() {
						select((selected + 1) % panels.size());
					}
				});
			}
		});
		this.addChild(this.next);

		this.panels = new ArrayList<GuiToolboxModelPanel>();
		this.panels.add(new GuiToolboxModelPanelBuild());
		this.panels.add(new GuiToolboxModelPanelSkin());
		this.panels.add(new GuiToolboxModelPanelSkeleton());
		this.panels.add(new GuiToolboxModelPanelAnimation());

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
}
