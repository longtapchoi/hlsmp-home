package me.duuchniuk.donutsmphomes.util;

import java.util.List;
import me.duuchniuk.donutsmphomes.home.Home;
import me.duuchniuk.donutsmphomes.home.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class HomesMenu {
   public static final String TITLE = Texts.color("&8HOMES");
   public static final NamespacedKey ACTION_KEY = new NamespacedKey("donutsmp_homes", "action");
   public static final NamespacedKey HOME_KEY = new NamespacedKey("donutsmp_homes", "home");
   private static final String SMALL_TEAM_HOME = "ᴛᴇᴀᴍ ʜᴏᴍᴇ";
   private static final String SMALL_DELETE = "ᴅᴇʟᴇᴛᴇ";
   private static final String SMALL_NO_HOME_SET = "ɴᴏ ʜᴏᴍᴇ sᴇᴛ";
   private static final int[] HOME_SLOTS = new int[]{12, 13, 14, 15, 16};
   private static final int[] DELETE_SLOTS = new int[]{21, 22, 23, 24, 25};

   private HomesMenu() {
   }

   public static Inventory create(HomeManager homeManager, Player player) {
      Inventory inventory = Bukkit.createInventory(player, 36, TITLE);
      inventory.setItem(10, teamHome());
      inventory.setItem(19, deleteTeamHome());

      for(int index = 0; index < HOME_SLOTS.length; ++index) {
         int number = index + 1;
         String slotHomeName = homeManager.slotName(number);
         Home home = (Home)homeManager.findHome(player.getUniqueId(), slotHomeName).orElse((Object)null);
         if (home != null) {
            inventory.setItem(HOME_SLOTS[index], homeItem(home, slotHomeName));
            inventory.setItem(DELETE_SLOTS[index], deleteItem(home, slotHomeName));
         } else {
            inventory.setItem(HOME_SLOTS[index], emptyHomeItem(slotHomeName));
            inventory.setItem(DELETE_SLOTS[index], emptyDeleteItem(slotHomeName));
         }
      }

      return inventory;
   }

   private static ItemStack teamHome() {
      return namedItem(Material.WHITE_BANNER, "&bᴛᴇᴀᴍ ʜᴏᴍᴇ", List.of("&fClick to teleport to ᴛᴇᴀᴍ ʜᴏᴍᴇ"), "team-home", (String)null);
   }

   private static ItemStack deleteTeamHome() {
      return namedItem(Material.BLUE_DYE, "&bᴅᴇʟᴇᴛᴇ", List.of("&fClick to delete team home"), "delete-team-home", (String)null);
   }

   private static ItemStack homeItem(Home home, String homeName) {
      return namedItem(Material.BLUE_BED, coloredHomeName(home.displayName()), List.of("&fClick to teleport to your home"), "teleport", homeName);
   }

   private static ItemStack deleteItem(Home home, String homeName) {
      return namedItem(Material.BLUE_DYE, coloredHomeName(home.displayName()), List.of("&fClick to delete your home"), "delete", homeName);
   }

   private static ItemStack emptyHomeItem(String homeName) {
      return namedItem(Material.LIGHT_GRAY_BED, "&fɴᴏ ʜᴏᴍᴇ sᴇᴛ", List.of("&f- Click to save your location"), "set-home", homeName);
   }

   private static ItemStack emptyDeleteItem(String homeName) {
      return namedItem(Material.GRAY_DYE, "&fɴᴏ ʜᴏᴍᴇ sᴇᴛ", List.of("&f- Click to save your location"), "none", homeName);
   }

   private static String coloredHomeName(String displayName) {
      return !displayName.contains("&") && !displayName.contains("§") ? "&b" + displayName : displayName;
   }

   private static ItemStack namedItem(Material material, String name, List<String> lore, String action, String homeName) {
      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(Texts.color(name));
      meta.setLore(lore.stream().map(Texts::color).toList());
      meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
      if (homeName != null) {
         meta.getPersistentDataContainer().set(HOME_KEY, PersistentDataType.STRING, homeName);
      }

      item.setItemMeta(meta);
      return item;
   }
}
