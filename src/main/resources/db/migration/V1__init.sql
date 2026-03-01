CREATE TABLE users (
                       id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name        VARCHAR(100) NOT NULL,
                       email       VARCHAR(150) NOT NULL UNIQUE,
    -- colonnes AuditEntity
                       created_by  VARCHAR(100),
                       created_on  TIMESTAMP,
                       updated_by  VARCHAR(100),
                       updated_on  TIMESTAMP
);

CREATE TABLE resources (
                           id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           name        VARCHAR(100) NOT NULL,
                           description TEXT,
                           capacity    INT NOT NULL DEFAULT 1,
                           active      BOOLEAN NOT NULL DEFAULT true,
    -- colonnes AuditEntity
                           created_by  VARCHAR(100),
                           created_on  TIMESTAMP,
                           updated_by  VARCHAR(100),
                           updated_on  TIMESTAMP
);

CREATE TABLE reservations (
                              id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              user_id      UUID NOT NULL REFERENCES users(id),
                              resource_id  UUID NOT NULL REFERENCES resources(id),
                              start_time   TIMESTAMP NOT NULL,
                              end_time     TIMESTAMP NOT NULL,
                              status       VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
                              CONSTRAINT chk_times CHECK (end_time > start_time),
    -- colonnes AuditEntity
                              created_by  VARCHAR(100),
                              created_on  TIMESTAMP,
                              updated_by  VARCHAR(100),
                              updated_on  TIMESTAMP
);

CREATE INDEX idx_reservations_resource_time
    ON reservations(resource_id, start_time, end_time)
    WHERE status != 'CANCELLED';