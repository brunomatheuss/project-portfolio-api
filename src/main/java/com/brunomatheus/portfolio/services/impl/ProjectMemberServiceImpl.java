package com.brunomatheus.portfolio.services.impl;

import com.brunomatheus.portfolio.enums.MemberRole;
import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.dtos.external.MemberDTO;
import com.brunomatheus.portfolio.dtos.request.AddProjectMemberRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectMemberResponseDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.entities.ProjectMemberEntity;
import com.brunomatheus.portfolio.exceptions.BusinessException;
import com.brunomatheus.portfolio.exceptions.NotFoundException;
import com.brunomatheus.portfolio.mappers.ProjectMapper;
import com.brunomatheus.portfolio.repositories.ProjectMemberRepository;
import com.brunomatheus.portfolio.repositories.ProjectRepository;
import com.brunomatheus.portfolio.services.external.ExternalMemberService;
import com.brunomatheus.portfolio.services.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ExternalMemberService externalMemberService;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDTO addMember(Long projectId, AddProjectMemberRequestDTO request) {
        ProjectEntity project = findProjectById(projectId);

        MemberDTO member = externalMemberService.findById(request.getMemberId());

        validateMemberRole(member);
        validateProjectMemberLimit(projectId);
        validateMemberActiveProjectsLimit(member.getId());
        validateMemberAlreadyAllocated(projectId, member.getId());

        ProjectMemberEntity projectMember = ProjectMemberEntity.builder()
                .projectId(projectId)
                .memberId(member.getId())
                .build();

        projectMemberRepository.save(projectMember);

        return projectMapper.toResponse(project);
    }

    @Override
    public void removeMember(Long projectId, Long memberId) {
        ProjectEntity project = findProjectById(projectId);

        ProjectMemberEntity allocation = projectMemberRepository
                .findByProjectIdAndMemberId(projectId, memberId)
                .orElseThrow(() -> new NotFoundException("Member allocation not found"));

        int currentMembers = projectMemberRepository.countByProjectId(projectId);

        if (currentMembers <= 1) {
            throw new BusinessException("Project must have at least one member allocated");
        }

        projectMemberRepository.delete(allocation);
    }

    @Override
    public List<ProjectMemberResponseDTO> listMembers(Long projectId) {
        findProjectById(projectId);

        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(allocation -> ProjectMemberResponseDTO.builder()
                        .memberId(allocation.getMemberId())
                        .build())
                .toList();
    }

    private ProjectEntity findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private void validateMemberRole(MemberDTO member) {
        if (member == null) {
            throw new NotFoundException("Member not found");
        }

        if (member.getRole() != MemberRole.EMPLOYEE) {
            throw new BusinessException("Only employees can be allocated to projects");
        }
    }

    private void validateProjectMemberLimit(Long projectId) {
        int memberCount = projectMemberRepository.countByProjectId(projectId);

        if (memberCount >= 10) {
            throw new BusinessException("A project can have at most 10 members");
        }
    }

    private void validateMemberActiveProjectsLimit(Long memberId) {
        int activeProjectsCount = projectMemberRepository.countActiveProjectsByMemberId(
                memberId,
                List.of(ProjectStatus.CLOSED, ProjectStatus.CANCELED)
        );

        if (activeProjectsCount >= 3) {
            throw new BusinessException("A member cannot be allocated to more than 3 active projects");
        }
    }

    private void validateMemberAlreadyAllocated(Long projectId, Long memberId) {
        boolean alreadyAllocated = projectMemberRepository
                .findByProjectIdAndMemberId(projectId, memberId)
                .isPresent();

        if (alreadyAllocated) {
            throw new BusinessException("Member is already allocated to this project");
        }
    }
}