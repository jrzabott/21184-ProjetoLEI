package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.SessionRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of SessionDao.
 * Uses Spring's JdbcTemplate for database operations.
 */
@Repository
public class JdbcSessionDao implements SessionDao {
    private final JdbcTemplate jdbc;

    public JdbcSessionDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public SessionRecord save(SessionRecord session) {
        String sql = """
            INSERT INTO sessions (start_time, end_time, total_exercises, correct_answers, incorrect_answers, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, session.startTime() != null ? Timestamp.valueOf(session.startTime()) : null);
            ps.setTimestamp(2, session.endTime() != null ? Timestamp.valueOf(session.endTime()) : null);
            ps.setInt(3, session.totalExercises() != null ? session.totalExercises() : 0);
            ps.setInt(4, session.correctAnswers() != null ? session.correctAnswers() : 0);
            ps.setInt(5, session.incorrectAnswers() != null ? session.incorrectAnswers() : 0);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new SessionRecord(
            id,
            session.startTime(),
            session.endTime(),
            session.totalExercises(),
            session.correctAnswers(),
            session.incorrectAnswers(),
            LocalDateTime.now()
        );
    }

    @Override
    public Optional<SessionRecord> findById(Long id) {
        String sql = "SELECT * FROM sessions WHERE id = ?";
        try {
            SessionRecord record = jdbc.queryForObject(sql, this::mapRow, id);
            return Optional.ofNullable(record);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public SessionRecord update(SessionRecord session) {
        String sql = """
            UPDATE sessions
            SET start_time = ?, end_time = ?, total_exercises = ?, correct_answers = ?, incorrect_answers = ?
            WHERE id = ?
            """;

        jdbc.update(sql,
            session.startTime() != null ? Timestamp.valueOf(session.startTime()) : null,
            session.endTime() != null ? Timestamp.valueOf(session.endTime()) : null,
            session.totalExercises() != null ? session.totalExercises() : 0,
            session.correctAnswers() != null ? session.correctAnswers() : 0,
            session.incorrectAnswers() != null ? session.incorrectAnswers() : 0,
            session.id()
        );

        return session;
    }

    @Override
    public List<SessionRecord> findAll() {
        String sql = "SELECT * FROM sessions ORDER BY created_at DESC";
        return jdbc.query(sql, this::mapRow);
    }

    private SessionRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SessionRecord(
            rs.getLong("id"),
            rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null,
            rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
            rs.getInt("total_exercises"),
            rs.getInt("correct_answers"),
            rs.getInt("incorrect_answers"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
