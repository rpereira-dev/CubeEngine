package com.grillecube.client.renderer.model.editor.mesher;

import java.util.HashMap;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInitializer;

public class EditableModel extends Model {

	/** blocks data : first key is the layer, value is the blocks data */
	private HashMap<String, EditableModelLayer> blocksDataLayers;

	/** a boolean set to true if mesh should be rebuild */
	private boolean meshUpToDate;
	private final EditableModelMeshingEvent meshingEvent;

	public EditableModel() {
		this(null);
	}

	public EditableModel(ModelInitializer modelInitializer) {
		super(modelInitializer);
		this.blocksDataLayers = new HashMap<String, EditableModelLayer>();
		this.meshingEvent = new EditableModelMeshingEvent(this);
	}

	/** return raw blocks data */
	public final HashMap<String, EditableModelLayer> getRawLayers() {
		return (this.blocksDataLayers);
	}

	/** return a new deep copy of raw blocks data */
	public final HashMap<String, EditableModelLayer> getRawLayersCopy() {
		HashMap<String, EditableModelLayer> layers = new HashMap<String, EditableModelLayer>();
		for (String layerName : this.blocksDataLayers.keySet()) {
			EditableModelLayer layer = this.blocksDataLayers.get(layerName);
			layers.put(layerName, layer.clone());
		}
		return (layers);
	}

	public final EditableModelLayer getLayer(String layerName) {
		return (this.blocksDataLayers.get(layerName));
	}

	public void removeLayer(String layerName) {
		this.blocksDataLayers.remove(layerName);
	}

	public void removeLayer(EditableModelLayer layer) {
		this.blocksDataLayers.remove(layer.getName());
	}

	public final void setLayer(EditableModelLayer editableModelLayer) {
		this.blocksDataLayers.put(editableModelLayer.getName(), editableModelLayer);
	}

	/** set raw blocks data */
	public final void setRawLayers(HashMap<String, EditableModelLayer> blockLayers) {
		this.blocksDataLayers = blockLayers;
	}

	@Override
	public final void onBound() {
		super.onBound();
		if (!this.isMeshUpToDate()) {
			this.setMeshUpToDate();
			this.meshingEvent.process();
			VoxelEngineClient.instance().getResourceManager().getEventManager().invokeEvent(this.meshingEvent);
		}
	}

	private final void setMeshUpToDate() {
		this.meshUpToDate = true;
	}

	private final boolean isMeshUpToDate() {
		return (this.meshUpToDate);
	}

	public final void requestMeshUpdate() {
		this.meshUpToDate = false;
	}

	/**
	 * @return : return number of set blocks
	 */
	public final int getBlockDataCount() {
		int sum = 0;
		for (EditableModelLayer layer : this.blocksDataLayers.values()) {
			sum += layer.getBlockDataCount();
		}
		return (sum);
	}
}