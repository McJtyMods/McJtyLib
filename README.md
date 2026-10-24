McJtyLib
========

Companion library for all my mods (RFTools, Deep Resonance)

This branch backports the 26.2 library to Minecraft 26.1.2 / NeoForge 26.1.2.94 and requires Java 25. Build with `./gradlew build`. The `check` lifecycle runs JUnit regression tests (`portingRegression` is an alias for `test`), covering transaction rollback, NBT conversion, and model JSON without launching Minecraft.

Build settings come from MultiWorkspace's [`26.1.2_neo/gradletools.gradle`](https://raw.githubusercontent.com/McJtyMods/MultiWorkspace/26.1.2_neo/gradletools.gradle). In the integrated workspace the root script is applied directly. For standalone work before that script is published, copy `../Multi_neo/gradletools.gradle` into this directory and locally exclude `/gradletools.gradle` with `.git/info/exclude`; keep the two files identical. Do not commit or push the local copy.

TOP and JEI use 26.1.2 artifacts. TOP resolves from the published Curse Maven file when its source project is absent. Patchouli retains the 26.2 port's manual fallback; its 26.1.2 integration is not enabled.

For downstream ports:

- GUI helpers take `GuiGraphicsExtractor`; screens extract state and receive the current mouse/key event objects.
- World rendering helpers submit geometry through `SubmitNodeCollector`. Dynamic block models collect model parts; custom geometry bakes quad collections.
- `GenericItemHandler`, `AutomationFilterItemHander`, `CustomTank`, and `GenericEnergyStorage` expose the new transactional capabilities. Their legacy methods remain available for existing container helpers.
- Block entity overrides use `ValueInput` / `ValueOutput`. Client-specific NBT hooks remain available and are carried in the update packet.
- Use the properties-based overloads of `DeferredBlocks`, `DeferredItems`, and `RBlockRegistry` for automatic registry IDs. Supplier-based registrations require the caller to set IDs before constructing items or blocks.
- Model data helpers live in `mcjty.lib.datagen.model`; item generation also writes the 26.1.2 client item definition. Recipe providers use registry lookups and `RecipeProvider.Runner`.
- Advancement criteria use the `net.minecraft.advancements.criterion` package. Tag providers accept blocks and items directly; item tags use `ItemTags`.

The historical `src/testmod` fixture is not configured as a source set. Integrated game testing belongs in MultiWorkspace's `workspace` module.
