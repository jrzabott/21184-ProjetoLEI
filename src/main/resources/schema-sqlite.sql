-- Schema SQLite — sintaxe incompativel com H2 (H2 usa BIGINT AUTO_INCREMENT).
-- Em SQLite, INTEGER PRIMARY KEY e um alias do rowid e auto-incrementa automaticamente.
-- Ver SqliteOperationalIT para verificacao de compatibilidade.

DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS exercises;

CREATE TABLE exercises (
    id             INTEGER PRIMARY KEY,
    type           TEXT    NOT NULL,
    difficulty     INTEGER NOT NULL CHECK (difficulty >= 1 AND difficulty <= 10),
    question       TEXT    NOT NULL,
    correct_answer TEXT    NOT NULL,
    created_at     TEXT    DEFAULT (datetime('now'))
);

CREATE TABLE sessions (
    id                INTEGER PRIMARY KEY,
    start_time        TEXT    NOT NULL,
    end_time          TEXT,
    total_exercises   INTEGER DEFAULT 0,
    correct_answers   INTEGER DEFAULT 0,
    incorrect_answers INTEGER DEFAULT 0,
    created_at        TEXT    DEFAULT (datetime('now'))
);

CREATE TABLE results (
    id           INTEGER PRIMARY KEY,
    session_id   INTEGER NOT NULL,
    exercise_id  INTEGER NOT NULL,
    user_answer  TEXT    NOT NULL,
    is_correct   INTEGER NOT NULL,
    created_at   TEXT    DEFAULT (datetime('now')),
    FOREIGN KEY (session_id)  REFERENCES sessions  (id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sessions_start_time ON sessions (start_time);
CREATE INDEX IF NOT EXISTS idx_results_session      ON results  (session_id);
CREATE INDEX IF NOT EXISTS idx_results_exercise     ON results  (exercise_id);
