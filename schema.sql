
CREATE TABLE IF NOT EXISTS game_config (

    tenant_id        VARCHAR(50) NOT NULL,
    game_type        VARCHAR(50) NOT NULL,
    min_players      INT NOT NULL,
    max_players      INT NOT NULL,
    roles_json       JSON,

    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (tenant_id, game_type)

);


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

INSERT INTO game_config (
    tenant_id,
    game_type,
    min_players,
    max_players,
    roles_json
) VALUES (
             'TEST-1',
             'LUDO',
             2,
             4,
             '["RED","GREEN","YELLOW","BLUE"]'
         );


CREATE TABLE IF NOT EXISTS rooms (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        VARCHAR(50) NOT NULL,
    room_code        VARCHAR(10) NOT NULL,
    game_type        TINYINT NOT NULL,
    match_type       VARCHAR(20) NOT NULL,
    bot_difficulty   VARCHAR(20),
    status           VARCHAR(20) NOT NULL,

    started_at       DATETIME,
    ended_at         DATETIME,

    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_room_code
        UNIQUE (room_code)

);


CREATE INDEX idx_room_tenant
    ON rooms (tenant_id);
CREATE INDEX idx_room_status
    ON rooms (status);
CREATE INDEX idx_room_code
    ON rooms (room_code);


CREATE TABLE IF NOT EXISTS players (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        VARCHAR(50) NOT NULL,
    user_id          BIGINT NOT NULL,
    role             VARCHAR(50) NOT NULL,
    status           VARCHAR(20) NOT NULL,
    joined_at        DATETIME NOT NULL,
    room_id          BIGINT NOT NULL,

    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_room_user
        UNIQUE (room_id, user_id),

    CONSTRAINT fk_room_players_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id)

);

CREATE INDEX idx_player_room
    ON players (room_id);
CREATE INDEX idx_player_user
    ON players (user_id);
CREATE INDEX idx_player_status
    ON players (status);


CREATE TABLE IF NOT EXISTS game_results (

    id               BIGINT AUTO_INCREMENT PRIMARY KEY,

    tenant_id        VARCHAR(50) NOT NULL,
    room_id          BIGINT NOT NULL,
    winner_id        BIGINT,
    completed_at     DATETIME NOT NULL,

    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_game_result_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id)

);

CREATE INDEX idx_result_game
    ON game_results (room_id);

CREATE INDEX idx_result_tenant
    ON game_results (tenant_id);

CREATE INDEX idx_result_winner
    ON game_results (winner_id);


ALTER TABLE game_db.rooms
    MODIFY COLUMN match_type VARCHAR(50) NULL;