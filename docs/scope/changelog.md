# Changelog

<!-- Uma entrada por semana, até domingo à noite. -->
<!-- Formato fixo: três linhas por entrada. Não elaborar além do necessário. -->
<!-- O changelog é verificado nas três entregas formais. -->

---

## Sem. 1 · 17–21 mar

**Feito:** Kick-off assíncrono com orientador Pedro Pestana. Proposta de projecto entregue a 25 de Março: sinopse, MVP com critérios de aceitação, stack tecnológica e calendário individual.  
**Bloqueou:** Nada.  
**Próxima semana:** Aprofundar levantamento de requisitos e definição de API. Planear estrutura do repositório.

---

## Sem. 2 · 24–28 mar

**Feito:** Semana de Páscoa. Sessão de planeamento detalhado com Claude: arquitectura em 3 camadas, decisões de arquitectura (ADR-001 a ADR-011 em rascunho), modelação de dados (Note, Interval, Scale, Chord, Exercise, Session, UserScore), contrato de API REST completo.  
**Bloqueou:** Nada crítico. Decisão de base de dados de produção (PostgreSQL vs SQLite3) em aberto - OI01.  
**Próxima semana:** Formalizar requisitos MoSCoW. Preparar documento de handoff para implementação.

---

## Sem. 3 · 31 mar–4 abr

**Feito:** Documento de handoff completo: requisitos RF01–RF14, RNF01–RNF08, constraints CB01–CB12, ordem de implementação por fases (0–9), definition of done, open items identificados (OI01–OI08 - ver docs/scope/requirements.md).  
**Bloqueou:** Documentos parciais, dediquei pouco tempo para as tarefas, e muita coisa ficou acumulada para revisão na semana seguinte.  
**Próxima semana:** Criar repositório GitHub. Preencher documentação do template do orientador e rever rascunhos de ADRs.

---

## Sem. 4 · 7–12 abr

**Feito:** Repositório GitHub criado (jrzabott/21184-ProjetoLEI) com estrutura do template do orientador. README, proposta, requisitos, changelog e risks preenchidos. ADRs ADR-001 a ADR-011 formalizados. RNF09 (TDD) adicionado. Diagramas C4 (contexto e contentores) e modelo de dados gerados e versionados. Open items OI01-OI08 registados formalmente em requirements.md. Revisão de linguagem em toda a documentação.  
**Bloqueou:** Link do repositório ainda por enviar ao orientador.  
**Próxima semana:** Enviar repositório ao orientador. Setup Spring Boot (Fase 0): criar projecto Maven, configurar H2, verificar arranque.

---

## Sem. 5 · 14–17 abr

**Feito:** Semana de baixa produtividade - contexto externo limitou disponibilidade. Sem commits de código. Tarefas de setup e documentação transferidas para a semana seguinte.
**Bloqueou:** -
**Próxima semana:** Recuperar ritmo: persistência (DAOs + schema SQL), estratégias de base de dados.

---

## Sem. 6 · 22–25 abr

**Feito:** (26 abr) Phase 2 Persistência: DTOs (SessionRecord, ExerciseRecord, ResultRecord). DAOs com JDBC puro: SessionDao, ExerciseDao, ResultDao. 24 testes de integração. ADR-012 (data model + persistence strategy). OI01 (Opção de BD de produção): Strategy + Factory patterns, suporte multi-BD (H2, SQLite, PostgreSQL), DataSourceConfig, testes de integração. DaoFactory para criação de DAOs com DataSource selecionada, logging de entradas/saídas.  
**Bloqueou:** -  
**Próxima semana:** Phase 3 (Geradores de exercícios), Phase 4 (REST Controllers), logging completo em todas as operações backend

---

## Sem. 7 · 28 abr–2 mai · DEMO INTERNA

**Feito:** (27 abr) Fase 3 concluída - geradores (IntervalExerciseGenerator, ScaleExerciseGenerator 8 notas raiz-a-raiz, ChordExerciseGenerator) com Strategy pattern, TDD, logging. ExerciseService com Factory pattern e avaliação baseada em notas MIDI por tipo (ADR-014). Fase 4 concluída - REST API: POST /api/exercises/generate, POST /api/exercises/answer (flat, exerciseId no corpo), POST /api/sessions/start e /end, GET /api/progress, GET /api/sandbox/note-info. ADR-013 (schema questionData) e ADR-014 (protocolo notas MIDI). ScaleType.getSemitonePattern(), ChordType.getVoicingIntervals(). 171 testes a passar. Validação por padrão de semítons (escalas qualquer oitava, acordes I-III-V qualquer oitava, intervalos notas exactas).  
**Bloqueou:** -  
**Próxima semana:** Demo interna com Pestana (28 abr–2 mai). Decisão biblioteca GUI para teste API. Relatório Intercalar (deadline 6 Mai).

---

## Sem. 8 · 28 abr–6 mai · DEMO INTERNA + INTERCALAR

**Feito:** feat/27-30 concluídos (semana anterior): RF07 persistência, RF08 dashboard com weakestAreas, RF09 dificuldade adaptativa, F02 no-consecutive-repeat. API polish (feat/38-41): hints pedagógicos nos exercícios, fix FK violation em session validation, RFC 7807 GlobalExceptionHandler central (ProblemDetail em todos os erros), Bean Validation em AnswerRequest, SessionResponse unificado com startedAt/endedAt, ProgressService enriquecido com zero-fill e timestamps. Wireframes dos 4 ecrãs HTML produzidos e PDF exportado para docs/design/ (feat/44-46). ADR-017 (deployment frontend em /frontend/) e ADR-018 (teclado CSS, range MIDI C2-C6, responsividade). Diagramas C4 e modelo ER corrigidos e actualizados (feat/45). Documentação humanizada: em dashes removidos, comentários redundantes limpos (feat/42-43). Auditoria abrangente do backend contra todos os contratos (feat/47): 7 domínios auditados, 40 inconsistências identificadas, prioridades corrigidas - gerador de escalas passa a usar sistema de dificuldade correctamente (modos a ADVANCED, pentatónicas a ELEMENTARY), campo options removido da API (violava ADR-014), ProgressController e SandboxController removeram try-catch local, schema.sql corrigido, testes de fronteira RF09 adicionados. 230 testes a passar. Relatório intercalar: Cap. 1 (Introdução) escrito, Cap. 2 (Desenho) com stack, diagramas C4, modelo de dados, algoritmos com contexto, padrões de design com referências GoF, 19 resumos ADR, bibliografia. Cap. 3 (Implementação) em progresso.  
**Bloqueou:** Demo interna com orientador não realizada. Sem contacto com o orientador esta semana.  
**Próxima semana:** Frontend Fase 5 (teclado virtual, Web Audio API). Submissão do relatório intercalar (6 mai).

---

## Sem. 9 · 7–9 mai

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 10 · 12–16 mai

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 11 · 19–23 mai

**Feito:** Frontend completo (feat/54-58): 6 módulos JS, 4 páginas HTML, E2E Cucumber 76 cenários. Branches de correcção pós-revisão multi-perspectiva (feat/59-69): P29 correctAnswer int[], P19 extract pattern, P22 increment guard, getDisplayName via enum, PIT mutation 1.17, SQLite operacional, DOM seguro, botão Enviar, LED MIDI. feat/70 correcções UX pós-revisão black-box: session.sessionId (critico - sessões pontuadas nao guardavam dados), feedback com nomes de nota em vez de numeros MIDI, Ouvir destaca apenas nota raiz, ortografia pós-reforma (Correto/corretas), singular/plural no dashboard, schema-sqlite.sql compativel com SQLite 3, favicon SVG, tooltip LED MIDI, SQLiteEndToEndIT.
**Bloqueou:** SqliteStrategy.getUrl() hardcodeia o caminho da BD - nao é possivel apontar o E2E test para ficheiro temporario sem refactoring adicional. Documentado no Javadoc de SqliteEndToEndIT.
**Próxima semana:** Merge de todas as branches. Cap. 4 e 5 do relatório final.

---

## Sem. 12 · 26–30 mai

**Feito:** (31 mai) Code review funcional completo + fixes: feat/71-74 backend (enunciados, nomes, dificuldade, deduplicação), feat/75-77 frontend (prepend raiz, 4 timbres, scheduling Web Audio), feat/78 E2E (94 testes), feat/79 perfil Maven sqlite + run config IntelliJ.
**Bloqueou:** Branches feat/54-79 ausentes de main — merge sequencial --no-ff pendente de OK.
**Próxima semana:** feat/80+ — teoria musical, submissão vazia, modal ajuda, backend guard evaluateScale.

---

## Sem. 13 · 2–6 jun

**Feito:** feat/83: fix(frontend) — modal de ajuda corrigido: teoria musical, não treino auditivo; Praticar sem pressão.
**Bloqueou:** -
**Próxima semana:** Merge sequencial feat/54-83 → main quando aprovado. Relatório final Cap. 4 e 5.

---

## Sem. 14 · 9–13 jun

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 15 · 16–20 jun · PREP. DEFESA

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 16 · 24 jun · ENTREGA FINAL

**Feito:**  
**Bloqueou:** -  
**Próxima semana:** - Defesa pública (6–10 Jul).
