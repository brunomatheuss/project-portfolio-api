package com.brunomatheus.portfolio.specifications;

import com.brunomatheus.portfolio.dtos.filter.ProjectFilter;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.enums.ProjectStatus;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectSpecificationTest {

    @Test
    void shouldHandleNullFilter() {
        Specification<ProjectEntity> spec = ProjectSpecification.withFilter(null);

        Root<ProjectEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        when(cb.and(any())).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).and(any());
    }

    @Test
    void shouldApplyStatusFilter() {
        ProjectFilter filter = new ProjectFilter();
        filter.setStatus(ProjectStatus.IN_ANALYSIS);

        Specification<ProjectEntity> spec = ProjectSpecification.withFilter(filter);

        Root<ProjectEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("status")).thenReturn(path);
        when(cb.equal(path, ProjectStatus.IN_ANALYSIS)).thenReturn(predicate);
        when(cb.and(any())).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(path, ProjectStatus.IN_ANALYSIS);
    }

    @Test
    void shouldApplyManagerIdFilter() {
        ProjectFilter filter = new ProjectFilter();
        filter.setManagerId(1L);

        Specification<ProjectEntity> spec = ProjectSpecification.withFilter(filter);

        Root<ProjectEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("managerId")).thenReturn(path);
        when(cb.equal(path, 1L)).thenReturn(predicate);
        when(cb.and(any())).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(path, 1L);
    }

    @Test
    void shouldApplyStartDateFromFilter() {
        ProjectFilter filter = new ProjectFilter();
        filter.setStartDateFrom(LocalDate.of(2026, 1, 1));

        Specification<ProjectEntity> spec = ProjectSpecification.withFilter(filter);

        Root<ProjectEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Comparable> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("startDate")).thenReturn(path);
        when(cb.greaterThanOrEqualTo(path, filter.getStartDateFrom())).thenReturn(predicate);
        when(cb.and(any())).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).greaterThanOrEqualTo(path, filter.getStartDateFrom());
    }

    @Test
    void shouldApplyStartDateToFilter() {
        ProjectFilter filter = new ProjectFilter();
        filter.setStartDateTo(LocalDate.of(2026, 12, 31));

        Specification<ProjectEntity> spec = ProjectSpecification.withFilter(filter);

        Root<ProjectEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Comparable> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("startDate")).thenReturn(path);
        when(cb.lessThanOrEqualTo(path, filter.getStartDateTo())).thenReturn(predicate);
        when(cb.and(any())).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).lessThanOrEqualTo(path, filter.getStartDateTo());
    }

    @Test
    void shouldApplyAllFiltersTogether() {
        ProjectFilter filter = new ProjectFilter();
        filter.setStatus(ProjectStatus.IN_ANALYSIS);
        filter.setManagerId(1L);
        filter.setStartDateFrom(LocalDate.of(2026, 1, 1));
        filter.setStartDateTo(LocalDate.of(2026, 12, 31));

        Specification<ProjectEntity> spec = ProjectSpecification.withFilter(filter);

        Root<ProjectEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> statusPath = mock(Path.class);
        Path<Object> managerPath = mock(Path.class);
        Path<Comparable> datePath = mock(Path.class);

        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        Predicate p3 = mock(Predicate.class);
        Predicate p4 = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(root.get("status")).thenReturn(statusPath);
        when(root.get("managerId")).thenReturn(managerPath);
        when(root.get("startDate")).thenReturn(datePath);

        when(cb.equal(statusPath, filter.getStatus())).thenReturn(p1);
        when(cb.equal(managerPath, filter.getManagerId())).thenReturn(p2);
        when(cb.greaterThanOrEqualTo(datePath, filter.getStartDateFrom())).thenReturn(p3);
        when(cb.lessThanOrEqualTo(datePath, filter.getStartDateTo())).thenReturn(p4);

        when(cb.and(any())).thenReturn(finalPredicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).equal(statusPath, filter.getStatus());
        verify(cb).equal(managerPath, filter.getManagerId());
        verify(cb).greaterThanOrEqualTo(datePath, filter.getStartDateFrom());
        verify(cb).lessThanOrEqualTo(datePath, filter.getStartDateTo());
    }
}