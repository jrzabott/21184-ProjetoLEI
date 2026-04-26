package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.ExerciseRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO para Exercício.
 */
public class ExerciseDao extends AbstractDao<ExerciseRecord> {
    public ExerciseDao(DataSource dataSource) {
        super(dataSource);
    }

    public ExerciseRecord save(ExerciseRecord exercise) throws SQLException {
        String sql = "INSERT INTO exercises (type, difficulty, question, correct_answer) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, exercise.type());
            ps.setInt(2, exercise.difficulty());
            ps.setString(3, exercise.question());
            ps.setString(4, exercise.correctAnswer());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return findById(keys.getLong(1)).orElse(null);
            }
        }
        return null;
    }

    public Optional<ExerciseRecord> findById(Long id) throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises WHERE id = ?";
        return queryForObject(sql, ps -> ps.setLong(1, id), this::mapRow);
    }

    public List<ExerciseRecord> findByType(String type) throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises WHERE type = ? ORDER BY created_at DESC";
        return queryForList(sql, ps -> ps.setString(1, type), this::mapRow);
    }

    public List<ExerciseRecord> findAll() throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises ORDER BY created_at DESC";
        return queryForList(sql, this::mapRow);
    }

    private ExerciseRecord mapRow(ResultSet rs) throws SQLException {
        return new ExerciseRecord(
            rs.getLong("id"),
            rs.getString("type"),
            rs.getInt("difficulty"),
            rs.getString("question"),
            rs.getString("correct_answer"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
