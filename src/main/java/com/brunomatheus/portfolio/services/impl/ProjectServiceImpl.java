package com.brunomatheus.portfolio.services.impl;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.enums.RiskLevel;
import com.brunomatheus.portfolio.dtos.filter.ProjectFilter;
import com.brunomatheus.portfolio.dtos.request.CreateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectStatusRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.exceptions.BusinessException;
import com.brunomatheus.portfolio.exceptions.NotFoundException;
import com.brunomatheus.portfolio.mappers.ProjectMapper;
import com.brunomatheus.portfolio.repositories.ProjectRepository;
import com.brunomatheus.portfolio.services.ProjectService;
import com.brunomatheus.portfolio.specifications.ProjectSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDTO create(CreateProjectRequestDTO request) {
        ProjectEntity projectEntity = projectMapper.toEntity(request);

        projectEntity.setStatus(ProjectStatus.IN_ANALYSIS);

        projectRepository.save(projectEntity);

        return projectMapper.toResponse(projectEntity, calculateRisk(projectEntity));
    }

    @Override
    public ProjectResponseDTO findById(Long id) {
        ProjectEntity projectEntity = findProjectById(id);
        return projectMapper.toResponse(projectEntity, calculateRisk(projectEntity));
    }

    @Override
    public Page<ProjectResponseDTO> findAll(ProjectFilter filter, Pageable pageable) {
        Page<ProjectEntity> page = projectRepository.findAll(
                ProjectSpecification.withFilter(filter),
                pageable
        );

        return page.map(project -> projectMapper.toResponse(project, calculateRisk(project)));
    }

    @Override
    public ProjectResponseDTO update(Long id, UpdateProjectRequestDTO request) {
        ProjectEntity projectEntity = findProjectById(id);

        projectMapper.updateEntity(projectEntity, request);

        projectRepository.save(projectEntity);

        return projectMapper.toResponse(projectEntity, calculateRisk(projectEntity));
    }

    @Override
    public ProjectResponseDTO updateStatus(Long id, UpdateProjectStatusRequestDTO request) {
        ProjectEntity projectEntity = findProjectById(id);

        validateStatusTransition(projectEntity.getStatus(), request.getStatus());

        projectEntity.setStatus(request.getStatus());

        projectRepository.save(projectEntity);

        return projectMapper.toResponse(projectEntity, calculateRisk(projectEntity));
    }

    @Override
    public void delete(Long id) {
        ProjectEntity projectEntity = findProjectById(id);

        validateDeletionAllowed(projectEntity);

        projectRepository.delete(projectEntity);
    }

    private ProjectEntity findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private void validateDeletionAllowed(ProjectEntity projectEntity) {
        if (projectEntity.getStatus() == ProjectStatus.INITIATED ||
                projectEntity.getStatus() == ProjectStatus.IN_PROGRESS ||
                projectEntity.getStatus() == ProjectStatus.CLOSED) {
            throw new BusinessException("Project cannot be deleted in current status");
        }
    }

    private void validateStatusTransition(ProjectStatus current, ProjectStatus next) {

        if (next == ProjectStatus.CANCELED) return;

        List<ProjectStatus> flow = List.of(
                ProjectStatus.IN_ANALYSIS,
                ProjectStatus.ANALYSIS_COMPLETED,
                ProjectStatus.ANALYSIS_APPROVED,
                ProjectStatus.INITIATED,
                ProjectStatus.PLANNED,
                ProjectStatus.IN_PROGRESS,
                ProjectStatus.CLOSED
        );

        int currentIndex = flow.indexOf(current);
        int nextIndex = flow.indexOf(next);

        if (nextIndex != currentIndex + 1) {
            throw new BusinessException("Invalid status transition");
        }
    }

    private RiskLevel calculateRisk(ProjectEntity projectEntity) {

        BigDecimal budget = projectEntity.getBudget();
        long months = ChronoUnit.MONTHS.between(
                projectEntity.getStartDate(),
                projectEntity.getExpectedEndDate()
        );

        if (budget.compareTo(new BigDecimal("100000")) <= 0 && months <= 3) {
            return RiskLevel.LOW;
        }

        if ((budget.compareTo(new BigDecimal("100000")) > 0 &&
                budget.compareTo(new BigDecimal("500000")) <= 0)
                || (months > 3 && months <= 6)) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.HIGH;
    }
}