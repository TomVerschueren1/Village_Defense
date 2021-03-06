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

package pl.plajer.villagedefense3.creatures;

import java.util.Queue;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.Utils;

/**
 * Created by Tom on 14/08/2014.
 */
public class BreakFenceListener extends BukkitRunnable {

  private Random random = new Random();
  private Main plugin = JavaPlugin.getPlugin(Main.class);

  @Override
  public void run() {
    for (World world : Bukkit.getServer().getWorlds()) {
      for (Entity entity : world.getEntities()) {
        if (!(entity.getType() == EntityType.ZOMBIE)) {
          continue;
        }
        Queue<Block> blocks = Utils.getLineOfSight((LivingEntity) entity, null, 1, 1);
        for (Block block : blocks) {
          if (block.getType() == Material.WOOD_DOOR || block.getType() == Material.WOODEN_DOOR /*|| block.getType() == Material.FENCE*/) {
            block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
            if (plugin.is1_9_R1() || plugin.is1_10_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
              block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, 5, 5);
            } else if (plugin.is1_13_R1()) {
              block.getWorld().playSound(block.getLocation(), Sound.valueOf("ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR"), 5F, 5F);
            }
            this.particleDoor(block);
            if (random.nextInt(20) == 5) {
              breakDoor(block);
              if (plugin.is1_9_R1() || plugin.is1_10_R1() || plugin.is1_11_R1() || plugin.is1_12_R1()) {
                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5, 5);
              } else if (plugin.is1_13_R1()) {
                block.getWorld().playSound(block.getLocation(), Sound.valueOf("ENTITY_ZOMBIE_BREAK_WOODEN_DOOR"), 5F, 5F);
              }
            }
          }
        }
      }
    }
  }

  private void particleDoor(org.bukkit.block.Block block) {
    for (BlockFace blockFace : BlockFace.values()) {
      if (block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR) {
        block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
      }
    }
  }

  private void breakDoor(org.bukkit.block.Block block) {
    for (BlockFace blockFace : BlockFace.values()) {
      if (block.getRelative(blockFace).getType() == Material.WOOD_DOOR || block.getRelative(blockFace).getType() == Material.WOODEN_DOOR) {
        block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10, 0.1, 0.1, 0.1, new MaterialData(Material.WOODEN_DOOR));
        block.setType(Material.AIR);
      }
    }
  }

}
