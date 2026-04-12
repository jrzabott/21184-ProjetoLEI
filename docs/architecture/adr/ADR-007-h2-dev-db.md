# ADR-007 — H2 para desenvolvimento; PostgreSQL ou SQLite3 para produção

**Data:** 2026-03-25  
**Estado:** Parcialmente aceite — decisão de produção em aberto (OI01)  
**Decisores:** Daniel Junior

---

## Contexto

O sistema necessita de persistência relacional para Exercise, Session e UserScore. A questão é a base de dados a usar durante o desenvolvimento e na entrega final. O scope é single-user (CB01/CB02) — não há requisitos de escalabilidade horizontal.

---

## Decisão

**Desenvolvimento:** H2 in-memory ou ficheiro, configurado via `application.properties`. A consola H2 (`/h2-console`) fica activa em desenvolvimento para verificação de tabelas. Schema gerido por `spring.jpa.hibernate.ddl-auto=create-drop` (in-memory) ou `update` (ficheiro).

**Produção/entrega:** PostgreSQL ou SQLite3 — decisão pendente (OI01). A decidir antes de Sem. 7 (demo interna). O backend usa JPA/Hibernate, pelo que a mudança de base de dados requer apenas alteração de `application.properties` e dependência Maven — sem mudança de código.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| PostgreSQL desde o início | Requer instalação e configuração antes de escrever uma linha de código — H2 elimina essa fricção no arranque |
| MongoDB | Base de dados não relacional não adequada para os relacionamentos definidos (Session 1:N UserScore, Exercise 1:N UserScore); modelo relacional é mais defensável academicamente |
| SQLite3 desde o início | Não tem consola web integrada; H2 oferece melhor experiência de desenvolvimento |

---

## Nota sobre OI01

A decisão entre PostgreSQL e SQLite3 para produção:
- **SQLite3:** ficheiro único, zero configuração de servidor, suficiente para single-user, mais simples de incluir no repositório
- **PostgreSQL:** mais reconhecível academicamente, melhor para o relatório demonstrar conhecimento de SGBD industrial

Decidir antes de Sem. 7. Actualizar este ADR com a decisão final.

---

## Consequências

**Positivas:**
- H2 permite iniciar desenvolvimento imediatamente sem setup de base de dados
- Abstracção JPA garante portabilidade entre bases de dados
- `spring.jpa.show-sql=true` facilita debugging durante desenvolvimento

**Negativas / trade-offs:**
- H2 in-memory perde dados ao reiniciar — usar modo ficheiro (`jdbc:h2:file:./musicaltrainer`) para persistência entre execuções
- Decisão de produção adiada cria incerteza — mitigado por abstracção JPA
