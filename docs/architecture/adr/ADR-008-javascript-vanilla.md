# ADR-008 - JavaScript vanilla no frontend (sem frameworks)

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O frontend necessita de um teclado visual interactivo, reprodução de som, captura de eventos MIDI, comunicação com a API REST, e 4-5 ecrãs distintos. O estudante tem zero experiência em desenvolvimento frontend. A escolha de stack frontend determina a curva de aprendizagem necessária.

---

## Decisão

O frontend é implementado em **HTML e JavaScript ES6+ puro (vanilla)**, sem frameworks (React, Vue, Angular, Svelte) e sem ferramentas de build (npm, Vite, Webpack, Parcel). Os ficheiros HTML abrem directamente no browser. Organização em módulos ES6 (`type="module"`) quando necessário para separação de responsabilidades.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| React | Requer JSX, bundler, npm, gestão de estado - tudo novo para o estudante. Curva de aprendizagem desproporcional ao scope. |
| Vue 3 | Mais simples que React mas ainda requer setup de build tools ou CDN com limitações. A directiva `v-model` e o sistema de componentes têm a sua própria curva. |
| HTMX + templates Thymeleaf | Mistura frontend e backend (ver ADR-001); reduz separação de camadas. |
| Alpine.js | Mais simples, mas ainda é uma dependência externa desnecessária para o scope. |

---

## Consequências

**Positivas:**
- Zero curva de aprendizagem de frameworks - o estudante aprende JS puro, que é mais transferível
- Zero ferramentas de build - `abrir no browser` é suficiente
- Código mais transparente e legível para o júri - sem abstracções de framework
- Compatível com CB07 (sem frameworks JavaScript)

**Negativas / trade-offs:**
- Gestão de DOM manual - mais verboso do que frameworks declarativas
- Sem reactivity automática - actualizações de UI requerem manipulação explícita do DOM
- Código JavaScript frontend requer mais comentários explicativos - mitigado pelo facto de Claude Code estar disponível para assistência linha a linha
