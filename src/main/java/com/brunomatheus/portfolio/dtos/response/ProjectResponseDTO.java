package com.brunomatheus.portfolio.dtos.response;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.enums.RiskLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDTO {

    private Long id;

    private String name;

    private LocalDate startDate;

    private LocalDate expectedEndDate;

    private LocalDate actualEndDate;

    private BigDecimal budget;

    private String description;

    private Long managerId;

    private ProjectStatus status;

    private RiskLevel riskLevel;

}