# Waypoint
### Commands
- `/wp set <name> [--color <color>] [--public]`: Create a new waypoint with the provided name, text color (used in the hologram of this waypoint). Public waypoints are visible to everyone.
- `/wp rm <name>`: Remove the waypoint with the provided name.
- `/wp ls [--page <page>] [--hide-public]`: List all available waypoints, including public ones. To exclude publics, set `--hide-public`.
- `/wp edit <waypoint> [--name <new-name>] [--color <color>] [--toggle-visibility]`: Update waypoint data.
- `/wp reloc <name>`: Relocate the waypoint to the player's current position.
- `/wp dist <name>`: Display the distance (in blocks) from the provided waypoint.
- `/wp nav info`: Display the currently active nagvigation.
- `/wp nav start <name> [--force]`: Start a navigation to the specified waypoint. The `--force` flag replaces the current navgation (if it exists).
- `/wp nav stop`: Stops the currently active navigation.
- `/wpa reload`: Reload configuration and waypoint holograms. **DOES NOT** reload storage.
