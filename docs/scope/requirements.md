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

- **RNF01** - **Stack backend:** Java 21 com Spring Boot (versão estável mais recente). Package raiz: `pt.uab.musicaltrainer`. Persistência via JDBC puro (`JdbcTemplate`) com DAOs - sem Spring Data JPA, sem Hibernate (ver ADR-010). DTOs implementados como Java records.
- **RNF02** - **Stack frontend:** HTML e JavaScript vanilla (ES6+). Proibido o uso de frameworks (React, Vue, Angular, Svelte) e de bundlers ou gestores de pacotes frontend (npm, Vite, Webpack).
- **RNF03** - **Áudio:** reprodução via Web Audio API nativa do browser exclusivamente. Sem bibliotecas externas de áudio.
- **RNF04** - **MIDI:** captura via Web MIDI API nativa do browser exclusivamente. Sem bibliotecas externas de MIDI.
- **RNF05** - **Base de dados:** H2 in-memory/ficheiro em desenvolvimento. PostgreSQL ou SQLite3 na versão de entrega (decisão pendente - OI01).
- **RNF06** - **Performance:** tempo de resposta do backend à validação de uma resposta < 200ms em ambiente local.
- **RNF07** - **Legibilidade académica:** código estruturado de forma que um docente de engenharia informática consiga ler e avaliar as decisões de arquitectura sem contexto adicional. Classes de domínio musical com Javadoc explicando o conceito musical subjacente.
- **RNF09** — **TDD:** todo o código de produção é precedido por um teste falhante (Red → Green → Refactor). Nenhuma feature implementada sem teste failing primeiro; nenhum bug corrigido sem teste que o reproduza. Stack: JUnit 5 + AssertJ + MockMvc + H2 in-memory (ver ADR-011).

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
