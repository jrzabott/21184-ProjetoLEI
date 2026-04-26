# OI01 — Suporte Multi-BD (Strategy + Factory Patterns)

## Visão geral

Implementação de suporte para múltiplas bases de dados (BD) em tempo de execução, permitindo selecionar entre H2 (desenvolvimento), SQLite (produção padrão) e PostgreSQL (produção opcional) via propriedade de configuração.

## Decisão de arquitectura

**Padrões usados:**
- **Strategy Pattern:** Define interface comum `DatabaseStrategy` para diferentes implementações de BD
- **Factory Pattern:** `DatabaseFactory` seleciona estratégia apropriada em tempo de execução

**Vantagens:**
- Suporte a múltiplas BDs sem acoplamento
- Fácil adicionar novas BDs no futuro
- Configuração em propriedades, sem mudanças de código

## Componentes

### 1. `DatabaseStrategy` (Interface)

Define contrato para cada implementação de BD:

```java
public interface DatabaseStrategy {
    String getDriverClassName();  // ex: "org.h2.Driver"
    String getUrl();              // ex: "jdbc:h2:mem:musicaltrainerdb"
    String getUsername();         // "sa" ou vazio
    String getPassword();         // vazio
    String getName();             // "H2", "SQLite", "PostgreSQL"
}
```

### 2. Implementações

- **H2Strategy:** BD em memória para testes e desenvolvimento
  - Driver: `org.h2.Driver`
  - URL: `jdbc:h2:mem:musicaltrainerdb`
  - Utilizador/Senha: `sa` / vazio

- **SqliteStrategy:** Arquivo SQLite para produção padrão
  - Driver: `org.sqlite.JDBC`
  - URL: `jdbc:sqlite:musical-trainer.db`
  - Utilizador/Senha: vazio / vazio

- **PostgresStrategy:** PostgreSQL para produção opcional
  - Driver: `org.postgresql.Driver`
  - URL: `jdbc:postgresql://host:port/database`
  - Utilizador/Senha: configurable via propriedades

### 3. `DatabaseFactory`

Seleciona estratégia baseado em `db.type` property:

```java
@Component
public class DatabaseFactory {
    public DatabaseStrategy getStrategy() {
        return switch (databaseType.toUpperCase()) {
            case "H2" -> new H2Strategy();
            case "SQLITE" -> new SqliteStrategy();
            case "POSTGRES" -> new PostgresStrategy(...);
            default -> throw new IllegalArgumentException(...);
        };
    }
}
```

### 4. `DataSourceConfig`

Cria `DataSource` bean usando estratégia selecionada:

```java
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() throws ClassNotFoundException {
        DatabaseStrategy strategy = databaseFactory.getStrategy();
        // Cria DataSource baseado na estratégia
        return new CustomDataSource(strategy);
    }
}
```

## Configuração

### `application.properties`

```properties
# Seleção de BD (H2, SQLITE, POSTGRES)
db.type=H2

# Configuração PostgreSQL (quando db.type=POSTGRES)
db.postgres.host=localhost
db.postgres.port=5432
db.postgres.database=musical_trainer
db.postgres.username=postgres
db.postgres.password=
```

### `application-test.properties`

```properties
db.type=H2
```

## Testes

**DatabaseFactoryTest:**
- Verifica seleção correcta de estratégia por db.type
- Testa cada strategy (H2, SQLite, PostgreSQL)
- Valida que tipo inválido lança exceção

**DataSourceConfigTest:**
- Verifica criação de DataSource bean
- Testa conexão com BD selecionada
- Valida pooling de conexões

## Próximas etapas

1. Integrar DataSource nos DAOs (remover conexões hardcoded)
2. Adicionar logging a todas as operações de BD
3. Testes de integração com múltiplas BDs

