package com.brunomatheus.portfolio.services;

import com.brunomatheus.portfolio.dtos.external.MemberDTO;
import com.brunomatheus.portfolio.dtos.request.AddProjectMemberRequestDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectMemberResponseDTO;
import com.brunomatheus.portfolio.dtos.response.ProjectResponseDTO;
import com.brunomatheus.portfolio.entities.ProjectEntity;
import com.brunomatheus.portfolio.entities.ProjectMemberEntity;
import com.brunomatheus.portfolio.enums.MemberRole;
import com.brunomatheus.portfolio.enums.ProjectStatus;
import com.brunomatheus.portfolio.exceptions.BusinessException;
import com.brunomatheus.portfolio.exceptions.NotFoundException;
import com.brunomatheus.portfolio.mappers.ProjectMapper;
import com.brunomatheus.portfolio.repositories.ProjectMemberRepository;
import com.brunomatheus.portfolio.repositories.ProjectRepository;
import com.brunomatheus.portfolio.services.external.ExternalMemberService;
import com.brunomatheus.portfolio.services.impl.ProjectMemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ExternalMemberService externalMemberService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectMemberServiceImpl projectMemberService;

    @Test
    void shouldAddMemberSuccessfully() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        MemberDTO member = MemberDTO.builder()
                .id(memberId)
                .role(MemberRole.EMPLOYEE)
                .build();

        AddProjectMemberRequestDTO request = AddProjectMemberRequestDTO.builder()
                .memberId(memberId)
                .build();

        ProjectResponseDTO responseDTO = ProjectResponseDTO.builder()
                .id(projectId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalMemberService.findById(memberId)).thenReturn(member);
        when(projectMemberRepository.countByProjectId(projectId)).thenReturn(0);
        when(projectMemberRepository.countActiveProjectsByMemberId(
                eq(memberId),
                eq(List.of(ProjectStatus.CLOSED, ProjectStatus.CANCELED))
        )).thenReturn(0);
        when(projectMemberRepository.findByProjectIdAndMemberId(projectId, memberId))
                .thenReturn(Optional.empty());
        when(projectMapper.toResponse(project)).thenReturn(responseDTO);

        ProjectResponseDTO result = projectMemberService.addMember(projectId, request);

        assertNotNull(result);
        assertEquals(projectId, result.getId());
        verify(projectMemberRepository).save(any(ProjectMemberEntity.class));
    }

    @Test
    void shouldThrowNotFoundWhenProjectDoesNotExistOnAddMember() {
        Long projectId = 1L;

        AddProjectMemberRequestDTO request = AddProjectMemberRequestDTO.builder()
                .memberId(2L)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectMemberService.addMember(projectId, request));

        verify(externalMemberService, never()).findById(any());
        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenMemberIsNull() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        AddProjectMemberRequestDTO request = AddProjectMemberRequestDTO.builder()
                .memberId(memberId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalMemberService.findById(memberId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> projectMemberService.addMember(projectId, request));

        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenMemberIsNotEmployee() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        MemberDTO member = MemberDTO.builder()
                .id(memberId)
                .role(MemberRole.OTHER)
                .build();

        AddProjectMemberRequestDTO request = AddProjectMemberRequestDTO.builder()
                .memberId(memberId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalMemberService.findById(memberId)).thenReturn(member);

        assertThrows(BusinessException.class, () -> projectMemberService.addMember(projectId, request));

        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenProjectAlreadyHasTenMembers() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        MemberDTO member = MemberDTO.builder()
                .id(memberId)
                .role(MemberRole.EMPLOYEE)
                .build();

        AddProjectMemberRequestDTO request = AddProjectMemberRequestDTO.builder()
                .memberId(memberId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalMemberService.findById(memberId)).thenReturn(member);
        when(projectMemberRepository.countByProjectId(projectId)).thenReturn(10);

        assertThrows(BusinessException.class, () -> projectMemberService.addMember(projectId, request));

        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenMemberAlreadyHasThreeActiveProjects() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        MemberDTO member = MemberDTO.builder()
                .id(memberId)
                .role(MemberRole.EMPLOYEE)
                .build();

        AddProjectMemberRequestDTO request = AddProjectMemberRequestDTO.builder()
                .memberId(memberId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalMemberService.findById(memberId)).thenReturn(member);
        when(projectMemberRepository.countByProjectId(projectId)).thenReturn(0);
        when(projectMemberRepository.countActiveProjectsByMemberId(
                eq(memberId),
                eq(List.of(ProjectStatus.CLOSED, ProjectStatus.CANCELED))
        )).thenReturn(3);

        assertThrows(BusinessException.class, () -> projectMemberService.addMember(projectId, request));

        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenMemberIsAlreadyAllocatedToProject() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        MemberDTO member = MemberDTO.builder()
                .id(memberId)
                .role(MemberRole.EMPLOYEE)
                .build();

        AddProjectMemberRequestDTO request = AddProjectMemberRequestDTO.builder()
                .memberId(memberId)
                .build();

        ProjectMemberEntity allocation = ProjectMemberEntity.builder()
                .projectId(projectId)
                .memberId(memberId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(externalMemberService.findById(memberId)).thenReturn(member);
        when(projectMemberRepository.countByProjectId(projectId)).thenReturn(0);
        when(projectMemberRepository.countActiveProjectsByMemberId(
                eq(memberId),
                eq(List.of(ProjectStatus.CLOSED, ProjectStatus.CANCELED))
        )).thenReturn(0);
        when(projectMemberRepository.findByProjectIdAndMemberId(projectId, memberId))
                .thenReturn(Optional.of(allocation));

        assertThrows(BusinessException.class, () -> projectMemberService.addMember(projectId, request));

        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void shouldRemoveMemberSuccessfully() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        ProjectMemberEntity allocation = ProjectMemberEntity.builder()
                .projectId(projectId)
                .memberId(memberId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndMemberId(projectId, memberId))
                .thenReturn(Optional.of(allocation));
        when(projectMemberRepository.countByProjectId(projectId)).thenReturn(2);

        assertDoesNotThrow(() -> projectMemberService.removeMember(projectId, memberId));

        verify(projectMemberRepository).delete(allocation);
    }

    @Test
    void shouldThrowNotFoundWhenAllocationDoesNotExistOnRemove() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndMemberId(projectId, memberId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectMemberService.removeMember(projectId, memberId));

        verify(projectMemberRepository, never()).delete(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenRemovingLastMember() {
        Long projectId = 1L;
        Long memberId = 2L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        ProjectMemberEntity allocation = ProjectMemberEntity.builder()
                .projectId(projectId)
                .memberId(memberId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndMemberId(projectId, memberId))
                .thenReturn(Optional.of(allocation));
        when(projectMemberRepository.countByProjectId(projectId)).thenReturn(1);

        assertThrows(BusinessException.class, () -> projectMemberService.removeMember(projectId, memberId));

        verify(projectMemberRepository, never()).delete(any());
    }

    @Test
    void shouldListMembersSuccessfully() {
        Long projectId = 1L;

        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .build();

        ProjectMemberEntity allocation1 = ProjectMemberEntity.builder()
                .projectId(projectId)
                .memberId(10L)
                .build();

        ProjectMemberEntity allocation2 = ProjectMemberEntity.builder()
                .projectId(projectId)
                .memberId(20L)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectId(projectId))
                .thenReturn(List.of(allocation1, allocation2));

        List<ProjectMemberResponseDTO> result = projectMemberService.listMembers(projectId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getMemberId());
        assertEquals(20L, result.get(1).getMemberId());
    }

    @Test
    void shouldThrowNotFoundWhenProjectDoesNotExistOnListMembers() {
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectMemberService.listMembers(projectId));

        verify(projectMemberRepository, never()).findByProjectId(any());
    }
}