CREATE TABLE members
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    nickname   VARCHAR(255) NOT NULL,
    role       ENUM ('ADMIN', 'USER')                NOT NULL,
    status     ENUM ('ACTIVE', 'INACTIVE', 'WITHDRAWN') NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE members_social_accounts
(
    member_id   BIGINT       NOT NULL,
    provider    ENUM ('GOOGLE', 'KAKAO') NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_social_accounts_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE refresh_tokens
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    token      VARCHAR(255) NOT NULL,
    member_id  BIGINT       NOT NULL,
    expires_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token UNIQUE (token)
);
