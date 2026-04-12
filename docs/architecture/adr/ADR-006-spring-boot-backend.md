# ADR-006 - Java 21 + Spring Boot para o backend

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O backend necessita de expor uma API REST, gerir persistência em base de dados relacional, e conter a lógica de negócio do domínio musical. A escolha de stack do backend é o ponto onde o estudante tem maior experiência profissional.

---

## Decisão

O backend é implementado em **Java 21** com **Spring Boot** (versão estável mais recente). Dependências: Spring Web (REST), Spring JDBC / `JdbcTemplate` (persistência via DAOs - ver ADR-010), H2 (base de dados em desenvolvimento). Package raiz: `pt.uab.musicaltrainer`.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| Node.js + Express | O estudante tem zero experiência profissional em Node.js. Introduziria curva de aprendizagem onde já há domínio em Java. |
| Python + FastAPI | Idem - familiar para scripting mas sem experiência profissional em backends Python. |
| Quarkus (Java nativo) | Curva de configuração mais elevada que Spring Boot; menor ecossistema de exemplos académicos; o estudante já conhece Spring. |
| Jakarta EE puro (sem Spring) | Mais verboso, menor produtividade, sem vantagem académica sobre Spring Boot. |

---

## Consequências

**Positivas:**
- O estudante maximiza qualidade do código backend - é o seu terreno de conforto
- Spring Boot minimiza configuração (auto-configuration, embedded Tomcat)
- Spring JDBC + H2 permite arrancar sem instalar base de dados; schema criado via `schema.sql`
- Ecossistema extenso - qualquer decisão tem documentação e exemplos disponíveis

**Negativas / trade-offs:**
- JVM startup time (~2-3s) - irrelevante para o scope académico local
- Maior footprint de memória do que alternativas como Quarkus native ou Node.js - irrelevante para ambiente de desenvolvimento e demo
