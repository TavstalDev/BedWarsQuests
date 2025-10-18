package io.github.tavstaldev.bedWarsQuests.models;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerMatchStats class represents the statistics of a player during a match.
 * It tracks various metrics such as kills, deaths, beds destroyed, and block interactions.
 */
public class PlayerMatchStats {
    /** The number of kills the player has achieved. */
    public int Kills = 0;

    /** The number of times the player has died. */
    public int Deaths = 0;

    /** The number of beds the player has destroyed. */
    public int BedsDestroyed = 0;

    /** The number of final kills the player has achieved. */
    public int FinalKills = 0;

    /** The player's current kill streak. */
    public int KillStreak = 0;

    /** The number of items the player has bought. */
    public int ItemsBought = 0;

    /** A map of block types to the number of blocks placed by the player. */
    public Map<String, Integer> BlocksPlaced = new HashMap<>();

    /** A map of block types to the number of blocks broken by the player. */
    public Map<String, Integer> BlocksBroken = new HashMap<>();

    /**
     * Adds the specified amount of blocks placed by the player for the given material.
     *
     * @param material The type of block placed.
     * @param amount   The number of blocks placed.
     */
    public void addBlockPlaced(Material material, int amount) {
        BlocksPlaced.put(material.name(), BlocksPlaced.getOrDefault(material.name(), 0) + amount);
    }

    /**
     * Adds the specified amount of blocks broken by the player for the given material.
     *
     * @param material The type of block broken.
     * @param amount   The number of blocks broken.
     */
    public void addBlockBroken(Material material, int amount) {
        BlocksBroken.put(material.name(), BlocksBroken.getOrDefault(material.name(), 0) + amount);
    }

    /**
     * Retrieves the total number of blocks placed by the player for a specific material.
     * If "any" is specified, the total count of all blocks placed is returned.
     *
     * @param material The type of block or "any" for all blocks.
     * @return The number of blocks placed.
     */
    public int getBlocksPlaced(String material) {
        if (material.equalsIgnoreCase("any")) {
            return BlocksPlaced.values().stream().mapToInt(Integer::intValue).sum();
        }
        return BlocksPlaced.getOrDefault(material, 0);
    }

    /**
     * Retrieves the total number of blocks broken by the player for a specific material.
     * If "any" is specified, the total count of all blocks broken is returned.
     *
     * @param material The type of block or "any" for all blocks.
     * @return The number of blocks broken.
     */
    public int getBlocksBroken(String material) {
        if (material.equalsIgnoreCase("any")) {
            return BlocksBroken.values().stream().mapToInt(Integer::intValue).sum();
        }
        return BlocksBroken.getOrDefault(material, 0);
    }

    /**
     * Resets all match statistics for the player to their default values.
     */
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