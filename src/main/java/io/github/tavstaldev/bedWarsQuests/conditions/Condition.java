package io.github.tavstaldev.bedWarsQuests.conditions;

import java.util.Map;

public class Condition {
    private String event;
    private int count;
    private Map<String, Object> constraints;

    public Condition(String event, int count, Map<String, Object> constraints) {
        this.event = event;
        this.count = count;
        this.constraints = constraints;
    }

    public String getEvent() { return event; }
    public int getCount() { return count; }
    public Map<String, Object> getConstraints() { return constraints; }

    public boolean hasConstraint(String key) {
        return constraints != null && constraints.containsKey(key);
    }

    public Object getConstraint(String key) {
        return constraints != null ? constraints.get(key) : null;
    }
}

