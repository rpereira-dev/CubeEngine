package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.utils.Color;
import com.grillecube.common.world.entity.collision.Positioneable;
import com.grillecube.common.world.entity.collision.Sizeable;

public abstract class CameraSelector implements Positioneable, Sizeable {

	private final GuiModelView guiModelView;
	private final Color color;

	public CameraSelector(GuiModelView guiModelView, Color color) {
		this.guiModelView = guiModelView;
		this.color = color;
	}

	public abstract void onMouseMove();

	public abstract void onLeftPressed();

	public abstract void onRightPressed();

	public abstract void onRightReleased();

	public abstract void onLeftReleased();

	public abstract void onMouseScroll(GuiEventMouseScroll<GuiModelView> event);

	public abstract Vector3i getBlock();

	public abstract Face getFace();

	public abstract int getX();

	public abstract int getY();

	public abstract int getZ();

	public abstract int getWidth();

	public abstract int getDepth();

	public abstract int getHeight();

	@Override
	public float getPositionX() {
		return (this.getX() * this.getBlockSizeUnit());
	}

	@Override
	public float getPositionY() {
		return (this.getY() * this.getBlockSizeUnit());
	}

	@Override
	public float getPositionZ() {
		return (this.getZ() * this.getBlockSizeUnit());
	}

	@Override
	public float getPositionVelocityX() {
		return 0;
	}

	@Override
	public float getPositionVelocityY() {
		return 0;
	}

	@Override
	public float getPositionVelocityZ() {
		return 0;
	}

	@Override
	public float getPositionAccelerationX() {
		return 0;
	}

	@Override
	public float getPositionAccelerationY() {
		return 0;
	}

	@Override
	public float getPositionAccelerationZ() {
		return 0;
	}

	@Override
	public void setPositionX(float x) {
	}

	@Override
	public void setPositionY(float y) {
	}

	@Override
	public void setPositionZ(float z) {
	}

	@Override
	public void setPositionVelocityX(float vx) {
	}

	@Override
	public void setPositionVelocityY(float vy) {
	}

	@Override
	public void setPositionVelocityZ(float vz) {
	}

	@Override
	public void setPositionAccelerationX(float ax) {
	}

	@Override
	public void setPositionAccelerationY(float ay) {
	}

	@Override
	public void setPositionAccelerationZ(float az) {
	}

	@Override
	public float getSizeX() {
		return (this.getWidth() * this.getBlockSizeUnit());
	}

	@Override
	public float getSizeY() {
		return (this.getDepth() * this.getBlockSizeUnit());
	}

	@Override
	public float getSizeZ() {
		return (this.getHeight() * this.getBlockSizeUnit());
	}

	@Override
	public float getSizeVelocityX() {
		return 0;
	}

	@Override
	public float getSizeVelocityY() {
		return 0;
	}

	@Override
	public float getSizeVelocityZ() {
		return 0;
	}

	@Override
	public float getSizeAccelerationX() {
		return 0;
	}

	@Override
	public float getSizeAccelerationY() {
		return 0;
	}

	@Override
	public float getSizeAccelerationZ() {
		return 0;
	}

	@Override
	public void setSizeX(float x) {
	}

	@Override
	public void setSizeY(float y) {
	}

	@Override
	public void setSizeZ(float z) {
	}

	@Override
	public void setSizeVelocityX(float vx) {
	}

	@Override
	public void setSizeVelocityY(float vy) {
	}

	@Override
	public void setSizeVelocityZ(float vz) {
	}

	@Override
	public void setSizeAccelerationX(float ax) {
	}

	@Override
	public void setSizeAccelerationY(float ay) {
	}

	@Override
	public void setSizeAccelerationZ(float az) {
	}

	public void update() {
	}

	public final float getBlockSizeUnit() {
		return (this.getModelLayer() == null ? 1.0f : this.getModelLayer().getBlockSizeUnit());
	}

	public final ModelInstance getModelInstance() {
		return (this.guiModelView.getSelectedModelInstance());
	}

	public final EditableModel getModel() {
		return (this.guiModelView.getSelectedModel());
	}

	public final EditableModelLayer getModelLayer() {
		return (this.guiModelView.getSelectedModelLayer());
	}

	public final ModelEditorCamera getCamera() {
		return (this.guiModelView.getCamera());
	}

	public boolean isLeftPressed() {
		return (this.guiModelView.isLeftPressed());
	}

	public final boolean isRightPressed() {
		return (this.guiModelView.isRightPressed());
	}

	public final float getMouseDX() {
		return (this.guiModelView.getMouseX() - this.guiModelView.getPrevMouseX());
	}

	public final float getMouseDY() {
		return (this.guiModelView.getMouseY() - this.guiModelView.getPrevMouseY());
	}

	public final GuiModelView getGuiModelView() {
		return (this.guiModelView);
	}

	public final EditableModelLayer getSelectedModelLayer() {
		return (this.guiModelView.getSelectedModelLayer());
	}

	public final ModelInstance getSelectedModelInstance() {
		return (this.guiModelView.getSelectedModelInstance());
	}

	public final float getMouseX() {
		return (this.guiModelView.getMouseX());
	}

	public final float getMouseY() {
		return (this.guiModelView.getMouseY());
	}

	public final ModelSkin getSelectedSkin() {
		return (this.guiModelView.getSelectedSkin());
	}

	public final Color getSelectedColor() {
		return (this.guiModelView.getSelectedColor());
	}

	public final Color getSelectorColor() {
		return (this.color);
	}

}
