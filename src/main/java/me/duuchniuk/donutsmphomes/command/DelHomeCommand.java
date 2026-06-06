package me.duuchniuk.donutsmphomes.command;

import me.duuchniuk.donutsmphomes.home.HomeManager;
import me.duuchniuk.donutsmphomes.util.Texts;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DelHomeCommand implements CommandExecutor {
   private final HomeManager homeManager;

   public DelHomeCommand(HomeManager homeManager) {
      this.homeManager = homeManager;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (args.length != 1) {
            Texts.send(player, "&cCách dùng: /delhome <tên>");
            return true;
         } else if (!this.homeManager.deleteHome(player.getUniqueId(), args[0])) {
            Texts.send(player, "&cKhông tìm thấy nhà.");
            return true;
         } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.75F, 0.9F);
            Texts.send(player, "Đã xóa nhà &f" + args[0] + "&7.");
            return true;
         }
      } else {
         sender.sendMessage("Chỉ người chơi mới dùng được lệnh này.");
         return true;
      }
   }
}
