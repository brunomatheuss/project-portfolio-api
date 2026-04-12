package com.brunomatheus.portfolio.services;

import com.brunomatheus.portfolio.dtos.external.MemberDTO;
import com.brunomatheus.portfolio.dtos.external.request.CreateMemberRequestDTO;

public interface ExternalMemberService {
    MemberDTO findById(Long memberId);
    MemberDTO create(CreateMemberRequestDTO request);
}
