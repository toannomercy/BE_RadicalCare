package com.radical.be_radicalcare.Specifications;

import com.radical.be_radicalcare.Entities.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecification {

    public static Specification<Vehicle> hasSegment(String segment) {
        return (root, query, criteriaBuilder) -> {
            return segment == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("segment"), segment);
        };
    }

    public static Specification<Vehicle> hasColor(String color) {
        return (root, query, criteriaBuilder) -> {
            return color == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("color"), color);
        };
    }

    public static Specification<Vehicle> isSold(Boolean sold) {
        return (root, query, criteriaBuilder) -> {
            return sold == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("sold"), sold);
        };
    }

    public static Specification<Vehicle> hasCategoryId(Integer categoryId) {
        return (root, query, criteriaBuilder) -> {
            return categoryId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("categoryId"), categoryId);
        };
    }

    public static Specification<Vehicle> hasMinCost(Double minCost) {
        return (root, query, criteriaBuilder) -> {
            return minCost == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("cost").get("baseCost"), minCost);
        };
    }

    public static Specification<Vehicle> hasMaxCost(Double maxCost) {
        return (root, query, criteriaBuilder) -> {
            return maxCost == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("cost").get("baseCost"), maxCost);
        };
    }
}
