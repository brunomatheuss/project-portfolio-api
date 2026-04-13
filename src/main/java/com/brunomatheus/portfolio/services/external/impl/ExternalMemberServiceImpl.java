package com.brunomatheus.portfolio.services.external.impl;

import com.brunomatheus.portfolio.dtos.external.MemberDTO;
import com.brunomatheus.portfolio.dtos.external.request.CreateMemberRequestDTO;
import com.brunomatheus.portfolio.exceptions.NotFoundException;
import com.brunomatheus.portfolio.services.external.ExternalMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ExternalMemberServiceImpl implements ExternalMemberService {

    private final Map<Long, MemberDTO> members = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public MemberDTO findById(Long id) {
        MemberDTO member = members.get(id);

        if (member == null) {
            throw new NotFoundException("Member not found");
        }

        return member;
    }

    @Override
    public MemberDTO create(CreateMemberRequestDTO request) {
        Long id = sequence.incrementAndGet();

        MemberDTO member = MemberDTO.builder()
                .id(id)
                .name(request.getName())
                .role(request.getRole())
                .build();

        members.put(id, member);

        return member;
    }
}