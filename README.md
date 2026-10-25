# McJtyLib for Minecraft 26.1.2 / Fabric

Shared library for McJty's mods. Requires Java 25, Fabric Loader 0.19.3+, Fabric API 0.155.3+26.1.2 and Forge Config API Port 26.1.5. Team Reborn Energy 5.0.0 is included in the built mod. Forge Config API Port preserves the TOML configuration API; its `net.neoforged` config classes do not require the NeoForge loader.

Build with `./gradlew build`. Four JUnit regression tests cover nested energy transactions, NBT conversion, block models and client item definitions. Native item and fluid transfer checks run with Fabric's actual mixins:

```sh
./gradlew -PtransferTests runTransferTest
```

That opt-in test mod checks item rollback and commit notifications, fluid rollback, bucket/millibucket/droplet conversion, capacity, simulated transfers and item energy component changes. It exits after the checks, without starting a playable server. The historical `src/testmod` fixture remains unconfigured; the new checks live in `src/fabricTest`.

## Shared build settings

The build uses MultiWorkSpace's root `gradletools.gradle`, then an optional standalone local copy, then the remote `26.1.2_fabric` script. While the workspace branch is unpublished, run this from the standalone library checkout:

```sh
cp ../Multi_fabric/gradletools.gradle gradletools.gradle
```

Keep the files byte-for-byte identical. Exclude `/gradletools.gradle` in `.git/info/exclude`. This local build aid must not be committed or pushed.

For integrated development, open `../Multi_fabric/build.gradle` and run `:workspace:runClient` or `:workspace:runServer`. The sibling `../TheOneProbe_fabric` checkout is now included automatically and supplies the TOP 26.1.2 Fabric source project. Override its location with `-Pmod.TheOneProbe=<path>`. Standalone builds without that source project compile against the published TOP Fabric API only; they do not load the 26.2 TOP mod at runtime. An explicitly supplied `-Ptop_version=<compatible Fabric version>` enables a published runtime dependency.

## Downstream Fabric ports

- Initialize registries from `ModInitializer.onInitialize()`. McJtyLib's deferred registry helpers expose `register()` without an event bus. Use the properties factory overloads to set registry IDs before constructing blocks and items.
- `IModule.init()`, `initClient()` and `initConfig()` take no loader event arguments. Keep client initialization in a `ClientModInitializer` entrypoint.
- Custom capabilities use Fabric `BlockApiLookup`; register annotated providers with `setup.registerBlockCapabilities(registry)` after the block registry is bound. Standard automation uses Fabric `ItemStorage.SIDED`, `FluidStorage.SIDED` and Team Reborn `EnergyStorage.SIDED`.
- `GenericItemHandler`, `AutomationFilterItemHander`, `CustomTank` and `GenericEnergyStorage` support native Fabric transactions. GUI-facing slot and integer energy helpers live in McJtyLib packages. `FluidStack` and tank GUI methods use millibuckets; native fluid storage uses 81 droplets per millibucket (81,000 per bucket).
- Attachments use Fabric `AttachmentRegistry` and `AttachmentTarget`. Register container data listeners on both sides; McJtyLib resolves synchronized attachment IDs from these listeners. Data component accesses take the registered component type, using `.get()` on deferred references.
- Extended menus use Fabric `ExtendedMenuType` and `MenuTools.open`. Client and server payload registrations are separate. Payload receivers run through the library's `PayloadContext`.
- Screens use `GuiGraphicsExtractor`; world geometry uses `SubmitNodeCollector`. Dynamic models use Fabric's quad emitter, retaining per-quad colors and contextual model parts. Screen events route modal-window input; tooltip components preserve an item's original tooltip image.
- Register TOP integration through the `theoneprobe` Fabric entrypoint. TOP and JEI remain optional at runtime. Patchouli manual integration remains unavailable until a compatible implementation is added.
- Data generation uses Fabric `DataGeneratorEntrypoint`: `pack.addProvider((output, lookup) -> { var data = new DataGen(output, lookup); modules.datagen(data, lookup); data.generate(); return data; })`. The existing `Dob` recipe/model/tag callbacks remain available. Global loot data now supplies native `LootItemFunction` values via `Dob.Builder.glm`; these are generated under `data/<namespace>/mcjtylib/loot_functions` and applied through Fabric's loot-drop event. Mods needing to add or remove arbitrary drops should use `LootTableEvents.MODIFY_DROPS` directly.

The three adapted container slot helpers retain their LGPL 2.1 notices and license under `META-INF`; other library code remains MIT.

## Backport validation

This branch starts from the 26.2 Fabric port. Minecraft advancement imports, camera access and item tags follow the 26.1.2 NeoForge reference. Fabric tag builders retain resource keys, as required by Fabric's 26.1.2 API.

The full integrated Gradle build now passes against the pinned Minecraft 26.1.2 Fabric dependencies:

```sh
cd ../Multi_fabric
./gradlew :TheOneProbe:build :TheOneProbe:gametestClasses :McJtyLib:build :workspace:classes
```

Both mods build, their access wideners validate, TOP's game-test sources compile, and all four McJtyLib JUnit regression tests pass. McJtyLib explicitly widens the block-entity supplier and constructor required by its registry helpers on Fabric 26.1.2. TOP's client game test also passes on this Minecraft version, exercising its real client and integrated server.

Native Fabric transfer regression checks also passed with both McJtyLib and TOP loaded; the log confirms McJtyLib registered its TOP provider. Run them from `../Multi_fabric` with `./gradlew -PtransferTests :McJtyLib:runTransferTest`.

TOP's client game test also passed with McJtyLib and JEI added to its runtime through a temporary Gradle init script, confirming the combined client/server runtime and TOP addon registration.

A fresh standalone checkout at `d572713b`, using the remote shared Gradle script and no local copy, reproduced Jenkins's eight compilation errors. Applying only the two block-entity access-widener entries made `./gradlew clean build` pass, including all four JUnit tests and access-widener validation. These entries must be committed and pushed with this branch for Jenkins to receive the fix.
