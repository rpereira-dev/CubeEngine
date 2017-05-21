# VoxelEngine

-----------------------------------------------------------------------

A Game engine for Voxel games, using OpenGL, GLFW, OpenAL, Netty

-----------------------------------------------------------------------

N.B: Project temporaly paused. Currently, this is an important year of my studies, and I won't have any more time to work on it.

-----------------------------------------------------------------------

## HOW TO USE ##
        - >> git clone https://github.com/toss-dev/VoxelEngine.git

        - >> cd VoxelEngine

        - >> ./gradlew eclipse

Then in Eclipse, import the project
(You can also import the gradle project directly if you are using the Eclipse plugin)

You now have 4 projects:

- VoxelEngine is the core engine
- POT is a game implementation
- Mod sample : a mod example, which will be imported by POT on launch
- Model Editor : the Model Editor tools



## TECHNICAL PART:
Down bellow are all the explanations about how things are implemented (more or less) deeply. It gives a great overview of the engine, and it may allow one to find a wrong implementation, or something that should have been thought differently 

# Face system

Front: x-       ;    Back: x+

Left: z-        ;    Right: z+

Bot(tom): y-    ;    Top: y+


# Terrains

Terrains are chunks of the world. They are 16x16x16 blocks. They are a sort of six-trees, as each terrain contains pointer to the neighbors terrains (left, right, front, back, top, bot...). They contains 2 16x16x16 arrays (unidimensional for optimisation), one for each block light value (1 byte per block), and 1 for block ids (2 bytes per blocks). If the light value array is null, it means no block are emetting lights nearly. If the block id array is empty, it means the terrain is full of air. (This null values are key point in memory management)

Moreover, each terrains has a list of 'BlockInstance', which contains... blocks instances (see bellow

# Terrains meshes

They are basically 1 gl vao and one gl vbo. A meshing algorythm fill a stack of vertex for each visible faces, which are then push to the vbo. Only visible faces data are push to the vbo. This is a crucial point!

i.e: let [1] be a block at coordinates (0:0;0), and [2], à block at (0,1,0). Then the top face of [1] and the bottom face of [2] aren't visible, and so arent push to the stack, to the vbo, and so they are not rendered by opengl!

The vertices pushed per faces depends on the block type (via Java function overload). Every block are cubes by default, but you can override the meshing function to generate any block mesh (i.e, liquid blocks are doing this


A mesh vertex contains 10 floats: positions (x, y, z), normals (x, y, z), texture coordinates (uvx, uvy), light value, reflection value

Positions are relative to the terrain ([0,16]). And are transformed (scaled and translated) by a matrix gpu-side

Normals: no need to normalise them, gpu will do it

Texture coordinates depends on the texture registered on engine initialisation, some utilitaires functions are implemented 

The light value depends on the light value of the block, and on ambiant occlusion

# Block

A block has a unique instance which is created on engine initialisation. This instance stores all the block data. But then, only it id is stored into the terrain as we said before

# BlockInstance
If a block need it own instance, (parameters), you can override the function 'Block.createBlockInstance()' , which when returning a non-null 'BlockInstance', the new BlockInstance will be updated on every terrain's update

i.e, liquid blocks need some parameters (liquid level, color...), and so has a BlockInstance

# Water

As explained upside, every water block has it own instance. Each instance has an amount of liquid, between MIN_LIQUID_AMOUNT and MAX_LIQUID_AMOUNT. The block rendering is done as simple block, but by translating vertices upper, or lower, depending on the amount of liquid for an instance. (this is done pretty fastly in the meshing algorythm)

The flowing update algorythm (for eacg liquid block instance): the most it is called, the faster / smoother the water will flow, and so the flowing effect will look realistic, but this is a quite costly call as it require to rebuild a terrain mesh. So for now it is call like each 4 frames)

Algorithm:

        var amount
        if amount < MIN_LIQUID_AMOUNT:
                disperseWater() (remove block)
        else if the block under this instance is air:
        			make the instance 'fall', reducing this block height by 1
        else
        			if the block under is liquid:
                        transfer as much liquid from the current instance, to the instance under
                if the current instance still has an amount of water:
                			transfer as much liquid possible to neighbor liquid block (x+1, z), (x, z+1), (x-1, z), (x, z-1)


# Light

//TODO

# Particles

I implemented two type of particles, which are both behaving like points particles cpu-side. They have a health value which decrease on update, and the particle is deleted when this value reach 0. Each particles has a scale, position, velocity, and color value. They are being updated once in the rendering thread, before each frames

# Billboarded particles.
They are billboarded rectangles, which always face the camera, gpu. I use geometry shader and a single call for each particle draws. (So I can render them depending on the distance from the camera). Each BillboardedParticle has a TextureAtlas reference. This object is basically a GLTexture, where the image is a texture atlas. On particle updates and rendering, I use a single integer to know which texture of the atlas should be use, and I interpolate it with the next texture of the atlas, so it create this nice smooth effect when updating the texture

# Cube particles.
These particles are rendered as cubes, using a single draw call (via glDrawArraysInstanced()). A vbo is fill up with 21 floats per cubes: it transformation matrix (16 floats), it color (4 floats), it health (1 float).


# Sky, environment
The sky is currently using an untextured skydome, colored using a 3D noise. I'm looking for a nicer solution (sky plane + volumetric clouds should be better)
Supporting fog (density, opacity, color)
Supporting day/night cycles
Climatic effects can be simulated using particles (dust, pollen, rain...)

# Models
- A 'Model' is contains all the data of a model, which is basically severals 'ModelPart', 'ModelSkin', 'ModelAnimation', 'ModelAttachementPoint'
- A 'ModelPart' contains the mesh (VAO/VBO) of a model's part (i.e, arm, leg...). It also contains a list of 'ModelPartAttachmentPoint'
- A 'ModelSkin' simply has a name, and a list of 'ModelPartSkin', one for each models part
- A 'ModelSkinPart' is linked to one of the 'ModelPart' and is simply a VBO (which is set as the color attribute when rendering the 'ModelPart')
- A 'ModelAnimation' represents a single animation of the global model (i.e dance, run, ...). It has a name and a list of 'ModelPartAnimation', and the global duration
- A 'ModelPartAnimation' is linked to a 'ModelAnimation' and a 'ModelPart', so when an animation is played, each part can have it own animation-transformation. It contains a list of 'ModelAnimationFrame'
- 'ModelAnimationFrame' is basically a key frame, it contains attributes: 'time', 'translation, 'rotation', 'rotation offset', 'scale'. Those key frames are interpolated on their time when the animation is playing 
- 'ModelAttachementPoint' : TODO
- 'ModelPartAttachmentPoint': TODO
- 'ModelInstance', and all the classes like 'ModelXInstance' ('ModelPartInstance', 'ModelAnimationInstance'...) represents an instance of X. Then the global 'Model' is only allocated once, and only some specifix instance - required attributes are allocated. It allows to create, as an example, 10000 butterflies without having any memory issues

# Entities
An entity is an (moving) object of the world.
It has a unique world id (set when added to a world), vec3 position, rotation, vec3 velocities, vec3 accelerations
It follows the physics rules we apply on it (gravity, frictions...)
It may have AI
It may have a model, and then it has it own ModelInstance


# World

A world is basically a set of Terrains. Each terrains are stored into a Hashmap (where the key is 3 integers representing terrain index, the hash function is optimized for this. (see 'Vector3i.hashCode()')

Every terrains which need to be updated are also in another list. (they are the 'loaded terrains')

Entities are stored within multiples data storage to make their rendering and updates faster. See 'EntityStorage.java'

# Events

See EventManager.java. A simple event system. One can register an event or an event callback via the EventManager

# Mod loader

It loads every jar file in the folder './mods', './mod', './plugins', './plugin', and if they have a class which inherit from 'Mod.class', and has a valid 'ModInfo' annotation, then it load it to the engine. Pretty easy, isn't it?

# Rendering pipeline

The game is rendering in two times: the world, and the GUI's

• The World:

The WorldRenderer contains some 'Renderer', which are object which render parts of the world.
They are allocated when they are needed, and deallocated when they arent anymore properly
For example, there is the ModelRenderer (which render every entity's model), the SkyRenderer, which render the sky, the TerrainRenderer...
Any modder can register new world renderer if needed.
First, the world is rendered to 2 FBOs, which are used for water reflection / refraction. (using clipping planes to optimize it)
Then, the world is rendered to a 3rd FBO.
Then the engine process the post processing effects to the 3rd FBO
Finally, the final image hold by the 3'FBO (fbo's texture) is rendered to the screen

• Gui Renderer:

The engine implements a whole set of GUI objects.
It can, for now, render labels (text), buttons, textures.
New font can easily be added to the game (one line of code), by using the hiero jar file (font generator), in the 'com.grillecube.renderer.gui.font' package

# Sounds

A custom set of object and functions are implemented for 3D sounds (using OpenAL, thanks to LWJGL bindings)

# Assets management :
Each mod / project should have it own assets, stored into a zip file. 
This file has to be registered on program initialisation
Then, it will be unzipped, and missing/modified files will be unzipped (so we ensure data is always present and not corrupted)
