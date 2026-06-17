ALTER TABLE game_detail
    MODIFY COLUMN away_hits   INT NOT NULL,
    MODIFY COLUMN away_errors INT NOT NULL,
    MODIFY COLUMN home_hits   INT NOT NULL,
    MODIFY COLUMN home_errors INT NOT NULL;
