# VoxelEngine

Minecraft like engine openGL / GLFW
-----------------------------------------------

• Currently working on the networking system

For technical questions, see comments in code :)

-----------------------------------------------
Client / Server model:
* using udp protocol (recvfrom, sendto functions)
	: see network files

-----------------------------------------------

Extern library used:
* OpenGL (modern pipeline)
* GLFW (opengl framework)
* glew (windows only, opengl version compatibility)
* pthread
* lodepng.c lodepng.h (png decoder / encoder)

-----------------------------------------------

NOT IMPLEMENTED YET:
* Detect hidden chunks of block: occlusion culling
* 

![alt tag](http://img11.hostingpics.net/pics/526266test.png)
