# Публикация на CurseForge

- `curseforge_description.md` — описание страницы (EN + RU), вставить в редактор описания в режиме Markdown.
- Файл мода: `build/libs/ds_immersive_portals-neoforge-1.21.1-<версия>.jar` (собирается `gradlew build`).
- Changelog: `../CHANGELOG.md`.

## Поля проекта

- Game: Minecraft, Class: **Mods**
- Name: **Dragon Survival x Immersive Portals**
- Summary: `Dragon Survival dragons work with Immersive Portals: synced through portals, no getting stuck, whole model at the portal, breath goes through.`
- Game version: **1.21.1**, Mod loader: **NeoForge**, Java 21, Environment: **Client + Server**
- License: MIT

## Relations

- Dragon Survival (420799): **Required Dependency**
- Immersive Portals - CE (1672810), Immersive Portals (355440), Compat Fork (1611989): **Optional Dependency** у всех трёх.
  Нужна любая одна сборка, а CurseForge App сама ставит все Required, так что Required на одной из них поставит её игроку
  с другой сборкой (две сборки IP = краш). Без IP NeoForge сам выдаст понятную ошибку о недостающей зависимости.
- Immersive Portals Improvements: **Optional Dependency**, когда он будет опубликован.
