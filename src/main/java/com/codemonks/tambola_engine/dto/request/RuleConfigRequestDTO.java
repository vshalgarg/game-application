package com.codemonks.tambola_engine.dto.request;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Host ne room-setup ke waqt jo ek claim-type (rule) configure ki thi,
// wahi is DTO me aata hai - EngineStartGameRequestDTO ke andar List ke
// roop me. GameSetupServiceImpl isi list ko loop karke domain GameRule
// objects banata hai.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleConfigRequestDTO {

    // Konsi claim-pattern hai (EARLY_FIVE, FULL_HOUSE, etc.)
    private RuleTypeEnum ruleType;

    // Is rule ko kis order me complete hona expect hai (lower = pehle).
    // FULL_HOUSE typically sabse high order rakhta hai - jab wo claim ho
    // jaaye, iska matlab game FINISHED ho sakta hai.
    private Integer order;

    // NAYA: host is rule ke liye kitne winners allow karna chahta hai
    // (jaise "First Row completion pe 5 players jeet sakte hain").
    // Agar host kuch na bheje, GameSetupServiceImpl isko default 1 maan lega.
    private Integer maxWinners;

    // Optional - host isko null chhod sakta hai (default-behavior use
    // hoga), ya future me koi specific value bhej sakta hai agar wo
    // rule configurable banaya gaya ho.
    private Integer threshold;
}