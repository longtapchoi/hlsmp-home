package me.duuchniuk.donutsmphomes.command;

import me.duuchniuk.donutsmphomes.home.Home;
import me.duuchniuk.donutsmphomes.home.HomeManager;
import me.duuchniuk.donutsmphomes.teleport.TeleportManager;
import me.duuchniuk.donutsmphomes.util.HomesMenu;
import me.duuchniuk.donutsmphomes.util.Texts;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class HomeCommand implements CommandExecutor {
   private final HomeManager homeManager;
   private final TeleportManager teleportManager;

   public HomeCommand(HomeManager homeManager, TeleportManager teleportManager) {
      this.homeManager = homeManager;
      this.teleportManager = teleportManager;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (args.length == 0) {
            player.openInventory(HomesMenu.create(this.homeManager, player));
            return true;
         } else if (args.length != 1) {
            Texts.send(player, "&cCách dùng: /home [tên]");
            return true;
         } else {
            Home home = this.homeManager.findHome(player.getUniqueId(), args[0]).orElse(null);
            if (home == null) {
               Texts.send(player, "&cKhông tìm thấy nhà.");
               return true;
            } else {
               this.teleportManager.startTeleport(player, home);
               return true;
            }
         }
      } else {
         sender.sendMessage("Chỉ người chơi mới dùng được lệnh này.");
         return true;
      }
   }
}
