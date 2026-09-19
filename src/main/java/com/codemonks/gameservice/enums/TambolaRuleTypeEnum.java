package com.codemonks.gameservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Static UI/API metadata for Tambola claim rules.
 *
 * <p>Only static metadata lives here. It is intentionally NOT persisted
 * in MySQL, Supabase, or any request payload. It must stay in sync with
 * the engine's {@code RuleTypeEnum}.
 */
@Getter
@RequiredArgsConstructor
public enum TambolaRuleTypeEnum {

    EARLY_FIVE("Early Five", "First player to mark any five numbers"),
    TOP_LINE("Top Line", "First player to complete the top line"),
    MIDDLE_LINE("Middle Line", "First player to complete the middle line"),
    BOTTOM_LINE("Bottom Line", "First player to complete the bottom line"),
    FULL_HOUSE("Full House", "First player to mark all numbers");

    private final String displayName;
    private final String description;

    public static TambolaRuleTypeEnum fromName(String ruleType) {
        for (TambolaRuleTypeEnum type : values()) {
            if (type.name().equalsIgnoreCase(ruleType)) {
                return type;
            }
        }
        return null;
    }
}