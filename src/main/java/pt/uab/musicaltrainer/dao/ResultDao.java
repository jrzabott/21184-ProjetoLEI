package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.ResultRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Result Data Access Object.
 * Plain JDBC with hardcoded SQL statements.
 */
public class ResultDao {
    private final DataSource dataSource;

    public ResultDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ResultRecord save(ResultRecord result) throws SQLException {
        String sql = "INSERT INTO results (session_id, exercise_id, user_answer, is_correct) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, result.sessionId());
            ps.setLong(2, result.exerciseId());
            ps.setString(3, result.userAnswer());
            ps.setBoolean(4, result.isCorrect());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return findById(keys.getLong(1)).orElse(null);
            }
        }
        return null;
    }

    public Optional<ResultRecord> findById(Long id) throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results WHERE id = ?";

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

    public List<ResultRecord> findBySessionId(Long sessionId) throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results WHERE session_id = ? ORDER BY created_at ASC";
        List<ResultRecord> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public List<ResultRecord> findAll() throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results ORDER BY created_at DESC";
        List<ResultRecord> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    private ResultRecord mapRow(ResultSet rs) throws SQLException {
        return new ResultRecord(
            rs.getLong("id"),
            rs.getLong("session_id"),
            rs.getLong("exercise_id"),
            rs.getString("user_answer"),
            rs.getBoolean("is_correct"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
