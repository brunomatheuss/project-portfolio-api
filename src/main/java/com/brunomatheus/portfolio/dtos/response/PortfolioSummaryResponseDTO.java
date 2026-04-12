package com.brunomatheus.portfolio.dtos.response;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummaryResponseDTO {

    private Map<ProjectStatus, Long> projectCountByStatus;

    private Map<ProjectStatus, BigDecimal> totalBudgetByStatus;

    private Double averageDurationOfClosedProjects;

    private Long totalUniqueAllocatedMembers;

}