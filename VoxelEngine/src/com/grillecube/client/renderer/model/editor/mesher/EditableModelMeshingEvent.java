package com.grillecube.client.renderer.model.editor.mesher;

import com.grillecube.common.event.Event;

/**
 * An event which is raised whenever an editable model mesh is generated
 * 
 * @author rpereira
 *
 */
public class EditableModelMeshingEvent extends Event {

	/** the mesher to be used for this model */
	private final ModelMesher modelMesher;
	private final EditableModel model;

	public EditableModelMeshingEvent(EditableModel theModel) {
		this.modelMesher = new ModelMesherCull();
		this.model = theModel;
	}

	@Override
	protected void process() {
		this.modelMesher.generate(this.model);

	}

	@Override
	protected void unprocess() {
		throw new UnsupportedOperationException("Can't cancel a model meshing!");
	}

	@Override
	protected void onReset() {
	}

	public final EditableModel getEditableModel() {
		return (this.model);
	}

}
