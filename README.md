# Dragon Survival x Immersive Portals

A small NeoForge 1.21.1 mod that makes [Dragon Survival](https://www.curseforge.com/projects/420799)
dragons work with [Immersive Portals](https://www.curseforge.com/projects/355440)
(also Immersive Portals - CE and the Sodium/Iris/DH Compat Fork). Pure mixin mod, needed on both client and server.

## What it fixes

| Switch | Problem | Fix |
|---|---|---|
| `dragon_sync` | Dragons seen through a portal look like humans (no size, wings, flight); dragon projectiles crash or vanish when crossing. Immersive Portals handles packets about entities in the other world without switching the player's world, and Dragon Survival looks the dragon up in `player.level()`. | Redirected mod packets are handled with the player's world switched too. |
| `dragon_hitbox` | Dragons are forced to crouch/crawl or get stuck when walking into a portal: Dragon Survival's "does the dragon fit" check tests the whole hitbox, including the part behind the portal. | The check uses the portal-clipped box, as Immersive Portals does for players. |
| `dragon_rendering` | Neck, wings and tail vanish into the portal (Immersive Portals only cuts/projects what the small hitbox touches); the first-person dragon body is drawn into portal views; a second "shrinking" model appears on every crossing. | The whole model area is cut at the portal and drawn on the other side; no first-person body in portal views; the client dragon model survives crossings. |
| `dragon_breath` | Breath stops at a portal and burns what is hidden behind it. | Breath goes through the portal and hits what is seen on the other side; the particles fly out of the portal. |
| `third_person_camera` | The third-person camera jumps close to a big dragon when it passes behind a portal. | It keeps Dragon Survival's distance. |
| `iris_projections` | With Iris installed (even without a shader pack) Immersive Portals draws nothing that sticks through a portal, so the rendering fixes are invisible. | Off only while a shader pack is active. |

Every fix can be switched off in `config/ds_immersive_portals.toml` (a switched-off fix is not patched in at all;
restart after changing). `dragon_sync`, `third_person_camera` and `iris_projections` are general Immersive Portals
fixes that Immersive Portals Improvements
also carries: when it is installed, this mod leaves them to it.

## Tested with

Minecraft 1.21.1, NeoForge 21.1.264, Dragon Survival 2.0.71, GeckoLib 4.8.3, Immersive Portals - CE 6.1.3.1.
Other versions may work, but were not tested.

## Building

```
gradlew build
```

The jar is `build/libs/ds_immersive_portals-neoforge-1.21.1-<version>.jar`. Dependencies come from CurseForge through
[CurseMaven](https://www.cursemaven.com/), file ids are in `gradle.properties`.

Test runs (offline flat world, RCON on port 25575, password `dptest`):
`gradlew runServer`, then `gradlew runClient` (player `DragonTester`) and `gradlew runClient2` (`Watcher`); the clients
join the server automatically. `-PipUpstream` uses the original Immersive Portals 6.0.7, `-PdisableMod` loads the mod
without applying any of its mixins, `-PmixinExport` writes the transformed classes to `run/<run>/.mixin.out`.

## License

MIT, see `LICENSE`. This covers the code, not the logo: `src/main/resources/logo.png` and `release/curseforge_avatar.png`
contain the Elder Dragon Bone texture from Dragon Survival, which is under the Dragon Survival License.

---

# Dragon Survival x Immersive Portals (RU)

Небольшой мод для NeoForge 1.21.1: драконы Dragon Survival нормально работают с Immersive Portals (и с Immersive
Portals - CE, и с Sodium/Iris/DH Compat Fork). Только миксины, нужен и на клиенте, и на сервере.

- **dragon_sync** — драконы за порталом больше не выглядят людьми (размер, крылья, полёт синхронизируются), снаряды
  драконов не падают/не исчезают при пересечении портала.
- **dragon_hitbox** — дракон не приседает/не ползёт и не застревает, заходя в портал.
- **dragon_rendering** — вся модель (шея, крылья, хвост) режется порталом и видна с другой стороны; тело от первого
  лица не рисуется в видах через портал; модель не пересоздаётся при каждом проходе.
- **dragon_breath** — дыхание проходит сквозь портал и действует на то, что видно на той стороне; частицы вылетают
  из портала; то, что спрятано за порталом в мире дракона, не задевается.
- **third_person_camera** — камера от третьего лица за порталом не прыгает к большому дракону.
- **iris_projections** — при установленном Iris (без шейдерпака) отрисовка сквозь порталы не выключается.

Каждое исправление выключается в `config/ds_immersive_portals.toml` (выключенное вообще не встраивается; после
изменения перезапустите игру). Если установлен Immersive Portals Improvements, общие исправления (`dragon_sync`,
`third_person_camera`, `iris_projections`) делает он, этот мод их пропускает.
