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

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 6 · 22–25 abr

**Feito:** (26 abr) Phase 2 Persistência: DTOs (SessionRecord, ExerciseRecord, ResultRecord). DAOs com JDBC puro: SessionDao, ExerciseDao, ResultDao. 24 testes de integração. ADR-012 (data model + persistence strategy). OI01 (Opção de BD de produção): Strategy + Factory patterns, suporte multi-BD (H2, SQLite, PostgreSQL), DataSourceConfig, testes de integração.  
**Bloqueou:** -  
**Próxima semana:** Phase 3 (Geradores de exercícios), Phase 4 (REST Controllers), logging em todas as operações backend

---

## Sem. 7 · 28 abr–2 mai · DEMO INTERNA

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 8 · 5–6 mai · INTERCALAR

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

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

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 12 · 26–30 mai

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

---

## Sem. 13 · 2–6 jun

**Feito:**  
**Bloqueou:**  
**Próxima semana:**

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
