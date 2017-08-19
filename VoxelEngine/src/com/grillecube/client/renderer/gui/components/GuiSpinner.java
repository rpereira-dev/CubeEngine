package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;
import java.util.Comparator;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseLeftPress;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerAdd;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerExpanded;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerPick;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerRemove;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerSorted;
import com.grillecube.common.utils.Pair;

/** a slider bar */
public abstract class GuiSpinner extends Gui {

	private static final GuiListenerMouseLeftPress<GuiSpinner> LISTENER = new GuiListenerMouseLeftPress<GuiSpinner>() {
		@Override
		public void invokeMouseLeftPress(GuiSpinner guiSpinner, double mousex, double mousey) {
			guiSpinner.expand();
		}
	};

	/** listeners */
	private ArrayList<GuiSpinnerListenerExpanded> guiSpinnerListenersExpanded;
	private ArrayList<GuiSpinnerListenerAdd> guiSpinnerListenersAdd;
	private ArrayList<GuiSpinnerListenerRemove> guiSpinnerListenersRemove;
	private ArrayList<GuiSpinnerListenerSorted> guiSpinnerListenersSorted;
	private ArrayList<GuiSpinnerListenerPick> guiSpinnerListenersPick;

	/** the objects hold */
	private final ArrayList<Pair<Object, String>> values;

	/** true if this spinner is expanded */
	private boolean expanded;

	/** the currently selected index */
	private int selectedIndex;

	public GuiSpinner() {
		super();
		this.values = new ArrayList<Pair<Object, String>>();
		this.expanded = false;
		this.selectedIndex = 0;
		this.addListener(LISTENER);
	}

	/** expands the gui spinner */
	public final void expand() {
		this.expanded = !this.expanded;
		if (this.guiSpinnerListenersExpanded != null) {
			for (GuiSpinnerListenerExpanded listener : this.guiSpinnerListenersExpanded) {
				listener.invokeSpinnerExpanded(this, this.expanded);
			}
		}
	}

	/** add a value to the spinner */
	public final void add(Object value) {
		this.add(value, value.toString());
	}

	/** add a value, and the given string name id to be shown */
	public final void add(Object value, String name) {
		this.values.add(new Pair<Object, String>(value, name));
		if (this.guiSpinnerListenersAdd != null) {
			for (GuiSpinnerListenerAdd listener : this.guiSpinnerListenersAdd) {
				listener.invokeSpinnerAddObject(this, this.values.size() - 1);
			}
		}
	}

	/** add a value to the spinner */
	public final void remove(Object value) {
		int index = this.values.indexOf(value);
		if (index == -1) {
			return;
		}
		this.remove(index);
	}

	public final void remove(int index) {
		this.values.remove(index);
		if (this.guiSpinnerListenersRemove != null) {
			for (GuiSpinnerListenerRemove listener : this.guiSpinnerListenersRemove) {
				listener.invokeSpinnerRemoveObject(this, index);
			}
		}
	}

	/** sort the spinner */
	public final void sort(Comparator<Object> comparator) {
		this.values.sort(comparator);
		if (this.guiSpinnerListenersSorted != null) {
			for (GuiSpinnerListenerSorted listener : this.guiSpinnerListenersSorted) {
				listener.invokeSpinnerSorted(this);
			}
		}
	}

	/** pick the given index */
	public final void pick(int index) {
		this.selectedIndex = index;
		if (this.guiSpinnerListenersPick != null) {
			for (GuiSpinnerListenerPick listener : this.guiSpinnerListenersPick) {
				listener.invokeSpinnerIndexPick(this, index);
			}
		}
	}

	public final void addListener(GuiSpinnerListenerAdd listener) {
		if (this.guiSpinnerListenersAdd == null) {
			this.guiSpinnerListenersAdd = new ArrayList<GuiSpinnerListenerAdd>();
		}
		this.guiSpinnerListenersAdd.add(listener);
	}

	public final void addListener(GuiSpinnerListenerRemove listener) {
		if (this.guiSpinnerListenersRemove == null) {
			this.guiSpinnerListenersRemove = new ArrayList<GuiSpinnerListenerRemove>();
		}
		this.guiSpinnerListenersRemove.add(listener);
	}

	public final void addListener(GuiSpinnerListenerExpanded listener) {
		if (this.guiSpinnerListenersExpanded == null) {
			this.guiSpinnerListenersExpanded = new ArrayList<GuiSpinnerListenerExpanded>();
		}
		this.guiSpinnerListenersExpanded.add(listener);
	}

	public final void addListener(GuiSpinnerListenerSorted listener) {
		if (this.guiSpinnerListenersSorted == null) {
			this.guiSpinnerListenersSorted = new ArrayList<GuiSpinnerListenerSorted>();
		}
		this.guiSpinnerListenersSorted.add(listener);
	}

	public final void addListener(GuiSpinnerListenerPick listener) {
		if (this.guiSpinnerListenersPick == null) {
			this.guiSpinnerListenersPick = new ArrayList<GuiSpinnerListenerPick>();
		}
		this.guiSpinnerListenersPick.add(listener);
	}

	public final boolean isExpanded() {
		return (this.expanded);
	}

	/** get the objets */
	public final ArrayList<Pair<Object, String>> getValues() {
		return (this.values);
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
	}

	/** do the rendering of this gui */
	protected void onRender(GuiRenderer guiRenderer) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

	/**
	 * @return the number of object hold by this spinner
	 */
	public final int count() {
		return (this.values.size());
	}

	/** get the value at given index */
	public final Object getObject(int index) {
		return (this.values.get(index).left);
	}

	/** get the name at given index */
	public final String getName(int index) {
		return (this.values.get(index).right);
	}

	public final Object getSelectedObject() {
		return (this.values.get(this.selectedIndex).left);
	}

	public final String getSelectedName() {
		return (this.values.get(this.selectedIndex).right);
	}
}