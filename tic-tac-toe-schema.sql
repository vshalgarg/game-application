CREATE TABLE realtime_room_lobby (

    room_id BIGINT PRIMARY KEY,
    room_code VARCHAR(10) NOT NULL UNIQUE,
    room_status VARCHAR(20) NOT NULL,
    players JSONB NOT NULL DEFAULT '[]'::jsonb,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE realtime_game_state (

    room_id BIGINT PRIMARY KEY,
    game_state_data JSONB NOT NULL,
    current_turn_user_id BIGINT,
    game_status VARCHAR(20) NOT NULL,
    winner_user_id BIGINT,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE realtime_game_moves (

    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    move_data JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_realtime_room_lobby_updated_at
ON realtime_room_lobby(updated_at);

CREATE INDEX idx_realtime_game_state_updated_at
ON realtime_game_state(updated_at);

CREATE INDEX idx_realtime_game_moves_room_id
ON realtime_game_moves(room_id);


ALTER PUBLICATION supabase_realtime
ADD TABLE realtime_room_lobby;

ALTER PUBLICATION supabase_realtime
ADD TABLE realtime_game_state;

ALTER PUBLICATION supabase_realtime
ADD TABLE realtime_game_moves;

SELECT *
FROM pg_publication_tables
WHERE pubname = 'supabase_realtime';


ALTER TABLE realtime_game_state
ADD COLUMN room_code VARCHAR(10) NOT NULL;

ALTER TABLE realtime_game_moves
ADD COLUMN room_code VARCHAR(10) NOT NULL;
