# ADR-004 — Web Audio API para reprodução de som

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O sistema precisa de reproduzir notas musicais no browser quando o utilizador interage com o teclado virtual, e sons de feedback (acerto/erro) após cada resposta. A questão é a tecnologia de áudio a usar no frontend.

---

## Decisão

O som é gerado inteiramente no browser via **Web Audio API nativa**, usando síntese de osciladores (OscillatorNode). A frequência de cada nota é calculada pela fórmula `f = 440 × 2^((midiNumber - 69) / 12)`. Não são usadas bibliotecas de áudio externas nem ficheiros de áudio pré-gravados.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| Tone.js (biblioteca de áudio) | Dependência externa desnecessária para o scope; adiciona complexidade de gestão de pacotes; Web Audio API nativa é suficiente para tocar notas isoladas |
| Ficheiros MP3/WAV por nota | Requer 88+ ficheiros de áudio; deployment mais complexo; sem flexibilidade para ajustar timbre; incompatível com CB08 (sem dependências externas de áudio) |
| Howler.js | Idem Tone.js — overhead desnecessário |

---

## Consequências

**Positivas:**
- Zero dependências externas de áudio
- Funciona em qualquer browser moderno (Chrome, Firefox, Safari, Edge) sem instalação
- Controlo total sobre o timbre e duração dos sons
- Sons de feedback (acerto/erro) facilmente implementados com formas de onda e frequências distintas

**Negativas / trade-offs:**
- Sons de osciladores sintéticos são menos realistas do que samples gravados de piano real — aceitável para o scope académico
- Web Audio API requer interacção do utilizador antes de criar som (limitação de segurança do browser) — requer gestão de AudioContext na primeira interacção
