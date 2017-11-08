# VoxelEngine

-----------------------------------------------------------------------

A Game engine for Voxel games, using OpenGL, GLFW, OpenAL, Netty

-----------------------------------------------------------------------

## HOW TO USE ##
        - >> git clone https://github.com/toss-dev/VoxelEngine.git

        - >> cd VoxelEngine

        - >> ./gradlew eclipse

Then in Eclipse, import the project
(You can also import the gradle project directly if you are using the Eclipse plugin)

You now have 3 projects:

- VoxelEngine is the core engine
- POT is a game implementation
- Mod sample : a mod example, which will be imported by POT on launch


## DEMO VIDEOS
https://www.youtube.com/playlist?list=PLTsKtD9K5K8nkeK2MzVr3JFv4ofuJTugb










## TECHNICAL PART:
Down bellow are all the explanations about how things are implemented (more or less) deeply. It gives a great overview of the engine, and it may allow one to find a wrong implementation, or something that should have been thought differently 

# Face system

Front: x-       ;    Back: x+

Left: z-        ;    Right: z+

Bot(tom): y-    ;    Top: y+


# Terrains

Terrains are chunks of the world. They are 16x16x16 blocks. They contains 2 16x16x16 arrays (unidimensional for optimisation), one for each block light value (1 byte per block), and 1 for block ids (2 bytes per blocks). If the light value array is null, it means no block are emetting lights nearly. If the block id array is empty, it means the terrain is full of air. (This null values are key point in memory management)

Moreover, each terrains has a list of 'BlockInstance', which contains... blocks instances (see bellow)

# Terrains meshes

They are basically 1 gl vao and one gl vbo. Various meshing algorithm should be implemented.

Currently, I'm using the 'greedy' meshing algorithm (see https://0fps.net/2012/06/30/meshing-in-a-minecraft-game/)

When building a mesh, the Mesher iterates through all terrain's block, and get it 'BlockRenderer'.

A 'BlockRenderer' is an interface which is supposed to push a specific block vertices to the vertex stack, when the mesher meshes it.

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

The light system is based on SOA implementations (https://www.seedofandromeda.com/blogs/29-fast-flood-fill-lighting-in-a-blocky-voxel-game-pt-1)

It is a flood filling algorithms, applied on byte arrays. The distinction between sunlight (ambiant lighting), and blocklights (lights) is made.

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
3D models implementation is based on Skeleton system.
The mesh vertex format is:

(x, y, z, ux, uy, vx, vy, nx, ny, nz, b1, b2, b3, w1, w2, w3)
('position', 'texture coordinates', 'normal', 'bones', 'weights')

Each bones 'bn' correspond to the ID of a bone on the skeleton, and the corresponding weight 'wn' is a factor on how much does the bone transformation affect this vertex. (so when animating, the mesh stays static, the bones are transformed, and then we apply the same transformation to vertices but using these weights)


- A 'Model' contains all the data of a model, which is basically a 'ModelSkeleton', a 'ModelMesh', 'List<ModelAnimation>', 'List<ModelSkin>'
- 'ModelMesh' : contains the mesh (VAO/VBO) of the whole model
- 'ModelSkin' simply has a name, and a texture.
- 'ModelSkeletonAnimation' represents a single animation for a skeleton (i.e dance, run, ...). It has a name and a list of Key frames for transformed bones at given times.

The ModelRenderer is only able to render 'ModelInstance', which are instances of a Model. This 'instance' system allows to get hundred of instances of the same model without performance loss.

You should consider binding your model to a specific Entity, using the ModelManager and EntityManager on program initialization. (so when this Entity spawns, a new 'ModelInstance' is automatically bound to it), and so whole rendering process does the job independantly

However, you can also dynamically bind an Entity and a Model, by creating a new 'ModelInstance', and add it to the ModelManager. (you should really not be doing this)

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
The world is rendered to it own FBO and texture. It can be displayed using a 'GuiTexture' for example.

• Gui Renderer:

Every concrete rendering are done here.

# GUI System

The engine implements it own GUI system, and a preset of GUI objects.
The system is based on parent/child hierarchy. (coordinates are always relative to the parent)
Each objects have a weight, which determines it layer when rendering.
Events are handled properly.
New font can easily be added to the game (one line of code), by using the hiero jar file (font generator), in the 'com.grillecube.renderer.gui.font' package

# Sounds

A custom set of object and functions are implemented for 3D sounds (using OpenAL, thanks to LWJGL bindings)

# Assets management :
Each mod / project should have it own assets, stored into a zip file. 
This file has to be registered on program initialisation
Then, it will be unzipped, and missing/modified files will be unzipped (so we ensure data is always present and not corrupted)
