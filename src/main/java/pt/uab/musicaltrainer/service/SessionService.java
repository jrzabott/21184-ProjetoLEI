package pt.uab.musicaltrainer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.api.ResourceNotFoundException;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.SessionRecord;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Gere o ciclo de vida das sessões de treino.
 * Inicio, fim, e cálculo de métricas simples.
 */
@Service
public class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    private final DaoFactory daoFactory;

    public SessionService(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public SessionRecord startSession() throws Exception {
        logger.debug("Iniciando nova sessão");
        SessionRecord session = new SessionRecord(null, LocalDateTime.now(), null, 0, 0, 0, null);
        SessionRecord saved = daoFactory.createSessionDao().save(session);
        logger.info("Sessão iniciada: id={}", saved.id());
        return saved;
    }

    public SessionRecord endSession(Long sessionId) throws Exception {
        logger.debug("Terminando sessão: id={}", sessionId);

        SessionRecord session = daoFactory.createSessionDao().findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada: " + sessionId));

        if (session.endTime() != null) {
            logger.info("Sessão {} já estava terminada - a devolver estado existente sem escrever", sessionId);
            return session;
        }

        SessionRecord ended = new SessionRecord(
            session.id(), session.startTime(), LocalDateTime.now(),
            session.totalExercises(), session.correctAnswers(),
            session.incorrectAnswers(), session.createdAt()
        );
        daoFactory.createSessionDao().update(ended);
        logger.info("Sessão terminada: id={}, total={}", sessionId, ended.totalExercises());
        return ended;
    }

    public double calculateAccuracy(SessionRecord session) {
        if (session.totalExercises() == 0) return 0.0;
        return (double) session.correctAnswers() / session.totalExercises();
    }

    public long calculateDurationSeconds(SessionRecord session) {
        if (session.endTime() == null) return 0L;
        return ChronoUnit.SECONDS.between(session.startTime(), session.endTime());
    }

    public void incrementCounters(Long sessionId, boolean correct) throws Exception {
        logger.debug("Incrementando contadores: sessionId={}, correct={}", sessionId, correct);
        daoFactory.createSessionDao().incrementCounters(sessionId, correct);
    }
}
