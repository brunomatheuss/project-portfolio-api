package com.brunomatheus.portfolio.dtos.request;

import com.brunomatheus.portfolio.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProjectStatusRequestDTO {

    @NotNull(message = "Status is required")
    private ProjectStatus status;

}