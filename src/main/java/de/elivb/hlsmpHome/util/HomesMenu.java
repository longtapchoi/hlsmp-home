package de.elivb.hlsmpHome.util;

import de.elivb.hlsmpHome.home.Home;
import de.elivb.hlsmpHome.home.HomeManager;
import de.elivb.hlsmpTeams.Manager.DataManager;
import de.elivb.hlsmpTeams.Team;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class HomesMenu {
   public static final String TITLE = Texts.color("&8NHÀ CỦA BẠN");
   public static final NamespacedKey ACTION_KEY = new NamespacedKey("donutsmp_homes", "action");
   public static final NamespacedKey HOME_KEY = new NamespacedKey("donutsmp_homes", "home");
   private static final int[] HOME_SLOTS = new int[]{12, 13, 14, 15, 16};
   private static final int[] DELETE_SLOTS = new int[]{21, 22, 23, 24, 25};

   private HomesMenu() {}

   public static Inventory create(HomeManager homeManager, Player player) {
      Inventory inventory = Bukkit.createInventory(player, 36, TITLE);

      // Slot 10 — team home teleport
      inventory.setItem(10, teamHomeItem());

      // Slot 19 — dynamic team home status
      inventory.setItem(19, teamHomeStatusItem(player));

      for (int index = 0; index < HOME_SLOTS.length; ++index) {
         int number = index + 1;
         String slotHomeName = homeManager.slotName(number);
         Home home = homeManager.findHome(player.getUniqueId(), slotHomeName).orElse(null);
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

   // Slot 10 — luôn hiện, click để tele team home
   private static ItemStack teamHomeItem() {
      return namedItem(
         Material.WHITE_BANNER,
         "&bɴʜà ɴʜóᴍ",
         List.of("&fNhấp để dịch chuyển đến Nhà Nhóm"),
         "team-home", null
      );
   }

   // Slot 19 — dynamic theo trạng thái team
   private static ItemStack teamHomeStatusItem(Player player) {
      Team teamsPlugin = getTeamsPlugin();

      // HLSMP-Teams không load
      if (teamsPlugin == null) {
         return namedItem(
            Material.RED_STAINED_GLASS_PANE,
            "&cBạn chưa gia nhập Team",
            List.of("&7Plugin HLSMP-Teams chưa được bật"),
            "none", null
         );
      }

      DataManager team = teamsPlugin.getTeamManager().getPlayerTeam(player.getName());

      // Không có team
      if (team == null) {
         return namedItem(
            Material.RED_STAINED_GLASS_PANE,
            "&cBạn chưa gia nhập Team",
            List.of(),
            "none", null
         );
      }

      String teamName = team.getName();

      // Có team nhưng chưa có home
      if (!team.hasHome()) {
         return namedItem(
            Material.RED_STAINED_GLASS_PANE,
            "&c" + teamName + " chưa đặt Team Home",
            List.of(),
            "none", null
         );
      }

      // Có team và có home
      return namedItem(
         Material.GREEN_STAINED_GLASS_PANE,
         "&aClick để tele đến " + teamName + " Home",
         List.of(),
         "team-home", null
      );
   }

   private static Team getTeamsPlugin() {
      Plugin plugin = Bukkit.getPluginManager().getPlugin("HLSMP-Teams");
      if (plugin instanceof Team teamsPlugin && plugin.isEnabled()) {
         return teamsPlugin;
      }
      return null;
   }

   private static ItemStack homeItem(Home home, String homeName) {
      return namedItem(Material.BLUE_BED, coloredHomeName(home.displayName()), List.of("&fClick để dịch chuyển đến nhà"), "teleport", homeName);
   }

   private static ItemStack deleteItem(Home home, String homeName) {
      return namedItem(Material.BLUE_DYE, coloredHomeName(home.displayName()), List.of("&fClick để xóa nhà"), "delete", homeName);
   }

   private static ItemStack emptyHomeItem(String homeName) {
      return namedItem(Material.LIGHT_GRAY_BED, "&fᴄʜưᴀ ᴄó ɴʜà", List.of("&f- Click để lưu vị trí hiện tại"), "set-home", homeName);
   }

   private static ItemStack emptyDeleteItem(String homeName) {
      return namedItem(Material.GRAY_DYE, "&fᴄʜưᴀ ᴄó ɴʜà", List.of("&f- Click để lưu vị trí hiện tại"), "none", homeName);
   }

   private static String coloredHomeName(String displayName) {
      return !displayName.contains("&") && !displayName.contains("§") ? "&b" + displayName : displayName;
   }

   private static ItemStack namedItem(Material material, String name, List<String> lore, String action, String homeName) {
      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(Texts.color(name));
      meta.setLore(lore.stream().map(Texts::color).toList());
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
      if (homeName != null) {
         meta.getPersistentDataContainer().set(HOME_KEY, PersistentDataType.STRING, homeName);
      }
      item.setItemMeta(meta);
      return item;
   }
}
