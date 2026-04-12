package com.brunomatheus.portfolio.controllers;

import com.brunomatheus.portfolio.dtos.request.AddProjectMemberRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectMemberResponseDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.services.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddProjectMemberRequestDTO request) {

        ProjectResponseDTO response = projectMemberService.addMember(projectId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId) {

        projectMemberService.removeMember(projectId, memberId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponseDTO>> listMembers(
            @PathVariable Long projectId) {

        List<ProjectMemberResponseDTO> response = projectMemberService.listMembers(projectId);

        return ResponseEntity.ok(response);
    }
}