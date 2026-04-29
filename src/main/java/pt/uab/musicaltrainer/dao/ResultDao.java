package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.ResultRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO para Resultado de Exercício.
 */
public class ResultDao extends AbstractDao<ResultRecord> {

    private static final Logger logger = LoggerFactory.getLogger(ResultDao.class);

    public ResultDao(DataSource dataSource) {
        super(dataSource);
    }

    public ResultRecord save(ResultRecord result) throws SQLException {
        String sql = "INSERT INTO results (session_id, exercise_id, user_answer, is_correct) VALUES (?, ?, ?, ?)";
        logger.debug("Guardando resultado: sessionId={}, exerciseId={}, correct={}",
            result.sessionId(), result.exerciseId(), result.isCorrect());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, result.sessionId());
            ps.setLong(2, result.exerciseId());
            ps.setString(3, result.userAnswer());
            ps.setBoolean(4, result.isCorrect());
            logger.debug("Parâmetros vinculados, executando INSERT");
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                long newId = keys.getLong(1);
                logger.info("Resultado guardado: id={}, sessionId={}, correct={}",
                    newId, result.sessionId(), result.isCorrect());
                return findById(newId).orElse(null);
            }
        }
        logger.warn("Falha ao recuperar id gerado para novo resultado");
        return null;
    }

    public Optional<ResultRecord> findById(Long id) throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results WHERE id = ?";
        logger.debug("Procurando resultado por id={}", id);
        return queryForObject(sql, ps -> ps.setLong(1, id), this::mapRow);
    }

    public List<ResultRecord> findBySessionId(Long sessionId) throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results WHERE session_id = ? ORDER BY created_at ASC";
        logger.debug("Procurando resultados por sessionId={}", sessionId);
        List<ResultRecord> result = queryForList(sql, ps -> ps.setLong(1, sessionId), this::mapRow);
        logger.info("Resultados encontrados: sessionId={}, count={}", sessionId, result.size());
        return result;
    }

    public List<ResultRecord> findAll() throws SQLException {
        String sql = "SELECT id, session_id, exercise_id, user_answer, is_correct, created_at FROM results ORDER BY created_at DESC";
        logger.debug("Procurando todos os resultados");
        List<ResultRecord> result = queryForList(sql, this::mapRow);
        logger.info("Total de resultados: {}", result.size());
        return result;
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

    /**
     * Agrega resultados por tipo de exercício.
     * Devolve mapa de tipo → [total, corrects].
     */
    public Map<String, long[]> countByExerciseType() throws SQLException {
        String sql = "SELECT e.type, COUNT(*) as total, " +
            "SUM(CASE WHEN r.is_correct THEN 1 ELSE 0 END) as correct_count " +
            "FROM results r JOIN exercises e ON r.exercise_id = e.id " +
            "GROUP BY e.type";

        Map<String, long[]> result = new java.util.LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String type    = rs.getString("type");
                long total     = rs.getLong("total");
                long correct   = rs.getLong("correct_count");
                result.put(type, new long[]{total, correct});
                logger.debug("Tipo={}: total={}, correct={}", type, total, correct);
            }
        }
        logger.info("Agregação por tipo: {} tipos encontrados", result.size());
        return result;
    }
}
