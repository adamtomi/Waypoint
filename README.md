# Waypoint
### Commands
`/wp set <name> [--color <color>] [--global]`: Create a new waypoint with the provided name, text color (used in the hologram of this waypoint). Global waypoints are visible to everyone.
`/wp rm <name>`: Remove the waypoint with the provided name.
`/wp ls [--page <page>] [--hide-global]`: List all available waypoints, including global ones. To exclude globals, set `--hide-global`.
`/wp edit <waypoint> [--name <new-name>] [--color <color>] [--toggle-global]`: Update waypoint data.
`/wp reloc <name>`: Relocate the waypoint to the player's current position.
