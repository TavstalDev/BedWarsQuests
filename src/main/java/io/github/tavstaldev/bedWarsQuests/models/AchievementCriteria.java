package io.github.tavstaldev.bedWarsQuests.models;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class AchievementCriteria {
    private final String type;
    private final String operator;
    protected final boolean singleMatch;

    public abstract boolean isSatisfied(Player player, Event event, boolean isAchievement);

    public AchievementCriteria(String type, String operator, boolean singleMatch) {

        this.type = type;
        this.operator = operator;
        this.singleMatch = singleMatch;
    }

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
            default:
            {
                BedWarsQuests.Logger().Warn("Unknown operator: " + operator + ", defaulting to EQUALS");
                return EOperator.EQUALS;
            }
        }
    }
}