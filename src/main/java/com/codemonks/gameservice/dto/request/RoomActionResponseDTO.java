package com.codemonks.gameservice.dto.request;


import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomActionResponseDTO {

    private RoomDetailsResponseDTO roomDetails;
    private String message;
}