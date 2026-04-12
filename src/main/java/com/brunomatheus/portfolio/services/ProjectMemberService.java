package com.brunomatheus.portfolio.services;

import com.brunomatheus.portfolio.dtos.request.AddProjectMemberRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectMemberResponseDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;

import java.util.List;

public interface ProjectMemberService {
    ProjectResponseDTO addMember(Long projectId, AddProjectMemberRequestDTO request);
    void removeMember(Long projectId, Long memberId);
    List<ProjectMemberResponseDTO> listMembers(Long projectId);
}
