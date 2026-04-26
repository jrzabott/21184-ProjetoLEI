package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.ResultRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO para Resultado de Exercício.
 */
public class ResultDao extends AbstractDao<ResultRecord> {
    public ResultDao(DataSource dataSource) {
        super(dataSource);
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
        return queryForObject(sql, ps -> ps.setLong(1, id), this::mapRow);
    }

    public List<ResultRecord> findBySessionId(Long sessionId) throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results WHERE session_id = ? ORDER BY created_at ASC";
        return queryForList(sql, ps -> ps.setLong(1, sessionId), this::mapRow);
    }

    public List<ResultRecord> findAll() throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results ORDER BY created_at DESC";
        return queryForList(sql, this::mapRow);
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
