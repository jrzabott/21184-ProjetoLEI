package pt.uab.musicaltrainer.dao;

import javax.sql.DataSource;
import org.springframework.stereotype.Component;

/**
 * Factory para criação de DAOs com a DataSource selecionada.
 */
@Component
public class DaoFactory {

    private final DataSource dataSource;

    public DaoFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SessionDao createSessionDao() {
        return new SessionDao(dataSource);
    }

    public ExerciseDao createExerciseDao() {
        return new ExerciseDao(dataSource);
    }

    public ResultDao createResultDao() {
        return new ResultDao(dataSource);
    }
}
