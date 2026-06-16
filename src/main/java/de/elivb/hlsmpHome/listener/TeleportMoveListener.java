package de.elivb.hlsmpHome.listener;

import de.elivb.hlsmpHome.teleport.TeleportManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TeleportMoveListener implements Listener {
   private final TeleportManager teleportManager;

   public TeleportMoveListener(TeleportManager teleportManager) {
      this.teleportManager = teleportManager;
   }

   @EventHandler
   public void onMove(PlayerMoveEvent event) {
      this.teleportManager.handleMove(event.getPlayer(), event.getTo());
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.teleportManager.cancel(event.getPlayer());
   }
}
