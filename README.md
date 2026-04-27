# Handbook
Datapack-driven in-game documentation for Minecraft mods. For mod and modpack
developers who are tired of not being able to support the latest versions, as
Patchouli and other derivatives often take six months or longer to update, or
only supports a single modloader.

This mod exists specifically to support the latest versions of Minecraft and
is very focused on in-game mod content. There is a robust datagen API that
lets mod developers maintain book content without manually editing and curating
JSON files, making it easy to update to new Minecraft versions. Examples of
this in action are for
[Homeostatic](https://github.com/wendall911/Homeostatic/tree/26.1/Common/src/main/java/homeostatic/data/book)
and
[Survivalist Essentials](https://github.com/wendall911/SurvivalistEssentials/blob/26.1/Common/src/main/java/survivalistessentials/data/client/handbook/SurvivalistEssentialsBookProvider.java)

To be clear, this mod is a fork of
[Patchouli](https://github.com/VazkiiMods/Patchouli)
and
[Patchouli Provider](https://github.com/BrassGoggledCoders/PatchouliProvider/tree/develop/1.21.x).
It is recommended to use those as a first alternative if they exist for the target
Minecraft version you are modding for. This exists only to move forward without waiting.

## Features
 - In-game text preview
 - Rich text formatting system that supports macros
 - Advancement-driven content unlocking
 - Nested categories and bookmarkable entries for quick and easy navigation
 - Several ready-to-use page types like text, crafting and image pages
 - Template system to create custom page types
 - Seamless integration with mods
 - Custom visuals and sounds
 - Easily localizable for other languages

## Differences
 - No multiblock visualization. May be re-added in future versions.
 - Entity viewing (WIP, needs fixing)
 - Datagen as a primary method for developing book content.

For more information, see the [docs](https://github.com/wendall911/Handbook/wiki).

## License Information

Much of Handbook's original code and assets are licensed under the CC-BY-NC-SA 3.0 Unported
license, and the repository is a direct fork for historical purposes.

Code for datagen and all other future updates or additions are under the MIT license.
