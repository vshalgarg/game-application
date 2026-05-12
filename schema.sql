
CREATE TABLE IF NOT EXISTS game_config (

    tenant_id        VARCHAR(50) NOT NULL,
    game_type        VARCHAR(50) NOT NULL,
    min_players      INT NOT NULL,
    max_players      INT NOT NULL,
    roles_json       TEXT,

    PRIMARY KEY (tenant_id, game_type)

);


CREATE TABLE IF NOT EXISTS rooms (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        VARCHAR(50) NOT NULL,
    room_code        VARCHAR(10) NOT NULL,
    game_type        TINYINT,
    status           VARCHAR(20) NOT NULL,

    created_at       DATETIME NOT NULL,
    updated_at       DATETIME NOT NULL,

    CONSTRAINT uk_room_code
        UNIQUE (room_code)

);

CREATE INDEX idx_room_tenant
    ON rooms (tenant_id);

CREATE INDEX idx_room_status
    ON rooms (status);

CREATE INDEX idx_room_code
    ON rooms (room_code);


CREATE TABLE IF NOT EXISTS room_players (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        VARCHAR(50) NOT NULL,
    room_code        VARCHAR(10) NOT NULL,
    user_id          BIGINT NOT NULL,
    role             VARCHAR(50) NOT NULL,
    status           VARCHAR(20) NOT NULL,

    joined_at        DATETIME NOT NULL,
    created_at       DATETIME NOT NULL,
    updated_at       DATETIME NOT NULL,

    CONSTRAINT uk_room_user
        UNIQUE (room_code, user_id)

);

CREATE INDEX idx_room_player_room
    ON room_players (room_code);

CREATE INDEX idx_room_player_user
    ON room_players (user_id);

CREATE INDEX idx_room_player_status
    ON room_players (status);

CREATE INDEX idx_room_player_tenant
    ON room_players (tenant_id);


CREATE TABLE IF NOT EXISTS games (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        VARCHAR(50) NOT NULL,
    room_code        VARCHAR(10) NOT NULL,
    status           VARCHAR(30) NOT NULL,
    current_turn     BIGINT,
    started_at       DATETIME NOT NULL,
    ended_at         DATETIME,

    created_at       DATETIME NOT NULL,
    updated_at       DATETIME NOT NULL

);

CREATE INDEX idx_game_room
    ON games (room_code);

CREATE INDEX idx_game_tenant
    ON games (tenant_id);

CREATE INDEX idx_game_status
    ON games (status);

CREATE INDEX idx_game_turn
    ON games (current_turn);

CREATE INDEX idx_tenant_room
    ON games (tenant_id, room_code);


CREATE TABLE IF NOT EXISTS game_results (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        VARCHAR(50) NOT NULL,
    game_id          BIGINT NOT NULL,
    winner_id        VARCHAR(50),
    completed_at     DATETIME NOT NULL,

    created_at       DATETIME NOT NULL,
    updated_at       DATETIME NOT NULL

);

CREATE INDEX idx_result_game
    ON game_results (game_id);

CREATE INDEX idx_result_tenant
    ON game_results (tenant_id);

CREATE INDEX idx_result_winner
    ON game_results (winner_id);


CREATE TABLE IF NOT EXISTS player_results (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        VARCHAR(50) NOT NULL,
    game_id          BIGINT NOT NULL,
    user_id          VARCHAR(50) NOT NULL,
    score            INT,
    player_rank      INT,

    created_at       DATETIME NOT NULL,
    updated_at       DATETIME NOT NULL,

    CONSTRAINT uk_game_user
        UNIQUE (game_id, user_id)

);

CREATE INDEX idx_player_game
    ON player_results (game_id);

CREATE INDEX idx_player_user
    ON player_results (user_id);

CREATE INDEX idx_player_tenant
    ON player_results (tenant_id);

CREATE INDEX idx_game_rank
    ON player_results (game_id, player_rank);


INSERT INTO game_config (
    tenant_id,
    game_type,
    min_players,
    max_players,
    roles_json
)
VALUES (
    'TEST-1',
    'TIC_TAC_TOE',
    2,
    2,
    '["X","O"]'
);
