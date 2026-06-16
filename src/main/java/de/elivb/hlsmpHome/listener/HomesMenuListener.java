package de.elivb.hlsmpHome.listener;

import de.elivb.hlsmpHome.home.Home;
import de.elivb.hlsmpHome.home.HomeManager;
import de.elivb.hlsmpHome.teleport.TeleportManager;
import de.elivb.hlsmpHome.util.HomesMenu;
import de.elivb.hlsmpHome.util.Texts;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class HomesMenuListener implements Listener {
   private final HomeManager homeManager;
   private final TeleportManager teleportManager;

   public HomesMenuListener(HomeManager homeManager, TeleportManager teleportManager) {
      this.homeManager = homeManager;
      this.teleportManager = teleportManager;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (!event.getView().getTitle().equals(HomesMenu.TITLE)) return;

      event.setCancelled(true);
      HumanEntity clicker = event.getWhoClicked();
      if (!(clicker instanceof Player player)) return;

      ItemStack item = event.getCurrentItem();
      if (item == null || !item.hasItemMeta()) return;

      ItemMeta meta = item.getItemMeta();
      PersistentDataContainer pdc = meta.getPersistentDataContainer();
      String action = pdc.get(HomesMenu.ACTION_KEY, PersistentDataType.STRING);
      String homeName = pdc.get(HomesMenu.HOME_KEY, PersistentDataType.STRING);
      if (action == null) return;

      switch (action) {
         case "teleport" -> {
            Home home = this.homeManager.findHome(player.getUniqueId(), homeName).orElse(null);
            if (home != null) {
               player.closeInventory();
               this.teleportManager.startTeleport(player, home);
            }
         }
         case "delete" -> {
            if (this.homeManager.deleteHome(player.getUniqueId(), homeName)) {
               player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.75F, 0.9F);
               player.openInventory(HomesMenu.create(this.homeManager, player));
            }
         }
         case "set-home" -> {
            this.homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.75F, 1.6F);
            player.openInventory(HomesMenu.create(this.homeManager, player));
         }
         case "team-home" -> {
            player.closeInventory();
            Bukkit.dispatchCommand(player, "team home");
         }
         // "none" — không làm gì (item chỉ hiển thị trạng thái)
      }
   }
}
