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
	 * IF clientProxy() is not empty and we are client-side, then load the proxy
	 * client side only
	 * 
	 * ELSE IF serverProxy() is not empty and we are server-side, then load the
	 * proxy server side only
	 * 
	 * ELSE there is no proxy, load the mod on each sides
	 */

	/** the classpath of the client proxy. */
	String clientProxy() default "";

	/** the classpath of the server proxy */
	String serverProxy() default "";
}
