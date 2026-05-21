# DnD Six Attributes

DnD Six Attributes adds a lightweight DnD-style attribute sheet for Minecraft players.

- GitHub: https://github.com/rinchanai/dnd-six-attributes
- Modrinth: https://modrinth.com/mod/dnd-six-attributes

## First-pass features

- Inventory-screen entry button for the attribute sheet.
- Six default attributes: Strength, Dexterity, Constitution, Intelligence, Wisdom, and Charisma.
- Steve baseline is 10 in every attribute.
- Allocated points start at 0 by default.
- Attribute values are clamped to 0..20.
- Other mods can register custom attributes through the public API.

## Default effects

- Strength: adds melee damage inside the vanilla attack-damage attribute layer.
- Dexterity: adjusts attack speed.
- Constitution: adjusts max health.
- Intelligence: adjusts vanilla magic damage.
- Wisdom: highlights nearby entities for the player only.
- Charisma: adjusts villager trade prices.
