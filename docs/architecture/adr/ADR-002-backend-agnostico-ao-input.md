# ADR-002 - Backend agnóstico à origem do input

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O sistema aceita input musical de duas fontes distintas: teclado virtual no browser (clique com rato) e controlador MIDI físico (hardware USB). Ambos precisam de enviar a nota pressionada para o backend validar a resposta. A questão é: o backend deve saber de onde veio a nota?

---

## Decisão

O backend não distingue a origem da resposta. Tanto o teclado virtual como o controlador MIDI físico enviam dados pelo **mesmo endpoint REST** (`POST /api/exercises/{id}/answer`) com o mesmo formato JSON. A distinção de origem (teclado virtual vs MIDI) é resolvida inteiramente no frontend, que normaliza os eventos de ambas as fontes para o mesmo callback `onNotePressed(midiNumber)` antes de chamar a API.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| Endpoints separados por tipo de input (`/answer/keyboard`, `/answer/midi`) | Viola separação de responsabilidades. O backend não tem razão para conhecer o mecanismo de input - essa é uma preocupação da camada de apresentação. |
| Campo `inputSource` no request body | Informação não usada pelo backend - adiciona campo sem valor funcional e cria acoplamento desnecessário. |

---

## Consequências

**Positivas:**
- Demonstra desacoplamento entre camada de entrada e lógica de negócio - princípio de engenharia com valor académico directo
- Permite futura extensão para outros inputs (microfone, notação textual) sem alterar o backend
- Backend mais simples: apenas valida a resposta, independentemente de como chegou

**Negativas / trade-offs:**
- O frontend é responsável por normalizar eventos heterogéneos (MouseEvent, MIDIMessageEvent) para o mesmo formato - lógica adicional no cliente
