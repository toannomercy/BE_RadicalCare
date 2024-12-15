package com.radical.be_radicalcare.Specifications;

import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Services.CategoryService;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.criteria.Predicate;

public class VehicleSpecification {

    public static Specification<Vehicle> hasSegmentIn(List<String> segments) {
        return (root, query, criteriaBuilder) -> {
            if (segments == null || segments.isEmpty()) {
                return criteriaBuilder.conjunction(); // Không áp dụng điều kiện nếu segments là null hoặc rỗng
            }
            return root.get("segment").in(segments);
        };
    }

    public static Specification<Vehicle> hasColorIn(List<String> colors) {
        return (root, query, criteriaBuilder) -> {
            if (colors == null || colors.isEmpty()) {
                return criteriaBuilder.conjunction(); // Không áp dụng điều kiện nếu colors là null hoặc rỗng
            }
            return root.get("color").in(colors);
        };
    }

    public static Specification<Vehicle> isSold(Boolean sold) {
        return (root, query, criteriaBuilder) ->
                sold == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("sold"), sold);
    }

    public static Specification<Vehicle> hasCategoryIdIn(List<Integer> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("categoryId").get("categoryId").in(categoryIds);
        };
    }

    public static Specification<Vehicle> hasCostBetween(Double minCost, Double maxCost) {
        return (root, query, criteriaBuilder) -> {
            if (minCost != null && maxCost != null) {
                return criteriaBuilder.between(
                        root.get("costId").get("baseCost"), // Sử dụng costId để truy cập
                        minCost, maxCost
                );
            } else if (minCost != null) {
                return criteriaBuilder.greaterThanOrEqualTo(
                        root.get("costId").get("baseCost"), minCost
                );
            } else if (maxCost != null) {
                return criteriaBuilder.lessThanOrEqualTo(
                        root.get("costId").get("baseCost"), maxCost
                );
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }

    // Tìm kiếm theo cateId dành cho mục đích tìm kiếm thay vì filter
    public static Specification<Vehicle> hasKeyword(String keyword, CategoryService categoryService) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction(); // Không áp dụng điều kiện nếu keyword null
        }

        String[] keywords = keyword.toLowerCase().split(" ");
        return (root, query, criteriaBuilder) -> {
            // Lấy ID danh mục tương ứng với từ khóa
            List<Long> categoryIds = categoryService.getCategoryIdsByName(keyword);

            // Tạo các điều kiện cho các thuộc tính của Vehicle
            List<Predicate> predicates = Arrays.stream(keywords)
                    .flatMap(word -> Stream.of(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("vehicleName")), "%" + word + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), "%" + word + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("segment")), "%" + word + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("version")), "%" + word + "%"),
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.join("costId").get("baseCost").as(String.class)), // Tìm kiếm baseCost
                                    "%" + word + "%"
                            )
                    ))
                    .collect(Collectors.toList());

            // Nếu có danh sách ID danh mục, thêm điều kiện tìm kiếm theo danh mục
            if (!categoryIds.isEmpty()) {
                predicates.add(root.get("categoryId").get("id").in(categoryIds));
            }

            // Trả về các điều kiện kết hợp
            return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // Sử dụng 'or' để tìm kiếm
        };
    }
}
