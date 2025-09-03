package io.github.tavstaldev.bedWarsQuests.models;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class PlayerMatchStats {
    public int Kills = 0;
    public int Deaths = 0;
    public int BedsDestroyed = 0;
    public int FinalKills = 0;
    public int KillStreak = 0;
    public int ItemsBought = 0;
    public Map<String, Integer> BlocksPlaced = new HashMap<>();
    public Map<String, Integer> BlocksBroken = new HashMap<>();

    public void addBlockPlaced(Material material, int amount) {
        BlocksPlaced.put(material.name(), BlocksPlaced.getOrDefault(material.name(), 0) + amount);
    }

    public void addBlockBroken(Material material, int amount) {
        BlocksBroken.put(material.name(), BlocksBroken.getOrDefault(material.name(), 0) + amount);
    }

    public int getBlocksPlaced(String material) {
        return BlocksPlaced.getOrDefault(material, 0);
    }

    public int getBlocksBroken(String material) {
        return BlocksBroken.getOrDefault(material, 0);
    }

    public void reset() {
        Kills = 0;
        Deaths = 0;
        BedsDestroyed = 0;
        FinalKills = 0;
        KillStreak = 0;
        ItemsBought = 0;
        BlocksPlaced.clear();
        BlocksBroken.clear();
    }
}
