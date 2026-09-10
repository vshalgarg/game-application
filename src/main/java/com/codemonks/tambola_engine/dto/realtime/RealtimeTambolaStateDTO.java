package com.codemonks.tambola_engine.dto.realtime;

import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

// Ye DTO seedha realtime_game_state table ke columns se maps karta hai
// (Ludo ke RealtimeGameStateDTO jaisa hi shape) - kyunki dono engines
// SAME physical table use kar rahe hain, alag-alag rows (room_id se
// differentiate hota hai, koi game_type column nahi chahiye kyunki
// roomId khud hi unique hai game-service ki taraf se).
//
// Ludo-specific columns (current_turn_user_id, bot_difficulty) is DTO
// me jaanbujh kar SHAMIL NAHI kiye - Tambola me "turn" ya "bot" jaisa
// koi concept hi nahi hai. Jackson un fields ko simply request-body me
// include hi nahi karega, aur wo columns Postgres me apne default
// (null) pe hi rahenge is row ke liye - koi problem nahi, columns
// nullable hain.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeTambolaStateDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("room_code")
    private String roomCode;

    // YAHI wo jagah hai jahan poora Tambola-specific nested data jaata
    // hai: calledNumbers, lastCalledNumber, playerTickets, claims,
    // activeRules - sab ek hi Map ke andar, jaisa Ludo apna board/token
    // state isi tarah ke Map me daalta hai.
    @JsonProperty("game_state_data")
    private Map<String, Object> gameState;

    @JsonProperty("players")
    private List<PlayerDTO> players;

    @JsonProperty("game_status")
    private String gameStatus;

    // Full House (final rule) jeetne wale player ki ID - baaki
    // rules (Early Five, Top Line) ke winners gameState blob ke
    // andar hi record rahenge (claims list me), ye column sirf
    // "overall/final winner" ke liye hai (Ludo ke winner_user_id
    // jaisa hi concept, single-winner-per-game).
    @JsonProperty("winner_user_id")
    private Long winnerUserId;
}