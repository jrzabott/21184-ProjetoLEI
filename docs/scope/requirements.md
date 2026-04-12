# Levantamento de Requisitos

**Projecto:** Musical Theory Trainer  
**Versão:** 1.0 · 2026-04-12  
**Referência MoSCoW:** https://www.productplan.com/glossary/moscow-prioritization/

---

## Método MoSCoW

| Categoria | Significado |
|-----------|------------|
| **Must have** | Obrigatório. Sem isto o projecto não é entregável. |
| **Should have** | Importante mas não crítico. Incluir se o tempo permitir. |
| **Could have** | Desejável. Só se tudo o resto estiver concluído. |
| **Won't have** | Explicitamente fora do âmbito desta versão. |

---

## Requisitos funcionais

### Must have

- **RF01** - A aplicação deve gerar exercícios de teoria musical proceduralmente, sem datasets externos.
- **RF02** - Os exercícios devem cobrir, no mínimo: notas e oitavas, intervalos, escalas maiores, escalas menores (natural e harmónica), tríades (maior, menor, diminuto).
- **RF03** - O utilizador deve poder responder a exercícios através de um teclado virtual no ecrã (clicável com rato).
- **RF05** - A aplicação deve avaliar automaticamente a resposta e fornecer feedback imediato (correcto/incorrecto + resposta certa se errou).
- **RF06** - O feedback deve incluir som distinto para acerto e som distinto para erro.
- **RF07** - A aplicação deve registar o histórico de respostas e sessões de treino.
- **RF11** - O teclado virtual deve reproduzir som ao ser clicado (via Web Audio API).
- **RF13** - O backend deve ser agnóstico à origem da resposta - não distingue se a nota veio do teclado virtual ou do MIDI físico.

### Should have

- **RF04** - O utilizador deve poder responder através de um controlador MIDI físico ligado por USB.
- **RF08** - A aplicação deve apresentar um dashboard com métricas de progresso: taxa de acerto por tipo de exercício, evolução ao longo do tempo, padrões de erro.
- **RF09** - A dificuldade dos exercícios deve ajustar-se automaticamente com base no desempenho recente (>80% sobe, <40% desce).
- **RF12** - O sistema deve detectar automaticamente um controlador MIDI físico ligado via Web MIDI API, sem configuração manual.

### Could have

- **RF10** - Deve existir um modo sandbox onde o utilizador toca livremente e vê o nome das notas e intervalos em tempo real, sem estar num exercício activo.
- **RF14** - Deve existir mnemónicos associados a intervalos (ex: 5ª perfeita → tema de Star Wars) apresentados como dica opcional. *(Nota: verificar direitos de autor - OI02)*

### Won't have (nesta versão)

- Modos musicais (dórico, frígio, lídio, etc.) - opcionais, decidir após MVP completo (OI05)
- Progressões de acordes - opcionais, decidir após MVP (OI06)
- Ditado rítmico - explicitamente fora do scope (CB05)
- Reconhecimento de áudio por microfone - reservado para trabalho futuro (CB04)
- Autenticação / múltiplos utilizadores - não aplicável; aplicação single-user (CB01, CB02)
- Versão mobile nativa - aplicação web; responsividade básica desejável mas não é requisito (CB06)

---

## Requisitos não-funcionais

### Must have

- **RNF01** - **Stack backend:** Java 21 com Spring Boot (versão estável mais recente). Package raiz: `pt.uab.musicaltrainer`. Persistência a recorrer a JDBC com DAOs - sem Spring Data JPA, sem Hibernate (ver ADR-010). DTOs implementados como Java records.
- **RNF02** - **Stack frontend:** HTML e JavaScript vanilla (ES6+). Desconsiderado o uso de frameworks (React, Vue, Angular, Svelte) e de bundlers ou gestores de pacotes frontend (npm, Vite, Webpack).
- **RNF03** - **Áudio:** reprodução via Web Audio API nativa do browser exclusivamente. Sem bibliotecas externas de áudio.
- **RNF04** - **MIDI:** captura via Web MIDI API nativa do browser exclusivamente. Sem bibliotecas externas de MIDI.
- **RNF05** - **Base de dados:** H2 in-memory/ficheiro em desenvolvimento. PostgreSQL ou SQLite3 na versão de entrega (decisão pendente - OI01).
- **RNF06** - **Performance:** tempo de resposta do backend à validação de uma resposta < 200ms em ambiente local.
- **RNF07** - **Legibilidade académica:** código estruturado de forma que um docente de engenharia informática consiga ler e avaliar as decisões de arquitectura sem contexto adicional. Classes de domínio musical com Javadoc explicando o conceito musical subjacente.
- **RNF09** - **TDD:** todo o código de produção é precedido por um teste falhante (Red → Green → Refactor). Nenhuma feature implementada sem teste failing primeiro; nenhum bug corrigido sem teste que o reproduza. Stack (*estimada*): JUnit 5 + AssertJ + MockMvc + H2 in-memory (ver ADR-011).

### Should have

- **RNF08** - **Git:** histórico de commits legível com mensagens descritivas seguindo Conventional Commits. Commits regulares ao longo do semestre (não acumular na semana de entrega).

---

## Restrições e limites de âmbito (Constraints)

| ID | Restrição |
|----|-----------|
| CB01 | Sem autenticação. Sem login, registo, sessões autenticadas, nem identidade de utilizador. |
| CB02 | Sem múltiplos utilizadores. Base de dados para um único utilizador implícito. |
| CB03 | Sem ML ou IA. Geração de exercícios e avaliação por lógica determinística em Java. |
| CB04 | Sem reconhecimento de áudio por microfone. Explicitamente fora do scope. |
| CB05 | Sem ditado rítmico. Padrões rítmicos fora do scope. |
| CB06 | Sem versão mobile nativa. Aplicação web; responsividade básica desejável, não obrigatória. |
| CB07 | Sem frameworks JavaScript. Frontend em HTML e JS vanilla. |
| CB08 | Sem bibliotecas de áudio externas. Apenas Web Audio API. |
| CB09 | Sem dependências externas de MIDI. Apenas Web MIDI API. |
| CB10 | Modos e progressões de acordes são opcionais - só após MVP completo. |
| CB11 | Design visual não é prioridade. Interface funcional e clara; sem investimento em UI elaborada. |
| CB12 | Scope fechado. Novas features requerem decisão explícita documentada num ADR. |

---

## Histórico de alterações

| Versão | Data | Alteração | Razão |
|--------|------|-----------|-------|
| 1.0 | 2026-04-12 | Versão inicial | Setup do repositório; consolidação dos requisitos definidos na proposta |
| 1.1 | 2026-04-12 | RNF09 adicionado (TDD Must have) | TDD adoptado como metodologia de desenvolvimento; ADR-011 documenta decisão |
| 1.2 | 2026-04-12 | Secção de Open Items adicionada | OI01-OI08 recuperados e documentados formalmente; OI03, OI04, OI07 estavam em falta desde a criação do repositório |

---

## Open Items (OI)

> Decisões ou questões identificadas durante o planeamento que não foram resolvidas no momento em que foram detectadas. Cada item deve ser fechado ou diferido formalmente antes da entrega intercalar (Sem. 8).

| ID | Título | Estado | Prazo orientativo |
|----|--------|--------|------------------|
| OI01 | Base de dados de produção (PostgreSQL vs SQLite3) | 🟡 Em aberto | Sem. 7 |
| OI02 | Mnemónicos e direitos de autor | 🟡 Em aberto | Antes de implementar RF14 |
| OI03 | Número de oitavas do teclado virtual | ✅ Resolvido | Sem. 1-2 |
| OI04 | Mocks e wireframes dos ecrãs | 🟡 Em aberto | Antes de Fase 5 (Sem. 9) |
| OI05 | Modos musicais (dórico, frígio, etc.) | 🔵 Diferido | Após MVP completo |
| OI06 | Progressões de acordes | 🔵 Diferido | Após MVP completo |
| OI07 | Formato de serialização de `questionData` | 🟡 Em aberto | Início Fase 3 |
| OI08 | Navegação entre ecrãs (SPA vs páginas separadas) | ✅ Resolvido | Sem. 3-4 |

### OI01 - Base de dados de produção

**Questão:** Usar PostgreSQL ou SQLite3 na versão de entrega?

**Contexto:** H2 in-memory é suficiente para desenvolvimento e testes. Para a entrega final é necessário um motor persistente. PostgreSQL é mais robusto e reconhecido academicamente; SQLite3 é mais simples de configurar e suficiente para utilizador único.

**Referências:** ADR-007, risks.md R05, RNF05

**Prazo:** Decidir antes de Sem. 7. Default orientativo: SQLite3 se não houver razão académica específica para PostgreSQL.

### OI02 - Mnemónicos e direitos de autor

**Questão:** Os mnemónicos musicais (ex: 5ª Perfeita - tema de Star Wars) podem ser usados no código e relatório sem violar direitos de autor?

**Contexto:** RF14 (Could have) prevê mnemónicos como dica opcional. Referências a temas protegidos por copyright, mesmo em contexto educativo, podem ser problemáticas.

**Referências:** RF14

**Prazo:** Antes de implementar RF14. Se a verificação for complexa, RF14 pode ser descartado - está marcado como "Could have".

### OI03 - Número de oitavas do teclado virtual

**Questão:** Quantas oitavas deve o teclado visual mostrar?

**Estado:** ✅ Resolvido - o critério de aceitação F03 da proposta define "pelo menos 2 oitavas (C3-B4)" como mínimo do MVP. Extensível se o tempo permitir.

**Resolvido em:** `docs/scope/proposta.md` F03 · Sem. 1-2

### OI04 - Mocks e wireframes dos ecrãs

**Questão:** Os wireframes dos ecrãs principais precisam de ser produzidos antes de iniciar o frontend.

**Contexto:** A Fase 5 (frontend) começa em Sem. 9-10. A pasta `docs/design/` existe mas está vazia. Ferramenta de design ainda não escolhida (ver `docs/design/README.md`).

**Referências:** `docs/design/README.md`, guia do projecto (Sem. 5-6 wireframes obrigatórios)

**Prazo:** Antes de Fase 5. Opções de ferramenta: Figma, Balsamiq, draw.io.

### OI05 - Modos musicais

**Estado:** 🔵 Diferido - explicitamente fora do scope desta versão (CB10, "Won't have"). Decidir após MVP completo se o tempo permitir.

### OI06 - Progressões de acordes

**Estado:** 🔵 Diferido - idem OI05 (CB10).

### OI07 - Formato de serialização de `questionData`

**Questão:** O campo `questionData` da entidade `Exercise` armazena JSON como String. Qual o schema exacto para cada tipo de exercício?

**Contexto:** Sem definição formal, geradores (Fase 3) e controllers (Fase 4) podem produzir formatos incompatíveis.

**Exemplo:**
```
INTERVAL:  { "noteA": 60, "noteB": 67 }  vs  { "notes": [60, 67] }
SCALE:     { "root": 60, "type": "MAJOR" }  vs  { "rootMidi": 60, "scaleType": "MAJOR" }
```

**Prazo:** Definir no início da Fase 3, antes de escrever qualquer gerador. Documentar como sub-decisão de ADR-003 ou num ADR próprio.

### OI08 - Navegação entre ecrãs

**Estado:** ✅ Resolvido - páginas HTML separadas (`index.html`, `select.html`, `exercise.html`, `progress.html`). Estado de sessão via `sessionStorage`.

**Resolvido em:** ADR-009 · Sem. 3-4
