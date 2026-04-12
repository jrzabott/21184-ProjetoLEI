# ADR-009 - Estrutura de ecrãs: páginas HTML separadas

**Data:** 2026-03-25  
**Estado:** Aceite - navegação por páginas separadas (não SPA)  
**Decisores:** Daniel Junior

---

## Contexto

A aplicação tem 5 ecrãs conceptuais distintos. A questão é a abordagem de navegação: Single Page Application (SPA) com estados em JS vs páginas HTML separadas com navegação browser nativa. Esta decisão foi inicialmente identificada como open item (OI08) e resolvida com base na experiência do estudante.

---

## Decisão

A navegação é feita com **páginas HTML separadas**, cada uma servida directamente pelo browser. Não há router em JavaScript. Os 5 ecrãs correspondem a ficheiros HTML distintos:

| Ecrã | Ficheiro |
|------|---------|
| Principal / Sandbox | `index.html` |
| Selecção de exercício | `select.html` (ou modal em `index.html`) |
| Exercício activo | `exercise.html` |
| Fim de sessão | Secção em `exercise.html` (estado oculto/visível) |
| Dashboard de progresso | `progress.html` |

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| SPA com router em JavaScript puro | Requer gestão de histórico de browser (`pushState`), lógica de routing, e gestão de estado global - complexidade desnecessária para o scope e para o nível de experiência frontend do estudante |
| SPA com framework (React Router, Vue Router) | Viola CB07 (sem frameworks); curva de aprendizagem desproporcional |

---

## Consequências

**Positivas:**
- Abordagem mais simples possível para um estudante sem experiência frontend
- Cada página é autónoma e testável independentemente
- Navegação browser nativa (botão "voltar") funciona sem configuração adicional
- Mais simples de explicar e defender perante o júri

**Negativas / trade-offs:**
- Estado de sessão (`sessionId`) precisa de ser passado entre páginas via URL query params ou `sessionStorage` - solução: `sessionStorage` para `sessionId` activo
- Cada página carrega os seus scripts de raiz - sem lazy loading (irrelevante para o scope)
