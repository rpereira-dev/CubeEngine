package com.grillecube.common.event;

public abstract class Event {
	
	private boolean _canceled = false;
	
	public String getName() {
		return (this.getClass().getSimpleName());
	}
	
	public void cancel() {
		if (!this._canceled) {
			this.onCancel();
			this._canceled = true;
		}
	}
	
	public void reset() {
		this._canceled = false;
	}

	private void onCancel() {}
}
