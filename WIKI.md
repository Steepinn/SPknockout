# SPknockout Wiki

## 1) What the plugin does

`SPknockout` turns **any** lethal damage into a knockout first (when enabled), instead of instant death:

- the player enters knockout instead of dying immediately (lava, void, mobs, players, etc. — all follow the same rule when `any-lethal-triggers-knockout` is `true`);
- an ally can revive them by sneaking nearby;
- a knocked-out player can surrender with `/die`;
- if nobody revives them in time, they die;
- **disconnecting while knocked out** counts as `/die`: the player dies normally (loot / death screen / respawn rules apply).
- while knocked out, **crawl mode** (configurable) lets players move slowly on the ground instead of being almost frozen.

**Repeat knockout cooldown:** only after a **successful revive** (picked up by an ally or self-revive potion), the player **cannot** enter knockout again for `repeat-knockout-cooldown-seconds` (default 60). **Any death** (including in knockout, `/die`, timeout) **clears** this cooldown, so after respawn they can be knocked out again immediately.

## 2) Commands

- `/die` — only while knocked out; ends knockout with death.
- `/knockout reload` — reloads `config.yml` without restarting the server.
- `/recovery <player>` — admin command; instantly revives the specified knocked-out player.

## 3) Installation

1. Copy the jar into the `plugins` folder.
2. Start the server.
3. Confirm `plugins/SPknockout/config.yml` was created.
4. Edit `config.yml` for your server.
5. Restart the server or run `/knockout reload`.

## 4) Full `config.yml` reference

### language

- `locale` — built-in message language: `en` or `ru`.
- Default is English (`en`).
- Any key under `messages` can be overridden manually.

### knockout

- `any-lethal-triggers-knockout` — if `true`, any lethal damage source can put the player into knockout (when not on repeat cooldown). If `false`, only entity damage that passes the old rules (players + optional mobs via `allow-mob-knockout`) triggers knockout; environmental lethal damage will **not** trigger knockout.
- `repeat-knockout-cooldown-seconds` — after a **successful revive only**, how long until the player can be knocked out again. Does **not** apply after dying in knockout; **any death clears** this timer. Set to `0` to disable.

#### knockout.crawl

- `enabled` — crawl-style pose and lighter slowness so the player can move on the ground while knocked out.
- `slowness-amplifier` — Slowness level while crawling (lower than `effects.slowness` so movement is possible). When `enabled` is `false`, `effects.slowness` is used instead.
- `pose` — Bukkit `Pose` name (e.g. `CRAWLING` on 1.21). Invalid names fall back to `SWIMMING`. With `enabled: false`, the old swim+crouch imitation is used.

- `duration-seconds` — how long knockout lasts before automatic death.
- `rescue-seconds` — how long a full revive takes.
- `horizontal-rescue-radius` — horizontal radius in which a rescuer counts.
- `rescuer-min-y-offset` — minimum Y difference for the rescuer (reduces rescues from below through blocks).
- `allow-environmental-damage-while-knocked`:
  - `false` — environmental damage (fall, fire, etc.) is blocked in knockout;
  - `true` — environmental damage is allowed.
- `allow-mob-knockout` — allow mobs to trigger knockout.
- `allow-finisher-hit` — allow players to finish knocked-out targets with melee hits.
- `team-only-rescue` — only players on the same scoreboard team can rescue.
- `post-revive-invulnerability-seconds` — invulnerability after being revived.

#### knockout.self-revive

- `enabled` — enable self-revive via potion roll.
- `chance` — base chance (`0.25` = 25%).
- `chances.healing` — chance when the potion base type is `HEALING`.
- `chances.regeneration` — chance when the potion base type is `REGENERATION`.
- `consume-potion` — consume one potion on successful self-revive.

Requirement: the player must have a potion with base type `HEALING` or `REGENERATION` in their inventory.

#### knockout.revive-health

- `mode`:
  - `percent-max` — health after revive is a fraction of max health;
  - `fixed` — fixed HP value.
- `value`:
  - for `percent-max` — number from 0.0 to 1.0 (e.g. `0.45`);
  - for `fixed` — absolute HP (e.g. `8.0` = 4 hearts).
- `minimum-hearts` — minimum hearts after revive (floor).

#### knockout.effects

- `slowness.amplifier` — slowness level in knockout.
- `slowness.refresh-duration-ticks` — effect duration refreshed each tick loop.
- `blindness.amplifier` — blindness level.
- `blindness.refresh-duration-ticks` — blindness refresh duration.

### messages

All strings support `&` color codes and `{percent}` for progress:

- `only-players`
- `no-permission`
- `not-knocked`
- `reload-success`
- `recovery-usage`
- `recovery-target-not-found`
- `recovery-target-not-knocked`
- `recovery-target-recovered`
- `recovery-admin-success`
- `self-revive-success`
- `knockout-title`
- `knockout-subtitle`
- `rescue-reset-title`
- `rescue-reset-subtitle`
- `rescue-progress-title`
- `rescue-progress-subtitle`
- `rescuer-actionbar`
- `revived-title`

### visuals.rescue-particles

- `enabled` — toggle particles.
- `particle` — Bukkit particle name (e.g. `HEART`).
- `every-ticks` — spawn interval.
- `count` — particles per spawn.
- `offset-x`, `offset-y`, `offset-z` — spread.
- `y-add` — vertical offset from the victim.

### sounds.revive

- `enabled` — play sound on revive.
- `sound` — Bukkit sound name (e.g. `ENTITY_PLAYER_LEVELUP`).
- `volume` — volume.
- `pitch` — pitch.

### admin

- `reload-permission` — permission for `/knockout reload`.
- `recovery-permission` — permission for `/recovery <player>`.

## 5) Example configs

### Hardcore (short knockout, harder rescue)

```yml
knockout:
  duration-seconds: 25
  rescue-seconds: 14
  horizontal-rescue-radius: 1.4
  allow-environmental-damage-while-knocked: true
  revive-health:
    mode: percent-max
    value: 0.30
    minimum-hearts: 2.0
```

### Casual (longer knockout, easier rescue)

```yml
knockout:
  duration-seconds: 90
  rescue-seconds: 7
  horizontal-rescue-radius: 2.2
  allow-environmental-damage-while-knocked: false
  revive-health:
    mode: percent-max
    value: 0.60
    minimum-hearts: 4.0
```

## 6) Troubleshooting

- Knockout does not trigger — check damage source (player / projectile) and plugin startup errors.
- `/die` does nothing — only works while knocked out; another plugin may override the command.
- `/knockout reload` fails — check `admin.reload-permission` and OP.
- `/recovery` fails — check `admin.recovery-permission`, player nickname, and that target is actually in knockout.
- Rescue does not progress — rescuer must sneak, be in radius; check `horizontal-rescue-radius`, `rescuer-min-y-offset`, `team-only-rescue`.
- Broken message colors — use `&a`, `&c`, `&7`, etc. in `messages`.
- No particles/sound — check `visuals.rescue-particles.enabled`, `sounds.revive.enabled`, and valid Bukkit names.
- Self-revive never happens — enable `knockout.self-revive.enabled`, ensure `HEALING`/`REGENERATION` potions, tune `chance` / `chances.*`.
