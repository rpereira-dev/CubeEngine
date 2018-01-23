package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.GuiPopUp;

/** simple callback interface to play with 'GuiPopUp' class */
public interface GuiPopUpCallback<T extends GuiPopUp> {

	/** callback if confirmed */
	public void onConfirm(T popUp);

	/** callback if cancel */
	public void onCancel(T popUp);
}
