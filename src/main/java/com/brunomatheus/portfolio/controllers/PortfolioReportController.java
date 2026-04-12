package com.brunomatheus.portfolio.controllers;

import com.brunomatheus.portfolio.dtos.response.PortfolioSummaryResponseDTO;
import com.brunomatheus.portfolio.services.PortfolioReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class PortfolioReportController {

    private final PortfolioReportService portfolioReportService;

    @GetMapping("/portfolio-summary")
    public ResponseEntity<PortfolioSummaryResponseDTO> getSummary() {

        PortfolioSummaryResponseDTO response = portfolioReportService.generateSummary();

        return ResponseEntity.ok(response);
    }
}