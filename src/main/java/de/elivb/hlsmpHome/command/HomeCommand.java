package de.elivb.hlsmpHome.command;

import de.elivb.hlsmpHome.home.Home;
import de.elivb.hlsmpHome.home.HomeManager;
import de.elivb.hlsmpHome.teleport.TeleportManager;
import de.elivb.hlsmpHome.util.HomesMenu;
import de.elivb.hlsmpHome.util.Texts;
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
