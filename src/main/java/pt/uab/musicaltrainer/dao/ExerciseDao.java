package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.ExerciseRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static pt.uab.musicaltrainer.dao.JdbcDateHelper.get;

/**
 * DAO para Exercício.
 */
public class ExerciseDao extends AbstractDao<ExerciseRecord> {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDao.class);

    public ExerciseDao(DataSource dataSource) {
        super(dataSource);
    }

    public ExerciseRecord save(ExerciseRecord exercise) throws SQLException {
        String sql = "INSERT INTO exercises (type, difficulty, question, correct_answer) VALUES (?, ?, ?, ?)";
        logger.debug("Guardando exercício: type={}, difficulty={}, question={}",
            exercise.type(), exercise.difficulty(), exercise.question());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, exercise.type());
            ps.setInt(2, exercise.difficulty());
            ps.setString(3, exercise.question());
            ps.setString(4, exercise.correctAnswer());
            logger.debug("Parâmetros vinculados, executando INSERT");
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                long newId = keys.getLong(1);
                logger.info("Exercício guardado: id={}, type={}", newId, exercise.type());
                return findById(newId).orElse(null);
            }
        }
        logger.warn("Falha ao recuperar id gerado para novo exercício");
        return null;
    }

    public Optional<ExerciseRecord> findById(Long id) throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises WHERE id = ?";
        logger.debug("Procurando exercício por id={}", id);
        return queryForObject(sql, ps -> ps.setLong(1, id), this::mapRow);
    }

    public List<ExerciseRecord> findByType(String type) throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises WHERE type = ? ORDER BY created_at DESC";
        logger.debug("Procurando exercícios por type={}", type);
        List<ExerciseRecord> result = queryForList(sql, ps -> ps.setString(1, type), this::mapRow);
        logger.info("Exercícios encontrados: type={}, count={}", type, result.size());
        return result;
    }

    public List<ExerciseRecord> findAll() throws SQLException {
        String sql = "SELECT id, type, difficulty, question, correct_answer, created_at FROM exercises ORDER BY created_at DESC";
        logger.debug("Procurando todos os exercícios");
        List<ExerciseRecord> result = queryForList(sql, this::mapRow);
        logger.info("Total de exercícios: {}", result.size());
        return result;
    }

    private ExerciseRecord mapRow(ResultSet rs) throws SQLException {
        return new ExerciseRecord(
            rs.getLong("id"),
            rs.getString("type"),
            rs.getInt("difficulty"),
            rs.getString("question"),
            rs.getString("correct_answer"),
            get(rs, "created_at")
        );
    }
}
