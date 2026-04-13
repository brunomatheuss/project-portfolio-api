package com.brunomatheus.portfolio.repositories;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.entities.ProjectMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, Long> {

    int countByProjectId(Long projectId);

    List<ProjectMemberEntity> findByProjectId(Long projectId);

    Optional<ProjectMemberEntity> findByProjectIdAndMemberId(Long projectId, Long memberId);

    @Query("""
        SELECT COUNT(DISTINCT pm.projectId)
        FROM ProjectMemberEntity pm
        JOIN ProjectEntity p ON p.id = pm.projectId
        WHERE pm.memberId = :memberId
        AND p.status NOT IN :inactiveStatuses
    """)
    int countActiveProjectsByMemberId(
            @Param("memberId") Long memberId,
            @Param("inactiveStatuses") List<ProjectStatus> inactiveStatuses
    );

    @Query("""
        SELECT COUNT(DISTINCT pm.memberId)
        FROM ProjectMemberEntity pm
    """)
    Long countDistinctMemberIds();
}