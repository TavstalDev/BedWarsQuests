package io.github.tavstaldev.bedWarsQuests.models;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * The AchievementCriteria class represents the criteria that must be met to complete an achievement.
 * It provides methods to evaluate whether the criteria are satisfied based on a player and an event.
 */
public abstract class AchievementCriteria {
    // The type of the criteria (e.g., event type or condition type).
    private final String type;

    // The operator used to evaluate the criteria (e.g., "==", "!=", ">", "<").
    private final String operator;

    // Indicates whether the criteria requires a single match to be satisfied.
    protected final boolean singleMatch;

    /**
     * Evaluates whether the criteria are satisfied based on the given player, event, and achievement status.
     *
     * @param player        The player for whom the criteria are being evaluated.
     * @param event         The event associated with the criteria.
     * @param isAchievement Indicates whether the evaluation is for an achievement.
     * @return True if the criteria are satisfied, false otherwise.
     */
    public abstract boolean isSatisfied(Player player, Event event, boolean isAchievement);

    /**
     * Constructs an AchievementCriteria instance with the specified type, operator, and single match flag.
     *
     * @param type        The type of the criteria.
     * @param operator    The operator used to evaluate the criteria.
     * @param singleMatch True if the criteria require a single match, false otherwise.
     */
    public AchievementCriteria(String type, String operator, boolean singleMatch) {
        this.type = type;
        this.operator = operator;
        this.singleMatch = singleMatch;
    }

    /**
     * Retrieves the operator as an EOperator enum value based on the operator string.
     * Logs a warning and defaults to EOperator.EQUALS if the operator is unknown.
     *
     * @return The corresponding EOperator enum value.
     */
    public EOperator getOperator() {
        switch (operator.toLowerCase()) {
            case "==":
            case "=":
            case "eq":
            case "equals":
                return EOperator.EQUALS;
            case "!=":
            case "<>":
            case "ne":
            case "not_equals":
                return EOperator.NOT_EQUALS;
            case ">":
            case "gt":
            case "greater_than":
            case "more_than":
                return EOperator.GREATER_THAN;
            case "<":
            case "lt":
            case "less_than":
            case "fewer_than":
                return EOperator.LESS_THAN;
            case ">=":
            case "gte":
            case "greater_than_or_equal":
            case "more_than_or_equal":
                return EOperator.GREATER_THAN_OR_EQUAL;
            case "<=":
            case "lte":
            case "less_than_or_equal":
            case "fewer_than_or_equal":
                return EOperator.LESS_THAN_OR_EQUAL;
            default: {
                BedWarsQuests.Logger().warn("Unknown operator: " + operator + ", defaulting to EQUALS");
                return EOperator.EQUALS;
            }
        }
    }
}
