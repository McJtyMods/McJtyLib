McJtyLib
========

Companion library for McJty mods (RFTools, Deep Resonance).

This branch targets Minecraft 26.3 / NeoForge 26.3.0.83 and McJtyLib 26.3-11.0.0. Use Java 25; the wrapper uses Gradle 9.2.1 and NeoGradle 7.1.39.

Build settings come from MultiWorkspace's `26.3_neo/gradletools.gradle`. An integrated build applies the root workspace script. A standalone build automatically finds `../Multi_neo/gradletools.gradle`, or `../gradletools.gradle` for a nested checkout. Without a local workspace, it uses the remote 26.3 branch. No copied script is needed inside McJtyLib.

```sh
./gradlew build apiJar
./gradlew portingRegression
./gradlew runGameTestServer
```

`check` runs four JUnit checks for energy rollback, NBT conversion, and model JSON. `runGameTestServer` loads the separate `gametest` source set and runs eight checks for item/fluid transactions, notifications, ghost slots, automation, inventory save data, undo, and bucket replacement. Game-test classes and structures are excluded from release jars. The historical `src/testmod` fixture remains unconfigured.

For downstream ports:

- Capabilities and container APIs use `ResourceHandler<ItemResource>`, `ResourceHandler<FluidResource>`, and `EnergyHandler`. Queries return immutable resources or stack snapshots. Transfers use `Transaction`; aborted transfers restore contents and suppress notifications.
- `GenericItemHandler` retains its stack convenience methods and the indexed `Size` / `Items` save layout from 26.2. It implements `IndexModifier<ItemResource>` for menu edits. Native `ItemStacksResourceHandler` also works as a menu inventory; other custom handlers must expose `IndexModifier<ItemResource>`. Player slots use vanilla inventory slots. Ghost slots support direct edits but reject real transfers.
- `CustomTank.fill` and `drain` take a `boolean simulate` instead of the removed NeoForge fluid action enum. Simulations roll back their transaction. Fluid containers use slot-backed `ItemAccess` so empty and filled buckets can change item type.
- GUI helpers use `GuiGraphicsExtractor`. Input uses SDL scancodes and `InputConstants` mouse constants; text focus enables/disables SDL text input. Quaternion pose operations use `PoseStack.rotate`, and render pipelines use Renderpearl, including additive OIT pipelines for improved transparency.
- Block entities use `ValueInput` / `ValueOutput`; client NBT hooks still travel in update packets. Server configuration uses `SYNCED` while retaining `mcjtylib-server.toml`.
- Model helpers live in `mcjty.lib.datagen.model`. Recipe providers take recipe/advancement bootstrap contexts. `DataGen` generates recipes and loot through reloadable registry bootstraps; tags use world registry lookups. Placement registries now register `MapCodec<? extends PlacementModifier>` values.

TOP's Maven artifact is configured as `26.3_neo-16.0.0-1`; the build suffix does not change its mod dependency version. Integrated builds use its source project. JEI is pinned to the published 26.3 version `31.12.0.65`. Patchouli integration remains unavailable until a compatible API is configured.

Port validation: main sources compiled against cached Minecraft 26.3 / NeoForge 26.3.0.83, all four JUnit checks passed, and all eight game tests passed in NeoForge with TOP installed. The sandbox prevented Gradle startup (local networking) and dependency downloads. Compile validation used the cached 26.2 JEI API; resolving the configured 26.3 JEI artifacts, a full Gradle build, datagen, and client rendering/input validation remain to be run outside that restriction.
