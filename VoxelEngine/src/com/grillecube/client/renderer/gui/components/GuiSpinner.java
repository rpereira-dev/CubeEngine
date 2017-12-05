package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;
import java.util.Comparator;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventAdd;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventExpanded;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventPick;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventRemove;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventRemoveAll;
import com.grillecube.client.renderer.gui.event.GuiSpinnerEventSorted;
import com.grillecube.common.utils.Pair;

/** a spinner list */
public abstract class GuiSpinner extends Gui {

	/** the objects hold */
	private final ArrayList<Pair<Object, String>> values;

	/** true if this spinner is expanded */
	private boolean expanded;

	/** the currently picked index */
	private int pickedIndex;

	/** default text */
	private String hintText;

	public GuiSpinner() {
		super();
		this.values = new ArrayList<Pair<Object, String>>();
		this.expanded = false;
		this.pickedIndex = -1;
		this.hintText = "...";
		this.addListener(ON_PRESS_FOCUS_LISTENER);
	}

	/** expands the gui spinner */
	public final void expand() {
		this.expanded = !this.expanded;
		this.onExpansionChanged();
		super.stackEvent(new GuiSpinnerEventExpanded<GuiSpinner>(this));
	}

	/** add a value to the spinner */
	public final void add(Object value) {
		this.add(value, String.valueOf(value));
	}

	/** add a value, and the given string name id to be shown */
	public final void add(Object value, String name) {
		this.add(value, name, this.values.size());
	}

	public final void add(Object value, String name, int index) {
		this.values.add(index, new Pair<Object, String>(value, name));
		this.onObjectAdded(index);
		super.stackEvent(new GuiSpinnerEventAdd<GuiSpinner>(this, index, value, name));
	}

	/** add a value to the spinner */
	public final Object remove(Object value) {
		int i = 0;
		for (Pair<Object, String> pair : this.values) {
			if (pair.left.equals(value)) {
				break;
			}
			++i;
		}
		if (i >= this.values.size()) {
			return (null);
		}
		return (this.remove(i));
	}

	public final Object remove(int index) {
		if (index < 0 || index >= this.values.size()) {
			return (null);
		}
		Pair<Object, String> value = this.values.remove(index);
		if (this.pickedIndex >= this.values.size()) {
			this.pick(this.values.size() - 1);
		}
		this.onObjectRemoved(index);
		super.stackEvent(new GuiSpinnerEventRemove<GuiSpinner>(this, index, value.left, value.right));
		return (value.left);
	}

	/** sort the spinner */
	public final void sort(Comparator<Object> comparator) {
		this.values.sort(comparator);
		this.onObjectsSorted();
		super.stackEvent(new GuiSpinnerEventSorted<GuiSpinner>(this));
	}

	/** remove every items of this spinner */
	public final void removeAll() {
		this.values.clear();
		this.onObjectsRemoved();
		super.stackEvent(new GuiSpinnerEventRemoveAll<GuiSpinner>(this));
	}

	/** callback when #GuiSpinner{@link #removeAll()} is called */
	protected void onObjectsRemoved() {
	}

	/** pick the given index */
	public final void pick(int index) {
		int prevIndex = this.pickedIndex;
		this.pickedIndex = index;
		this.onIndexPicked(this.pickedIndex);
		super.stackEvent(new GuiSpinnerEventPick<GuiSpinner>(this, prevIndex));
	}

	public final boolean isExpanded() {
		return (this.expanded);
	}

	/** get the objets */
	public final ArrayList<Pair<Object, String>> getValues() {
		return (this.values);
	}

	/** do the rendering of this gui */
	protected void onRender(GuiRenderer guiRenderer) {
	}

	/**
	 * @return the number of object hold by this spinner
	 */
	public final int count() {
		return (this.values.size());
	}

	/** get the name at given index */
	public final Object getPickedObject() {
		return (this.getObject(this.getPickedIndex()));
	}

	public final String getPickedName() {
		return (this.getName(this.getPickedIndex()));
	}

	/** get the value at given index */
	public final Object getObject(int index) {
		if (index < 0 || index >= this.values.size()) {
			return (null);
		}
		return (this.values.get(index).left);
	}

	/** get the name at given index */
	public final String getName(int index) {
		if (index < 0 || index >= this.values.size()) {
			return (null);
		}
		return (this.values.get(index).right);
	}

	/** get the index of the picked item (or -1 if empty, or no item picked) */
	public final int getPickedIndex() {
		return (this.pickedIndex);
	}

	/** set the hint to be displayed by default (or when empty) */
	public final void setHint(String hintText) {
		this.hintText = hintText;
		this.onHintChanged();
	}

	public final String getHint() {
		return (this.hintText);
	}

	/** called whenever the hint changes */
	protected void onHintChanged() {
	}

	protected void onIndexPicked(int pickedIndex) {
	}

	protected void onExpansionChanged() {
	}

	protected void onObjectAdded(int index) {
	}

	protected void onObjectsSorted() {
	}

	protected void onObjectRemoved(int index) {
	}
}