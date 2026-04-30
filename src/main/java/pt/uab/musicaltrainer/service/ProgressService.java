package pt.uab.musicaltrainer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.api.ProgressResponse;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.SessionRecord;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Agrega dados de progresso para o dashboard.
 */
@Service
public class ProgressService {

    private static final Logger logger = LoggerFactory.getLogger(ProgressService.class);
    private final DaoFactory daoFactory;

    public ProgressService(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public ProgressResponse buildProgress() throws Exception {
        logger.debug("Construindo dados de progresso");

        List<SessionRecord> sessions = daoFactory.createSessionDao().findAll();

        long totalSessions = sessions.size();
        long totalExercises = sessions.stream().mapToLong(SessionRecord::totalExercises).sum();
        long totalCorrect = sessions.stream().mapToLong(SessionRecord::correctAnswers).sum();
        double accuracy = totalExercises == 0 ? 0.0 : (double) totalCorrect / totalExercises;

        List<ProgressResponse.SessionSummary> recent = sessions.stream()
            .limit(10)
            .map(s -> new ProgressResponse.SessionSummary(
                s.id(),
                s.totalExercises() == 0 ? 0.0 : (double) s.correctAnswers() / s.totalExercises(),
                s.totalExercises()
            ))
            .collect(Collectors.toList());

        logger.info("Progresso: sessions={}, exercises={}, accuracy={}", totalSessions, totalExercises, accuracy);

        return new ProgressResponse(totalSessions, totalExercises, accuracy, Map.of(), recent);
    }
}
