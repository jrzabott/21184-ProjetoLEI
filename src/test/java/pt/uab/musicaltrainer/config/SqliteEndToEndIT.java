package pt.uab.musicaltrainer.config;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.api.GenerateRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Teste de integração end-to-end com SQLite como base de dados.
 * Verifica que schema-sqlite.sql e inicializado correctamente e que
 * o fluxo completo (sessao → exercicio → resposta → progresso) funciona.
 *
 * Limitacoes conhecidas (documentadas como marcadores de regressao):
 *
 * 1) SqliteStrategy.getUrl() hardcodeia o caminho ("jdbc:sqlite:musical-trainer.db").
 *    DataSourceConfig usa strategy.getUrl() directamente, nao le spring.datasource.url.
 *    Nao e possivel apontar para um ficheiro temporario via @TestPropertySource.
 *    Correccao futura: injectar o caminho via @Value em SqliteStrategy.
 *
 * 2) Se o musical-trainer.db existir com timestamps guardados como epoch ms
 *    (comportamento de versoes anteriores), o SessionDao falha ao fazer parse
 *    com "Unparseable date: 1779446259732". O schema-sqlite.sql usa datetime('now')
 *    que produz "2026-05-22 10:37:39" mas a BD antiga tem numeros.
 *    Correccao futura: migrar dados existentes ou normalizar o mapRow() no SessionDao.
 *
 * atenção: este teste altera dados do ficheiro musical-trainer.db se correr
 * com db.type=SQLITE. Em ambiente CI deve usar H2 (o default). Para correr
 * intencionalmente: mvn test -Dtest=SqliteEndToEndIT -Ddb.type=SQLITE
 */
@Tag("sqlite")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "db.type=SQLITE"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SqliteEndToEndIT {

    @Autowired TestRestTemplate rest;

    @Test
    @Order(1)
    void startSessionReturnsSqliteId() {
        var session = rest.postForObject("/api/sessions/start", Map.of(), Map.class);
        assertThat(session).isNotNull();
        // campo e sessionId (nao id) — este teste tambem verifica o contrato do SessionResponse
        // o bug session.id em vez de session.sessionId causava sessionId=0 e dados perdidos
        assertThat(session.get("sessionId")).isNotNull();
        long id = ((Number) session.get("sessionId")).longValue();
        assertThat(id).isGreaterThan(0);
    }

    @Test
    @Order(2)
    void generateAndAnswerExerciseInSqlite() {
        var session = rest.postForObject("/api/sessions/start", Map.of(), Map.class);
        long sessionId = ((Number) session.get("sessionId")).longValue();

        var ex = rest.postForObject("/api/exercises/generate",
            new GenerateRequest("INTERVAL", 1, sessionId), Map.class);
        assertThat(ex.get("exerciseId")).isNotNull();

        long exerciseId = ((Number) ex.get("exerciseId")).longValue();
        List<?> notes = (List<?>) ex.get("notes");
        int[] noteArr = notes.stream().mapToInt(n -> ((Number) n).intValue()).toArray();

        var answer = rest.postForObject("/api/exercises/answer",
            Map.of("exerciseId", exerciseId, "sessionId", sessionId,
                   "notes", noteArr, "responseTimeMs", 500), Map.class);
        assertThat(answer.get("correct")).isNotNull();
    }

    @Test
    @Order(3)
    void endSessionAndGetProgressInSqlite() {
        // cria sessao com dados para verificar o fluxo completo
        var session = rest.postForObject("/api/sessions/start", Map.of(), Map.class);
        long sessionId = ((Number) session.get("sessionId")).longValue();

        var ex = rest.postForObject("/api/exercises/generate",
            new GenerateRequest("CHORD", 1, sessionId), Map.class);
        long exId = ((Number) ex.get("exerciseId")).longValue();
        List<?> notes = (List<?>) ex.get("notes");
        int[] noteArr = notes.stream().mapToInt(n -> ((Number) n).intValue()).toArray();
        rest.postForObject("/api/exercises/answer",
            Map.of("exerciseId", exId, "sessionId", sessionId,
                   "notes", noteArr, "responseTimeMs", 500), Map.class);

        var ended = rest.postForObject("/api/sessions/" + sessionId + "/end", Map.of(), Map.class);
        assertThat(((Number) ended.get("totalExercises")).intValue()).isEqualTo(1);

        // verificar que o progresso reflecte os dados guardados
        var progress = rest.getForObject("/api/progress", Map.class);
        assertThat(((Number) progress.get("totalSessions")).longValue()).isGreaterThan(0);
    }
}
