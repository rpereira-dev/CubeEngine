package com.grillecube.client.renderer.model.editor.gui.toolbox;

import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;

/** a view which handles model creation */
public abstract class GuiToolboxModelPanel extends GuiView {

	public abstract void refresh();

	public abstract String getTitle();

	public final EditableModel getModel() {
		return (((GuiToolboxModel) this.getParent()).getModel());
	}
}