# Guidebook
Datapack driven in-game documentation for Minecraft mods. For mod and modpack
developers who are tired of not being able to support the latest versions, as
Patchouli and other derivitives often takes six months or longer to update, or
only support a single modloader.

Fork of [Patchouli](https://github.com/VazkiiMods/Patchouli) and [Patchouli Provider](https://github.com/BrassGoggledCoders/PatchouliProvider/tree/develop/1.21.x)

For more information, see the [docs](https://vazkiimods.github.io/Patchouli/docs/intro).

## License Information

Guidebook's original code and assets are licensed under the CC-BY-NC-SA 3.0 Unported
license.  We recognize that this is not ideal, and are open to changing the licensing of
the code in the future.

Please note that this mod uses official Mojang mappings (Mojmap). If you depend on
Guidebook as normal, or only consume Guidebook's API, there should be no licensing
concerns, as the mod is remapped to Intermediary (or SRG, for Forge) on compile.

There is a license concern, however, if you bundle Guidebook with your mod using
Jar-in-Jar.  Building a mod which uses Mixin inserts a refmap, which for Guidebook will
contain raw Mojang mappings in a JSON file.  If this presents a licensing problem to you,
then do not bundle Guidebook and just depend on it externally.  I recommend using normal
dependencies either way, as Jar-in-Jar inflates your archive sizes to store a mod that
will probably be in most modpacks anyways.
