package pt.uab.musicaltrainer.dao;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Factory para criação de DAOs com a DataSource selecionada.
 */
@Component
public class DaoFactory {

    private static final Logger logger = LoggerFactory.getLogger(DaoFactory.class);
    private final DataSource dataSource;

    public DaoFactory(DataSource dataSource) {
        this.dataSource = dataSource;
        logger.info("DaoFactory inicializado com DataSource");
    }

    public SessionDao createSessionDao() {
        logger.debug("Criando instância de SessionDao");
        SessionDao dao = new SessionDao(dataSource);
        logger.debug("SessionDao criada com sucesso");
        return dao;
    }

    public ExerciseDao createExerciseDao() {
        logger.debug("Criando instância de ExerciseDao");
        ExerciseDao dao = new ExerciseDao(dataSource);
        logger.debug("ExerciseDao criada com sucesso");
        return dao;
    }

    public ResultDao createResultDao() {
        logger.debug("Criando instância de ResultDao");
        ResultDao dao = new ResultDao(dataSource);
        logger.debug("ResultDao criada com sucesso");
        return dao;
    }
}
