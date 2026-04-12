package com.brunomatheus.portfolio.dtos.filter;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectFilter {

    private ProjectStatus status;

    private Long managerId;

    private LocalDate startDateFrom;

    private LocalDate startDateTo;

}