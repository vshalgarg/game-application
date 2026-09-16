package com.codemonks.gameservice.dto.request;

import com.codemonks.gameservice.enums.AvatarId;
import com.codemonks.gameservice.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequestDTO {
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private Gender gender;
    private String countryCode;
    private AvatarId avatarId;
    private String email;
    private String phoneNumber;
}