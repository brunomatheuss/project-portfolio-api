package com.brunomatheus.portfolio.specifications;

import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.dtos.filter.ProjectFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<ProjectEntity> withFilter(ProjectFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getManagerId() != null) {
                predicates.add(cb.equal(root.get("managerId"), filter.getManagerId()));
            }

            if (filter.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("startDate"),
                        filter.getStartDateFrom()
                ));
            }

            if (filter.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("startDate"),
                        filter.getStartDateTo()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}