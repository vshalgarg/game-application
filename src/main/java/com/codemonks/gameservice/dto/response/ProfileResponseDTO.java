package com.codemonks.gameservice.dto.response;

import com.codemonks.gameservice.enums.AvatarId;
import com.codemonks.gameservice.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private Long userId;
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dob;
    private Gender gender;
    private String countryCode;
    private AvatarId avatarId;
    private String phoneNumber;
    private Boolean IsProfileCompleted;
}