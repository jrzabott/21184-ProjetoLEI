# ADR-023 — Presets de timbre Web Audio e scheduling nativo

**Data:** 2026-05-31
**Estado:** aceite

## Contexto

O som original (onda sine, envelope simples) soava a notificacao de sistema.
Sem opcao de escolha para o utilizador. playNotes() usava setTimeout() sujeito
a throttling do browser em abas inactivas ou sob carga, causando drops e
timing inconsistente em sequencias longas.

## Decisao

4 presets de timbre com envelopes distintos:
- sine: referencia — mais proxima de flauta, percecao de altura clara
- triangle: mais quente que sine, menos brilhante
- sawtooth: brilhante, mais facil de ouvir em ambientes ruidosos
- piano: sawtooth + decay rapido, imita percussao de martelo

Selector de radio buttons abaixo do teclado em ambas as paginas (index.html,
exercise.html). Escolha persiste via sessionStorage (mt_timbre). Scheduling
nativo Web Audio API via ac.currentTime em feat/77 (passo seguinte).

## Alternativas rejeitadas

- Amostras de audio reais (violaria ADR-003 — geracao procedural sem datasets externos)
- Sintetizador FM completo (complexidade desproporcionada ao MVP)
- Apenas sine com envelope melhorado (nao resolve a falta de escolha do utilizador)

## Consequencias

Som notavelmente melhor na gama C2-C6. Sem dependencias externas.
Sawtooth e piano pedem amplitude ligeiramente reduzida (0.22 vs 0.35)
para nao saturar em volumes altos. Piano tem decay exponencial mais rapido —
nao adequado para notas muito longas (> 2s).
