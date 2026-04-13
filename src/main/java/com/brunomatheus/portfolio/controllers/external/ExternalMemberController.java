package com.brunomatheus.portfolio.controllers.external;

import com.brunomatheus.portfolio.dtos.external.request.CreateMemberRequestDTO;
import com.brunomatheus.portfolio.dtos.external.MemberDTO;
import com.brunomatheus.portfolio.services.external.ExternalMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/external/members")
@RequiredArgsConstructor
public class ExternalMemberController {

    private final ExternalMemberService externalMemberService;

    @PostMapping
    public ResponseEntity<MemberDTO> create(@Valid @RequestBody CreateMemberRequestDTO request) {
        MemberDTO response = externalMemberService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> findById(@PathVariable Long id) {
        MemberDTO response = externalMemberService.findById(id);
        return ResponseEntity.ok(response);
    }
}