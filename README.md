# Musical Theory Trainer

> Aprende teoria musical através de exercícios procedurais com feedback imediato - Duolingo para teoria musical.

**Estudante:** Daniel Junior · 2304335  
**Orientador:** Pedro Pestana  
**UC:** Projecto de Engenharia Informática · Universidade Aberta · 2025/26  
**Repositório:** https://github.com/jrzabott/21184-ProjetoLEI

---

## Estado actual

🟡 **Amarelo** - Documentação inicial completa. Implementação não iniciada (Sem. 4 - a aguardar setup Spring Boot).

---

## O que está implementado

- [ ] Modelo de domínio Java (Note, Interval, Scale, Chord)
- [ ] Geração procedural de exercícios (intervalos, escalas, acordes)
- [ ] API REST (generate, answer, sessions, progress, sandbox)
- [ ] Teclado virtual com som (Web Audio API)
- [ ] Input MIDI físico (Web MIDI API)
- [ ] Persistência de sessões e resultados (H2 / PostgreSQL)
- [ ] Dashboard de progresso
- [ ] Modo sandbox
- [ ] Dificuldade adaptativa

---

## O que está pendente

- [ ] Setup Spring Boot + estrutura de packages - **próximo passo**
- [ ] Modelo de domínio Java (Fase 1)
- [ ] Schema SQL + DAO classes com JDBC (Fase 2)
- [ ] Geradores de exercícios (Fase 3)
- [ ] REST Controllers (Fase 4)
- [ ] Frontend: teclado virtual (Fase 5)
- [ ] Frontend: ecrã de exercício (Fase 6)
- [ ] Frontend: dashboard (Fase 7)
- [ ] Ecrã de fim de sessão (Fase 8)
- [ ] Ecrã de selecção de exercício (Fase 9)

---

## Como instalar e correr

### Pré-requisitos

```
Java 21+
Maven 3.9+
```

### Instalação

```bash
# 1. Clonar o repositório
git clone https://github.com/jrzabott/21184-ProjetoLEI.git
cd 21184-ProjetoLEI

# 2. Correr o backend
cd src
mvn spring-boot:run
```

### Acesso

```
Backend API: http://localhost:8080/api
H2 Console (dev): http://localhost:8080/h2-console
Frontend: abrir index.html no browser (sem servidor necessário)
```

---

## Decisões de arquitectura principais

| Decisão | Alternativa considerada | Razão da escolha |
|---------|------------------------|-----------------|
| Java 21 + Spring Boot + JDBC (backend) | Node.js / FastAPI | Experiência profissional do estudante - JDBC puro com DAOs em vez de JPA: SQL explícito, zero Hibernate magic |
| HTML + JS vanilla (frontend) | React / Vue | Zero experiência frontend - frameworks adicionariam curva desnecessária |
| Geração procedural (sem datasets) | Ficheiros de dados externos | Simplifica deploy, demonstra que o modelo musical está formalizado no código |
| Web Audio API nativa | Biblioteca de áudio (Tone.js) | Zero dependências externas, suficiente para o scope |
| H2 (dev) + PostgreSQL/SQLite3 (prod) | PostgreSQL logo de início | H2 permite arrancar sem configuração; decisão prod em aberto (OI01) |

Para detalhe completo: `docs/architecture/adr/`

---

## Referências e IA utilizada

### Referências técnicas

- Spring Boot — https://spring.io/projects/spring-boot
- Web Audio API (MDN) — https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API
- Web Audio API (compatibilidade) — https://caniuse.com/audio-api
- Web MIDI API (MDN) — https://developer.mozilla.org/en-US/docs/Web/API/Web_MIDI_API
- Web MIDI API (compatibilidade) — https://caniuse.com/midi *(não suportado em Safari — ver ADR-005)*
- C4 Model — https://c4model.com
- Conventional Commits — https://www.conventionalcommits.org

### Ferramentas de IA utilizadas

| Ferramenta | Para que foi usada |
|-----------|-------------------|
| Claude (claude.ai) | Definição de arquitectura, levantamento de requisitos, modelação do domínio musical, planeamento de implementação |
| Claude Code | Assistência na implementação (a partir de Sem. 5), documentação técnica, revisão de código |

---

*Última actualização: 2026-04-12 · Sem. 4*
