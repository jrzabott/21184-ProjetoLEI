# Musical Theory Trainer

> Treino auditivo de teoria musical com feedback imediato - exercícios procedurais de intervalos, escalas e acordes via teclado virtual ou controlador MIDI.

**Estudante:** Daniel Junior · 2304335  
**Orientador:** Pedro Pestana  
**UC:** Projecto de Engenharia Informática · Universidade Aberta · 2025/26  
**Repositório:** https://github.com/jrzabott/21184-ProjetoLEI

---

## Estado actual

🟢 **Projeto completo** - Backend + frontend implementados. 442 testes a passar. Deploy em Render.  
🟢 **Relatório final entregue** - Cap. 1-5 completos. Deadline 24 Jun cumprida. Aprovado pelo orientador.  
🟢 **Defesa pública** - 6–10 Jul 2026.

**Última actualização:** Sem. 16 · 24 Jun 2026

---

## Como instalar e correr

**Requisitos:** Java 21+, Maven 3.9+

```bash
git clone https://github.com/jrzabott/21184-ProjetoLEI
cd 21184-ProjetoLEI
mvn spring-boot:run
```

- **API REST:** `http://localhost:8080`
- **Swagger UI (documentação interactiva):** `http://localhost:8080/swagger-ui.html`
- **Frontend:** ainda não implementado (Sem. 9+)

Por defeito usa H2 em memória (sem configuração adicional). Para SQLite: `mvn spring-boot:run -P sqlite`.

---

## O que está implementado

### Backend (completo)
- [x] Modelo de domínio musical: `Note`, `Interval`, `Scale` (28 tipos), `Chord` — value objects imutáveis
- [x] Persistência JDBC pura: `SessionDao`, `ExerciseDao`, `ResultDao` — zero ORM
- [x] Suporte multi-BD: H2 (dev), SQLite, PostgreSQL — Strategy + Factory pattern (ADR-009)
- [x] Geração procedural de exercícios por dificuldade: intervalos, escalas, tríades
- [x] Dificuldade adaptativa (RF09): janela 100 exercícios, limiares 80%/40%, clamp ±2
- [x] Sem repetição consecutiva do mesmo exercício na mesma sessão (RF-F02)
- [x] Dashboard de progresso: taxa de acerto por tipo, identificação de padrões fracos (RF08)
- [x] API REST completa: `/generate`, `/answer`, `/sessions`, `/progress`, `/sandbox`
- [x] RFC 7807 `ProblemDetail` em todos os erros (`GlobalExceptionHandler`)
- [x] Hints pedagógicos por tipo de exercício
- [x] 342 testes: unitários, integração, property-based (jqwik)
- [x] 100 testes E2E: Cucumber + Selenide, Chrome headless

### Frontend (completo)
- [x] Teclado virtual C2-C6, 4 presets de timbre, Web Audio API nativa
- [x] Exercícios de intervalos, escalas (28 tipos) e acordes
- [x] Modo sessão pontuado + modo sandbox (SESSION_NONE)
- [x] Dashboard de progresso: taxa por tipo, áreas mais fracas
- [x] Web MIDI API (controlador externo opcional)
- [x] Feedback imediato + hints por tipo de exercício

### Documentação (completa)
- [x] 23 ADRs em `docs/architecture/adr/`
- [x] Diagramas C4 nível 1 e 2 em `docs/architecture/`
- [x] Modelo de dados ER em `docs/architecture/data-model.png`
- [x] Wireframes dos 4 ecrãs em `docs/design/wireframes.pdf`
- [x] Relatório intercalar + relatório final em `docs/report/`

---

## Como correr os testes

```bash
mvn test
```

Resultado esperado: `Tests run: 342, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`

Para correr também os testes E2E (Cucumber + Selenide, requer Chrome):

```bash
mvn verify
```

---

## Decisões de arquitectura principais

| Decisão | Alternativa considerada | Razão |
|---|---|---|
| Java 21 + Spring Boot | Node.js / FastAPI | Experiência prévia; ecosistema familiar |
| JDBC puro (sem ORM) | Spring Data JPA | SQL explícito, zero Hibernate magic, queries visíveis |
| HTML + JS vanilla | React / Vue | CB07 (sem frameworks); menor curva de aprendizado no frontend |
| Geração procedural | Datasets externos | Deploy simplificado; modelo musical formalizado no código |
| H2 (dev) + SQLite/PostgreSQL (prod) | PostgreSQL apenas | H2 arranca sem configuração; SQLite para deploy simples (ADR-009) |
| Web Audio API nativa | Tone.js | Zero dependências externas; suficiente para o scope (ADR-018) |

Detalhe completo: `docs/architecture/adr/`

---

## Referências e IA utilizada

### Referências técnicas

- Spring Boot - https://spring.io/projects/spring-boot
- Web Audio API (MDN) - https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API
- Web MIDI API (MDN) - https://developer.mozilla.org/en-US/docs/Web/API/Web_MIDI_API
- C4 Model - https://c4model.com
- RFC 7807 - https://www.rfc-editor.org/rfc/rfc7807
- Conventional Commits - https://www.conventionalcommits.org

### Ferramentas de IA utilizadas

| Ferramenta | Para que foi usada |
|---|---|
| Claude Code | Arquitectura, implementação, documentação técnica, revisão de código, relatório |
| Gemini CLI | Extracção de PDFs, conteúdo Moodle, validação de sugestões |

---

## Changelog

### Sem. 16 · 24 Jun · Entrega Final

Relatório final submetido e aprovado (Cap. 1-5, 23 ADRs, bibliografia APA). fix/90: timestamps SQLite corrigidos via JdbcDateHelper (H2 + SQLite + PostgreSQL). Apresentação de defesa atualizada. 442 testes (342 unit/integração + 100 E2E). Deploy activo em Render.

### Sem. 15 · 16–20 Jun · Prep. Defesa

chore/84: PITest removido (conflito irresolvível JUnit Platform/Cucumber). feat/85: db.sqlite.path configurável. feat/86: Dockerfile multi-stage + fly.toml (SQLite persistente em /data). docs/87: artefactos de entrega (screenshots, performance-results, PPTX).

### Sem. 13 · 2–6 Jun

Merge sequencial feat/54–83 → main. feat/80: enunciado intervalo mostra tipo+raiz (teoria, não ear training). feat/82: submissão vazia conta como incorreta. feat/83: modal de ajuda corrigido. 342 testes unitários e de integração.

### Sem. 8 · 28 Abr–6 Mai · Intercalar

Auditoria abrangente do backend: 40 inconsistências identificadas e corrigidas. Relatório intercalar completo: Cap. 1-3, 19 ADRs. 230 testes.

### Sem. 7 · 28 Abr–2 Mai

Fase 3 concluída: geradores de exercícios (Strategy pattern, TDD). ExerciseService com Factory pattern, avaliação por notas MIDI. Fase 4 concluída: REST API completa com RFC 7807. RF07 (persistência), RF08 (dashboard), RF09 (dificuldade adaptativa), F02 (no-consecutive-repeat). Wireframes 4 ecrãs + PDF. ADR-017, ADR-018. 171 testes.

### Sem. 6 · 22–25 Abr

Fase 2 concluída: DTOs, DAOs JDBC puros (SessionDao, ExerciseDao, ResultDao). 24 testes de integração. Strategy + Factory para multi-BD (H2/SQLite/PostgreSQL). ADR-012. OI01 resolvido.

### Sem. 4–5 · 7–17 Abr

Repositório GitHub criado com estrutura do template. ADR-001 a ADR-011 formalizados. Diagramas C4 e modelo de dados. Repositório enviado ao orientador (13 Abr). Spring Boot 3.3.0 configurado, modelo de domínio completo (Note, Interval, Scale, Chord).

---

*Última actualização: 24 Jun 2026 · Sem. 16*
