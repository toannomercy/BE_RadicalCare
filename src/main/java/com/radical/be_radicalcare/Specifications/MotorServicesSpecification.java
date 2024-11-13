package com.radical.be_radicalcare.Specifications;

import com.radical.be_radicalcare.Entities.MotorService;
import org.springframework.data.jpa.domain.Specification;

public class MotorServicesSpecification {

    public static Specification<MotorService> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
        };
    }
}
