ALTER TABLE game
    ADD COLUMN game_number TINYINT NOT NULL DEFAULT 1,
    DROP INDEX uq_game,
    ADD UNIQUE KEY uq_game (season, game_type, game_date, home_team, away_team, game_number);
