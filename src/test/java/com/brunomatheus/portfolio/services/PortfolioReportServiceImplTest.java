package com.brunomatheus.portfolio.services;

import com.brunomatheus.portfolio.dtos.response.PortfolioSummaryResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.repositories.ProjectMemberRepository;
import com.brunomatheus.portfolio.repositories.ProjectRepository;
import com.brunomatheus.portfolio.services.impl.PortfolioReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioReportServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private PortfolioReportServiceImpl portfolioReportService;

    @Test
    void shouldGenerateSummaryWithEmptyProjectList() {
        when(projectRepository.findAll()).thenReturn(List.of());
        when(projectMemberRepository.countDistinctMemberIds()).thenReturn(0L);

        PortfolioSummaryResponseDTO result = portfolioReportService.generateSummary();

        assertNotNull(result);
        assertEquals(0.0, result.getAverageDurationOfClosedProjects());
        assertEquals(0L, result.getTotalUniqueAllocatedMembers());

        for (ProjectStatus status : ProjectStatus.values()) {
            assertEquals(0L, result.getProjectCountByStatus().get(status));
            assertEquals(BigDecimal.ZERO, result.getTotalBudgetByStatus().get(status));
        }
    }

    @Test
    void shouldGenerateSummarySuccessfully() {
        ProjectEntity inAnalysisProject = ProjectEntity.builder()
                .id(1L)
                .status(ProjectStatus.IN_ANALYSIS)
                .budget(new BigDecimal("100000"))
                .startDate(LocalDate.of(2026, 1, 1))
                .expectedEndDate(LocalDate.of(2026, 3, 1))
                .build();

        ProjectEntity closedProject = ProjectEntity.builder()
                .id(2L)
                .status(ProjectStatus.CLOSED)
                .budget(new BigDecimal("200000"))
                .startDate(LocalDate.of(2026, 1, 1))
                .actualEndDate(LocalDate.of(2026, 1, 11))
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(inAnalysisProject, closedProject));
        when(projectMemberRepository.countDistinctMemberIds()).thenReturn(5L);

        PortfolioSummaryResponseDTO result = portfolioReportService.generateSummary();

        assertNotNull(result);
        assertEquals(5L, result.getTotalUniqueAllocatedMembers());
        assertEquals(10.0, result.getAverageDurationOfClosedProjects());

        Map<ProjectStatus, Long> projectCountByStatus = result.getProjectCountByStatus();
        assertEquals(1L, projectCountByStatus.get(ProjectStatus.IN_ANALYSIS));
        assertEquals(1L, projectCountByStatus.get(ProjectStatus.CLOSED));

        Map<ProjectStatus, BigDecimal> totalBudgetByStatus = result.getTotalBudgetByStatus();
        assertEquals(new BigDecimal("100000"), totalBudgetByStatus.get(ProjectStatus.IN_ANALYSIS));
        assertEquals(new BigDecimal("200000"), totalBudgetByStatus.get(ProjectStatus.CLOSED));
    }

    @Test
    void shouldIgnoreProjectsWithoutValidClosedDatesWhenCalculatingAverageDuration() {
        ProjectEntity closedWithoutActualEndDate = ProjectEntity.builder()
                .id(1L)
                .status(ProjectStatus.CLOSED)
                .budget(new BigDecimal("100000"))
                .startDate(LocalDate.of(2026, 1, 1))
                .actualEndDate(null)
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(closedWithoutActualEndDate));
        when(projectMemberRepository.countDistinctMemberIds()).thenReturn(1L);

        PortfolioSummaryResponseDTO result = portfolioReportService.generateSummary();

        assertNotNull(result);
        assertEquals(0.0, result.getAverageDurationOfClosedProjects());
    }

    @Test
    void shouldTreatNullBudgetAsZero() {
        ProjectEntity projectWithNullBudget = ProjectEntity.builder()
                .id(1L)
                .status(ProjectStatus.PLANNED)
                .budget(null)
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(projectWithNullBudget));
        when(projectMemberRepository.countDistinctMemberIds()).thenReturn(1L);

        PortfolioSummaryResponseDTO result = portfolioReportService.generateSummary();

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalBudgetByStatus().get(ProjectStatus.PLANNED));
    }
}