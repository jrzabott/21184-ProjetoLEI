# Resultados de Performance - RNF06

**Data:** 2026-06-16T16:33:03
**Host:** localhost (ambiente local anonimizado)
**Sistema:** Windows AMD64
**Metodo:** 10 requests por endpoint via curl; tempo total HTTP (connect + transfer)
**Requisito:** RNF06 - validacao < 200ms em ambiente local

## Resultados

| Endpoint | Metodo | Media (ms) | Min (ms) | Max (ms) | RNF06 |
|----------|--------|-----------|---------|---------|-------|
| /api/exercises/generate | POST | 15.1 | 9.8 | 28.7 | ✅ |
| /api/exercises/generate | POST | 12.3 | 8.6 | 20.3 | ✅ |
| /api/exercises/generate | POST | 10.0 | 8.0 | 17.4 | ✅ |
| /api/progress | GET | 8.7 | 5.2 | 14.9 | ✅ |
| /api/sandbox/note/60 | GET | 3.6 | 2.9 | 5.6 | ✅ |

## Observacoes

- Todas as medicoes em ambiente local (loopback); latencia de rede nao incluida
- 10 requests por endpoint para amortizar variancia de JVM warm-up
- Informacao do host anonimizada: apenas sistema operativo e arquitectura
- RNF06 define limite de 200ms; todos os endpoints ficam abaixo desse limiar