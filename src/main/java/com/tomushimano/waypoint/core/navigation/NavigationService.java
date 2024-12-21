package com.tomushimano.waypoint.core.navigation;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.BukkitUtil;
import com.tomushimano.waypoint.util.ConcurrentUtil;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

// TODO cancel all on reload (or just refresh the configs)
@Singleton
public class NavigationService {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(NavigationService.class);
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-navigation-pool #%1$d").build()
    );
    private final Map<UUID, Navigation> activeNavigations = new ConcurrentHashMap<>();
    private final Configurable config;
    private final MessageConfig messageConfig;

    @Inject
    public NavigationService(final @Cfg Configurable config, final MessageConfig messageConfig) {
        this.config = config;
        this.messageConfig = messageConfig;
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

    public void startNavigation(final Player player, final Waypoint destination, final Runnable arrivalHook) {
        final UUID uniqueId = player.getUniqueId();
        if (this.activeNavigations.containsKey(uniqueId)) {
            throw new IllegalArgumentException("Player already has a particle stream running: '%s'".formatted(uniqueId));
        }

        final UUID navigationId = UUID.randomUUID();

        final ParticleConfig config = ParticleConfig.from(this.config);
        final ParticleStream stream = ParticleStream.init(
                calculateOrigin(player),
                destination.getPosition().toLocation(),
                config,
                () -> particleStreamFinished(uniqueId, navigationId)
        );

        final Navigation navigation = new Navigation(navigationId, destination, stream, arrivalHook);
        this.activeNavigations.put(uniqueId, navigation);
        // this.executor.execute(() -> stream.play(player));
        this.executor.execute(new NavigationTask(player, destination, this.messageConfig, stream));
    }

    private Location calculateOrigin(final Player player) {
        return player.getLocation().clone().add(0, this.config.get(StandardKeys.Navigation.Y_OFFSET), 0);
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

    public void updateNavigation(final Player player) {
        final Navigation navigation = this.activeNavigations.get(player.getUniqueId());
        if (navigation == null) return;

        if (navigation.destination().distance(player) <= this.config.get(StandardKeys.Navigation.ARRIVAL_DISTANCE)) {
            // The player has arrived
            navigation.arrivalHook().run();
            stopNavigation(player);
        } else {
            navigation.stream().updateOrigin(calculateOrigin(player));
        }
    }

    public void updateNavigations(final Waypoint waypoint) {
        for (final Navigation navigation : this.activeNavigations.values()) {
            if (!navigation.destination().equals(waypoint)) continue;

            navigation.stream().updateDestination(waypoint.getPosition().toLocation());
        }
    }

    public void performShutdown() {
        LOGGER.info("Cancelling running navigations ({})...", this.activeNavigations.size());
        ConcurrentUtil.terminate(this.executor, 1L);
        this.activeNavigations.clear();
    }

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
