package com.brunomatheus.portfolio.services.impl;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.dtos.response.PortfolioSummaryResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.repositories.ProjectMemberRepository;
import com.brunomatheus.portfolio.repositories.ProjectRepository;
import com.brunomatheus.portfolio.services.PortfolioReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioReportServiceImpl implements PortfolioReportService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public PortfolioSummaryResponseDTO generateSummary() {
        List<ProjectEntity> projects = projectRepository.findAll();

        Map<ProjectStatus, Long> projectCountByStatus = countProjectsByStatus(projects);
        Map<ProjectStatus, BigDecimal> totalBudgetByStatus = sumBudgetByStatus(projects);
        Double averageDurationOfClosedProjects = calculateAverageDurationOfClosedProjects(projects);
        Long totalUniqueAllocatedMembers = projectMemberRepository.countDistinctMemberIds();

        return PortfolioSummaryResponseDTO.builder()
                .projectCountByStatus(projectCountByStatus)
                .totalBudgetByStatus(totalBudgetByStatus)
                .averageDurationOfClosedProjects(averageDurationOfClosedProjects)
                .totalUniqueAllocatedMembers(totalUniqueAllocatedMembers)
                .build();
    }

    private Map<ProjectStatus, Long> countProjectsByStatus(List<ProjectEntity> projects) {
        Map<ProjectStatus, Long> result = new EnumMap<>(ProjectStatus.class);

        Arrays.stream(ProjectStatus.values()).forEach(status -> result.put(status, 0L));

        for (ProjectEntity project : projects) {
            ProjectStatus status = project.getStatus();
            result.put(status, result.get(status) + 1);
        }

        return result;
    }

    private Map<ProjectStatus, BigDecimal> sumBudgetByStatus(List<ProjectEntity> projects) {
        Map<ProjectStatus, BigDecimal> result = new EnumMap<>(ProjectStatus.class);

        Arrays.stream(ProjectStatus.values()).forEach(status -> result.put(status, BigDecimal.ZERO));

        for (ProjectEntity project : projects) {
            ProjectStatus status = project.getStatus();
            BigDecimal currentTotal = result.get(status);
            BigDecimal budget = project.getBudget() != null ? project.getBudget() : BigDecimal.ZERO;

            result.put(status, currentTotal.add(budget));
        }

        return result;
    }

    private Double calculateAverageDurationOfClosedProjects(List<ProjectEntity> projects) {
        List<ProjectEntity> closedProjects = projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.CLOSED)
                .filter(project -> project.getStartDate() != null && project.getActualEndDate() != null)
                .toList();

        if (closedProjects.isEmpty()) {
            return 0.0;
        }

        return closedProjects.stream()
                .mapToLong(project -> ChronoUnit.DAYS.between(
                        project.getStartDate(),
                        project.getActualEndDate()
                ))
                .average()
                .orElse(0.0);
    }
}