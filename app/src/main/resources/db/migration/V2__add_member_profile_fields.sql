ALTER TABLE members
    ADD COLUMN profile_image_url VARCHAR(500) NULL,
    ADD COLUMN favorite_team     ENUM ('DOOSAN', 'LG', 'KT', 'SSG', 'NC', 'SAMSUNG', 'HANWHA', 'LOTTE', 'KIA', 'KIWOOM') NULL,
    ADD COLUMN bio               VARCHAR(100) NULL,
    ADD COLUMN is_public         BOOLEAN      NOT NULL DEFAULT TRUE;
