package com.yue.route.register.entity;

import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Validated
public class PredicateDefinition {
    private String name;

    private Map<String, String> args = new LinkedHashMap<>();

    public PredicateDefinition() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public void addArg(String key, String value) {
        this.args.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PredicateDefinition that = (PredicateDefinition) o;
        return Objects.equals(name, that.name) && Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PredicateDefinition{");
        sb.append("name='").append(name).append('\'');
        sb.append(", args=").append(args);
        sb.append('}');
        return sb.toString();
    }
}
