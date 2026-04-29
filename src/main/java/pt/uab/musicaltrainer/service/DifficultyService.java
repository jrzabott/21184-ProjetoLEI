package pt.uab.musicaltrainer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.ResultRecord;

import java.util.List;

/**
 * Implementa o algoritmo de dificuldade adaptativa (RF09).
 * Analisa os últimos 100 exercícios do tipo e sugere ajuste.
 * <p>
 * Algoritmo: acerto >= 80% → +1; acerto < 40% → -1; senão → manter.
 * Resultado clampado entre 1 e 10.
 */
@Service
public class DifficultyService {

    private static final Logger logger = LoggerFactory.getLogger(DifficultyService.class);
    private static final int    HISTORY_SIZE     = 100;
    private static final double THRESHOLD_UP     = 0.80;
    private static final double THRESHOLD_DOWN   = 0.40;

    private final DaoFactory daoFactory;

    public DifficultyService(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Sugere o próximo nível baseado no histórico recente.
     *
     * @param exerciseType tipo (INTERVAL, SCALE, CHORD)
     * @param currentDifficulty nível actual (1-10)
     * @return nível sugerido (1-10, clampado)
     */
    public int suggestDifficulty(String exerciseType, int currentDifficulty) throws Exception {
        logger.debug("Calculando dificuldade: type={}, current={}", exerciseType, currentDifficulty);

        List<ResultRecord> recent = daoFactory.createResultDao()
            .findLastNByExerciseType(exerciseType, HISTORY_SIZE);

        if (recent.isEmpty()) {
            logger.debug("Sem histórico para type={} — manter {}", exerciseType, currentDifficulty);
            return currentDifficulty;
        }

        long correct  = recent.stream().filter(ResultRecord::isCorrect).count();
        double accuracy = (double) correct / recent.size();

        int suggested;
        if (accuracy >= THRESHOLD_UP) {
            suggested = currentDifficulty + 1;
            logger.info("Acerto {}% >= 80% — aumentar: {} -> {}", (int)(accuracy*100), currentDifficulty, suggested);
        } else if (accuracy < THRESHOLD_DOWN) {
            suggested = currentDifficulty - 1;
            logger.info("Acerto {}% < 40% — diminuir: {} -> {}", (int)(accuracy*100), currentDifficulty, suggested);
        } else {
            suggested = currentDifficulty;
            logger.debug("Acerto {}% — manter {}", (int)(accuracy*100), currentDifficulty);
        }

        int clamped = Math.max(1, Math.min(10, suggested));
        logger.info("Dificuldade sugerida: type={}, result={}", exerciseType, clamped);
        return clamped;
    }
}
