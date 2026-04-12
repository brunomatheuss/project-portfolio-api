package com.brunomatheus.portfolio.dtos.external;

import com.brunomatheus.portfolio.enums.MemberRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

    private Long id;

    private String name;

    private MemberRole role;
}