package de.elivb.hlsmpHome.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import de.elivb.hlsmpHome.home.HomeManager;
import de.elivb.hlsmpHome.util.Texts;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class HomeNameTabCompleter implements TabCompleter {
   private final HomeManager homeManager;

   public HomeNameTabCompleter(HomeManager homeManager) {
      this.homeManager = homeManager;
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (sender instanceof Player player) {
         if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            List<String> matches = new ArrayList();

            for(String name : this.homeManager.getRawHomeNames(player.getUniqueId())) {
               String plain = Texts.stripColors(name).toLowerCase(Locale.ROOT);
               if (name.toLowerCase(Locale.ROOT).startsWith(input) || plain.startsWith(input)) {
                  matches.add(name);
               }
            }

            return matches;
         }
      }

      return List.of();
   }
}
