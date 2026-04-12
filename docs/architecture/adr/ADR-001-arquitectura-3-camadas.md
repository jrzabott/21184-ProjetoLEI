# ADR-001 - Arquitectura em 3 camadas

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O projecto necessita de uma estrutura que demonstre competências de engenharia de software perante um júri académico externo. A separação de responsabilidades é um critério de avaliação explícito. Adicionalmente, o sistema envolve três domínios técnicos distintos - lógica musical (Java), interface de utilizador (browser), e persistência (base de dados) - com características e tecnologias diferentes em cada camada.

---

## Decisão

A aplicação é dividida em três camadas separadas e independentes: **Frontend** (browser - HTML/JS vanilla), **Backend** (Java/Spring Boot - lógica de negócio e API REST), e **Persistência** (base de dados - H2 em desenvolvimento, PostgreSQL/SQLite3 em produção). As camadas comunicam exclusivamente através de contratos definidos: o frontend chama a API REST do backend via `fetch`; o backend acede à base de dados via JDBC (JdbcTemplate) com DAOs (ver ADR-010).

---

## Alternativas consideradas

| Alternativa | Razão de rejeição                                                                                                                                                                                                                                                                                                                                                                                                                                    |
|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Monolito com templates server-side (Thymeleaf) | Mistura frontend e backend - dificulta avaliação independente de cada camada. A separação HTML/JS puro + API REST agregam valor. <br> Ademais, favorecem uma clara separação entre backend e frontend, permitindo que o desenvolvimento ocorra em simultâneo sem a necessidade de, em caso de problemas, um módulo depender de outro. <br> Pode mesmo favorecer em termos de escalabilidade se necessário mais do que uma simples instância por vez. |
| Arquitectura de microserviços | Overhead de complexidade desproporcional ao âmbito deste projeto.<br> Um serviço com API REST bem estruturado é suficiente e mais simples de denfender.                                                                                                                                                                                                                                                                                   |

---

## Consequências

**Positivas:**
- Cada camada pode ser avaliada e testada independentemente pelo júri
- Separação clara de responsabilidades facilita documentação no relatório
- Frontend e backend podem ser desenvolvidos em paralelo se necessário (e principalmente, em caso de problemas que bloqueiem o avanço. Suítes de tests em separado)
- Extensível: futura adição de mobile app ou CLI não exige alterar o backend

**Negativas / trade-offs:**
- **(PENDENTE -** *necessário?***)** Necessidade de configurar CORS (Cross-Origin Resource Sharing) no backend para aceitar requests do frontend local
- Ligeiramente mais código boilerplate do que um monolito integrado
- Partilha de modelo de dados entre frontend e backend pode ser mais complexa (pode vir a ser mitigada por DTOs e documentação/contratos clara/os)