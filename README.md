McJtyLib
========

Companion library for all my mods (RFTools, Deep Resonance)

This branch targets Minecraft 26.2 / NeoForge and requires Java 25. Build with `./gradlew build`. The `check` lifecycle runs JUnit regression tests (`portingRegression` is an alias for `test`), covering transaction rollback, NBT conversion, and model JSON without launching Minecraft.

Build settings come from MultiWorkspace's `26.2_neo/gradletools.gradle`. In the integrated workspace the root script is applied directly. For standalone work before that script is published, copy `../Multi_neo/gradletools.gradle` into this directory and locally exclude it with `.git/info/exclude`; keep the two files identical. Do not commit or push the local copy.

TOP and JEI use real 26.2 artifacts. Patchouli's old API is no longer loaded; manual requests display an unavailable message until a compatible 26.2 integration can be added.

For downstream ports:

- GUI helpers take `GuiGraphicsExtractor`; screens extract state and receive the current mouse/key event objects.
- World rendering helpers submit geometry through `SubmitNodeCollector`. Dynamic block models collect model parts; custom geometry bakes quad collections.
- `GenericItemHandler`, `AutomationFilterItemHander`, `CustomTank`, and `GenericEnergyStorage` expose the new transactional capabilities. Their legacy methods remain available for existing container helpers.
- Block entity overrides use `ValueInput` / `ValueOutput`. Client-specific NBT hooks remain available and are carried in the update packet.
- Use the properties-based overloads of `DeferredBlocks`, `DeferredItems`, and `RBlockRegistry` for automatic registry IDs. Supplier-based registrations require the caller to set IDs before constructing items or blocks.
- Model data helpers now live in `mcjty.lib.datagen.model`; item generation also writes the 26.2 client item definition. Recipe providers use registry lookups and `RecipeProvider.Runner`.

The historical `src/testmod` fixture is not configured as a source set. Integrated game testing belongs in MultiWorkspace's `workspace` module.
