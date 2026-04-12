# Proposta de Projecto

**Título:** Musical Theory Trainer  
**Estudante:** Daniel Junior · 2304335  
**Orientador:** Pedro Pestana  
**Data:** 25 de Março de 2026  
**Versão:** 1.0

---

## Sinopse

Aprender teoria musical é um obstáculo frequente para músicos autodidactas e estudantes de conservatório. O problema não é falta de recursos — há livros, vídeos e aplicações. O problema é a ausência de feedback imediato e personalizado: saber se aquela é mesmo uma 5ª Perfeita ou um acorde menor só é confirmado quando há um professor na sala, o que raramente acontece. O resultado é que conceitos fundamentais ficam imprecisos durante anos, o que limita a capacidade de improvisar, ler partituras e comunicar musicalmente.

O Musical Theory Trainer é uma aplicação web que resolve este problema através de exercícios gerados proceduralmente e avaliação automática de respostas. O utilizador ouve ou vê uma estrutura musical — uma nota, um intervalo, uma escala, um acorde — responde através de um teclado virtual no ecrã ou de um controlador MIDI físico, e a aplicação diz imediatamente se acertou ou errou, com explicação. A ideia central foi descrita informalmente como "Duolingo para teoria musical": exercícios curtos, feedback imediato, progressão visível, sem fricção. Não há login, não há subscrição, não há gamificação forçada — apenas o exercício e o feedback.

O resultado esperado é uma aplicação web funcional que arranca com um único comando, corre no browser sem instalação adicional, e permite a qualquer utilizador completar uma sessão de exercícios de teoria musical em menos de 5 minutos. O sucesso é verificado pelos critérios de aceitação definidos na secção MVP: se a aplicação gera exercícios, valida respostas correctamente, toca som, aceita input MIDI, e regista progresso, o objectivo está atingido.

---

## MVP — Definição e critérios de aceitação

### F01 — Modelo de domínio musical

**Critério de aceitação:**  
Dado um número MIDI qualquer entre 0 e 127, `Note.fromMidi(n)` devolve o nome correcto da nota com oitava (ex: MIDI 60 → C4). Dado C4 e G4, `Interval.between(C4, G4)` devolve "5ª Perfeita" (7 semítons). Dada a raiz C4 e tipo MAJOR, `Scale.getNotes()` devolve [C4, D4, E4, F4, G4, A4, B4, C5]. Dado C4 e MAJOR, `Chord.getNotes()` devolve [C4, E4, G4]. Todos os casos verificados por testes unitários que passam.

### F02 — Geração procedural de exercícios

**Critério de aceitação:**  
`POST /api/exercises/generate` com `{"type": "INTERVAL", "difficulty": 1}` devolve resposta 200 com `exerciseId`, notas em MIDI, descrição textual da pergunta, e exactamente 4 opções de resposta incluindo a correcta. O mesmo para `SCALE_IDENTIFICATION` e `CHORD_IDENTIFICATION`. Nenhum exercício repetido consecutivamente na mesma sessão.

### F03 — Teclado virtual com reprodução de som

**Critério de aceitação:**  
Ao clicar numa tecla do teclado visual no browser, a nota correspondente é reproduzida via Web Audio API num prazo imperceptível (< 100ms). O teclado cobre pelo menos 2 oitavas (C3–B4). Teclas brancas e pretas visualmente distinguíveis. Funciona sem instalação adicional em Chrome e Firefox actuais.

### F04 — Input MIDI físico

**Critério de aceitação:**  
Com um controlador MIDI USB ligado ao computador, ao pressionar uma tecla física, a nota correspondente é enviada para o mesmo callback que o teclado virtual. A aplicação detecta o dispositivo automaticamente sem configuração manual. Se não houver dispositivo MIDI, a aplicação funciona normalmente com o teclado virtual e mostra mensagem discreta (não é erro crítico).

### F05 — Validação automática e feedback imediato

**Critério de aceitação:**  
`POST /api/exercises/{id}/answer` com a resposta correcta devolve `{"correct": true}` com explicação em menos de 200ms (ambiente local). Com resposta errada, devolve `{"correct": false}` com a resposta correcta e explicação da diferença. O frontend mostra feedback visual e sonoro distinto para acerto (som ding) e erro (som buzz) em menos de 100ms após receber a resposta.

### F06 — Persistência de sessões e resultados

**Critério de aceitação:**  
`POST /api/sessions/start` cria registo em base de dados e devolve `sessionId`. `POST /api/sessions/{id}/end` actualiza o registo com tempo total, exercícios realizados e taxa de acerto. `GET /api/progress` devolve histórico com pelo menos as últimas 10 sessões e métricas por tipo de exercício. Dados persistem entre reinícios da aplicação (modo ficheiro H2 ou PostgreSQL).

### F07 — Dashboard de progresso

**Critério de aceitação:**  
`progress.html` carrega e exibe: taxa de acerto global, breakdown por tipo de exercício (intervalos / escalas / acordes), e lista de pontos fracos identificados. Dados actualizados após cada sessão. Página funcional mesmo com zero sessões registadas (estado vazio tratado graciosamente).

### F08 — Modo sandbox

**Critério de aceitação:**  
Na página principal, em modo sandbox, ao pressionar qualquer tecla (virtual ou MIDI), o nome da nota é exibido imediatamente. Ao pressionar duas teclas em simultâneo ou em sequência rápida, o intervalo entre elas é calculado e exibido com nome e número de semítons. Sem limite de tempo, sem avaliação de resposta.

### F09 — Dificuldade adaptativa

**Critério de aceitação:**  
Se o utilizador acertar mais de 80% dos últimos 10 exercícios do mesmo tipo, o próximo exercício gerado tem `difficulty` incrementada em 1 (máx. 5). Se acertar menos de 40%, `difficulty` decrementada em 1 (mín. 1). Lógica implementada no frontend com estado local dos últimos 10 resultados.

---

## Stack tecnológica

| Componente | Tecnologia escolhida | Justificação |
|-----------|---------------------|-------------|
| Backend | Java 21 + Spring Boot + JDBC | Experiência profissional do estudante; Spring Boot minimiza configuração; JDBC puro com DAOs em vez de JPA — SQL explícito e defensável (ver ADR-010) |
| Frontend | HTML + JavaScript ES6+ vanilla | Zero experiência frontend — frameworks adicionariam curva de aprendizagem desnecessária; vanilla JS é suficiente para o scope |
| Áudio | Web Audio API (nativa do browser) | Zero dependências externas; funciona em qualquer browser moderno; suficiente para tocar notas isoladas |
| MIDI | Web MIDI API (nativa do browser) | Zero dependências externas; detecção automática de dispositivos; implementação mínima com efeito desproporcional na demo |
| Base de dados (dev) | H2 in-memory / ficheiro | Arranca sem configuração; console web integrada para verificação de tabelas |
| Base de dados (prod) | PostgreSQL ou SQLite3 | Decisão em aberto — ver OI01 em docs/scope/risks.md |
| Build | Maven | Standard no ecossistema Java/Spring |

---

## Esboço de arquitectura — C4 Nível 1

**Sistema:** Musical Theory Trainer — aplicação web de exercícios de teoria musical

**Utilizadores:**
- Estudante de teoria musical — acede à aplicação no browser, realiza exercícios através do teclado virtual ou controlador MIDI físico, consulta o progresso no dashboard

**Sistemas externos:**
- Controlador MIDI USB (hardware físico, opcional) — ligado ao computador do utilizador; browser captura eventos via Web MIDI API sem intermediários de software
- Nenhum outro sistema externo — a aplicação é completamente autossuficiente; não há APIs de terceiros, não há serviços externos, não há autenticação externa

> Diagrama formal em `docs/architecture/c4-context.png` (a criar em Sem. 5–6)

---

## Calendário individual detalhado

| Semanas | Datas | Conteúdo planeado | Marco |
|---------|-------|------------------|-------|
| Sem. 1–2 | 17–28 mar | Kick-off com orientador. Proposta: sinopse, MVP com critérios de aceitação, stack, C4 nível 1. | **Proposta (25 mar) ✅** |
| Sem. 3–4 | 31 mar–11 abr | Levantamento de requisitos MoSCoW. Definição completa da API. Modelação de dados. Configuração do repositório GitHub com estrutura completa. | |
| Sem. 5–6 | 14–25 abr | Fase 0–2: Setup Spring Boot, modelo de domínio Java (Note, Interval, Scale, Chord), testes unitários, entidades JPA. ADRs das decisões principais. | |
| Sem. 7 | 28 abr–2 mai | Fase 3–4: Geradores de exercícios, REST Controllers. Demo interna ao orientador: backend funcional com todos os endpoints testados via curl. | **Demo interna** |
| Sem. 8 | 5–6 mai | Relatório intercalar: Cap. 1 e Cap. 2 completos (arquitectura C4 nível 1+2, modelo de dados, ADRs). Cap. 3: estado de implementação. | **Intercalar (6 mai)** |
| Sem. 9–10 | 7–16 mai | Fase 5–6: Frontend — teclado virtual com som (Web Audio API), input MIDI, ecrã de exercício activo com fluxo completo. | |
| Sem. 11–12 | 19–30 mai | Fase 7–9: Dashboard de progresso, ecrã de fim de sessão, ecrã de selecção. Testes de funcionalidade e integração. | |
| Sem. 13 | 2–6 jun | Revisão geral do sistema. Validação de todos os critérios de aceitação. Capturas de ecrã para Cap. 4. | |
| Sem. 14 | 9–13 jun | Cap. 4 (Testes) e Cap. 5 (Conclusões). Revisão bibliográfica APA. Preparação de anexos. | |
| Sem. 15 | 16–20 jun | Reunião de preparação para defesa com orientador. Ensaio de perguntas de júri. Revisão final do relatório. | **Prep. defesa** |
| Sem. 16 | 24 jun | Submissão do relatório final. Código e demo linkados no repositório. | **Final (24 jun)** |
