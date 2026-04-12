# ADR-010 - DAO classes com JDBC puro em vez de Spring Data JPA

**Data:** 2026-04-12  
**Estado:** Aceite - substitui a abordagem JPA referenciada em ADR-007  
**Decisores:** Daniel Junior

---

## Contexto

A camada de persistência precisa de mapear os objectos de domínio (Exercise, Session, UserScore) para a base de dados relacional e executar queries. 

A abordagem inicial considerada era Spring Data JPA com `@Entity` e `JpaRepository`. 

Lembrei-me da minha experiência com o projeto final de curso da UpSkill aonde descobri "tarde demais" que JPA não é uma solução *out-of-the-box ready*. Existe estudo e considerações sobre problemas e armadilhas:

  - N + 1
  - Produtos Cartesianos Enormes
  - A dependência de *Large Entities* - objectos enormes que representam todo um registo de uma tabela para obter um campo
  - Lidar com ciclo de vida de forma "indireta"

JPA introduz complexidade de "plumbing" (gestão de sessões, lazy loading, proxies, cache de primeiro e segundo nível, configuração de DDL), torna ainda mais difícil de controlar e ainda posso embaralhar-me durante a defesa caso algo corra menos bem durante a defesa do projeto.

---

## Decisão

A camada de persistência usa **DAO classes (Data Access Objects) somado a java Records (POJOs)**, com SQL hardcoded em cada DAO. <br>Sem anotações mágicas`@Entity`, sem `JpaRepository`, sem Hibernate, sem `spring-boot-starter-data-jpa`. O schema da base de dados é gerido por um ficheiro `src/main/resources/schema.sql` carregado automaticamente pelo Spring Boot no arranque. DTOs são implementados como **Java records** (Java 16+).

### **Estrutura:** 
#### *(Rascunho pode sofrer alterações)*
```
pt.uab.musicaltrainer
  .dao/
      ExerciseDao.java      - CRUD + queries de Exercise
      SessionDao.java       - CRUD + queries de Session
      UserScoreDao.java     - CRUD + queries de UserScore
  .dto/
      ExerciseRequest.java  - record
      ExerciseResponse.java - record
      AnswerRequest.java    - record
      AnswerResponse.java   - record
      SessionResponse.java  - record
      ProgressResponse.java - record
```

### **Exemplo de DAO:** 
#### *(Rascunho gerado por IA pode e muito provavelmente há de ser alterado)*
```java
@Repository
public class ExerciseDao {
    private final JdbcTemplate jdbc;

    public ExerciseDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Exercise findById(Long id) {
        String sql = "SELECT * FROM exercises WHERE id = ?";
        return jdbc.queryForObject(sql, this::mapRow, id);
    }

    public Long save(Exercise exercise) {
        String sql = """
            INSERT INTO exercises (type, difficulty, question_data, correct_answer, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, exercise.type().name());
            ps.setInt(2, exercise.difficulty());
            ps.setString(3, exercise.questionData());
            ps.setString(4, exercise.correctAnswer());
            ps.setTimestamp(5, Timestamp.from(exercise.createdAt()));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
```

**Dependência Maven:** `spring-boot-starter-jdbc` (em vez de `spring-boot-starter-data-jpa`).
<br> *Dependência anda em avaliação.*

---

## Alternativas consideradas

| Alternativa | Razão de rejeição                                                                                                                                                                                                                                                                                              |
|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Spring Data JPA + `@Entity` + `JpaRepository` | Introduz Hibernate, lazy loading, proxies, cache - "plumbing" que o desenolvedor não controla completamente e que é difícil de defender em detalhes se o júri perguntar sobre comportamento inesperado. Abstracção excessiva para o scope.                                                                     |
| MyBatis | Mais configuração que `JdbcTemplate`; menos standard no ecossistema Spring; sem vantagem sobre JDBC directo para este scope.                                                                                                                                                                                   |
| JDBC puro sem Spring (`DriverManager`, `Connection`) | Mais verboso que `JdbcTemplate` sem ganho real; gestão manual de conexões é propensa a erros (connection leaks). `JdbcTemplate` resolve isso mantendo o SQL explícito.  *Ainda estou a pensar na abordagem, se JDBC puro ou JDBC templates, inclinado a menos linhas de códigos ou gestão de erros e estados.* |
---

## Consequências

**Positivas:**
- Todo o SQL é visível e explícito nos DAOs - evitando anotações mágicas do Spring e comportamentos pouco observáveis
- Controla sobre o que acontece em cada operação de base de dados
- Fácil de explicar e defender: "este método executa este SQL com estes parâmetros" ao invés de: Spring com a anotação @Entity e JpaRepository tem este comportamento "mágico", em que o SQL é gerado por baixo dos panos e o comportamento pode ser afectado por configurações de cache, lazy loading, etc.
- Schema em `schema.sql` é legível e documenta a estrutura da base de dados de forma inequívoca
- DTOs como Java records são imutáveis, concisos e modernos (Java 16+). Sintaxe curta e legível.
- Mudança de base de dados (H2 → PostgreSQL/SQLite3) requer apenas alterar o driver e o `schema.sql` - sem anotações JPA para ajustar

**Negativas / trade-offs:**
- Mais código boilerplate do que JPA para operações simples (INSERT, SELECT por ID)
  - hardcoding de SQL em cada método, ou a extração para um ficheiro de mapeamento SQL (como `SqlMapper`), ou large clobs com versionamento na DB. *Ainda a decidir melhor abordagem.*
- Row mapping manual em cada DAO.
- Sem DDL automático - o schema tem de ser escrito explicitamente em `schema.sql` (isto é uma vantagem disfarçada: o schema é explícito, revisto e versionável)

---
