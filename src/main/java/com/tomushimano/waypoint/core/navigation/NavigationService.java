package com.tomushimano.waypoint.core.navigation;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.util.BukkitUtil;
import com.tomushimano.waypoint.util.ConcurrentUtil;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.Bukkit;
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

@Singleton
public class NavigationService {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(NavigationService.class);
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-navigation-pool #%1$d").build()
    );
    private final Map<UUID, NavigationTask> activeNavigations = new ConcurrentHashMap<>();
    private final Configurable config;
    private final Configurable langConfig;

    @Inject
    public NavigationService(final @Cfg Configurable config, final @Lang Configurable langConfig) {
        this.config = config;
        this.langConfig = langConfig;
    }

    private Optional<NavigationTask> navigation(final Player player) {
        return Optional.ofNullable(this.activeNavigations.get(player.getUniqueId()));
    }

    public Optional<Waypoint> currentDestination(final Player player) {
        return navigation(player).map(NavigationTask::destination);
    }

    public void startNavigation(final Player player, final Waypoint destination, final Runnable arrivalHook) {
        final UUID uniqueId = player.getUniqueId();
        if (this.activeNavigations.containsKey(uniqueId)) {
            throw new IllegalArgumentException("Player already has a particle stream running: '%s'".formatted(uniqueId));
        }

        final UUID navigationId = UUID.randomUUID();
        final NavigationTask task = new NavigationTask(
                navigationId,
                player,
                destination,
                this.langConfig,
                this.config,
                () -> {
                    arrivalHook.run();
                    navigationFinished(uniqueId, navigationId);
                }
        );

        this.activeNavigations.put(uniqueId, task);
        this.executor.execute(task);
    }

    private void navigationFinished(final UUID uniqueId, final UUID navigationId) {
        final NavigationTask navigation = this.activeNavigations.get(uniqueId);
        if (navigation == null) return;

        if (navigation.uniqueId().equals(navigationId)) {
            this.activeNavigations.remove(uniqueId);
            reportFinishedParticleStream(uniqueId, navigation);
        }
    }

    private void reportFinishedParticleStream(final UUID ownerId, final NavigationTask navigation) {
        final OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerId);
        final String formattedOwner = BukkitUtil.formatPlayer(owner);

        LOGGER.info("Navigation (to '{}') owned by '{}' has finished.", navigation.destination().getName(), formattedOwner);
    }

    public void stopNavigation(final Player player) {
        final NavigationTask navigation = this.activeNavigations.remove(player.getUniqueId());
        if (navigation != null) navigation.cancel();
    }

    public void updateNavigations(final Waypoint waypoint) {
        for (final NavigationTask navigation : this.activeNavigations.values()) {
            if (!navigation.destination().equals(waypoint)) continue;

            navigation.queueUpdate();
        }
    }

    public void performShutdown() {
        LOGGER.info("Cancelling running navigations ({})...", this.activeNavigations.size());
        ConcurrentUtil.terminate(this.executor, 1L);
        this.activeNavigations.clear();
    }

    public void cancelAll(final Waypoint destination) {
        final Set<Map.Entry<UUID, NavigationTask>> markedForRemoval = this.activeNavigations.entrySet()
                .stream()
                .filter(x -> x.getValue().destination().equals(destination))
                .collect(Collectors.toSet());

        LOGGER.info("Cancelling all ({}) navigations to waypoint '{}'...", markedForRemoval.size(), destination.getName());
        for (final Map.Entry<UUID, NavigationTask> entry : markedForRemoval) {
            entry.getValue().cancel();
            this.activeNavigations.remove(entry.getKey(), entry.getValue());
        }
    }
}
