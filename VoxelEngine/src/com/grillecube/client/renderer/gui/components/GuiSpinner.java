package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;
import java.util.Comparator;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseLeftPress;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerAdd;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerExpanded;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerRemove;
import com.grillecube.client.renderer.gui.listeners.GuiSpinnerListenerSorted;

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

	/** the objects hold */
	private final ArrayList<Object> values;

	/** true if this spinner is expanded */
	private boolean expanded;

	public GuiSpinner() {
		super();
		this.values = new ArrayList<Object>();
		this.expanded = false;
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
		this.values.add(value);
		if (this.guiSpinnerListenersAdd != null) {
			for (GuiSpinnerListenerAdd listener : this.guiSpinnerListenersAdd) {
				listener.invokeSpinnerAddObject(this, value);
			}
		}
	}

	/** add a value to the spinner */
	public final void remove(Object value) {
		int index = this.values.indexOf(value);
		if (index == -1) {
			return;
		}
		this.values.remove(index);
		if (this.guiSpinnerListenersRemove != null) {
			for (GuiSpinnerListenerRemove listener : this.guiSpinnerListenersRemove) {
				listener.invokeSpinnerRemoveObject(this, value, index);
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

	public final boolean isExpanded() {
		return (this.expanded);
	}

	/** get the objets */
	public final ArrayList<Object> getValues() {
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
	public final Object get(int index) {
		return (this.values.get(index));
	}
}