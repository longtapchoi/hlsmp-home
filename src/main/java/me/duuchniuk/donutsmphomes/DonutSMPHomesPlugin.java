package me.duuchniuk.donutsmphomes;

import me.duuchniuk.donutsmphomes.command.DelHomeCommand;
import me.duuchniuk.donutsmphomes.command.HomeCommand;
import me.duuchniuk.donutsmphomes.command.HomeNameTabCompleter;
import me.duuchniuk.donutsmphomes.command.SetHomeCommand;
import me.duuchniuk.donutsmphomes.home.HomeManager;
import me.duuchniuk.donutsmphomes.listener.HomesMenuListener;
import me.duuchniuk.donutsmphomes.listener.TeleportMoveListener;
import me.duuchniuk.donutsmphomes.teleport.TeleportManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class DonutSMPHomesPlugin extends JavaPlugin {
   private HomeManager homeManager;
   private TeleportManager teleportManager;

   public void onEnable() {
      this.homeManager = new HomeManager(this);
      this.teleportManager = new TeleportManager(this);
      this.registerCommands();
      this.getServer().getPluginManager().registerEvents(new TeleportMoveListener(this.teleportManager), this);
      this.getServer().getPluginManager().registerEvents(new HomesMenuListener(this.homeManager, this.teleportManager), this);
   }

   public void onDisable() {
      if (this.teleportManager != null) {
         this.teleportManager.cancelAll();
      }

      if (this.homeManager != null) {
         this.homeManager.save();
      }

   }

   private void registerCommands() {
      this.requireCommand("sethome").setExecutor(new SetHomeCommand(this.homeManager));
      PluginCommand home = this.requireCommand("home");
      home.setExecutor(new HomeCommand(this.homeManager, this.teleportManager));
      home.setTabCompleter(new HomeNameTabCompleter(this.homeManager));
      PluginCommand delHome = this.requireCommand("delhome");
      delHome.setExecutor(new DelHomeCommand(this.homeManager));
      delHome.setTabCompleter(new HomeNameTabCompleter(this.homeManager));
   }

   private PluginCommand requireCommand(String name) {
      PluginCommand command = this.getCommand(name);
      if (command == null) {
         throw new IllegalStateException("Missing command in plugin.yml: " + name);
      } else {
         return command;
      }
   }
}
