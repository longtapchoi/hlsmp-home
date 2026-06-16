package de.elivb.hlsmpHome.command;

import de.elivb.hlsmpHome.home.HomeManager;
import de.elivb.hlsmpHome.util.Texts;
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
            Texts.send(player, "&cCách dùng: /sethome <tên>");
            return true;
         } else if (this.homeManager.setHomeInFirstFreeSlot(player.getUniqueId(), args[0], player.getLocation()).isEmpty()) {
            Texts.send(player, "&cBạn đã đặt quá nhiều nhà.");
            return true;
         } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.75F, 1.6F);
            Texts.send(player, "Đã lưu nhà &f" + args[0] + "&7.");
            return true;
         }
      } else {
         sender.sendMessage("Chỉ người chơi mới dùng được lệnh này.");
         return true;
      }
   }
}
