package com.brunomatheus.portfolio.controllers;

import com.brunomatheus.portfolio.dtos.filter.ProjectFilter;
import com.brunomatheus.portfolio.dtos.request.CreateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectRequestDTO;
import com.brunomatheus.portfolio.dtos.request.UpdateProjectStatusRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(
            @Valid @RequestBody CreateProjectRequestDTO request) {

        ProjectResponseDTO response = projectService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> findById(@PathVariable Long id) {

        ProjectResponseDTO response = projectService.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponseDTO>> findAll(
            ProjectFilter filter,
            Pageable pageable) {

        Page<ProjectResponseDTO> response = projectService.findAll(filter, pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequestDTO request) {

        ProjectResponseDTO response = projectService.update(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectStatusRequestDTO request) {

        ProjectResponseDTO response = projectService.updateStatus(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        projectService.delete(id);

        return ResponseEntity.noContent().build();
    }
}