package me.deadlight.ezchestshop.data;


import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.deadlight.ezchestshop.EzChestShop;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class PlayerContainer {
    private static final Cache<UUID, PlayerContainer> cache = CacheBuilder.newBuilder()
            .maximumSize(16)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    private final UUID playerId;

    private PlayerContainer(@NotNull UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId);
    }

    public static PlayerContainer get(OfflinePlayer offlinePlayer) {
        UUID playerId = offlinePlayer.getUniqueId();
        PlayerContainer result = cache.getIfPresent(playerId);

        if (result == null) {
            result = new PlayerContainer(playerId);
            cache.put(playerId, result);
        }

        return result;
    }
}
