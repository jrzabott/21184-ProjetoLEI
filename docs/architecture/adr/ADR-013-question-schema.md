# ADR-013: Schema do campo question nos exercícios

**Estado:** Aceite
**Data:** 27 abr 2026

## Contexto

O campo `question` na tabela `exercises` armazena JSON como String.
Sem schema definido, geradores (Fase 3) e controllers (Fase 4) podiam
produzir formatos incompatíveis. OI07 estava em aberto desde Fase 1.

## Decisão

Usar JSON plano com números MIDI (0-127). Cada tipo de exercício tem
o seu próprio schema mínimo:

**INTERVAL:** `{"notes": [midiA, midiB]}`
Dois números MIDI. O intervalo é calculado a partir deles pelo domínio.

**SCALE:** `{"root": midiRaiz, "type": "MAJOR"}`
Raiz + tipo. As notas são reconstruídas pelo domínio (determinístico).

**CHORD:** `{"root": midiRaiz, "type": "MAJOR"}`
Raiz + tipo. As notas são reconstruídas pelo domínio (determinístico).

O campo `correct_answer` armazena a resposta como string legível:
- INTERVAL: nome do intervalo (ex: "5a Perfeita")
- SCALE: tipo da escala (ex: "MAJOR", "MINOR_NATURAL", "HARMONIC_MINOR")
- CHORD: tipo do acorde (ex: "MAJOR", "MINOR", "DIMINISHED", "AUGMENTED")

## Justificativa

Storage mínimo, reconstrução no domínio. Scale.get() e Chord.get() são
determinísticos dado root + type - guardar as notas seria redundante e
criaria risco de inconsistência. Para INTERVAL, guardar os dois MIDI numbers
é suficiente para recalcular tudo (nome, semítons, distratores).

## Consequências

- Geradores guardam schema mínimo; controllers enriquecem a resposta REST
- Mudanças de schema requerem novo gerador + migração cuidadosa
- Nenhuma migração de BD necessária para esta decisão (campo é VARCHAR)

## Referências

- OI07 em docs/scope/requirements.md
- ADR-003 (geração procedural sem datasets)
- ADR-010 (JDBC sem JPA)
