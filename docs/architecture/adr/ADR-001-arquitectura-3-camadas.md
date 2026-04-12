# ADR-001 — Arquitectura em 3 camadas

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O projecto necessita de uma estrutura que demonstre competências de engenharia de software perante um júri académico externo. A separação de responsabilidades é um critério de avaliação explícito. Adicionalmente, o sistema envolve três domínios técnicos distintos — lógica musical (Java), interface de utilizador (browser), e persistência (base de dados) — com características e tecnologias diferentes em cada camada.

---

## Decisão

A aplicação é dividida em três camadas separadas e independentes: **Frontend** (browser — HTML/JS vanilla), **Backend** (Java/Spring Boot — lógica de negócio e API REST), e **Persistência** (base de dados — H2 em desenvolvimento, PostgreSQL/SQLite3 em produção). As camadas comunicam exclusivamente através de contratos definidos: o frontend chama a API REST do backend via `fetch`; o backend acede à base de dados via JDBC (JdbcTemplate) com DAOs (ver ADR-010).

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| Monolito com templates server-side (Thymeleaf) | Mistura frontend e backend — dificulta avaliação independente de cada camada. A separação HTML/JS puro + API REST tem maior valor académico demonstrável. |
| Arquitectura de microserviços | Overhead de complexidade desproporcional ao scope. Um serviço com API REST bem estruturado é suficiente e mais defensável. |

---

## Consequências

**Positivas:**
- Cada camada pode ser avaliada e testada independentemente pelo júri
- Separação clara de responsabilidades facilita documentação no relatório
- Frontend e backend podem ser desenvolvidos em paralelo se necessário
- Extensível: futura adição de mobile app ou CLI não exige alterar o backend

**Negativas / trade-offs:**
- Necessidade de configurar CORS no backend para aceitar requests do frontend local
- Ligeiramente mais código boilerplate do que um monolito integrado
