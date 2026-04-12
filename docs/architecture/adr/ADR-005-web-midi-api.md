# ADR-005 — Web MIDI API para input de hardware MIDI

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O sistema deve suportar input musical via controlador MIDI físico (teclado USB). A questão é como capturar eventos MIDI no browser sem dependências externas, sabendo que a compatibilidade é limitada.

---

## Decisão

O input MIDI físico é captado via **Web MIDI API nativa do browser** (`navigator.requestMIDIAccess()`). O browser detecta automaticamente os dispositivos MIDI ligados. Ao receber evento `noteon`, o mesmo callback `onNotePressed(midiNumber)` usado pelo teclado virtual é invocado — garantindo agnósticidade do backend (ver ADR-002). Se a API não estiver disponível (Firefox, Safari), a aplicação funciona normalmente com o teclado virtual e exibe uma mensagem discreta.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| JazzPlugin / WebMIDIAPIShim | Plugins browser são obsoletos e inseguros; adiciona complexidade de instalação que contradiz o objectivo de "arranca no browser sem instalação" |
| Servidor MIDI com WebSocket (Node.js) | Introduz um terceiro processo a gerir; overhead desproporcional; viola simplicidade do scope |
| Ignorar MIDI completamente | RF04 (MIDI físico) é "Should have" — o impacto na demo/defesa é desproporcional ao esforço de implementação; vale a pena implementar com graceful degradation |

---

## Consequências

**Positivas:**
- Zero dependências externas de MIDI
- Detecção automática de dispositivos sem configuração manual
- Código de captura mínimo (~20 linhas para o caso básico)
- Efeito na demo ao vivo é significativo — distingue o projecto de implementações puramente simuladas

**Negativas / trade-offs:**
- **Compatibilidade limitada:** Web MIDI API só funciona em browsers Chromium (Chrome, Edge, Opera). Firefox e Safari não suportam. Documentado como R03 em risks.md.
- Requer `{ sysex: false }` no request de acesso — pode gerar prompt de permissão no browser na primeira utilização
- RF04 classificado como "Should have" para que a falta de MIDI não impeça a entrega
