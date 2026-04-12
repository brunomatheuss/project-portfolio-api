package com.brunomatheus.portfolio.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {

    IN_ANALYSIS("em análise"),
    ANALYSIS_COMPLETED("análise realizada"),
    ANALYSIS_APPROVED("análise aprovada"),
    INITIATED("iniciado"),
    PLANNED("planejado"),
    IN_PROGRESS("em andamento"),
    CLOSED("encerrado"),
    CANCELED("cancelado");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }

}