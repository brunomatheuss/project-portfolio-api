package com.brunomatheus.portfolio.services;

import com.brunomatheus.portfolio.dtos.filter.ProjectFilter;
import com.brunomatheus.portfolio.dtos.request.CreateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectStatusRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponseDTO create(CreateProjectRequestDTO request);
    ProjectResponseDTO findById(Long id);
    Page<ProjectResponseDTO> findAll(ProjectFilter filter, Pageable pageable);
    ProjectResponseDTO update(Long id, UpdateProjectRequestDTO request);
    ProjectResponseDTO updateStatus(Long id, UpdateProjectStatusRequestDTO request);
    void delete(Long id);
}