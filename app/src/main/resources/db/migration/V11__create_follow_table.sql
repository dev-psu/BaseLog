CREATE TABLE follows (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    follower_id BIGINT      NOT NULL,
    following_id BIGINT     NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_follow (follower_id, following_id),
    KEY idx_follow_follower  (follower_id),
    KEY idx_follow_following (following_id)
);
