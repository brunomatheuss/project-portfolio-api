package com.brunomatheus.portfolio.dtos.external.request;

import com.brunomatheus.portfolio.enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMemberRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private MemberRole role;
}
