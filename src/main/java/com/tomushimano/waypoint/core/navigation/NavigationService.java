package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.BukkitUtil;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// TODO handle waypoint deletion and relocation
// TODO cancel all on reload
@Singleton
public class NavigationService {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(NavigationService.class);
    private final Map<UUID, Navigation> activeNavigations = new ConcurrentHashMap<>();
    private final Configurable config;

    @Inject
    public NavigationService(final @Cfg Configurable config) {
        this.config = config;
    }

    public boolean isNavigating(final Player player) {
        return this.activeNavigations.containsKey(player.getUniqueId());
    }

    public Optional<Waypoint> currentDestination(final Player player) {
        final Navigation navigation = this.activeNavigations.get(player.getUniqueId());
        return navigation == null
                ? Optional.empty()
                : Optional.of(navigation.destination());
    }

    public void startNavigation(final Player player, final Waypoint destination) {
        final UUID uniqueId = player.getUniqueId();
        if (this.activeNavigations.containsKey(uniqueId)) {
            throw new IllegalArgumentException("Player already has a particle stream running: '%s'".formatted(uniqueId));
        }

        final UUID navigationId = UUID.randomUUID();
        final Location origin = player.getLocation().clone().add(0, this.config.get(StandardKeys.Navigation.Y_OFFSET), 0);
        final ParticleConfig config = ParticleConfig.from(this.config);
        final ParticleStream stream = ParticleStream.init(
                origin,
                destination.getPosition().toLocation(),
                config,
                () -> particleStreamFinished(uniqueId, navigationId)
        );

        final Navigation navigation = new Navigation(navigationId, destination, stream);
        this.activeNavigations.put(uniqueId, navigation);
        stream.play(player);
    }

    private void particleStreamFinished(final UUID uniqueId, final UUID navigationId) {
        final Navigation navigation = this.activeNavigations.get(uniqueId);
        if (navigation == null) return;

        if (navigation.uniqueId().equals(navigationId)) {
            this.activeNavigations.remove(uniqueId);
            reportFinishedParticleStream(uniqueId, navigation);
        }
    }

    private void reportFinishedParticleStream(final UUID ownerId, final Navigation navigation) {
        final OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerId);
        final String formattedOwner = BukkitUtil.formatPlayer(owner);

        LOGGER.info("Navigation (to '{}') owned by '{}' has finished.", navigation.destination().getName(), formattedOwner);
    }

    public void stopNavigation(final Player player) {
        final Navigation navigation = this.activeNavigations.remove(player.getUniqueId());
        if (navigation != null) {
            navigation.stream().cancel();
        }
    }

    public void performShutdown() {
        LOGGER.info("Canelling running navigations ({})...", this.activeNavigations.size());
        for (final Map.Entry<UUID, Navigation> entry : this.activeNavigations.entrySet()) {
            entry.getValue().stream().cancel();
        }

        this.activeNavigations.clear();
    }

    // TODO invoke upon waypoint deletion
    public void cancelAll(final Waypoint destination) {
        final Set<Map.Entry<UUID, Navigation>> markedForRemoval = this.activeNavigations.entrySet()
                .stream()
                .filter(x -> x.getValue().destination().equals(destination))
                .collect(Collectors.toSet());

        LOGGER.info("Cancelling all ({}) navigations to waypoint '{}'...", markedForRemoval.size(), destination.getName());
        for (final Map.Entry<UUID, Navigation> entry : markedForRemoval) {
            entry.getValue().stream().cancel();
            this.activeNavigations.remove(entry.getKey(), entry.getValue());
        }
    }
}
