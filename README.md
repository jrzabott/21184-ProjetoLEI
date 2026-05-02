# Musical Theory Trainer

> Treino auditivo de teoria musical com feedback imediato - exercícios procedurais de intervalos, escalas e acordes via teclado virtual ou controlador MIDI.

**Estudante:** Daniel Junior · 2304335  
**Orientador:** Pedro Pestana  
**UC:** Projecto de Engenharia Informática · Universidade Aberta · 2025/26  
**Repositório:** https://github.com/jrzabott/21184-ProjetoLEI

---

## Estado actual

🟢 **Backend completo** - Fases 0-4.3 concluídas. 230 testes a passar. API REST funcional e documentada.  
🟢 **Relatório intercalar entregue** - Cap. 1-3 completos. Deadline 6 Mai cumprida.  
🟡 **Frontend a iniciar** - Wireframes prontos. Implementação agendada para Sem. 9-12.

**Última actualização:** Sem. 8 · 2 Mai 2026

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

Por defeito usa H2 em memória (sem configuração adicional). Para SQLite: `--spring.profiles.active=sqlite`.

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
- [x] 230 testes: unitários, integração, property-based (jqwik)

### Documentação (completa)
- [x] 19 ADRs em `docs/architecture/adr/`
- [x] Diagramas C4 nível 1 e 2 em `docs/architecture/`
- [x] Modelo de dados ER em `docs/architecture/data-model.png`
- [x] Wireframes dos 4 ecrãs em `docs/design/wireframes.pdf`
- [x] Relatório intercalar em `docs/report/` (Markdown + .docx)

### Pendente
- [ ] Frontend: teclado virtual + Web Audio API (Sem. 9-10)
- [ ] Frontend: ecrã de exercício + MIDI input (Sem. 11)
- [ ] Frontend: dashboard + session-end (Sem. 12)
- [ ] Cap. 4 (Testes) e Cap. 5 (Conclusões) — relatório final (24 Jun)

---

## Como correr os testes

```bash
mvn test
```

Resultado esperado: `Tests run: 230, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`

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

### Sem. 8 · 28 Abr–6 Mai · Intercalar

Auditoria abrangente do backend: 40 inconsistências identificadas e corrigidas. Gerador de escalas passa a usar sistema de dificuldade correctamente. Campo `options` removido da API (violava ADR-014). Controllers corrigidos para propagar erros via `GlobalExceptionHandler`. Schema SQL corrigido. Testes de fronteira RF09 adicionados. ADR-019 (expansão domínio de escalas). Relatório intercalar completo: Cap. 1-3, 19 ADRs resumidos, Goldilocks + Vygotsky referenciados, .docx gerado a partir do template UAb. 230 testes.

### Sem. 7 · 28 Abr–2 Mai

Fase 3 concluída: geradores de exercícios (Strategy pattern, TDD). ExerciseService com Factory pattern, avaliação por notas MIDI. Fase 4 concluída: REST API completa com RFC 7807. RF07 (persistência), RF08 (dashboard), RF09 (dificuldade adaptativa), F02 (no-consecutive-repeat). Wireframes 4 ecrãs + PDF. ADR-017, ADR-018. 171 testes.

### Sem. 6 · 22–25 Abr

Fase 2 concluída: DTOs, DAOs JDBC puros (SessionDao, ExerciseDao, ResultDao). 24 testes de integração. Strategy + Factory para multi-BD (H2/SQLite/PostgreSQL). ADR-012. OI01 resolvido.

### Sem. 4–5 · 7–17 Abr

Repositório GitHub criado com estrutura do template. ADR-001 a ADR-011 formalizados. Diagramas C4 e modelo de dados. Repositório enviado ao orientador (13 Abr). Spring Boot 3.3.0 configurado, modelo de domínio completo (Note, Interval, Scale, Chord).

---

*Última actualização: 2 Mai 2026 · Sem. 8*
