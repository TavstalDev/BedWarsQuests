package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;

/**
 * The BlockBreakCriteria class represents a specific achievement criterion
 * that checks if a player has broken a certain number of blocks of a specific material
 * in a match or across all matches, depending on the configuration.
 */
public class BlockBreakCriteria extends AchievementCriteria {
    // The number of blocks that need to be broken to satisfy the criterion.
    private final int count;

    // The material type of the blocks that need to be broken.
    private final String material;

    /**
     * Constructs a BlockBreakCriteria instance with the specified parameters.
     *
     * @param operator      The comparison operator to use (e.g., EQUALS, GREATER_THAN).
     * @param isSingleMatch Whether the criterion applies to a single match or all matches.
     * @param count         The number of blocks required to satisfy the criterion.
     * @param material      The material type of the blocks to be broken.
     */
    public BlockBreakCriteria(String operator, boolean isSingleMatch, int count, String material) {
        super("block_break", operator, isSingleMatch);
        this.count = count;
        this.material = material;
    }

    /**
     * Checks if the criterion is satisfied for the given player and event.
     *
     * @param player        The player whose progress is being checked.
     * @param event         The event that triggered the check.
     * @param isAchievement Whether the check is for an achievement or a daily objective.
     * @return True if the criterion is satisfied, false otherwise.
     */
    @Override
    public boolean isSatisfied(org.bukkit.entity.Player player, org.bukkit.event.Event event, boolean isAchievement) {
        // Retrieve the number of blocks broken of the specified material by the player.
        int value = PlayerCacheManager.get(player.getUniqueId()).getMatchStats().getBlocksBroken(material);

        if (!this.singleMatch) {
            BedWarsQuests.Logger().warn("BlockBreakCriteria is only supported for single match achievements/objectives.");
        }

        // Compare the retrieved value with the required count using the specified operator.
        return switch (getOperator()) {
            case EQUALS -> value == count;
            case NOT_EQUALS -> value != count;
            case GREATER_THAN -> value > count;
            case LESS_THAN -> value < count;
            case GREATER_THAN_OR_EQUAL -> value >= count;
            case LESS_THAN_OR_EQUAL -> value <= count;
        };
    }
}
