package com.brunomatheus.portfolio.repositories;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>,
        JpaSpecificationExecutor<ProjectEntity> {

    List<ProjectEntity> findByStatus(ProjectStatus status);
}