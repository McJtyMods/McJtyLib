# McJtyLib for Minecraft 26.3 / Fabric

Shared library for McJty's mods. Requires Java 25, Fabric Loader 0.19.5+, Fabric API 0.164.0+26.3 and Forge Config API Port 26.3.1. Team Reborn Energy 5.0.0 is included in the built mod. Forge Config API Port preserves the TOML configuration API; its `net.neoforged` config classes do not require the NeoForge loader.

Build with `./gradlew build`. Four JUnit regression tests cover nested energy transactions, NBT conversion, block models and client item definitions. Native item and fluid transfer checks run with Fabric's actual mixins:

```sh
./gradlew -PtransferTests runTransferTest
```

That opt-in test mod checks item rollback and commit notifications, fluid rollback, bucket/millibucket/droplet conversion, capacity, simulated transfers and item energy component changes. It exits after the checks, without starting a playable server. The historical `src/testmod` fixture remains unconfigured; the new checks live in `src/fabricTest`.

## Shared build settings

The build applies the integrated workspace's `gradletools.gradle`, the sibling
`../Multi_fabric/gradletools.gradle`, a nested parent script, or the published
`26.3_fabric` script in that order. No copied script is needed inside this checkout.
Java 25, Gradle 9.6.0 and Loom 1.17.21 match the 26.3 workspace.

For integrated development, open `../Multi_fabric/build.gradle` and run `:workspace:runClient` or `:workspace:runServer`. TOP can be supplied from another Fabric checkout with `-Pmod.TheOneProbe=/data/Projects/26.3/TheOneProbe_fabric`.

Run `./gradlew -PtransferTests runDatagenTest` to exercise recipe, advancement,
loot, global-function and client-model generation. Output stays under `build/`.
Run `./gradlew -PtransferTests -PclientTests runClient` for the opt-in client checks;
they check pipeline initialization, SDL mouse constants, Unicode input and text
focus, then exit without opening a world. Test fixtures stay out of release jars.

## Downstream Fabric ports

- Initialize registries from `ModInitializer.onInitialize()`. McJtyLib's deferred registry helpers expose `register()` without an event bus. Use the properties factory overloads to set registry IDs before constructing blocks and items.
- `IModule.init()`, `initClient()` and `initConfig()` take no loader event arguments. Keep client initialization in a `ClientModInitializer` entrypoint.
- Custom capabilities use Fabric `BlockApiLookup`; register annotated providers with `setup.registerBlockCapabilities(registry)` after the block registry is bound. Standard automation uses Fabric `ItemStorage.SIDED`, `FluidStorage.SIDED` and Team Reborn `EnergyStorage.SIDED`.
- `GenericItemHandler`, `AutomationFilterItemHander`, `CustomTank` and `GenericEnergyStorage` support native Fabric transactions. GUI-facing slot and integer energy helpers live in McJtyLib packages. `FluidStack` and tank GUI methods use millibuckets; native fluid storage uses 81 droplets per millibucket (81,000 per bucket).
- Attachments use Fabric `AttachmentRegistry` and `AttachmentTarget`. Register container data listeners on both sides; McJtyLib resolves synchronized attachment IDs from these listeners. Data component accesses take the registered component type, using `.get()` on deferred references.
- Extended menus use Fabric `ExtendedMenuType` and `MenuTools.open`. Client and server payload registrations are separate. Payload receivers run through the library's `PayloadContext`.
- Screens use SDL key and mouse constants from `InputConstants`. Text widgets notify `TextInputManager` whenever focus changes. Quaternion pose operations use `PoseStack.rotate`. Screens use `GuiGraphicsExtractor`; world geometry uses `SubmitNodeCollector`. Dynamic models use Fabric's quad emitter, retaining per-quad colors and contextual model parts. Screen events route modal-window input; tooltip components preserve an item's original tooltip image.
- Register TOP integration through the `theoneprobe` Fabric entrypoint. TOP and JEI remain optional at runtime. Patchouli manual integration remains unavailable until a compatible implementation is added.
- Data generation uses Fabric `DataGeneratorEntrypoint`. Recipes and advancements now share a reloadable registry bootstrap; loot uses the same reloadable output layer. The existing constructor still accepts Fabric's combined lookup: `pack.addProvider((output, lookup) -> { var data = new DataGen(output, lookup); modules.datagen(data, lookup); data.generate(); return data; })`. The existing `Dob` recipe/model/tag callbacks remain available. Global loot data now supplies native `LootItemFunction` values via `Dob.Builder.glm`; these are generated under `data/<namespace>/mcjtylib/loot_functions` and applied through Fabric's loot-drop event. Mods needing to add or remove arbitrary drops should use `LootTableEvents.MODIFY_DROPS` directly.

The three adapted container slot helpers retain their LGPL 2.1 notices and license under `META-INF`; other library code remains MIT.

## Validation

The 26.3 port was compiled against cached Minecraft/Fabric artifacts and the exact
31.12.0.65 JEI common API. All four JUnit tests passed. Native Fabric transfer tests,
seven generated resource files, and the client input checks were exercised with
the local 26.3 Fabric TOP checkout. Full Gradle execution is blocked in this sandbox
by Gradle's local-networking requirement. A network-enabled Gradle build must still
resolve the standalone Maven dependencies; JEI runtime integration was not exercised.
