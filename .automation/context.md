# Handbook — Project Context

## What This Is
A datapack-driven in-game documentation mod for Minecraft. Exists to support the latest Minecraft versions without waiting for Patchouli to update — Patchouli and derivatives often lag six months or more behind new versions, and historically only supported a single modloader.

This mod is a fork of [Patchouli](https://github.com/VazkiiMods/Patchouli) and [Patchouli Provider](https://github.com/BrassGoggledCoders/PatchouliProvider/tree/develop/1.21.x). If Patchouli supports the target Minecraft version, use that instead. See `README.md` for full fork history and rationale.

## License
Original code and assets: CC-BY-NC-SA 3.0 Unported (inherited from Patchouli fork).

Datagen and all new additions: MIT.

## Branch Convention
Each branch targets a specific Minecraft version. Maintained branches:

| Branch | Modloaders          |
|--------|---------------------|
| 1.21.1 | NeoForge + Fabric   |
| 26.1   | NeoForge + Fabric   |

Branches `1.21`, `1.21.10`, and `1.21.x` exist locally — confirm with the user before treating these as actively maintained.

## Key Features
- Datapack-driven book content with a datagen API (preferred over manual JSON editing)
- Rich text, macros, advancement-driven unlocking, nested categories, bookmarks
- Multiple page types: text, crafting, image, templates for custom types
- No multiblock visualization (removed from Patchouli); entity viewing is WIP

## How Dependents Consume Handbook
Mods declare it in `gradle.properties` as a version variable. Check a mod's `gradle.properties` for a Handbook version variable to discover dependents. Known consumers include Homeostatic and SurvivalistEssentials — see those repos for datagen usage examples.

## Release Process
Follow the standard wendall911 mod release process. See `../docs/minecraft/MINECRAFT_DEVELOPMENT_NOTES.md` for the full release sequence. Releases are per branch — a release on `26.1` does not release `1.21.1`.
