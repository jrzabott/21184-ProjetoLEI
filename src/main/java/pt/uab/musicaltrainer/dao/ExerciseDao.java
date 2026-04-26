package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.ExerciseRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Exercise Data Access Object.
 * Plain JDBC with hardcoded SQL statements.
 */
public class ExerciseDao {
    private final DataSource dataSource;

    public ExerciseDao(DataSource dataSource) {
        this.dataSource = dataSource;
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

    public List<ExerciseRecord> findByType(String type) throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises WHERE type = ? ORDER BY created_at DESC";
        List<ExerciseRecord> exercises = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                exercises.add(mapRow(rs));
            }
        }
        return exercises;
    }

    public List<ExerciseRecord> findAll() throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises ORDER BY created_at DESC";
        List<ExerciseRecord> exercises = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                exercises.add(mapRow(rs));
            }
        }
        return exercises;
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
