package com.grillecube.common.resources;

import java.io.File;
import java.io.IOException;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.json.ModelImporter;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;

public class ModelManager extends GenericManager<Model> {

	public ModelManager(ResourceManager resource_manager) {
		super(resource_manager);
	}

	/** register a model the to engine */
	public Model registerModel(String filepath) {

		// check if the model file actually exists
		File file = new File(filepath);
		if (!file.exists() || !file.canRead() || !file.isFile()) {
			Logger.get().log(Logger.Level.WARNING, "Failed to register a model: ", filepath,
					"IO Error: file is a directory? cant read file? file doesnt exists?");
			return (null);
		}

		// else if client side
		for (Model model : super.getObjects()) {
			if (model.getFilepath().equals(filepath)) {
				Logger.get().log(Logger.Level.WARNING, "Tried to register a model already registered: ", filepath);
				return (null);
			}
		}

		Model model;
		try {
			model = ModelImporter.loadModel(filepath);
		} catch (IOException e) {
			Logger.get().log(Level.FINE, "Registering failed: " + e.getLocalizedMessage());
			return (null);
		}
		super.registerObject(model);
		return (model);
	}

	@Override
	protected void onObjectRegistered(Model object) {
	}

	@Override
	protected void onInitialized() {
	}

	@Override
	protected void onStopped() {
		for (Model model : super.getObjects()) {
			model.delete();
		}
	}

	@Override
	protected void onCleaned() {
	}

	@Override
	protected void onLoaded() {
	}

	/** return the model with the given id */
	public Model getModel(int modelID) {

		// if wrong id...
		if (modelID < 0 || modelID >= super.getObjectCount()) {
			return (null);
		}

		return (super.getObjectByID(modelID));
	}
}
