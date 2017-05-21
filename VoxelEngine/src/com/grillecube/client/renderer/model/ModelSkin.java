package com.grillecube.client.renderer.model;

import java.util.ArrayList;

public class ModelSkin {

	private String _name;
	private ArrayList<ModelPartSkin> _part_skins;

	public ModelSkin(String name) {
		this._name = name;
		this._part_skins = new ArrayList<ModelPartSkin>();
	}

	/** set skin data for the given id */
	public int addPart(ModelPartSkin skin) {
		int partID = this._part_skins.size();
		this._part_skins.add(skin);
		return (partID);
	}

	/** set skin data for the given id */
	public void removeSkin(ModelPartSkin skin) {
		this._part_skins.remove(skin);
	}

	public ArrayList<ModelPartSkin> getPartsSkin() {
		return (this._part_skins);
	}

	public String getName() {
		return (this._name);
	}

	@Override
	public String toString() {
		return (this.getName());
	}

	public void removePart(int index) {
		ModelPartSkin skin = this._part_skins.remove(index);
		skin.delete();
	}

	public ModelPartSkin getPart(int id) {
		if (id < 0 || id >= this._part_skins.size()) {
			return (null);
		}
		return (this._part_skins.get(id));
	}
}
