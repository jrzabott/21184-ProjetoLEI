# ADR-016 - Contrato de Erros: RFC 7807 Problem Details

**Data:** 2026-04-30
**Estado:** Aceite

## Contexto

Testes manuais via Swagger revelaram que erros HTTP da API Musical Trainer
retornavam formatos inconsistentes: plain text, JSON genérico do Spring, ou
nada. Qualquer consumidor frontend que tentasse parsear erros de forma uniforme
falhava.

## Decisão

Todos os erros HTTP usam `ProblemDetail` (RFC 7807, Spring Boot 3 nativo).
Um `GlobalExceptionHandler` central (@ControllerAdvice) substitui os
try-catch individuais em cada controller.

Shape fixo para todos os erros:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "O campo 'notes' é obrigatório",
  "instance": "/api/exercises/answer"
}
```

Mapeamento de excepções:

| Excepção | Status | Quando |
|---|---|---|
| MethodArgumentNotValidException | 400 | Bean Validation falhou (@NotNull etc.) |
| HttpMessageNotReadableException | 400 | Corpo em falta ou JSON inválido |
| IllegalArgumentException | 400 | Input inválido (tipo desconhecido, valor fora de range) |
| ResourceNotFoundException | 404 | Recurso não encontrado por ID |
| Exception (catch-all) | 500 | Erro inesperado |

## Razão

RFC 7807 é o standard da industria para erros REST. Spring Boot 3 suporta
ProblemDetail nativamente - zero dependências extra. Qualquer cliente HTTP
(frontend proprio, Swagger, Postman, integradores externos) consegue parsear
erros de forma uniforme sem lógica ad-hoc.

## Consequencias

Todos os controllers ficam sem try-catch de negócio. Novos endpoints herdam
o comportamento automaticamente. Commit de ADR separado do código por clareza
de historial.
