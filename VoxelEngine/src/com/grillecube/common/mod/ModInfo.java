/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.common.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModInfo {

	/** the mod name */
	String name() default "unamed";

	/** the mod author */
	String author() default "unknown";

	/** the mod version */
	String version() default "0.0";

	/**
	 * HOW PROXIES WORKS:
	 * 
	 * IF clientProxy() AND serverProxy() are empty, then the mod is loaded on
	 * both side
	 * 
	 * ELSE IF clientProxy() is not empty, and serverProxy() is empty, the mod
	 * is loaded client-side only
	 * 
	 * ELSE (so, IF clientProxy() is empty AND serverProxy() is not empty), the
	 * mod is loaded server-side only
	 */

	/** the classpath of the client proxy. */
	Class<? extends IMod> clientProxy() default com.grillecube.common.mod.IMod.class;

	/** the classpath of the server proxy */
	Class<? extends IMod> serverProxy() default com.grillecube.common.mod.IMod.class;
}
