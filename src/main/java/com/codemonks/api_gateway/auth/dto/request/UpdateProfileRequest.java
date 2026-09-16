package com.codemonks.api_gateway.auth.dto.request;

import com.codemonks.api_gateway.auth.enums.AvatarId;
import com.codemonks.api_gateway.auth.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String displayName;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private Gender gender;
    private String countryCode;
    private AvatarId avatarId;
    private String email;
    private String phoneNumber;
}