package de.elivb.hlsmpHome.home;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import de.elivb.hlsmpHome.util.Texts;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class HomeManager {
   public static final int MAX_HOMES = 5;
   private final JavaPlugin plugin;
   private final File file;
   private final Map<UUID, Map<String, Home>> homes = new LinkedHashMap<>();

   public HomeManager(JavaPlugin plugin) {
      this.plugin = plugin;
      this.file = new File(plugin.getDataFolder(), "homes.yml");
      this.load();
   }

   public void setHome(UUID owner, String rawName, Location location) {
      this.setHome(owner, rawName, rawName, location);
   }

   public Optional<String> setHomeInFirstFreeSlot(UUID owner, String displayName, Location location) {
      for (int slot = 1; slot <= 5; ++slot) {
         String slotName = this.slotName(slot);
         if (this.findHome(owner, slotName).isEmpty()) {
            this.setHome(owner, slotName, displayName, location);
            return Optional.of(slotName);
         }
      }
      return Optional.empty();
   }

   public void setHome(UUID owner, String slotName, String displayName, Location location) {
      this.homes.computeIfAbsent(owner, (ignored) -> new LinkedHashMap<>()).put(this.key(slotName), new Home(slotName, displayName, location.clone()));
      this.save();
   }

   public boolean deleteHome(UUID owner, String name) {
      Map<String, Home> playerHomes = this.homes.get(owner);
      if (playerHomes == null) {
         return false;
      }
      Optional<Home> home = this.findHome(owner, name);
      if (home.isEmpty()) {
         return false;
      }
      playerHomes.remove(this.key(home.get().slotName()));
      this.save();
      return true;
   }

   public Optional<Home> findHome(UUID owner, String name) {
      Map<String, Home> playerHomes = this.homes.get(owner);
      if (playerHomes == null) {
         return Optional.empty();
      }
      String directKey = this.key(name);
      Home direct = playerHomes.get(directKey);
      if (direct != null) {
         return Optional.of(direct);
      }
      String normalizedInput = this.normalizeName(name);
      return playerHomes.values().stream()
            .filter((home) -> this.normalizeName(home.slotName()).equals(normalizedInput)
                  || this.normalizeName(home.displayName()).equals(normalizedInput))
            .findFirst();
   }

   public Collection<Home> getHomes(UUID owner) {
      return this.homes.getOrDefault(owner, Map.of()).values().stream()
            .sorted(Comparator.comparing((home) -> Texts.stripColors(home.slotName()).toLowerCase(Locale.ROOT)))
            .toList();
   }

   public ArrayList<String> getRawHomeNames(UUID owner) {
      ArrayList<String> names = new ArrayList<>();
      for (Home home : this.getHomes(owner)) {
         names.add(home.displayName());
      }
      return names;
   }

   public String slotName(int number) {
      return "Home " + number;
   }

   public void load() {
      this.homes.clear();
      if (this.file.exists()) {
         FileConfiguration config = YamlConfiguration.loadConfiguration(this.file);
         ConfigurationSection root = config.getConfigurationSection("homes");
         if (root != null) {
            for (String uuidText : root.getKeys(false)) {
               UUID owner = UUID.fromString(uuidText);
               ConfigurationSection playerSection = root.getConfigurationSection(uuidText);
               if (playerSection != null) {
                  Map<String, Home> playerHomes = new LinkedHashMap<>();
                  for (String homeKey : playerSection.getKeys(false)) {
                     ConfigurationSection section = playerSection.getConfigurationSection(homeKey);
                     if (section != null) {
                        String worldName = section.getString("world");
                        World world = worldName == null ? null : Bukkit.getWorld(worldName);
                        if (world == null) {
                           this.plugin.getLogger().warning("Skipping home with missing world: " + uuidText + "/" + homeKey);
                        } else {
                           Location location = new Location(world,
                                 section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                                 (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
                           String slotName = section.getString("slot", this.decodeKey(homeKey));
                           String displayName = section.getString("name", slotName);
                           playerHomes.put(this.key(slotName), new Home(slotName, displayName, location));
                        }
                     }
                  }
                  this.homes.put(owner, playerHomes);
               }
            }
         }
      }
   }

   public void save() {
      if (!this.plugin.getDataFolder().exists()) {
         this.plugin.getDataFolder().mkdirs();
      }
      FileConfiguration config = new YamlConfiguration();
      for (Map.Entry<UUID, Map<String, Home>> playerEntry : this.homes.entrySet()) {
         String basePath = "homes." + playerEntry.getKey();
         for (Map.Entry<String, Home> homeEntry : playerEntry.getValue().entrySet()) {
            Home home = homeEntry.getValue();
            Location location = home.location();
            String path = basePath + "." + homeEntry.getKey();
            config.set(path + ".slot", home.slotName());
            config.set(path + ".name", home.displayName());
            config.set(path + ".world", location.getWorld().getName());
            config.set(path + ".x", location.getX());
            config.set(path + ".y", location.getY());
            config.set(path + ".z", location.getZ());
            config.set(path + ".yaw", location.getYaw());
            config.set(path + ".pitch", location.getPitch());
         }
      }
      try {
         config.save(this.file);
      } catch (IOException exception) {
         this.plugin.getLogger().severe("Could not save homes.yml: " + exception.getMessage());
      }
   }

   private String normalizeName(String name) {
      return Texts.stripColors(name).toLowerCase(Locale.ROOT);
   }

   private String key(String name) {
      return Base64.getUrlEncoder().withoutPadding().encodeToString(name.getBytes(StandardCharsets.UTF_8));
   }

   private String decodeKey(String key) {
      return new String(Base64.getUrlDecoder().decode(key), StandardCharsets.UTF_8);
   }
}
