package pt.uab.musicaltrainer.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Leitura e escrita de LocalDateTime via JDBC de forma portavel entre H2, SQLite e PostgreSQL.
 *
 * Raiz do problema: setTimestamp() no driver SQLite JDBC (xerial) grava o valor como epoch
 * em milissegundos (INTEGER), mas getTimestamp() espera texto no formato ISO. H2 e PostgreSQL
 * usam tipos TIMESTAMP nativos e nao exibem este comportamento.
 *
 * Solucao: setString() com formato ISO para escrita; getString() + parse flexivel para leitura.
 * Todos os drivers JDBC aceitam strings ISO para colunas de data/hora.
 */
class JdbcDateHelper {

    private static final DateTimeFormatter WRITE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // fraccao de segundo opcional: H2 e PostgreSQL podem incluir microsegundos na leitura
    private static final DateTimeFormatter READ_FMT = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
        .optionalEnd()
        .toFormatter();

    private JdbcDateHelper() {}

    static void set(PreparedStatement ps, int index, LocalDateTime ldt) throws SQLException {
        if (ldt == null) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, ldt.format(WRITE_FMT));
        }
    }

    static LocalDateTime get(ResultSet rs, String column) throws SQLException {
        String val = rs.getString(column);
        if (val == null) return null;
        return LocalDateTime.parse(val, READ_FMT);
    }
}
