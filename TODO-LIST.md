# THE EPIC WORLD FIRST VOXEL ENGINE TODO-LIST

## Priorities: the most '<b>!</b>' there is on a point, the most important it is

## Before implementing any of bellow points, please ask me (rpereira-dev). As I build most of the engine for now, I think it will be better if we brainstorm together, + if I check your ideas, before the actual implementation (so you don't loose several days codding for useless work...) also someone else may already be working on the same point, so it will allow me to let you get in touch

- Terrains:
  - <b>!!</b> create a system to compress/uncompress AUTOMATICALLY raw blocks (when there is much physics activities in a terrain, it should be uncompressed for faster physics, but otherwise it should be compressed to save memory)
  - BlockInstance : find a way to handle concurrency
  - Optimize shaders
  
- Blocks:
  - create a block editor module for the model editor, so we can create custom meshes faster
  - <b>!!</b> check that water/light blocks works properly on very cases
  
- Sky, environment:
  - <b>!!!!</b> a rebuild of sky and environment can be great, the sky currently doesn't look pretty good, and doesn't offer a lot of customization possibilities
  - <b>!</b> solar lens flare
  - <b>!</b> depth of field
  - <b>!</b> wind effects on specified blocks
  - <b>!</b> motion blur

- Rendering Pipeline:
  - <b>!!!!</b> make shadow looking better (Cascaded Shadow map)
  - <b>!!!!</b> reflection/refraction optimizations (+ implementation in some 'WorldRenderer')
  - update terrain ambiant occlusion (particularly "in-cave")
  - check implementation / optimize (maybe re-implement) the occlusion culling algorythm for terrain. Two are currently implemented: simple frustum + distance culling (done on each world terrains loaded), and this algorythm https://tomcc.github.io/2014/08/31/visibility-1.html from a mojang 
  
- Guis:
  - <b>!!!!</b> implement some more Guis (textfields, checkbox, scrollbar-view, spinners...)

- Editor:
  - <b>!!!!!!!!</b> update "CameraEditor.java" raycast algorythm, so it match with model origin (x, y, z), model rotation (x, y, z) AND modelpart scaling
  - <b>!!!!</b> finish up equipments system
  - <b>!!!!</b> create import/export

- World:
  - <b>!!!!!!!!!!</b> saving / loading (with entities, particles, time etc...)
  - physics: terrain physics (falling blocks)
  - physics: entities: check thats movement/collision works properly

- Models, Models part, ModelInstance, EntityModeled:
  - ~~finish up equipment system on model editor~~
  - rebuild model import/export system
</del>

- Sounds:
  - ~~test out that every function implemented works properly~~
  
- Networking:
  - test out that every function implemented works properly: create a small test server to run tests

- Client/Server:
  - <b>!!!!!!!!!!</b> The engine is "all in one", meaning there client and server are inside the same project. Then we should find a way so no client-only code run server-side, and vise versa. We'll have to implement a system similar to the "@SideOnly(Side.CLIENT)" annotation in Minecraft-Forge

- Events:
  - add severals game events to make modding easier

- Website:
  - create a showcase website (with webgl, something original)
  - java documentation

- Android:
  - port to android
  
