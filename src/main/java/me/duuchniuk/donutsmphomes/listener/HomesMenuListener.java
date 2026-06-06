package me.duuchniuk.donutsmphomes.listener;

import me.duuchniuk.donutsmphomes.home.Home;
import me.duuchniuk.donutsmphomes.home.HomeManager;
import me.duuchniuk.donutsmphomes.teleport.TeleportManager;
import me.duuchniuk.donutsmphomes.util.HomesMenu;
import me.duuchniuk.donutsmphomes.util.Texts;
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
      if (event.getView().getTitle().equals(HomesMenu.TITLE)) {
         event.setCancelled(true);
         HumanEntity var3 = event.getWhoClicked();
         if (var3 instanceof Player) {
            Player player = (Player)var3;
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta()) {
               ItemMeta meta = item.getItemMeta();
               PersistentDataContainer pdc = meta.getPersistentDataContainer();
               String action = (String)pdc.get(HomesMenu.ACTION_KEY, PersistentDataType.STRING);
               String homeName = (String)pdc.get(HomesMenu.HOME_KEY, PersistentDataType.STRING);
               if (action != null) {
                  switch (action) {
                     case "teleport":
                        Home home = (Home)this.homeManager.findHome(player.getUniqueId(), homeName).orElse((Object)null);
                        if (home != null) {
                           player.closeInventory();
                           this.teleportManager.startTeleport(player, home);
                        }
                        break;
                     case "delete":
                        if (this.homeManager.deleteHome(player.getUniqueId(), homeName)) {
                           player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.75F, 0.9F);
                           player.openInventory(HomesMenu.create(this.homeManager, player));
                        }
                        break;
                     case "set-home":
                        this.homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.75F, 1.6F);
                        player.openInventory(HomesMenu.create(this.homeManager, player));
                        break;
                     case "team-home":
                     case "delete-team-home":
                        Texts.send(player, "&cTeam homes are visual-only in this standalone plugin.");
                  }

               }
            }
         }
      }
   }
}
