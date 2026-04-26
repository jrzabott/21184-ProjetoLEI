# ADR-012: Data Model and Persistence Strategy

**Data:** 2026-04-26  
**Estado:** Aceite  
**Decisores:** Daniel Junior  
**Referências:** ADR-010 (JDBC DAO pattern), ADR-007 (H2 database)

---

## Contexto

Fase 2 requer implementação da camada de persistência: armazenar sessões de treino, exercícios gerados, e resultados de respostas do utilizador. O modelo de dados precisa ser simples, legível, e alinhado com os requisitos funcionais F06 (persistência de sessões) e os critérios de aceitação da proposta.

---

## Decisão

### Entidades e Schema

Três tabelas principais no schema `schema.sql`:

#### `sessions` 
Regista uma sessão de treino completa.

```sql
CREATE TABLE sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    total_exercises INT DEFAULT 0,
    correct_answers INT DEFAULT 0,
    incorrect_answers INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

- `id`: Chave primária, auto-incrementada
- `start_time`: Quando a sessão começou
- `end_time`: Quando a sessão terminou (null se ainda ativa)
- `total_exercises`: Número total de exercícios nesta sessão
- `correct_answers`: Quantos acertos
- `incorrect_answers`: Quantos erros
- `created_at`: Timestamp de criação (auditoria)

#### `exercises`
Regista um exercício gerado algoritmicamente.

```sql
CREATE TABLE exercises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    difficulty INT NOT NULL CHECK (difficulty >= 1 AND difficulty <= 10),
    question VARCHAR(500) NOT NULL,
    correct_answer VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

- `id`: Chave primária, auto-incrementada (atribuída quando o exercício é gerado)
- `type`: Tipo de exercício (INTERVAL, SCALE, CHORD)
- `difficulty`: Nível de dificuldade (1-10)
- `question`: Descrição ou dados do exercício (serializado conforme necessário)
- `correct_answer`: A resposta correcta (em formato legível)
- `created_at`: Timestamp de criação

#### `results`
Regista cada resposta do utilizador a um exercício.

```sql
CREATE TABLE results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    user_answer VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE SET NULL
);
```

- `id`: Chave primária, auto-incrementada
- `session_id`: FK para sessions (ON DELETE CASCADE - apagar resultado se sessão for apagada)
- `exercise_id`: FK para exercises (ON DELETE SET NULL - manter resultado mesmo se exercício for apagado)
- `user_answer`: A resposta que o utilizador deu
- `is_correct`: Se a resposta estava correta
- `created_at`: Timestamp de criação (auditoria)

### Data Access Objects (Plain JDBC)

Três DAOs simples, sem Spring annotations, com hardcoded SQL:

```java
public class SessionDao {
    private final DataSource dataSource;
    
    public SessionRecord save(SessionRecord session) throws SQLException {
        String sql = "INSERT INTO sessions (start_time, ...) VALUES (?, ...)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // ... set params
            ps.executeUpdate();
            // ... extract generated key
        }
        return saved;
    }
    // ... findById, update, findAll ...
}
```

**Características:**
- Plain Java, sem `@Repository` ou Spring magic
- Constructor-injected DataSource
- try-with-resources para automatic connection/statement cleanup
- Throws `SQLException` (caller decides error handling)
- Simple `mapRow()` private method para convert ResultSet → Record

### DTOs (Java Records)

Imutáveis, type-safe:

```java
public record SessionRecord(
    Long id,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer totalExercises,
    Integer correctAnswers,
    Integer incorrectAnswers,
    LocalDateTime createdAt
) {}

public record ExerciseRecord(
    Long id,
    String type,
    Integer difficulty,
    String question,
    String correctAnswer,
    LocalDateTime createdAt
) {}

public record ResultRecord(
    Long id,
    Long sessionId,
    Long exerciseId,
    String userAnswer,
    Boolean isCorrect,
    LocalDateTime createdAt
) {}
```

---

## Rationale

**Por que este schema?**
- Minimal: apenas o necessário para rastrear sessões e resultados
- Auditoria integrada: `created_at` em cada tabela
- Integridade referencial: FK constraints com políticas apropriadas (CASCADE para sessions, SET NULL para exercises)
- Simples de entender: qualquer alguém consegue ler `schema.sql` e compreender o modelo

**Por que DAOs com JDBC puro (sem Spring)?**
- Zero magic: SQL é explícito, visível no código
- Fácil de defender: "Este método executa este SQL com estes parâmetros"
- Controle completo: sabemos exatamente o que acontece em cada operação
- Legibilidade: não há anotações mágicas ou lazy loading surpresas
- Spring magic fica para a API (controllers) - modelo de dados fica limpo

**Por que Records?**
- Imutável por design (segurança de thread)
- Boilerplate mínimo (constructor, equals, hashCode, toString gerados automaticamente)
- Type-safe: compiler verifica os tipos
- Moderno (Java 16+) e legível

---

## Consequências

### Positivas
- Schema claro e auditável
- DAOs simples, sem Hibernate magic ou lazy loading
- Fácil de testar (just create DAO with DataSource, no Spring container needed)
- Fácil de explicar à defesa: "SQL explícito, conversão manual para Records"
- Código de negócio (aplicação) fica separado de plumbing de persistência

### Negativas / Trade-offs
- Mais boilerplate em cada DAO do que com JPA (mas menos configuração)
- SQL duplicado entre métodos? Podemos extrair constantes se padrão emergir
- Sem cache de segundo nível (e.g. Hibernate second-level cache) - mas não é requisito

---

## Impactos

- **Fase 3 (Geradores):** Geradores chamam `ExerciseDao.save()` para persistir exercícios gerados
- **Fase 4 (Controllers):** Controllers injectam DAOs, usam para `GET /api/progress`, `POST /api/sessions/start`, etc.
- **Testes:** Testes de integração criam DataSource (via `@SpringBootTest` H2), injectam em DAOs, testam operações reais

---

**Status:** ✅ Pronto para Fase 3  
**Próximo:** Implementação de controllers que usam estes DAOs
