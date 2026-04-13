package com.brunomatheus.portfolio.services.external;

import com.brunomatheus.portfolio.dtos.external.MemberDTO;
import com.brunomatheus.portfolio.dtos.external.request.CreateMemberRequestDTO;
import com.brunomatheus.portfolio.enums.MemberRole;
import com.brunomatheus.portfolio.exceptions.NotFoundException;
import com.brunomatheus.portfolio.services.external.impl.ExternalMemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalMemberServiceImplTest {

    private ExternalMemberServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ExternalMemberServiceImpl();
    }

    @Test
    void shouldCreateMemberSuccessfully() {
        CreateMemberRequestDTO request = CreateMemberRequestDTO.builder()
                .name("Bruno")
                .role(MemberRole.EMPLOYEE)
                .build();

        MemberDTO result = service.create(request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Bruno", result.getName());
        assertEquals(MemberRole.EMPLOYEE, result.getRole());
    }

    @Test
    void shouldFindMemberByIdSuccessfully() {
        CreateMemberRequestDTO request = CreateMemberRequestDTO.builder()
                .name("Bruno")
                .role(MemberRole.EMPLOYEE)
                .build();

        MemberDTO created = service.create(request);

        MemberDTO found = service.findById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Bruno", found.getName());
    }

    @Test
    void shouldThrowNotFoundWhenMemberDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> service.findById(999L));
    }

    @Test
    void shouldIncrementIdsCorrectly() {
        CreateMemberRequestDTO request1 = CreateMemberRequestDTO.builder()
                .name("User1")
                .role(MemberRole.EMPLOYEE)
                .build();

        CreateMemberRequestDTO request2 = CreateMemberRequestDTO.builder()
                .name("User2")
                .role(MemberRole.EMPLOYEE)
                .build();

        MemberDTO member1 = service.create(request1);
        MemberDTO member2 = service.create(request2);

        assertEquals(member1.getId() + 1, member2.getId());
    }
}