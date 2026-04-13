package com.brunomatheus.portfolio.mappers;

import com.brunomatheus.portfolio.dtos.request.CreateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.enums.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {

    private ProjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ProjectMapper();
    }

    @Test
    void shouldMapToEntity() {
        CreateProjectRequestDTO request = CreateProjectRequestDTO.builder()
                .name("Projeto Teste")
                .startDate(LocalDate.of(2026, 1, 1))
                .expectedEndDate(LocalDate.of(2026, 3, 1))
                .actualEndDate(null)
                .budget(new BigDecimal("100000"))
                .description("Desc")
                .managerId(1L)
                .build();

        ProjectEntity entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("Projeto Teste", entity.getName());
        assertEquals(new BigDecimal("100000"), entity.getBudget());
        assertEquals(1L, entity.getManagerId());
    }

    @Test
    void shouldReturnNullWhenToEntityReceivesNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldMapToResponse() {
        ProjectEntity entity = ProjectEntity.builder()
                .id(1L)
                .name("Projeto")
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(1))
                .budget(new BigDecimal("50000"))
                .description("Desc")
                .managerId(2L)
                .status(ProjectStatus.IN_ANALYSIS)
                .build();

        ProjectResponseDTO response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(ProjectStatus.IN_ANALYSIS, response.getStatus());
    }

    @Test
    void shouldReturnNullWhenToResponseReceivesNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void shouldMapToResponseWithRiskLevel() {
        ProjectEntity entity = ProjectEntity.builder()
                .id(1L)
                .name("Projeto")
                .status(ProjectStatus.IN_ANALYSIS)
                .build();

        ProjectResponseDTO response = mapper.toResponse(entity, RiskLevel.HIGH);

        assertNotNull(response);
        assertEquals(RiskLevel.HIGH, response.getRiskLevel());
    }

    @Test
    void shouldReturnNullWhenToResponseWithRiskReceivesNull() {
        assertNull(mapper.toResponse(null, RiskLevel.HIGH));
    }

    @Test
    void shouldUpdateEntityOnlyWithNonNullFields() {
        ProjectEntity entity = ProjectEntity.builder()
                .name("Old")
                .budget(new BigDecimal("100"))
                .description("Old desc")
                .build();

        UpdateProjectRequestDTO request = UpdateProjectRequestDTO.builder()
                .name("New")
                .budget(new BigDecimal("200"))
                .description(null)
                .build();

        mapper.updateEntity(entity, request);

        assertEquals("New", entity.getName());
        assertEquals(new BigDecimal("200"), entity.getBudget());
        assertEquals("Old desc", entity.getDescription());
    }

    @Test
    void shouldDoNothingWhenUpdateEntityReceivesNull() {
        ProjectEntity entity = new ProjectEntity();

        mapper.updateEntity(entity, null);
        mapper.updateEntity(null, new UpdateProjectRequestDTO());

        assertNotNull(entity);
    }
}