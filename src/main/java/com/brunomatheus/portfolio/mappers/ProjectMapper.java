package com.brunomatheus.portfolio.mappers;

import com.brunomatheus.portfolio.enums.RiskLevel;
import com.brunomatheus.portfolio.dtos.request.CreateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectEntity toEntity(CreateProjectRequestDTO request) {
        if (request == null) {
            return null;
        }

        return ProjectEntity.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .expectedEndDate(request.getExpectedEndDate())
                .actualEndDate(request.getActualEndDate())
                .budget(request.getBudget())
                .description(request.getDescription())
                .managerId(request.getManagerId())
                .build();
    }

    public ProjectResponseDTO toResponse(ProjectEntity entity) {
        if (entity == null) {
            return null;
        }

        return ProjectResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .startDate(entity.getStartDate())
                .expectedEndDate(entity.getExpectedEndDate())
                .actualEndDate(entity.getActualEndDate())
                .budget(entity.getBudget())
                .description(entity.getDescription())
                .managerId(entity.getManagerId())
                .status(entity.getStatus())
                .build();
    }

    public ProjectResponseDTO toResponse(ProjectEntity entity, RiskLevel riskLevel) {
        if (entity == null) {
            return null;
        }

        return ProjectResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .startDate(entity.getStartDate())
                .expectedEndDate(entity.getExpectedEndDate())
                .actualEndDate(entity.getActualEndDate())
                .budget(entity.getBudget())
                .description(entity.getDescription())
                .managerId(entity.getManagerId())
                .status(entity.getStatus())
                .riskLevel(riskLevel)
                .build();
    }

    public void updateEntity(ProjectEntity entity, UpdateProjectRequestDTO request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            entity.setName(request.getName());
        }

        if (request.getStartDate() != null) {
            entity.setStartDate(request.getStartDate());
        }

        if (request.getExpectedEndDate() != null) {
            entity.setExpectedEndDate(request.getExpectedEndDate());
        }

        if (request.getActualEndDate() != null) {
            entity.setActualEndDate(request.getActualEndDate());
        }

        if (request.getBudget() != null) {
            entity.setBudget(request.getBudget());
        }

        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        if (request.getManagerId() != null) {
            entity.setManagerId(request.getManagerId());
        }
    }
}