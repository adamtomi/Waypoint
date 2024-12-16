package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// TODO handle waypoint deletion and relocation
@Singleton
public class NavigationService {
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
        final ParticleStream stream = ParticleStream.init(
                player.getLocation().clone().add(0, this.config.get(StandardKeys.Navigation.Y_OFFSET), 0),
                destination.getPosition().toLocation(),
                () -> new Particle.DustOptions(this.config.get(StandardKeys.Navigation.PARTICLE_COLOR), this.config.get(StandardKeys.Navigation.PARTICLE_SIZE)),
                () -> particleStreamFinished(uniqueId, navigationId)
        );

        final Navigation navigation = new Navigation(navigationId, destination, stream);
        this.activeNavigations.put(uniqueId, navigation);
        // TODO play indefinitely until player arrives.
        stream.play(player, 15);
    }

    private void particleStreamFinished(final UUID uniqueId, final UUID navigationId) {
        final Navigation navigation = this.activeNavigations.get(uniqueId);
        if (navigation == null) return;

        if (navigation.uniqueId().equals(navigationId)) this.activeNavigations.remove(uniqueId);
    }

    public void stopNavigation(final Player player) {
        final Navigation navigation = this.activeNavigations.remove(player.getUniqueId());
        if (navigation != null) {
            navigation.stream().cancel();
        }
    }
}
