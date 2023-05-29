package com.yue.route.register.entity;

import java.net.URI;
import java.util.*;

    public class RouteDefinition {
    private String id;

    private List<PredicateDefinition> predicates = new ArrayList<>();

    private List<FilterDefinition> filters = new ArrayList<>();

    private URI uri;

    private Map<String, Object> metadata = new HashMap<>();

    private int order = 0;

    public RouteDefinition() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PredicateDefinition> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<PredicateDefinition> predicates) {
        this.predicates = predicates;
    }

    public List<FilterDefinition> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDefinition> filters) {
        this.filters = filters;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteDefinition that = (RouteDefinition) o;
        return this.order == that.order && Objects.equals(this.id, that.id)
                && Objects.equals(this.predicates, that.predicates) && Objects.equals(this.filters, that.filters)
                && Objects.equals(this.uri, that.uri) && Objects.equals(this.metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.predicates, this.filters, this.uri, this.metadata, this.order);
    }

    @Override
    public String toString() {
        return "Route{" + "id='" + id + '\'' + ", predicates=" + predicates + ", filters=" + filters
                + ", uri=" + uri + ", order=" + order + ", metadata=" + metadata + '}';
    }
}
