# ADR-005 - Web MIDI API para input de hardware MIDI

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O sistema deve suportar input musical via controlador MIDI físico (teclado USB). 

**Questão:** Como capturar eventos MIDI no browser sem dependências externas, sabendo que a compatibilidade é limitada?

---

## Decisão

O input MIDI físico é captado via **Web MIDI API nativa do browser** (`navigator.requestMIDIAccess()`). \

O browser detecta automaticamente os dispositivos MIDI ligados. Ao receber evento `noteon`, o mesmo callback `onNotePressed(midiNumber)` usado pelo teclado virtual é invocado — independente da entrada o processamento ocorre da mesma maneira após tradução pelo FrontEnd (ver ADR-002). 

Se a API não estiver disponível (Firefox, Safari), a aplicação funciona normalmente com o teclado virtual e exibe uma mensagem discreta.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição                                                                                                                                                                                                               |
|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| JazzPlugin / WebMIDIAPIShim | Plugins browser são obsoletos e inseguros (de acordo com breve busca na internet); adicionam complexidade de instalação que contradiz o objectivo de "arranca no browser sem instalação"                                        |
| Servidor MIDI com WebSocket (Node.js) | Introduz um terceiro processo a gerir; overhead desproporcional; viola princípio da simplicidade que estou a tentar seguir                                                                                                      |
| Ignorar MIDI completamente | RF04 (MIDI físico) é "Should have" - o impacto na demo/defesa é desproporcional ao esforço de implementação; vale a pena implementar com graceful degradation (isto é, sem introduzir complexidade adicional e se houver tempo) |

---

## Consequências

**Positivas:**
- Zero dependências externas de MIDI
- Detecção automática de dispositivos sem configuração manual
- Código de captura mínimo (pelo exemplos encontrados no Google: cerca ~20 linhas para o caso básico)
- Efeito na demo ao vivo é significativo - distingue o projecto de implementações puramente simuladas ou teóricas

**Negativas / trade-offs:**
- **Safari não suportado:** Web MIDI API funciona em Chrome, Edge, Firefox e Opera. Safari é o único browser moderno sem suporte (ver https://caniuse.com/midi). A aplicação funciona normalmente em Safari — apenas o input de hardware MIDI físico fica indisponível. Documentado como R03 em risks.md.
- Requer `{ sysex: false }` no request de acesso - pode gerar prompt de permissão no browser na primeira utilização
- RF04 classificado como "Should have" para que a ausência de hardware MIDI não impeça a entrega
