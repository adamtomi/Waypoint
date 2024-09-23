-- Create "waypoints" table. Fields "id" and "ownerId" are UUIDs.
CREATE TABLE IF NOT EXISTS `waypoints` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `ownerId` VARCHAR(36) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `global` BIT NOT NULL,
    `world` VARCHAR(255) NOT NULL,
    `x` DOUBLE NOT NULL,
    `y` DOUBLE NOT NULL,
    `z` DOUBLE NOT NULL,
    `yaw` FLOAT NOT NULL,
    `pitch` FLOAT NOT NULL
);

-- Create unique index so that one name can be used once by every user.
CREATE UNIQUE INDEX IF NOT EXISTS `waypoints_ownerId_name` ON `waypoints`(`ownerId`, `name`);
