package com.brunomatheus.portfolio.repositories;

import com.brunomatheus.portfolio.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>,
        JpaSpecificationExecutor<ProjectEntity> {

}