ALTER TABLE game
    MODIFY COLUMN status ENUM ('SCHEDULED', 'LIVE', 'COMPLETED', 'CANCELED', 'POSTPONED') NOT NULL DEFAULT 'SCHEDULED';

CREATE TABLE IF NOT EXISTS game_detail
(
    game_id     BIGINT  NOT NULL,
    away_hits   TINYINT NOT NULL,
    away_errors TINYINT NOT NULL,
    home_hits   TINYINT NOT NULL,
    home_errors TINYINT NOT NULL,
    innings     JSON    NOT NULL,
    PRIMARY KEY (game_id),
    CONSTRAINT fk_game_detail_game FOREIGN KEY (game_id) REFERENCES game (id)
);
