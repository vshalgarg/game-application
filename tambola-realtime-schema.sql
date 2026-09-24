-- =============================================================================
-- Tambola Realtime Tables + Supabase Realtime Event Enablement
-- -----------------------------------------------------------------------------
-- TAMBOLA ONLY. This script does NOT touch the shared (tic-tac-toe/ludo)
-- realtime_room_lobby / realtime_game_state tables' structure, publication
-- membership, or replica identity.
--
-- Run this in the Supabase SQL editor for the project that hosts the Tambola
-- game-engine tables. It is idempotent (safe to re-run).
--
-- NOTE: DDL below mirrors the LIVE schema (veuhkewyrnuaofnanycz). room_code is
-- NOT NULL on every realtime row the engine writes (rules / tickets / claims /
-- game-state), so the Tambola engine MUST supply it on every insert.
--
-- Why:
--   The React UI subscribes to `postgres_changes` on:
--     realtime_tambola_rules, tambola_tickets, tambola_claims,
--     realtime_tambola_game_state
--   For Supabase to emit a realtime event when a row is INSERT/UPDATE/DELETE:
--     1) the table MUST be part of the `supabase_realtime` publication
--     2) REPLICA IDENTITY FULL so DELETE events carry the full old row
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1) Tambola tables (create only if missing)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS realtime_tambola_rules (
    room_id            BIGINT NOT NULL,
    room_code          VARCHAR NOT NULL,
    rule_type          VARCHAR NOT NULL,
    rule_order         INTEGER,
    max_winners        INTEGER NOT NULL DEFAULT 1,
    threshold          INTEGER,
    winner_player_ids  JSONB NOT NULL DEFAULT '[]'::jsonb,
    version            BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (room_id, rule_type)
);

CREATE TABLE IF NOT EXISTS tambola_tickets (
    ticket_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    room_id      BIGINT NOT NULL,
    room_code    VARCHAR NOT NULL,
    player_id    BIGINT NOT NULL,
    ticket_rows  JSONB NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tambola_claims (
    claim_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    room_id      BIGINT NOT NULL,
    room_code    VARCHAR NOT NULL,
    player_id    BIGINT NOT NULL,
    ticket_id    BIGINT NOT NULL,
    rule_type    VARCHAR NOT NULL,
    status       VARCHAR NOT NULL,
    submitted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS realtime_tambola_game_state (
    room_id                BIGINT PRIMARY KEY,
    room_code              VARCHAR NOT NULL,
    game_status            VARCHAR NOT NULL,
    called_numbers         JSONB NOT NULL DEFAULT '[]'::jsonb,
    timer_interval_seconds INTEGER,
    next_tick_at           TIMESTAMPTZ,
    players                JSONB NOT NULL DEFAULT '[]'::jsonb,
    version                BIGINT NOT NULL DEFAULT 0,
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    finished_at            TIMESTAMPTZ
);

-- -----------------------------------------------------------------------------
-- 2) Join the Supabase realtime publication (idempotent) - TAMBOLA tables only
-- -----------------------------------------------------------------------------
DO $$
DECLARE
    t TEXT;
BEGIN
    FOREACH t IN ARRAY ARRAY[
        'realtime_tambola_rules',
        'tambola_tickets',
        'tambola_claims',
        'realtime_tambola_game_state'
    ]
    LOOP
        IF NOT EXISTS (
            SELECT 1
            FROM pg_publication_tables
            WHERE pubname = 'supabase_realtime'
              AND schemaname = 'public'
              AND tablename = t
        ) THEN
            EXECUTE format('ALTER PUBLICATION supabase_realtime ADD TABLE public.%I', t);
            RAISE NOTICE 'Added % to supabase_realtime', t;
        ELSE
            RAISE NOTICE '% already in supabase_realtime', t;
        END IF;
    END LOOP;
END $$;

-- -----------------------------------------------------------------------------
-- 3) REPLICA IDENTITY FULL -> every event carries the full (old/new) row.
-- -----------------------------------------------------------------------------
ALTER TABLE realtime_tambola_rules        REPLICA IDENTITY FULL;
ALTER TABLE tambola_tickets               REPLICA IDENTITY FULL;
ALTER TABLE tambola_claims                REPLICA IDENTITY FULL;
ALTER TABLE realtime_tambola_game_state   REPLICA IDENTITY FULL;

-- -----------------------------------------------------------------------------
-- 3.1) Grants (RLS may be OFF, but table-level GRANTs are still required for
--      the PostgREST anon/authenticated keys to read/write via the REST API).
--
--      TAMBOLA tables: full CRUD.
--      realtime_room_lobby: READ-ONLY SELECT only (no structural change) -
--      the Tambola poller reads its `players` array for abandonment detection.
-- -----------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE
    realtime_tambola_rules,
    tambola_tickets,
    tambola_claims,
    realtime_tambola_game_state
TO anon, authenticated, service_role;

GRANT SELECT ON TABLE realtime_room_lobby
TO anon, authenticated, service_role;

-- -----------------------------------------------------------------------------
-- 4) Verify: TAMBOLA tables inside the realtime publication
-- -----------------------------------------------------------------------------
SELECT schemaname, tablename
FROM pg_publication_tables
WHERE pubname = 'supabase_realtime'
  AND tablename IN ('realtime_tambola_rules','tambola_tickets','tambola_claims','realtime_tambola_game_state')
ORDER BY tablename;