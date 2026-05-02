# ADR-019 - Expansao do dominio de escalas alem do MVP original

**Data:** 2026-05-02
**Estado:** Aceite
**Decisores:** Daniel Junior

---

## Contexto

O MVP original definia tres tipos de escala: MAJOR, MINOR_NATURAL e HARMONIC_MINOR.
Durante a implementacao do modelo de dominio (feat/13 a feat/25), o enum ScaleType
foi construido com 28 tipos de escala classificados por DifficultyLevel, incluindo:

- Modos diatonicos: DORIAN, PHRYGIAN, LYDIAN, MIXOLYDIAN, LOCRIAN
- Variantes menores: MELODIC_MINOR, HARMONIC_MAJOR
- Escalas pentatonicas: PENTATONIC_MAJOR, PENTATONIC_MINOR
- Blues: BLUES
- Simetricas: WHOLE_TONE, CHROMATIC, HALF_WHOLE_OCTATONIC, WHOLE_HALF_OCTATONIC
- Exoticas: PHRYGIAN_DOMINANT, LYDIAN_DOMINANT, DORIAN_FLAT_2, LOCRIAN_NATURAL_2,
  ALTERED, DOUBLE_HARMONIC_MAJOR, BYZANTINE

O requisito RF02 menciona "escalas maiores, escalas menores (natural e harmonica)" como
minimo do MVP. Esta decisao vai alem desse minimo sem ter sido documentada num ADR na
altura — omissao identificada na auditoria de Sem. 8 (feat/47).

---

## Decisao

Manter os 28 tipos de escala no dominio. O gerador usa `ScaleType.availableFor(band)` para
desbloquear tipos progressivamente conforme a dificuldade:

- BEGINNER (1-2): apenas MAJOR
- ELEMENTARY (3-4): + MINOR_NATURAL, PENTATONIC_MAJOR, PENTATONIC_MINOR
- INTERMEDIATE (5-6): + HARMONIC_MINOR, MELODIC_MINOR, BLUES
- ADVANCED (7-8): + modos diatonicos (DORIAN, PHRYGIAN, LYDIAN, MIXOLYDIAN, LOCRIAN),
  WHOLE_TONE, HARMONIC_MAJOR
- EXPERT (9-10): + todas as restantes (escalas exoticas, simetricas, cromatica)

Aliases (IONIAN=MAJOR, AEOLIAN=MINOR_NATURAL, SUPER_LOCRIAN=ALTERED,
MINOR_BLUES=BLUES, BYZANTINE=DOUBLE_HARMONIC_MAJOR) sao detectados via
`ScaleType.isAlias()` e nunca gerados - evitam exercicios duplicados.

---

## Alternativas consideradas

| Alternativa | Razao de rejeicao |
|---|---|
| Manter apenas os 3 tipos do MVP | O modelo ja estava construido com os 28 tipos. Remover seria trabalho sem beneficio pedagogico. O sistema de dificuldade torna a expansao invisivel para utilizadores iniciantes. |
| Adicionar tipos incrementalmente por branch | Desnecessario — o modelo de dominio e o sistema de dificuldade foram desenhados para acomodar qualquer numero de tipos sem mudancas de codigo no gerador. |

---

## Consequencias

**Positivas:**
- Utilizadores com nivel ADVANCED e EXPERT tem material significativamente mais rico
- Nenhuma mudanca de codigo e necessaria para adicionar um novo tipo de escala futuro —
  basta classificar no enum com a DifficultyLevel correcta
- O frontend pode mostrar progressao real ao utilizador conforme sobe de dificuldade

**Negativas / trade-offs:**
- CB12 (scope fechado, mudancas requerem ADR previo) foi violado na altura da implementacao;
  esta ADR regulariza a situacao retroactivamente
- A presenca de 28 tipos aumenta a complexidade do `WeaknessHintProvider`, que precisa de
  ter dicas para todos os tipos canonicos

---

## Referencias

- RF02 - requisito de tipos de escala (minimo definido na proposta)
- CB12 - scope fechado, novas funcionalidades requerem ADR
- ADR-015 - taxonomia de dificuldade (DifficultyLevel enum e availableFor())
- feat/47 - auditoria que identificou esta ADR como em falta
