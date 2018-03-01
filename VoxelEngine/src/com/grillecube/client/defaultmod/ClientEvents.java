package com.grillecube.client.defaultmod;

import com.grillecube.client.openal.ALH;
import com.grillecube.client.openal.ALSound;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.event.Listener;
import com.grillecube.common.event.world.entity.EventEntityPlaySound;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.entity.WorldEntity;

public class ClientEvents implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		// default events
		EventManager eventManager = manager.getEventManager();

		// TODO keep this here?
		eventManager.addListener(new Listener<EventEntityPlaySound>() {

			@Override
			public void pre(EventEntityPlaySound event) {
			}

			@Override
			public void post(EventEntityPlaySound event) {
				WorldEntity e = event.getEntity();
				float x = e.getPositionX();
				float y = e.getPositionY();
				float z = e.getPositionZ();
				float vx = e.getPositionVelocityX();
				float vy = e.getPositionVelocityY();
				float vz = e.getPositionVelocityZ();
				Vector3f pos = new Vector3f(x, y, z);
				Vector3f vel = new Vector3f(vx, vy, vz);
				ALSound sound = ALH.alhLoadSound(event.getSound());
				((ResourceManagerClient) manager).getSoundManager().playSoundAt(sound, pos, vel);
			}
		});
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}
}
