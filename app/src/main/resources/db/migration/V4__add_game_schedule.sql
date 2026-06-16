CREATE TABLE game
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    season      SMALLINT NOT NULL,
    game_type   ENUM ('EXHIBITION', 'REGULAR', 'POSTSEASON') NOT NULL,
    game_date   DATE     NOT NULL,
    game_time   TIME     NULL,
    home_team   ENUM ('DOOSAN', 'LG', 'KT', 'SSG', 'NC', 'SAMSUNG', 'HANWHA', 'LOTTE', 'KIA', 'KIWOOM') NOT NULL,
    away_team   ENUM ('DOOSAN', 'LG', 'KT', 'SSG', 'NC', 'SAMSUNG', 'HANWHA', 'LOTTE', 'KIA', 'KIWOOM') NOT NULL,
    venue       VARCHAR(100) NULL,
    home_score  INT      NULL,
    away_score  INT      NULL,
    status      ENUM ('SCHEDULED', 'COMPLETED', 'CANCELED', 'POSTPONED') NOT NULL DEFAULT 'SCHEDULED',
    game_number SMALLINT NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_game (season, game_type, game_date, home_team, away_team, game_number)
);
