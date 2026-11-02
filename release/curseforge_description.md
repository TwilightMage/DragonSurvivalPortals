# Dragon Survival x Immersive Portals

Makes **Dragon Survival** dragons work properly with **Immersive Portals** on **NeoForge 1.21.1**: they look like dragons
when seen through a portal, walk through portals without getting stuck, are drawn whole at the portal, and their breath
goes through to the other side.

It only contains fixes for this pair of mods: it adds no blocks, items or commands, and it does not change how portals work
for anything else.

## What it fixes

- **Dragons seen through a portal.** Immersive Portals sends data about entities in another dimension in a way Dragon
  Survival's sync could not follow, so a dragon on the other side of a portal looked like a human, without its size,
  wings or flight state. The data now reaches the right dragon. Projectiles crossing a portal keep their data too
  (the original Immersive Portals and the Compat Fork need this; CE 6.0.12+ already has it).
- **Walking into a portal.** Dragon Survival's "does the dragon fit here" check also counted the blocks behind the portal,
  which could force a dragon to crouch or crawl, or leave it stuck. It now checks only the part of the hitbox in front of
  the portal.
- **The dragon model at a portal.** A dragon's hitbox is about the size of a player, but its neck, wings and tail reach
  several blocks further, and those parts vanished into the portal. The whole model is now cut at the portal plane and
  the rest is drawn on the other side.
- **Second model on every crossing.** The dragon model used to be recreated when you crossed, so a second "shrinking"
  model appeared for a moment. It now stays the same model.
- **First-person body.** Your own dragon body is no longer drawn into the world you see through a portal.
- **Breath through portals.** Any Dragon Survival breath ability now goes through a portal: it affects what you see on the
  other side and its particles fly out of the far side. Blocks and mobs hidden behind the portal in your own world are
  no longer hit.
- **Third-person camera.** The camera no longer jumps close to a big dragon when it passes behind a portal.
- **Iris installed, no shader pack.** Immersive Portals turns off drawing anything that sticks through a portal as soon as
  Iris is installed. It now does that only while a shader pack is active.

## Requirements

Install on **both the client and the server**.

Required:

- Minecraft 1.21.1, NeoForge
- [Dragon Survival](https://www.curseforge.com/projects/420799) and its dependency GeckoLib
- **One** Immersive Portals build, with its dependency Cloth Config:
  - [Immersive Portals - CE](https://www.curseforge.com/projects/1672810) (the one this mod is tested on)
  - [Immersive Portals](https://www.curseforge.com/projects/355440) (NeoForge 6.0.x)
  - [Immersive Portals Sodium/Iris/DH Compat Fork](https://www.curseforge.com/projects/1611989)

Never install two Immersive Portals builds together.

Optional:

- **Immersive Portals Improvements**: not needed, and it works alongside this mod. Both mods contain the general fixes
  (mod data through portals, third-person camera, Iris), so this mod leaves those to it when it is installed.
- **Iris**: not needed; see the shader note under Known limitations.

## Config

`config/ds_immersive_portals.toml` has one switch per fix, each with a description in English and Russian:
`dragon_sync`, `dragon_hitbox`, `dragon_rendering`, `dragon_breath`, `third_person_camera`, `iris_projections`.
A switched-off fix is not patched in at all, so if an update of Dragon Survival or Immersive Portals breaks one, switch it
off and the game starts again. Restart the game after changing the file.

## Tested with

NeoForge 21.1.264, Dragon Survival 2.0.71, GeckoLib 4.8.3, Immersive Portals - CE 6.1.3.1, on a dedicated server.
Other versions may work, but were not tested. GeckoLib 4.9.3 fails to parse some Dragon Survival animations, so 4.8.3 is
recommended. If something breaks, please report the versions you use.

## Known limitations

- While an Iris shader pack is active, Immersive Portals does not draw anything that sticks through a portal, so a dragon
  is not shown on the other side.
- When you mine a block through a portal, Dragon Survival may pick the claw tool for the wrong world. This only affects
  the client-side mining speed.

## License

MIT.

---

## Русский

Драконы **Dragon Survival** нормально работают с **Immersive Portals** на **NeoForge 1.21.1**: за порталом выглядят
драконами, проходят сквозь портал не застревая, рисуются у портала целиком, а их дыхание достаёт до другой стороны. В моде
только исправления для этой пары: новых блоков, предметов и команд нет, для всего остального порталы работают как раньше.

**Что исправляет**

- **Драконы за порталом.** Дракон по ту сторону портала выглядел человеком: без размера, крыльев и полёта. Теперь данные
  доходят до нужного дракона. Снаряды при пересечении портала тоже сохраняют данные (нужно для оригинального Immersive
  Portals и Compat Fork; в CE 6.0.12+ это уже есть).
- **Вход в портал.** Проверка «помещается ли дракон» учитывала блоки за порталом, из-за чего дракон мог приседать, ползти
  или застревать. Теперь проверяется только часть хитбокса перед порталом.
- **Модель у портала.** Шея, крылья и хвост выходят далеко за хитбокс и пропадали в портале. Теперь вся модель режется по
  плоскости портала, а остальное видно с другой стороны.
- **Вторая модель при проходе.** Модель больше не пересоздаётся при каждом проходе, «сжимающегося» двойника нет.
- **Тело от первого лица** не рисуется в мире, который виден через портал.
- **Дыхание сквозь портал.** Любое дыхание DS действует на то, что видно на той стороне, частицы вылетают из портала.
  Блоки и мобы, спрятанные за порталом в вашем мире, больше не задеваются.
- **Камера от третьего лица** не прыгает к большому дракону, когда оказывается за порталом.
- **Iris без шейдерпака.** Immersive Portals отключал отрисовку всего, что торчит сквозь портал, если просто установлен
  Iris. Теперь только при включённом шейдерпаке.

**Что нужно**

Ставить на **клиент и сервер**.

- Обязательно: Minecraft 1.21.1, NeoForge, [Dragon Survival](https://www.curseforge.com/projects/420799) (с GeckoLib) и
  **одна** сборка Immersive Portals (с Cloth Config): [CE](https://www.curseforge.com/projects/1672810) (на ней мод
  проверяется), [оригинальный](https://www.curseforge.com/projects/355440) или
  [Compat Fork](https://www.curseforge.com/projects/1611989). Две сборки вместе ставить нельзя.
- Необязательно: **Immersive Portals Improvements** (совместим; общие исправления тогда делает он) и **Iris**.

**Настройки:** `config/ds_immersive_portals.toml`, у каждого исправления свой выключатель с описанием. После изменения
перезапустите игру.

**Проверено на:** NeoForge 21.1.264, Dragon Survival 2.0.71, GeckoLib 4.8.3 (4.9.3 не читает часть анимаций DS),
Immersive Portals - CE 6.1.3.1, выделенный сервер. Другие версии могут работать, но не проверялись.

**Известные ограничения:** при включённом шейдерпаке Iris дракон не виден с другой стороны портала. При добыче блока через
портал DS может выбрать коготь не для того мира (влияет только на скорость добычи на клиенте).
