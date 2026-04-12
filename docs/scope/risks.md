# Gestão de Riscos

**Projecto:** Musical Theory Trainer  
**Versão:** 1.0 · 2026-04-12

---

## Tabela de riscos

| ID | Risco | Probabilidade | Impacto | Mitigação |
|----|-------|--------------|---------|-----------|
| R01 | **Scope creep** — adicionar features além do MVP (modos musicais, progressões de acordes, ditado rítmico) | Alta | Alto | Scope fechado por CB10–CB12. Qualquer extensão requer decisão explícita num ADR e aprovação prévia. MVP com critérios de aceitação observáveis contractualizado na proposta. |
| R02 | **Inexperiência em frontend** — o estudante tem zero experiência em HTML/CSS/JS; complexidade pode ser subestimada | Alta | Médio | Frontend em vanilla JS (sem frameworks) para minimizar curva. Fases 5–9 planeadas com checkpoints obrigatórios antes de avançar. Assistência de Claude Code na implementação frontend linha a linha. |
| R03 | **Compatibilidade Web MIDI API** — a API é suportada apenas em Chromium-based browsers; não funciona em Firefox ou Safari | Alta | Baixo | Documentado como constraint conhecido. RF04 é "Should have", não "Must have". Se não funcionar em ambiente de demo, documenta-se o motivo — não é falha crítica. |
| R04 | **Concorrência com outros eFólios** — o calendário do semestre tem 4 eFólios com deadlines em Abril/Maio que competem com o tempo de desenvolvimento | Alta | Alto | Fases 0–2 (backend core) prioritizadas para Sems. 5–6 quando as deadlines de eFólios estão resolvidas. Semanas 9–12 reservadas para frontend quando há maior disponibilidade. |
| R05 | **Decisão de base de dados de produção** — escolha entre PostgreSQL e SQLite3 está em aberto (OI01); atrasar pode afetar a configuração do relatório intercalar | Média | Médio | Decidir antes de Sem. 7 (demo interna). SQLite3 é o default se não houver razão académica específica para PostgreSQL. H2 cobre completamente o desenvolvimento até à decisão. |
| R06 | **Histórico de commits tardio** — acumular commits na semana de entrega é uma fragilidade visível para o júri | Média | Médio | Commits a cada sessão de trabalho, mesmo que pequenos. Changelog semanal funciona como pressão positiva para trabalho contínuo. Pestana revê repositório às segundas. |

---

## Histórico de actualização

| Data | Risco | Evento | Estado |
|------|-------|--------|--------|
| 2026-04-12 | R01–R06 | Versão inicial — riscos identificados no setup do projecto | Em monitorização |
