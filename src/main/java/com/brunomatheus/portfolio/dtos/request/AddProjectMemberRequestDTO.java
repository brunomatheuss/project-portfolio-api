package com.brunomatheus.portfolio.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProjectMemberRequestDTO {

    @NotNull
    private Long memberId;

}