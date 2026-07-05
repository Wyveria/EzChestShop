package me.deadlight.ezchestshop.listeners;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import me.deadlight.ezchestshop.EzChestShop;
import me.deadlight.ezchestshop.Constants;
import me.deadlight.ezchestshop.data.LanguageManager;
import me.deadlight.ezchestshop.data.PlayerContainer;
import me.deadlight.ezchestshop.events.PlayerTransactEvent;
import me.deadlight.ezchestshop.utils.Utils;
import me.deadlight.ezchestshop.utils.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PlayerTransactionListener implements Listener {
    @EventHandler
    public void onTransaction(PlayerTransactEvent event) {
        sendDiscordWebhook(event);
        if (((TileState) event.getContainerBlock().getState(false)).getPersistentDataContainer().getOrDefault(Constants.ENABLE_MESSAGE_KEY, PersistentDataType.INTEGER, 0) == 1) {
            OfflinePlayer owner = event.getOwner();
            List<UUID> getters = event.getAdminsUUID();
            getters.add(owner.getUniqueId());

            if (event.isBuy()) {
                for (UUID adminUUID : getters) {
                    Player admin = Bukkit.getPlayer(adminUUID);
                    if (admin != null) {
                        if (admin.isOnline()) {
                            admin.getPlayer().sendMessage(LanguageManager.getInstance().transactionBuyInform(event.getCustomer().getName(),
                                    event.getCount(), event.getItemName(), event.getPrice()));
                        }
                    }
                }
            } else {
                for (UUID adminUUID : getters) {
                    Player admin = Bukkit.getPlayer(adminUUID);
                    if (admin != null) {
                        if (admin.isOnline()) {
                            if (admin.isOnline()) {
                                admin.getPlayer().sendMessage(LanguageManager.getInstance().transactionSellInform(
                                        event.getCustomer().getName(), event.getCount(), event.getItemName(), event.getPrice()));
                            }
                        }
                    }
                }
            }
        }
    }

    public void sendDiscordWebhook(PlayerTransactEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        Block block = event.getContainerBlock();

        final String buyerName;
        final String sellerName;
        final String itemName = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : event.getItemName();
        final String price = EzChestShop.getEconomy().format(event.getPrice());
        final String shopLocation = block.getWorld().getName() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ();
        final String time = DateTimeFormatter.ISO_DATE_TIME.format(event.getTime()).replace("T", " | ").replace("Z", "").replace("-", "/");
        final String quantity = NumberFormat.getInstance(Locale.ENGLISH).format(event.getCount());
        final String ownerName = event.getOwner().getName();

        if (event.isBuy()) {
            buyerName = event.getCustomer().getName();
            sellerName = event.getOwner().getName();
        } else {
            buyerName = event.getOwner().getName();
            sellerName = event.getCustomer().getName();
        }

        EzChestShop.getScheduler().runTaskAsynchronously(
                () -> DiscordWebhook.queueTransaction(buyerName, sellerName, itemName, price, shopLocation, time, quantity, ownerName));
    }

}
