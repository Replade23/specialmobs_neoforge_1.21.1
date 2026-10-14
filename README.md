# Special Mobs - NeoForge 1.21.1 port

An unofficial port of [Special Mobs](https://github.com/FatherToast/specialmobs) by **FatherToast** (v4.2.0, Forge 1.20.1)
to **NeoForge 1.21.1** (21.1.x, Java 21). The library mod [Crust](https://github.com/FatherToast/crust), also by
FatherToast, is embedded in the same jar under the package `fathertoast.specialmobs.crust`.

All credit for the mod, its design, mobs, textures and original code goes to FatherToast.
This port was made for the Crash Landing Reborn modpack and is shared in case it is useful to others.
It is not affiliated with or endorsed by the original author.

## Status
- All mob families ported and enabled: creepers, zombies, skeletons, spiders, drowned, zombified piglins,
  wither skeletons, slimes, magma cubes, cave spiders, silverfish, endermen, witches, ghasts, blazes.
- Config system (Crust) works, including server -> client config sync (NeoForge payloads).
- Mixins, advancement fixes and the Jade plugin are ported.
- Tested in singleplayer and on a dedicated server by the porter. Not every mob and interaction has been tested in depth.
- Not ported: Ryoamic, Tinkers' Construct and Crust plugin compat.
- Languages: only `en_us` and `it_it` are included.

## Main changes from the original
- Forge -> NeoForge: registries (`DeferredHolder`), events, networking (payloads), mod metadata (`neoforge.mods.toml`).
- Items: NBT -> data components. Attributes and effects use holders. Loot tables converted to the 1.21 format.
- Access transformers use Mojang names (`META-INF/accesstransformer.cfg`).
- Data generation of loot tables is not included; the JSON files are shipped directly.
- Unused code, textures and translations removed; PNGs losslessly recompressed to keep the jar small.

## Building
Requires Java 21. The Jade API is needed only to compile the optional Jade plugin:
put the Jade jar for NeoForge 1.21.1 (15.x) into `libs/` (not committed, not bundled in the result).

    ./gradlew build        (Windows: gradlew.bat build)

The jar is written to `build/libs/`.

## License
GPL-3.0-only, same as the original Special Mobs and Crust. See [LICENSE](LICENSE).
Original copyright notices are kept. Source is in this repository.

## Reporting issues
Problems with this port should be reported here, not to the original author.
