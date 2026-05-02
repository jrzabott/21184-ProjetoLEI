-- Drop tables in reverse order of FK dependencies
DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS exercises;

-- Table: exercises
CREATE TABLE exercises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    difficulty INT NOT NULL CHECK (difficulty >= 1 AND difficulty <= 10),
    question VARCHAR(500) NOT NULL,
    correct_answer VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: sessions
CREATE TABLE sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    total_exercises INT DEFAULT 0,
    correct_answers INT DEFAULT 0,
    incorrect_answers INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: results
CREATE TABLE results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    user_answer VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_sessions_start_time ON sessions (start_time);
CREATE INDEX idx_results_session ON results (session_id);
CREATE INDEX idx_results_exercise ON results (exercise_id);
