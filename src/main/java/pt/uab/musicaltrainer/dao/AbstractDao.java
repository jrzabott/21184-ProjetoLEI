package pt.uab.musicaltrainer.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe base para DAOs que usam JDBC puro.
 * Fornece métodos template para operações de leitura comuns (queryForObject, queryForList).
 * Encapsula o padrão try-with-resources e mapagem de ResultSet.
 */
public abstract class AbstractDao<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);
    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executa query que retorna zero ou um registro.
     *
     * @param sql a query SQL
     * @param setter lambda que define os parametros do PreparedStatement
     * @param mapper lambda que converte ResultSet em objeto T
     * @return Optional contendo o objeto mapeado ou vazio
     */
    protected Optional<T> queryForObject(String sql, ParameterSetter setter, RowMapper<T> mapper) throws SQLException {
        logger.debug("Executando queryForObject com SQL: {}", sql);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.setParameters(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Executa query que retorna múltiplos registros.
     *
     * @param sql a query SQL
     * @param setter lambda que define os parametros do PreparedStatement
     * @param mapper lambda que converte cada linha do ResultSet em objeto T
     * @return Lista de objetos mapeados (vazia se nenhum resultado)
     */
    protected List<T> queryForList(String sql, ParameterSetter setter, RowMapper<T> mapper) throws SQLException {
        logger.debug("Executando queryForList com SQL: {}", sql);
        List<T> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.setParameters(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Executa query sem parametros que retorna múltiplos registros.
     *
     * @param sql a query SQL
     * @param mapper lambda que converte cada linha do ResultSet em objeto T
     * @return Lista de objetos mapeados
     */
    protected List<T> queryForList(String sql, RowMapper<T> mapper) throws SQLException {
        return queryForList(sql, ps -> {}, mapper);
    }

    /**
     * Define parametros num PreparedStatement.
     */
    @FunctionalInterface
    protected interface ParameterSetter {
        void setParameters(PreparedStatement ps) throws SQLException;
    }

    /**
     * Mapeia uma linha de ResultSet para objeto T.
     */
    @FunctionalInterface
    protected interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }
}
