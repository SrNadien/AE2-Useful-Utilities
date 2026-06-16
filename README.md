# AE2 Utilities

A utility mod for [Applied Energistics 2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2) that adds quality-of-life features for managing your ME network.

---

## Features

### Pick Block from ME Network
Middle-click any block in the world to instantly retrieve up to 64 of that block directly from your ME network — no need to open the terminal. Works as long as you have a linked wireless terminal (or wireless crafting terminal) with enough power.

**Curios support:** If [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) is installed, the mod will also check terminals equipped in curio slots, so you don't need to keep the terminal in your regular inventory.

### Chunk Loading via Infinity/Dimensional Card *(Configurable)*
When using [AE Infinity Booster](https://www.curseforge.com/minecraft/mc-mods/ae-infinity-booster), wireless terminals with an **Infinity Card** or **Dimensional Card** inserted can optionally keep the chunk containing their linked Wireless Access Point loaded at all times. This means your ME network stays active and accessible even when no players are nearby — useful for autocrafting, farms, or any setup that needs to run continuously.

This behavior is **disabled by default** and can be toggled in the mod config (`ae2utilities-common.toml`).

---

## Requirements

| Mod | Version | Type |
|---|---|---|
| Applied Energistics 2 | `19.2.17` | Required |
| AE2WTLib | `19.5.0` | Required |
| AE Infinity Booster | `1.21.1-1.0.0.54` | Required |
| Curios API | `9.5.1+1.21.1` | Optional |

---

## Compatibility

- **Curios API** — terminals in curio slots are detected automatically if Curios is installed. The mod works fine without it.

---

## Links

- [Source Code](https://github.com/SrNadien/AE2-Utilities)
- [Issue Tracker](https://https://github.com/SrNadien/AE2-Utilities/issues)
