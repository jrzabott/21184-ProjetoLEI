package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.SessionRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Session Data Access Object.
 * Plain JDBC with hardcoded SQL statements.
 */
public class SessionDao {
    private final DataSource dataSource;

    public SessionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SessionRecord save(SessionRecord session) throws SQLException {
        String sql = "INSERT INTO sessions (start_time, end_time, total_exercises, correct_answers, incorrect_answers) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(session.startTime()));
            ps.setTimestamp(2, session.endTime() != null ? Timestamp.valueOf(session.endTime()) : null);
            ps.setInt(3, session.totalExercises());
            ps.setInt(4, session.correctAnswers());
            ps.setInt(5, session.incorrectAnswers());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return findById(keys.getLong(1)).orElse(null);
            }
        }
        return null;
    }

    public Optional<SessionRecord> findById(Long id) throws SQLException {
        String sql = "SELECT id, start_time, end_time, total_exercises, correct_answers, incorrect_answers, created_at FROM sessions WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public SessionRecord update(SessionRecord session) throws SQLException {
        String sql = "UPDATE sessions SET start_time = ?, end_time = ?, total_exercises = ?, correct_answers = ?, incorrect_answers = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(session.startTime()));
            ps.setTimestamp(2, session.endTime() != null ? Timestamp.valueOf(session.endTime()) : null);
            ps.setInt(3, session.totalExercises());
            ps.setInt(4, session.correctAnswers());
            ps.setInt(5, session.incorrectAnswers());
            ps.setLong(6, session.id());
            ps.executeUpdate();
        }
        return session;
    }

    public List<SessionRecord> findAll() throws SQLException {
        String sql = "SELECT id, start_time, end_time, total_exercises, correct_answers, incorrect_answers, created_at FROM sessions ORDER BY created_at DESC";
        List<SessionRecord> sessions = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sessions.add(mapRow(rs));
            }
        }
        return sessions;
    }

    private SessionRecord mapRow(ResultSet rs) throws SQLException {
        return new SessionRecord(
            rs.getLong("id"),
            rs.getTimestamp("start_time").toLocalDateTime(),
            rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
            rs.getInt("total_exercises"),
            rs.getInt("correct_answers"),
            rs.getInt("incorrect_answers"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
