# ADR-014: Protocolo de resposta baseado em notas MIDI

**Estado:** Aceite
**Data:** 27 abr 2026

## Contexto

A primeira versão do protocolo de resposta usava um campo `answer: String` com o nome
do intervalo, escala ou acorde (ex: "5a Perfeita", "MAJOR"). Este modelo obrigava o
utilizador a escolher de uma lista — removendo a componente de performance e tornando
o exercício de escolha múltipla, não de prática musical.

O propósito da aplicação é treinar ouvido, mão e mente em conjunto. Isso requer que
o utilizador toque as notas, nao que clique numa etiqueta.

## Decisão

O utilizador responde a exercícios tocando notas num teclado virtual ou controlador
MIDI físico. A resposta é uma sequência de números MIDI enviada ao servidor.

### Endpoint de resposta

```
POST /api/exercises/answer
{
  "exerciseId": 42,
  "notes": [60, 62, 64, 65, 67, 69, 71, 72],
  "responseTimeMs": 6200
}
```

- `exerciseId` no corpo (nao no path) — é um detalhe interno, sem valor semântico para o utilizador
- `notes` — array de números MIDI na ordem em que foram tocados
- Apenas escalas ascendentes suportadas; ordem de pressão = ordem de envio

### Regras de validação por tipo

**INTERVAL:**
- Exactamente 2 notas
- Devem corresponder exactamente aos MIDI gerados (mesmo root, mesma oitava)
- Racional: treino de ouvido enraizado — identificar o intervalo específico gerado, nao apenas qualquer intervalo com a mesma distância

**SCALE:**
- Exactamente N notas, onde N = número de notas da escala + 1 (raiz → raiz uma oitava acima)
  - Escalas de 7 notas (diatónicas: MAJOR, MINOR_NATURAL, HARMONIC_MINOR): 8 notas
  - Escalas de 5 notas (pentatónicas): 6 notas
  - Escalas de 6 notas (blues): 7 notas
  - N é sempre determinado por `ScaleType.getSemitonePattern().length + 1`
- Qualquer oitava de partida é válida: valida-se o padrão de intervalos, nao os MIDI absolutos
- Padrão validado: diferenças entre notas consecutivas devem corresponder ao padrão da escala
- Escala MAJOR: [2, 2, 1, 2, 2, 2, 1] (W W H W W W H) — exemplo
- Última nota = primeira nota + 12 (sempre, independente do tipo)

**CHORD:**
- Exactamente 3 notas em ordem ascendente (I - III - V, sem inversões)
- Qualquer oitava de partida é válida
- Primeira nota deve ter o mesmo pitch class (nota sem oitava) que a raiz do exercício
- Padrão de intervalos: MAJOR=[4,3], MINOR=[3,4], DIMINISHED=[3,3], AUGMENTED=[4,4]
- Janela de agrupamento de notas simultâneas: 5000ms (arpejo e acorde bloqueado são equivalentes)
- Inversões nao suportadas nesta versão (OI09 diferido)

### SessionId

SessionId é gerido de forma transparente pelo frontend. Viaja em cada pedido de resposta.
A sessão é iniciada explicitamente via `POST /api/sessions/start` quando o utilizador entra
no modo de exercício.

### Coluna correct_answer

A coluna `exercises.correct_answer` armazena o array de notas esperadas como JSON string
(ex: `"[60,62,64,65,67,69,71,72]"`) para exibição de feedback ao utilizador
("tocaste X, a resposta correcta era Y"). O valor é calculado no momento de geração
do exercício a partir do questionJson + domínio.

### Dificuldade

- Gerida pelo **backend** (DifficultyService) — por tipo de exercício, de forma independente
- Baseada nos últimos 100 resultados por tipo: acerto >= 80% → sobe, < 40% → desce
- Backend clamps a dificuldade pedida em ±2 relação ao nível sugerido
- `GenerateResponse` inclui `suggestedDifficulty` como sugestão informativa para o frontend
- Frontend é responsável por passar `difficulty` em cada pedido de geração

> Nota: versão inicial previa gestão no frontend. Movido para backend em 28 abr 2026
> para garantir consistência entre sessões e centralizar lógica de negócio (ADR-015).

## Consequências

- AnswerRequest DTO: `{ exerciseId, notes[], responseTimeMs }` — sem `answer: String`
- ExerciseService.evaluateAnswer() aceita `int[] notes` e aplica lógica por tipo
- ScaleExerciseGenerator produz N notas (raiz → raiz oitava acima, N depende do tipo de escala)
- Avaliação de escalas é independente de oitava (padrão de intervalos) e independente de N fixo
- Avaliação de intervalos é dependente de oitava (notas exactas)
- Avaliação de acordes é independente de oitava, dependente de ordem (I-III-V)
## Referências

- RF01, RF02, RF03, RF04, RF13 (backend agnóstico à origem da nota)
- ADR-002 (backend agnóstico ao input)
- ADR-003 (geração procedural)
- ADR-013 (schema questionData)
- Respostas a perguntas de requisitos — ficheiro de sessão 2026-04-27
