
-- Realtime Tables - PostgreSQL / Supabase

CREATE TABLE IF NOT EXISTS realtime_room_lobby (

    room_id BIGINT PRIMARY KEY,
    room_code VARCHAR(10) NOT NULL UNIQUE,
    room_status VARCHAR(20) NOT NULL,
    players JSONB NOT NULL DEFAULT '[]'::jsonb,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS realtime_game_state (

    room_id BIGINT PRIMARY KEY,
    room_code VARCHAR(10) NOT NULL,
    game_state_data JSONB NOT NULL,
    current_turn_user_id BIGINT,
    game_status VARCHAR(20) NOT NULL,
    winner_user_id BIGINT,
    players JSONB NOT NULL DEFAULT '[]'::jsonb,
    bot_difficulty VARCHAR(20),
    state_sequence BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS realtime_game_moves (

    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    room_code VARCHAR(10) NOT NULL,
    player_id BIGINT NOT NULL,
    move_data JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE INDEX IF NOT EXISTS idx_realtime_room_lobby_updated_at
ON realtime_room_lobby(updated_at);

CREATE INDEX IF NOT EXISTS idx_realtime_game_state_updated_at
ON realtime_game_state(updated_at);

CREATE INDEX IF NOT EXISTS idx_realtime_game_state_room_code
ON realtime_game_state(room_code);

CREATE INDEX IF NOT EXISTS idx_realtime_game_moves_room_id
ON realtime_game_moves(room_id);

CREATE INDEX IF NOT EXISTS idx_realtime_game_moves_room_code
ON realtime_game_moves(room_code);
