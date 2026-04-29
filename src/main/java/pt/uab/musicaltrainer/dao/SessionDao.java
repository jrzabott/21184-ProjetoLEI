package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.SessionRecord;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO para Sessão de Treino.
 */
public class SessionDao extends AbstractDao<SessionRecord> {
    private static final Logger logger = LoggerFactory.getLogger(SessionDao.class);

    public SessionDao(DataSource dataSource) {
        super(dataSource);
    }

    public SessionRecord save(SessionRecord session) throws SQLException {
        String sql = "INSERT INTO sessions (start_time, end_time, total_exercises, correct_answers, incorrect_answers) VALUES (?, ?, ?, ?, ?)";
        logger.debug("Salvando nova sessão: startTime={}, endTime={}, totalExercises={}, correctAnswers={}, incorrectAnswers={}",
            session.startTime(), session.endTime(), session.totalExercises(), session.correctAnswers(), session.incorrectAnswers());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(session.startTime()));
            ps.setTimestamp(2, session.endTime() != null ? Timestamp.valueOf(session.endTime()) : null);
            ps.setInt(3, session.totalExercises());
            ps.setInt(4, session.correctAnswers());
            ps.setInt(5, session.incorrectAnswers());
            logger.debug("Parâmetros vinculados, executando INSERT");
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                long newId = keys.getLong(1);
                logger.info("Sessão salva com sucesso, ID gerado: {}", newId);
                return findById(newId).orElse(null);
            }
        }
        logger.warn("Falha ao recuperar ID gerado para nova sessão");
        return null;
    }

    public Optional<SessionRecord> findById(Long id) throws SQLException {
        String sql = "SELECT id, start_time, end_time, total_exercises, correct_answers, incorrect_answers, created_at FROM sessions WHERE id = ?";
        return queryForObject(sql, ps -> ps.setLong(1, id), this::mapRow);
    }

    public SessionRecord update(SessionRecord session) throws SQLException {
        String sql = "UPDATE sessions SET start_time = ?, end_time = ?, total_exercises = ?, correct_answers = ?, incorrect_answers = ? WHERE id = ?";
        logger.debug("Atualizando sessão ID={}: startTime={}, endTime={}, totalExercises={}, correctAnswers={}, incorrectAnswers={}",
            session.id(), session.startTime(), session.endTime(), session.totalExercises(), session.correctAnswers(), session.incorrectAnswers());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(session.startTime()));
            ps.setTimestamp(2, session.endTime() != null ? Timestamp.valueOf(session.endTime()) : null);
            ps.setInt(3, session.totalExercises());
            ps.setInt(4, session.correctAnswers());
            ps.setInt(5, session.incorrectAnswers());
            ps.setLong(6, session.id());
            logger.debug("Parâmetros vinculados, executando UPDATE para ID={}", session.id());
            int rowsAffected = ps.executeUpdate();
            logger.info("Sessão ID={} atualizada com sucesso, {} linhas afetadas", session.id(), rowsAffected);
        }
        return session;
    }

    public List<SessionRecord> findAll() throws SQLException {
        String sql = "SELECT id, start_time, end_time, total_exercises, correct_answers, incorrect_answers, created_at FROM sessions ORDER BY created_at DESC";
        return queryForList(sql, this::mapRow);
    }

    public void incrementCounters(Long sessionId, boolean correct) throws SQLException {
        logger.debug("Incrementando contadores: sessionId={}, correct={}", sessionId, correct);
        String sql = "UPDATE sessions SET " +
            "total_exercises   = total_exercises + 1, " +
            "correct_answers   = correct_answers + ?, " +
            "incorrect_answers = incorrect_answers + ? " +
            "WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, correct ? 1 : 0);
            ps.setInt(2, correct ? 0 : 1);
            ps.setLong(3, sessionId);
            int rows = ps.executeUpdate();
            logger.info("Contadores actualizados: sessionId={}, correct={}, rows={}", sessionId, correct, rows);
        }
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
