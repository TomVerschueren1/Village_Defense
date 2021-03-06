/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense3.handlers.setup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.arena.Arena;
import pl.plajer.villagedefense3.arena.ArenaRegistry;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_10_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_11_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_12_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_13_R1;
import pl.plajer.villagedefense3.arena.initializers.ArenaInitializer1_9_R1;
import pl.plajer.villagedefense3.handlers.ChatManager;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;
import pl.plajer.villagedefense3.handlers.ShopManager;
import pl.plajer.villagedefense3.utils.Utils;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventoryEvents implements Listener {

  private Main plugin;

  public SetupInventoryEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (event.getWhoClicked().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getWhoClicked();
    if (!player.hasPermission("villagedefense.admin.create") || !event.getInventory().getName().contains("Arena:")
            || event.getInventory().getHolder() != null || event.getCurrentItem() == null
            || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
      return;
    }

    String name = event.getCurrentItem().getItemMeta().getDisplayName();
    name = ChatColor.stripColor(name);

    Arena arena = ArenaRegistry.getArena(event.getInventory().getName().replace("Arena: ", ""));
    if (event.getCurrentItem().getType() == Material.NAME_TAG && event.getCursor().getType() == Material.NAME_TAG) {
      event.setCancelled(true);
      if (!event.getCursor().hasItemMeta()) {
        player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
        return;
      }
      if (!event.getCursor().getItemMeta().hasDisplayName()) {
        player.sendMessage(ChatColor.RED + "This item doesn't has a name!");
        return;
      }

      player.performCommand("vd " + arena.getID() + " set MAPNAME " + event.getCursor().getItemMeta().getDisplayName());
      event.getCurrentItem().getItemMeta().setDisplayName(ChatColor.GOLD + "Set a mapname (currently: " + event.getCursor().getItemMeta().getDisplayName());
      return;
    }
    ClickType clickType = event.getClick();
    if (name.contains("ending location")) {
      event.setCancelled(true);
      player.closeInventory();
      player.performCommand("vd " + arena.getID() + " set ENDLOC");
      return;
    }
    if (name.contains("starting location")) {
      event.setCancelled(true);
      player.closeInventory();
      player.performCommand("vd " + arena.getID() + " set STARTLOC");
      return;
    }
    if (name.contains("lobby location")) {
      event.setCancelled(true);
      player.closeInventory();
      player.performCommand("vd " + arena.getID() + " set LOBBYLOC");
      return;
    }
    if (name.contains("maximum players")) {
      event.setCancelled(true);
      if (clickType.isRightClick()) {
        event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
        player.performCommand("vd " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
      }
      if (clickType.isLeftClick()) {
        event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
        player.performCommand("vd " + arena.getID() + " set MAXPLAYERS " + event.getCurrentItem().getAmount());
      }
      player.closeInventory();
      player.openInventory(new SetupInventory(arena).getInventory());
    }

    if (name.contains("minimum players")) {
      event.setCancelled(true);
      if (clickType.isRightClick()) {
        event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
        player.performCommand("vd " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
      }
      if (clickType.isLeftClick()) {
        event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
        player.performCommand("vd " + arena.getID() + " set MINPLAYERS " + event.getCurrentItem().getAmount());
      }
      player.closeInventory();
      player.openInventory(new SetupInventory(arena).getInventory());
    }
    if (name.contains("Add game sign")) {
      event.setCancelled(true);
      plugin.getMainCommand().getAdminCommands().addSign(player, arena.getID());
      return;
    }
    if (event.getCurrentItem().getType() != Material.NAME_TAG) {
      event.setCancelled(true);
    }
    if (name.contains("Add villager spawn")) {
      event.setCancelled(true);
      player.performCommand("vd " + arena.getID() + " addspawn villager");
      player.closeInventory();
      return;

    }
    if (name.contains("Add zombie spawn")) {
      event.setCancelled(true);
      player.performCommand("vd " + arena.getID() + " addspawn zombie");
      player.closeInventory();
    }
    if (name.contains("Add doors")) {
      event.setCancelled(true);
      player.performCommand("vd " + arena.getID() + " addspawn doors");
      player.closeInventory();
      return;

    }
    if (name.contains("Set chest shop")) {
      event.setCancelled(true);
      Block targetBlock;
      targetBlock = player.getTargetBlock(null, 100);
      if (targetBlock == null || targetBlock.getType() != Material.CHEST) {
        player.sendMessage(ChatColor.RED + "Look at the chest! You are targeting something else!");
        return;
      }
      boolean found = false;
      for (ItemStack stack : ((Chest) targetBlock.getState()).getBlockInventory()) {
        if (stack == null) continue;
        if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
          if (stack.getItemMeta().getLore().get(stack.getItemMeta().getLore().size() - 1).contains(ChatManager.colorMessage("In-Game.Messages.Shop-Messages.Currency-In-Shop"))) {
            found = true;
            break;
          }
        }
      }
      if (!found) {
        player.sendMessage(ChatColor.RED + "No items in shop have price set! Set their prices using /vda setprice! You can ignore this warning");
      }
      Utils.saveLoc("instances." + arena.getID() + ".shop", targetBlock.getLocation(), false);
      ShopManager.registerShop(arena);
      player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
    }
    if (name.contains("Register arena")) {
      event.setCancelled(true);
      event.getWhoClicked().closeInventory();
      if (ArenaRegistry.getArena(arena.getID()).isReady()) {
        event.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
        return;
      }
      String[] locations = new String[]{"lobbylocation", "Startlocation", "Endlocation"};
      String[] spawns = new String[]{"zombiespawns", "villagerspawns"};
      for (String s : locations) {
        if (!ConfigurationManager.getConfig("arenas").isSet("instances." + arena.getID() + "." + s) || ConfigurationManager.getConfig("arenas")
                .getString("instances." + arena.getID() + "." + s).equals(Utils.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
          event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure following spawn properly: " + s + " (cannot be world spawn location)");
          return;
        }
      }
      for (String s : spawns) {
        if (!ConfigurationManager.getConfig("arenas").isSet("instances." + arena.getID() + "." + s) || ConfigurationManager.getConfig("arenas")
                .getConfigurationSection("instances." + arena.getID() + "." + s).getKeys(false).size() < 2) {
          event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure following mob spawns properly: " + s + " (must be minimum 2 spawns)");
          return;
        }
      }
      if (ConfigurationManager.getConfig("arenas").getConfigurationSection("instances." + arena.getID() + ".doors") == null) {
        event.getWhoClicked().sendMessage(ChatColor.RED + "Arena validation failed! Please configure doors properly");
        return;
      }
      event.getWhoClicked().sendMessage(ChatColor.GREEN + "Validation succeeded! Registering new arena instance: " + arena.getID());
      FileConfiguration config = ConfigurationManager.getConfig("arenas");
      config.set("instances." + arena.getID() + ".isdone", true);
      ConfigurationManager.saveConfig(config, "arenas");
      List<Sign> signsToUpdate = new ArrayList<>();
      ArenaRegistry.unregisterArena(arena);
      if (plugin.getSignManager().getLoadedSigns().containsValue(arena)) {
        for (Sign s : plugin.getSignManager().getLoadedSigns().keySet()) {
          if (plugin.getSignManager().getLoadedSigns().get(s).equals(arena)) {
            signsToUpdate.add(s);
          }
        }
      }
      if (plugin.is1_9_R1()) {
        arena = new ArenaInitializer1_9_R1(arena.getID(), plugin);
      } else if (plugin.is1_10_R1()) {
        arena = new ArenaInitializer1_10_R1(arena.getID(), plugin);
      } else if (plugin.is1_11_R1()) {
        arena = new ArenaInitializer1_11_R1(arena.getID(), plugin);
      } else if (plugin.is1_12_R1()) {
        arena = new ArenaInitializer1_12_R1(arena.getID(), plugin);
      } else {
        arena = new ArenaInitializer1_13_R1(arena.getID(), plugin);
      }
      arena.setReady(true);
      arena.setMinimumPlayers(ConfigurationManager.getConfig("arenas").getInt("instances." + arena.getID() + ".minimumplayers"));
      arena.setMaximumPlayers(ConfigurationManager.getConfig("arenas").getInt("instances." + arena.getID() + ".maximumplayers"));
      arena.setMapName(ConfigurationManager.getConfig("arenas").getString("instances." + arena.getID() + ".mapname"));
      arena.setLobbyLocation(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString("instances." + arena.getID() + ".lobbylocation")));
      arena.setStartLocation(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString("instances." + arena.getID() + ".Startlocation")));
      arena.setEndLocation(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString("instances." + arena.getID() + ".Endlocation")));
      for (String string : ConfigurationManager.getConfig("arenas").getConfigurationSection("instances." + arena.getID() + ".zombiespawns").getKeys(false)) {
        String path = "instances." + arena.getID() + ".zombiespawns." + string;
        arena.addZombieSpawn(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path)));
      }
      for (String string : ConfigurationManager.getConfig("arenas").getConfigurationSection("instances." + arena.getID() + ".villagerspawns").getKeys(false)) {
        String path = "instances." + arena.getID() + ".villagerspawns." + string;
        arena.addVillagerSpawn(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path)));
      }
      for (String string : ConfigurationManager.getConfig("arenas").getConfigurationSection("instances." + arena.getID() + ".doors").getKeys(false)) {
        String path = "instances." + arena.getID() + ".doors." + string + ".";
        arena.addDoor(Utils.getLocation(false, ConfigurationManager.getConfig("arenas").getString(path + "location")),
                (byte) ConfigurationManager.getConfig("arenas").getInt(path + "byte"));
      }
      ArenaRegistry.registerArena(arena);
      arena.start();
      for (Sign s : signsToUpdate) {
        plugin.getSignManager().getLoadedSigns().put(s, arena);
      }
    }
  }
}
