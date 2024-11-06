package com.radical.be_radicalcare.Specifications;

import com.radical.be_radicalcare.Entities.Vehicle;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class VehicleSpecification {

    public static Specification<Vehicle> hasSegmentIn(List<String> segments) {
        return (root, query, criteriaBuilder) ->
                segments.isEmpty() ? criteriaBuilder.conjunction() : root.get("segment").in(segments);
    }

    public static Specification<Vehicle> hasColorIn(List<String> colors) {
        return (root, query, criteriaBuilder) ->
                colors.isEmpty() ? criteriaBuilder.conjunction() : root.get("color").in(colors);
    }

    public static Specification<Vehicle> isSold(Boolean sold) {
        return (root, query, criteriaBuilder) ->
                sold == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("sold"), sold);
    }

    public static Specification<Vehicle> hasCategoryIdIn(List<Integer> categoryIds) {
        return (root, query, criteriaBuilder) ->
                categoryIds.isEmpty() ? criteriaBuilder.conjunction() : root.get("categoryId").get("id").in(categoryIds);
    }

    public static Specification<Vehicle> hasCostBetween(Double minCost, Double maxCost) {
        return (root, query, criteriaBuilder) -> {
            if (minCost != null && maxCost != null) {
                return criteriaBuilder.between(root.get("costId").get("baseCost"), minCost, maxCost);
            } else if (minCost != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("costId").get("baseCost"), minCost);
            } else if (maxCost != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("costId").get("baseCost"), maxCost);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }
}
