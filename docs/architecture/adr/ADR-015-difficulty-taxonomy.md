# ADR-015: Taxonomia de Dificuldade para Exercícios

**Estado:** Aceite
**Data:** 28 abr 2026

## Contexto

O campo `difficulty` nos exercícios era armazenado como inteiro 1-10 mas sem semântica formal.
Os geradores usavam magic numbers (`if (difficulty <= 3)`) espalhados pelo código.
O requisito RF09 requer adaptação automática de dificuldade baseada no histórico do utilizador.
Sem uma taxonomia formal, implementar RF09 de forma correcta e consistente é impossível.

## Decisão

Criar o enum `DifficultyLevel` no package `domain` com 5 bandas semânticas.
Cada tipo de exercício (IntervalType, ScaleType, ChordType) carrega o seu nível.
Os geradores filtram via `availableFor(DifficultyLevel)` - sem magic numbers.

### DifficultyLevel: 5 bandas para a escala 1-10

| Banda | Numérico | Semântica |
|-------|----------|-----------|
| BEGINNER    | 1-2 | Material mais familiar e consonante |
| ELEMENTARY  | 3-4 | Segunda camada de complexidade |
| INTERMEDIATE | 5-6 | Dificuldade intermédia |
| ADVANCED    | 7-8 | Material avançado |
| EXPERT      | 9-10 | Escalas exóticas e dissonâncias extremas |

### Classificação de IntervalType

| Banda | Intervalos |
|-------|-----------|
| BEGINNER | Uníssono (P1), 3ª Maior (M3), 5ª Perfeita (P5), Oitava (P8) |
| ELEMENTARY | 2ª Maior (M2), 3ª Menor (m3), 4ª Perfeita (P4), 7ª Menor (m7), 7ª Maior (M7) |
| ADVANCED | 2ª Menor (m2), Trítono, 5ª Aug/6ª Menor (aug5), 6ª Maior (M6) |

Racional: ordenação por consonância - intervalos perfeitos primeiro, dissonâncias depois.
Intervalos compostos (> 12 semítons) fora do scope nesta versão.

### Classificação de ScaleType

| Banda | Tipos |
|-------|-------|
| BEGINNER | MAJOR, IONIAN |
| ELEMENTARY | MINOR_NATURAL, AEOLIAN, PENTATONIC_MAJOR, PENTATONIC_MINOR |
| INTERMEDIATE | HARMONIC_MINOR, MELODIC_MINOR, BLUES, MINOR_BLUES |
| ADVANCED | DORIAN, PHRYGIAN, LYDIAN, MIXOLYDIAN, LOCRIAN, WHOLE_TONE, HARMONIC_MAJOR |
| EXPERT | PHRYGIAN_DOMINANT, LYDIAN_DOMINANT, DORIAN_FLAT_2, LOCRIAN_NATURAL_2, ALTERED, SUPER_LOCRIAN, CHROMATIC, HALF_WHOLE_OCTATONIC, WHOLE_HALF_OCTATONIC, DOUBLE_HARMONIC_MAJOR, BYZANTINE |

Raízes brancas (C, D, E, F, G, A, B) para BEGINNER e ELEMENTARY - mais familiares ao iniciante.

### Classificação de ChordType

| Banda | Tipos |
|-------|-------|
| BEGINNER | MAJOR |
| INTERMEDIATE | MINOR |
| ADVANCED | DIMINISHED, AUGMENTED |

### Algoritmo RF09 (DifficultyService)

- Analisar últimos 100 exercícios do mesmo tipo
- Se taxa acerto >= 80% - sugerir dificuldade +1
- Se taxa acerto < 40% - sugerir dificuldade -1
- Senão - manter dificuldade actual
- Clamp: resultado sempre entre 1 e 10
- ExerciseService aplica clamp adicional de ±2 relativamente à sugestão

## Consequências

- Zero magic numbers nos geradores - eliminados completamente
- `availableFor(DifficultyLevel)` em todos os enums de exercício
- `DifficultyService` implementa RF09 de forma centralizada
- `GenerateResponse` inclui `suggestedDifficulty` para informação ao frontend
- Escala proposta originalmente como 1-5 (proposta.md F09) revista para 1-10 - ver revisão em proposta.md

## Referências

- RF09, ADR-013, ADR-014
- proposta.md F09 (revisão 28 abr 2026)
