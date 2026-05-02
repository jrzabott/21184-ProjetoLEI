# Musical Theory Trainer

> Aprende teoria musical através de exercícios procedurais com feedback imediato - Duolingo para teoria musical.

**Estudante:** Daniel Junior · 2304335  
**Orientador:** Pedro Pestana  
**UC:** Projecto de Engenharia Informática · Universidade Aberta · 2025/26  
**Repositório:** https://github.com/jrzabott/21184-ProjetoLEI

---

## Estado actual

🟢 **Verde** - Backend completo (Fases 0-4). 230 testes a passar. API REST funcional. Wireframes completos. ADRs documentados.  
🟡 **Em progresso** - Relatório intercalar (deadline 6 mai)

**Última actualização:** Sem. 8 (30 Abr 2026)

---

## O que está implementado

- [x] Modelo de domínio Java (Note, Interval, Scale, Chord) ✅
- [x] Persistência JDBC pura - SessionDao, ExerciseDao, ResultDao ✅
- [x] Suporte multi-BD - H2 (dev), SQLite e PostgreSQL (produção) ✅
- [x] Geração procedural de exercícios (intervalos, escalas, tríades) ✅
- [x] API REST completa (generate, answer, sessions, progress, sandbox) ✅
- [x] RFC 7807 GlobalExceptionHandler com Bean Validation ✅
- [x] Dificuldade adaptativa (RF09) ✅
- [x] Dashboard de progresso (RF08) ✅
- [x] 230 testes de integração a passar ✅
- [x] Wireframes dos 4 ecrãs (docs/design/) ✅
- [x] 19 ADRs documentados ✅
- [ ] Teclado virtual com som (Web Audio API) - Fase 5
- [ ] Input MIDI físico (Web MIDI API) - Fase 5
- [ ] Frontend implementado - a partir de Sem. 9

---

## O que está pendente

### Fase 5: Frontend - Teclado Virtual
- [ ] index.html + piano.js + audio.js
- [ ] Web Audio API (síntese sonora)
- [ ] Ligação ao backend via fetch

### Fase 6-9: Frontend - Ecrãs
- [ ] exercise.html - ecrã de exercício activo
- [ ] progress.html - dashboard de progresso
- [ ] Ecrã de fim de sessão, selecção de exercício

---

## Como instalar e correr

> Instruções a completar na Fase 0 (setup Spring Boot). Nenhum código existe ainda.

Requisitos previstos: Java 21+, Maven 3.9+. O backend arrancará com `mvn spring-boot:run`; o frontend abre directamente no browser sem servidor adicional.

---

## Decisões de arquitectura principais

| Decisão | Alternativa considerada | Razão da escolha                                                                                                               |
|---------|------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| Java 21 + Spring Boot | Node.js / FastAPI | Experiência prévia - JDBC puro com DAOs em vez de JPA: SQL explícito, zero Hibernate magic                                     |
| HTML + JS vanilla (frontend) | React / Vue | Pouca experiência c/ frontend - frameworks adicionariam curva de aprendizado desnecessária                                     |
| Geração procedural (sem datasets) | Ficheiros de dados externos | Simplifica deploy, modelo musical está formalizado no código                                                                   |
| Web Audio API nativa | Biblioteca de áudio (Tone.js) | Zero dependências externas, suficiente para o scope                                                                            |
| H2 (dev) + PostgreSQL/SQLite3 (prod) | PostgreSQL logo de início | H2 permite arrancar sem configuração; decisão prod em aberto (OI01); ainda incerto se tentari suportar múltiplas engines de DB |

Para detalhe completo: `docs/architecture/adr/`

---

## Referências e IA utilizada

### Referências técnicas

- Spring Boot - https://spring.io/projects/spring-boot
- Web Audio API (MDN) - https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API
- Web Audio API (compatibilidade) - https://caniuse.com/audio-api
- Web MIDI API (MDN) - https://developer.mozilla.org/en-US/docs/Web/API/Web_MIDI_API
- Web MIDI API (compatibilidade) - https://caniuse.com/midi *(não suportado em Safari - ver ADR-005)*
- C4 Model - https://c4model.com
- Conventional Commits - https://www.conventionalcommits.org

### Ferramentas de IA utilizadas

| Ferramenta                 | Para que foi usada                                                                                                           |
|----------------------------|------------------------------------------------------------------------------------------------------------------------------|
| Claude (claude.ai)         | Definição de arquitectura, levantamento de requisitos, modelação do domínio musical, planeamento de implementação            |
| Gemini (gemini.google.com) | Desafiar, validar e dupla verificação de sugestões da IA principal                                                           |
| Grok (grok.com)            | Desafiar, validar e tripla verificação de sugestões da IA principal. Diferentes ferramentas, sugerem diferentes perspectivas |
| Claude Code                | Assistência na implementação (a partir de Sem. 5), documentação técnica, revisão de código  

---

## Changelog

### 2026-04-24 · Sem. 5

**Task 1.1 - Setup Spring Boot** 
Spring Boot 3.3.0 configurado com Java 21 e Maven 3.9.15. Package base `pt.uab.musicaltrainer` criado, aplicação arranca sem erros. Schema SQL definido com tabelas para exercises, sessions, results. H2 em memória para desenvolvimento. `mvn clean test` passa, build sucesso.

**Task 1.2 - Modelo de Domínio (Note)**
Classe Note implementada com conversão MIDI para notação musical (C4, D#4, etc). Static cache de 128 notas (C-1 a G9) - O(1) lookups. Métodos: `fromMidi(int)`, `getName()`, `getOctave()`, `getMidiNumber()`. 7 testes, todos passing. TDD aplicado - testes escritos antes da implementação.

**Task 1.3 - Modelo de Domínio (Interval)**
Classe Interval implementada para identificação de intervalos musicais. Factory estático `Interval.between(Note, Note)` calcula distância em semítoms e mapeia para nome completo (Uníssono, 2ª Menor/Maior, 5ª Perfeita, Oitava Perfeita, etc). 10 testes cobrindo todos intervalos até oitava, incluindo casos descendentes. TDD aplicado - IntervalTest escrito e falhado antes da implementação.

**Task 1.4 - Modelo de Domínio (Scale)**
Classe Scale implementada com suporte para 6 tipos de escalas: MAJOR, MINOR_NATURAL, HARMONIC_MINOR, DORIAN, PENTATONIC_MINOR, BLUES. Factory estático `Scale.get(type, root)` gera notas usando padrões de semítons (interval patterns). 8 testes cobrindo diferentes escalas e raízes, incluindo diferenças entre tipos. TDD aplicado - testes escritos antes da implementação.

**Task 1.5 - Modelo de Domínio (Chord)**
Classe Chord implementada com suporte para 4 tipos de tríades: MAJOR, MINOR, DIMINISHED, AUGMENTED. Factory estático `Chord.get(type, root)` gera notas usando padrões de semítons. Value object com equals()/hashCode() para comparação confiável. 13 testes cobrindo todos tipos de tríades, diferenciação entre tipos e raízes, igualdade, e uso em coleções hash-based. TDD aplicado - testes escritos antes da implementação.

---

*Última actualização: 2026-04-24 · Sem. 5*
