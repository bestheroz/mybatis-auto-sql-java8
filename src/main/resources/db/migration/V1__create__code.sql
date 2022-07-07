CREATE TABLE code
(
    id            BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    type          VARCHAR(100)          NOT NULL,
    `value`       VARCHAR(100)          NOT NULL,
    text          VARCHAR(1000)         NOT NULL,
    available     BOOLEAN DEFAULT FALSE NOT NULL,
    display_order INT(3)                NOT NULL,
    created_by    BIGINT(20)            NOT NULL,
    created       DATETIME(6)           NOT NULL,
    updated_by    BIGINT(20)            NOT NULL,
    updated       DATETIME(6)           NOT NULL
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
