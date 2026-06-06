package me.duuchniuk.donutsmphomes.command;

import me.duuchniuk.donutsmphomes.home.HomeManager;
import me.duuchniuk.donutsmphomes.util.Texts;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SetHomeCommand implements CommandExecutor {
   private final HomeManager homeManager;

   public SetHomeCommand(HomeManager homeManager) {
      this.homeManager = homeManager;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (args.length != 1) {
            Texts.send(player, "&cUsage: /sethome <nazwa>");
            return true;
         } else if (this.homeManager.setHomeInFirstFreeSlot(player.getUniqueId(), args[0], player.getLocation()).isEmpty()) {
            Texts.send(player, "&cYou have too many homes.");
            return true;
         } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.75F, 1.6F);
            Texts.send(player, "Saved home &f" + args[0] + "&7.");
            return true;
         }
      } else {
         sender.sendMessage("Only players can use this command.");
         return true;
      }
   }
}
