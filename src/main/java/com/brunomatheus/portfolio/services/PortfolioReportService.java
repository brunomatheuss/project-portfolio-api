package com.brunomatheus.portfolio.services;

import com.brunomatheus.portfolio.dtos.response.PortfolioSummaryResponseDTO;

public interface PortfolioReportService {
    PortfolioSummaryResponseDTO generateSummary();
}
