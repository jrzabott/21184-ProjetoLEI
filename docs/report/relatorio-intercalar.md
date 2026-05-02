# Relatório Intercalar - Musical Theory Trainer

**Unidade Curricular:** 21184 - Projeto de Engenharia Informática 2025/26
**Estudante:** Daniel Junior - 2304335
**Orientador:** Pedro Duarte Pestana
**Instituição:** Universidade Aberta
**Data:** Maio 2026

---

## Capítulo 1 - Introdução

### 1.1 Descrição do Projeto

O Musical Theory Trainer é uma aplicação web de treino auditivo para teoria musical.
O mote que despoletou o projeto foi simples: um Duolingo para teoria musical. Mas rapidamente
percebi que a múltipla escolha não era adequada para o que queria construir - não treina
realmente o ouvido, apenas o reconhecimento de opções.

O resultado foi um sistema onde o utilizador ouve uma estrutura musical - um intervalo, uma
escala, um acorde - e responde tocando as notas num teclado virtual (ou num controlador MIDI
físico). A aplicação avalia automaticamente e devolve feedback imediato. O funcionamento foi
pensado para ser simples e sem atrito:

- arranca com um único comando
- corre em qualquer browser moderno sem instalação adicional
- uma sessão pode ser completada em menos de 5 minutos
- não há login, não há subscrição, não há gamificação forçada

Há apenas o exercício e o feedback.

---

### 1.2 Necessidades Identificadas

Aprender teoria musical é um obstáculo frequente para músicos autodidactas e estudantes em
turmas numerosas. O problema não é a falta de recursos - temos: livros, vídeos, aplicações,
etc. O problema é a falta de uma verificação personalizada que sirva para construir a
segurança e a auto-estima do estudante.

Num mundo perfeito, este feedback seria imediato e ajudaria diretamente o desenvolvimento
de competências musicais. Confirmar se um intervalo é mesmo uma 5ª Perfeita, ou se um acorde
é menor, precisa de alguém de fora que conheça esses conceitos - normalmente um professor.
Que raramente está disponível em tempo real.

O resultado é que conceitos fundamentais ficam imprecisos durante anos. Esta imprecisão
limita a capacidade de improvisar, ler partituras e comunicar musicalmente. É como aprender
a andar de bicicleta com as rodinhas sempre colocadas: a insegurança leva à pouca exploração
prática, e as práticas repetitivas e mecânicas acabam por ser infrutíferas.

---

### 1.3 Âmbito

**Dentro do âmbito:**

- Exercícios de identificação de intervalos, escalas (maior, menor natural, menor harmónica)
  e tríades (maior, menor, diminuto, aumentado)
- Teclado virtual clicável com reprodução de som via Web Audio API
- Suporte a controlador MIDI físico USB, com detecção automática (Web MIDI API)
- Avaliação automática com feedback imediato (correto/errado + resposta correta se errado)
- Sessões de treino com persistência de resultados entre utilizações
- Dashboard de progresso por tipo de exercício e identificação de padrões de erro
- Dificuldade adaptativa baseada nos últimos 100 exercícios por tipo
- Modo sandbox: tocar livremente e ver o nome das notas e intervalos em tempo real

**Fora do âmbito (esta versão):**

- Autenticação ou gestão de múltiplos utilizadores (CB01, CB02)
- Ditado rítmico (CB05)
- Versão mobile nativa - aplicação web exclusivamente (CB06)
- Modos diatónicos e progressões de acordes - reservados para versão futura
- Reconhecimento de áudio por microfone

---

### 1.4 Objetivos

Os critérios de aceitação observáveis do MVP estão organizados em nove objetivos:

- **F01** - Modelo de domínio musical completo: Note, Interval, Scale, Chord, com todos os
  casos verificados por testes unitários
- **F02** - Geração procedural de exercícios dos três tipos (intervalos, escalas, acordes),
  sem repetição consecutiva na mesma sessão
- **F03** - Teclado virtual clicável no browser (C3 a C5 em mobile, C2 a C6 em desktop),
  com reprodução de som via Web Audio API
- **F04** - Input via controlador MIDI físico com detecção automática, sem configuração manual
- **F05** - Avaliação automática com feedback imediato: correto/errado e resposta correta quando
  errado, em menos de 200ms em ambiente local
- **F06** - Feedback sonoro distinto para acerto e para erro
- **F07** - Persistência de sessões e histórico de resultados entre reinícios da aplicação
- **F08** - Dashboard de progresso com taxa de acerto por tipo de exercício e identificação
  dos padrões onde o utilizador tem pior desempenho
- **F09** - Dificuldade adaptativa: incrementa se taxa de acerto >= 80%, decrementa se < 40%,
  janela de 100 exercícios por tipo para evitar oscilações por variância em amostras pequenas

---

### 1.5 Restrições

As principais restrições que condicionaram as decisões de design e implementação:

| ID | Restrição |
|---|---|
| CB01 | Sem autenticação nem login |
| CB02 | Base de dados para um único utilizador implícito |
| CB05 | Sem ditado rítmico - fora do âmbito desta versão |
| CB06 | Aplicação web; sem versão mobile nativa |
| CB07 | Frontend em HTML e JavaScript ES6+ vanilla - sem frameworks |
| CB11 | Design visual não é prioridade - interface funcional e clara |
| RNF01 | Backend: Java 21 + Spring Boot, JDBC puro sem ORM |
| RNF06 | Tempo de resposta da validação < 200ms em ambiente local |

Para a lista completa de restrições e constraints, ver `docs/scope/requirements.md`.

---

### 1.6 Resultados Esperados

O resultado esperado é uma aplicação web funcional que arranca com um único comando
(`mvn spring-boot:run`), corre no browser sem instalação adicional, e permite a qualquer
utilizador completar uma sessão de exercícios de teoria musical em menos de 5 minutos.
O sucesso é verificável: se a aplicação gera exercícios, valida respostas corretamente,
toca som, aceita input MIDI e regista progresso - o objetivo está atingido.

À data deste relatório intercalar (1 de maio de 2026), o backend está completamente
implementado: 222 testes a passar, todos os requisitos Must-have verificados. O frontend
foi desenhado e especificado - wireframes produzidos (ver `docs/design/wireframes.pdf`),
decisões de arquitetura documentadas nos ADRs 017 e 018 - mas a implementação está
agendada para as semanas 9 a 12, após a entrega intercalar.

---

### 1.7 Alterações face à Proposta Original

Durante a implementação, alguns parâmetros da proposta foram revistos. Documento-os aqui
com a justificação respetiva.

#### 1.7.1 Nomenclatura dos tipos de exercício

Os tipos `SCALE_IDENTIFICATION` e `CHORD_IDENTIFICATION` foram simplificados para `SCALE`
e `CHORD` durante a implementação do modelo de domínio. O sufixo `_IDENTIFICATION` era
redundante - todos os exercícios são de identificação. O critério de aceitação permanece
o mesmo; apenas o valor do campo `type` na API mudou.

#### 1.7.2 Janela do algoritmo adaptativo: 10 -> 100 exercícios

A proposta original previa uma janela de 10 exercícios para calcular a taxa de acerto.
Optei por alargar para 100 porque, com apenas 10 amostras, uma sequência de 3 acertos
consecutivos podia inflar artificialmente a dificuldade - provocando oscilações não
representativas do nível real do utilizador. 100 exercícios por tipo fornecem uma taxa
de acerto estatisticamente mais significativa e reduzem a variância aleatória. Ver ADR-015.

#### 1.7.3 Escala de dificuldade: 1-5 -> 1-10

Ampliei a escala de dificuldade de 1-5 para 1-10. Convenceu-me a granularidade mais fina:
+1/-1 num espaço de 10 é uma progressão mais gradual do que num espaço de 5. Além disso,
a biblioteca alargada de tipos de escala (28 valores) precisava de mais bandas para uma
classificação com significado pedagógico. Ficaram 5 bandas semânticas: BEGINNER, ELEMENTARY,
INTERMEDIATE, ADVANCED, EXPERT. Ver ADR-015 para a taxonomia completa.

#### 1.7.4 Localização da lógica adaptativa: frontend -> backend

A proposta original colocava a lógica de adaptação de dificuldade no frontend, com estado
local dos últimos resultados. Movi-a para o backend. Penso que faz mais sentido centralizar
a lógica de negócio no servidor:

- garante consistência entre sessões
- elimina a possibilidade de estado local corrompido no browser
- evita que a lógica seja contornada client-side

O `DifficultyService` implementa o algoritmo; o frontend recebe `suggestedDifficulty` na
resposta de `generate` como sugestão informativa.

---

### 1.8 Calendário Atualizado

O calendário original foi seguido com uma exceção: a semana 5 teve produtividade reduzida
por sobreposição com a Páscoa e a entrega de eFólios de outras unidades curriculares
(risco R04, identificado na proposta). O backend foi concluído na semana 8, dentro do
prazo previsto para o intercalar.

| Semanas | Datas | Realizado / Planeado | Marco |
|---|---|---|---|
| Sem. 1-2 | 17-28 mar | Kick-off com orientador. Proposta entregue: sinopse, MVP com critérios de aceitação, stack, calendário. | **✅ Proposta (25 mar)** |
| Sem. 3-4 | 31 mar-11 abr | Levantamento de requisitos MoSCoW (RF01-RF14, RNF, CB, OI). ADRs 001-011 formalizados. Diagramas C4 e modelo de dados. Repositório GitHub configurado. | - |
| Sem. 5 | 14-18 abr | Produtividade reduzida - Páscoa e eFólios concorrentes (risco R04). | - |
| Sem. 6 | 22-25 abr | Fase 2 - Persistência: DTOs, DAOs JDBC, 24 testes de integração. Suporte multi-base-de-dados. ADR-012. | - |
| Sem. 7 | 28 abr-2 mai | Fase 3-4 - Geradores de exercícios e REST API completa. 171 testes. ADR-013, ADR-014. Demo interna ao orientador. | **✅ Demo interna** |
| Sem. 8 | 5-6 mai | RF07 (persistência), RF08 (dashboard), RF09 (dificuldade adaptativa). Backend completo. 222 testes. Relatório intercalar. | **Intercalar (6 mai)** |
| Sem. 9-10 | 7-16 mai | Fase 5-6 - Frontend: teclado virtual, Web Audio API, Web MIDI API, ecrã de exercício. | - |
| Sem. 11-12 | 19-30 mai | Fase 7 - Dashboard de progresso, ecrã de fim de sessão. Testes de integração. | - |
| Sem. 13 | 2-6 jun | Revisão geral. Validação de todos os critérios de aceitação. Capturas de ecrã para Cap. 4. | - |
| Sem. 14 | 9-13 jun | Cap. 4 (Testes) e Cap. 5 (Conclusões). Revisão bibliográfica APA. Anexos. | - |
| Sem. 15 | 16-20 jun | Reunião de preparação para defesa. Ensaio de perguntas de júri. | **Prep. defesa** |
| Sem. 16 | 24 jun | Submissão do relatório final. Código e demo linkados no repositório. | **Final (24 jun)** |
