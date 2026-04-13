package com.brunomatheus.portfolio.services;

import com.brunomatheus.portfolio.dtos.filter.ProjectFilter;
import com.brunomatheus.portfolio.dtos.request.CreateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectStatusRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.enums.RiskLevel;
import com.brunomatheus.portfolio.exceptions.BusinessException;
import com.brunomatheus.portfolio.exceptions.NotFoundException;
import com.brunomatheus.portfolio.mappers.ProjectMapper;
import com.brunomatheus.portfolio.repositories.ProjectRepository;
import com.brunomatheus.portfolio.services.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void shouldCreateProjectSuccessfully() {
        CreateProjectRequestDTO request = new CreateProjectRequestDTO();

        ProjectEntity entity = new ProjectEntity();
        entity.setBudget(new BigDecimal("100000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        when(projectMapper.toEntity(request)).thenReturn(entity);

        when(projectMapper.toResponse(any(), any()))
                .thenReturn(ProjectResponseDTO.builder().build());

        ProjectResponseDTO response = projectService.create(request);

        assertNotNull(response);
        assertEquals(ProjectStatus.IN_ANALYSIS, entity.getStatus());
        verify(projectRepository).save(entity);
    }

    @Test
    void shouldFindProjectByIdSuccessfully() {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(1L);
        entity.setBudget(new BigDecimal("100000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(projectMapper.toResponse(any(), any()))
                .thenReturn(ProjectResponseDTO.builder().id(1L).build());

        ProjectResponseDTO response = projectService.findById(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowNotFoundWhenProjectDoesNotExist() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.findById(1L));
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        ProjectEntity entity = new ProjectEntity();
        entity.setStatus(ProjectStatus.IN_ANALYSIS);
        entity.setBudget(new BigDecimal("100000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        UpdateProjectStatusRequestDTO request = new UpdateProjectStatusRequestDTO();
        request.setStatus(ProjectStatus.ANALYSIS_COMPLETED);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(projectMapper.toResponse(any(), any()))
                .thenReturn(ProjectResponseDTO.builder().build());

        ProjectResponseDTO response = projectService.updateStatus(1L, request);

        assertNotNull(response);
        assertEquals(ProjectStatus.ANALYSIS_COMPLETED, entity.getStatus());
        verify(projectRepository).save(entity);
    }

    @Test
    void shouldThrowWhenInvalidStatusTransition() {
        ProjectEntity entity = new ProjectEntity();
        entity.setStatus(ProjectStatus.IN_ANALYSIS);

        UpdateProjectStatusRequestDTO request = new UpdateProjectStatusRequestDTO();
        request.setStatus(ProjectStatus.INITIATED);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(BusinessException.class,
                () -> projectService.updateStatus(1L, request));
    }

    @Test
    void shouldAllowCancelFromAnyStatus() {
        ProjectEntity entity = new ProjectEntity();
        entity.setStatus(ProjectStatus.IN_ANALYSIS);
        entity.setBudget(new BigDecimal("100000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        UpdateProjectStatusRequestDTO request = new UpdateProjectStatusRequestDTO();
        request.setStatus(ProjectStatus.CANCELED);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(projectMapper.toResponse(any(), any()))
                .thenReturn(ProjectResponseDTO.builder().build());

        assertDoesNotThrow(() -> projectService.updateStatus(1L, request));
    }

    @Test
    void shouldDeleteProjectSuccessfully() {
        ProjectEntity entity = new ProjectEntity();
        entity.setStatus(ProjectStatus.PLANNED);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertDoesNotThrow(() -> projectService.delete(1L));

        verify(projectRepository).delete(entity);
    }

    @Test
    void shouldThrowWhenDeletingProjectInInvalidStatus() {
        ProjectEntity entity = new ProjectEntity();
        entity.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(BusinessException.class,
                () -> projectService.delete(1L));
    }

    @Test
    void shouldCalculateLowRisk() {
        ProjectEntity entity = new ProjectEntity();
        entity.setBudget(new BigDecimal("100000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(projectMapper.toResponse(any(), eq(RiskLevel.LOW)))
                .thenReturn(ProjectResponseDTO.builder().build());

        projectService.findById(1L);

        verify(projectMapper).toResponse(entity, RiskLevel.LOW);
    }

    @Test
    void shouldCalculateHighRisk() {
        ProjectEntity entity = new ProjectEntity();
        entity.setBudget(new BigDecimal("600000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(projectMapper.toResponse(any(), eq(RiskLevel.HIGH)))
                .thenReturn(ProjectResponseDTO.builder().build());

        projectService.findById(1L);

        verify(projectMapper).toResponse(entity, RiskLevel.HIGH);
    }

    @Test
    void shouldFindAllProjectsSuccessfully() {
        ProjectEntity entity = new ProjectEntity();
        entity.setBudget(new BigDecimal("100000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        Page<ProjectEntity> page = new PageImpl<>(List.of(entity));

        when(projectRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        when(projectMapper.toResponse(any(), any()))
                .thenReturn(ProjectResponseDTO.builder().build());

        Page<ProjectResponseDTO> result =
                projectService.findAll(new ProjectFilter(), PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(projectRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(projectMapper).toResponse(any(), any());
    }

    @Test
    void shouldUpdateProjectSuccessfully() {
        Long projectId = 1L;

        ProjectEntity entity = new ProjectEntity();
        entity.setId(projectId);
        entity.setBudget(new BigDecimal("100000"));
        entity.setStartDate(LocalDate.now());
        entity.setExpectedEndDate(LocalDate.now().plusMonths(2));

        UpdateProjectRequestDTO request = new UpdateProjectRequestDTO();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(entity));

        when(projectMapper.toResponse(any(), any()))
                .thenReturn(ProjectResponseDTO.builder().id(projectId).build());

        ProjectResponseDTO result = projectService.update(projectId, request);

        assertNotNull(result);
        assertEquals(projectId, result.getId());

        verify(projectMapper).updateEntity(entity, request);
        verify(projectRepository).save(entity);
    }
}