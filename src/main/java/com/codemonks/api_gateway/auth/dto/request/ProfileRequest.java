package com.codemonks.api_gateway.auth.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {

    private Long userId;

    private String name;

    private String email;
}
