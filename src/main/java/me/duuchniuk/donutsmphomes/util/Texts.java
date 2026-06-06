package me.duuchniuk.donutsmphomes.util;

import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public final class Texts {
   private static final Pattern LEGACY_COLOR = Pattern.compile("(?i)[&§][0-9A-FK-ORX]");
   private static final String PREFIX = "&3&lNHÀ &8» &7";

   private Texts() {
   }

   public static String color(String text) {
      return ChatColor.translateAlternateColorCodes('&', text);
   }

   public static String stripColors(String text) {
      return ChatColor.stripColor(color(text)).replaceAll("(?i)&x(&[0-9A-F]){6}", "").replaceAll(LEGACY_COLOR.pattern(), "");
   }

   public static void send(Player player, String message) {
      player.sendMessage(color("&3&lNHÀ &8» &7" + message));
   }

   public static void sendRaw(Player player, String message) {
      player.sendMessage(color(message));
   }
}
