# Changelog

## 1.2.4 - 2026-03-27

- Added admin command `/recovery <player>` to instantly revive a knocked-out player.
- Added permission `knockout.admin.recovery` and config key `admin.recovery-permission`.
- Added recovery-related messages to `config.yml` and `KnockoutConfig`:
  - `recovery-usage`
  - `recovery-target-not-found`
  - `recovery-target-not-knocked`
  - `recovery-target-recovered`
  - `recovery-admin-success`
- Updated `plugin.yml` with `recovery` command and new permission node.
- Updated `WIKI.md` with admin usage, permissions, and troubleshooting for recovery.
