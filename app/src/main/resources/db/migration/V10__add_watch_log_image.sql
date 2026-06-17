CREATE TABLE watch_log_image (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    watch_log_id BIGINT       NOT NULL,
    image_url    VARCHAR(500) NOT NULL,
    image_type   VARCHAR(10)  NOT NULL,
    sort_order   INT          NOT NULL DEFAULT 0,
    created_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_watch_log_image FOREIGN KEY (watch_log_id) REFERENCES watch_log (id) ON DELETE CASCADE,
    INDEX idx_watch_log_image_log (watch_log_id, sort_order)
);
