package me.duuchniuk.donutsmphomes.teleport;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.duuchniuk.donutsmphomes.home.Home;
import me.duuchniuk.donutsmphomes.util.Texts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class TeleportManager {
   private static final int WARMUP_SECONDS = 5;
   private final JavaPlugin plugin;
   private final Map<UUID, PendingTeleport> pendingTeleports = new HashMap();

   public TeleportManager(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public void startTeleport(final Player player, final Home home) {
      this.cancel(player);
      Location start = player.getLocation().clone();
      PendingTeleport pending = new PendingTeleport(start, (BukkitTask)null);
      this.pendingTeleports.put(player.getUniqueId(), pending);
      BukkitTask task = (new BukkitRunnable() {
         private int secondsLeft;

         {
            Objects.requireNonNull(TeleportManager.this);
            this.secondsLeft = 5;
         }

         public void run() {
            if (player.isOnline() && TeleportManager.this.pendingTeleports.containsKey(player.getUniqueId())) {
               if (this.secondsLeft <= 0) {
                  TeleportManager.this.pendingTeleports.remove(player.getUniqueId());
                  player.teleport(home.location());
                  player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8F, 1.7F);
                  Texts.sendRaw(player, "&7You teleported to your home");
                  this.cancel();
               } else {
                  TeleportManager.this.sendCountdown(player, this.secondsLeft);
                  player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.55F, 1.1F + (float)(5 - this.secondsLeft) * 0.12F);
                  --this.secondsLeft;
               }
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(this.plugin, 0L, 20L);
      this.pendingTeleports.put(player.getUniqueId(), new PendingTeleport(start, task));
   }

   public void handleMove(Player player, Location to) {
      PendingTeleport pending = (PendingTeleport)this.pendingTeleports.get(player.getUniqueId());
      if (pending != null && to != null) {
         Location from = pending.startLocation();
         if (!from.getWorld().equals(to.getWorld()) || from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            this.cancel(player);
            Texts.sendRaw(player, "&cTeleport cancelled because you moved!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.9F, 1.0F);
         }

      }
   }

   public void cancel(Player player) {
      PendingTeleport pending = (PendingTeleport)this.pendingTeleports.remove(player.getUniqueId());
      if (pending != null && pending.task() != null) {
         pending.task().cancel();
      }

   }

   public void cancelAll() {
      for(PendingTeleport pending : this.pendingTeleports.values()) {
         if (pending.task() != null) {
            pending.task().cancel();
         }
      }

      this.pendingTeleports.clear();
   }

   private void sendCountdown(Player player, int secondsLeft) {
      Component component = LegacyComponentSerializer.legacyAmpersand().deserialize("&7Teleporting in &b" + secondsLeft + "s");
      player.sendActionBar(component);
   }

   private static record PendingTeleport(Location startLocation, BukkitTask task) {
   }
}
