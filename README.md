# Overgeared: Buffed Gear (addon)

Fabric 1.20.1 addon for Overgeared. Requires Overgeared and Fabric API.

- Adds `overgeared_buffed_gear:diamond_*` and `netherite_*` (sword, pickaxe, axe, shovel, hoe, helmet, chestplate, leggings, boots) with x1.6 / x1.8 stats. Edit `DIAMOND_BUFF` / `NETHERITE_BUFF` in `BuffedGear.java`.
- Steel gear + Overgeared diamond upgrade template + diamond -> buffed diamond gear.
- Buffed diamond gear + netherite template + netherite ingot -> buffed netherite gear.
- Vanilla diamond/netherite gear recipes are disabled.
- Vanilla diamond/netherite gear in loot tables is swapped for the buffed versions.

Not handled: vanilla items still exist (creative tab, /give, villager trades, mob spawn gear, existing worlds).

## Build
Push to GitHub: the Actions workflow builds and uploads the jar (Actions > run > Artifacts).
Locally: `gradle build` (Gradle 8.8+, Java 17), jar in `build/libs/`.

## Before publishing
Change the author in `fabric.mod.json`, add a LICENSE, and optionally rename the `com.example.buffedgear` package.
