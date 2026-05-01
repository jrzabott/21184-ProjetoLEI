# ADR-009 - Estrutura de ecrãs: páginas HTML separadas

**Data:** 2026-03-25  
**Estado:** Aceite - navegação por páginas separadas (não SPA)  
**Decisores:** Daniel Junior

---

## Contexto

A aplicação tem 5 ecrãs conceptuais distintos. 

Fiquei em dúvida quanto à abordagem de navegação: 
  1. Single Page Application (SPA) com estados em JS 
  2. páginas HTML separadas com navegação nativa. 

Esta decisão foi inicialmente identificada como open item (OI08) e resolvida com base na simplicidade e experiência prévia do desenvolvedor (o próprio Daniel).

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

| Alternativa | Razão de rejeição                                                                                                                                                                                                                                                                                        |
|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| SPA com router em JavaScript puro | Requer gestão de histórico de browser (`pushState`), lógica de routing, e gestão de estado global - complexidade desnecessária para o scope e para o nível de experiência atual.<br> Além do mais *Single Page Applications*, sem um framework implicaria meta-desenvolvimento para lidar com o routing. |
| SPA com framework (React Router, Vue Router) | Viola CB07 (sem frameworks); curva de aprendizagem desproporcional                                                                                                                                                                                                                                       |

---

## Consequências

**Positivas:**
- Abordagem mais simples possível
- Cada página é autónoma e testável independentemente
- Navegação browser nativa (botão "voltar") funciona sem configuração adicional
- Mais simples de explicar e visualizar

**Negativas / trade-offs:**
- Estado de sessão (`sessionId`) precisa de ser passado entre páginas via URL query params ou `sessionStorage` - solução: `sessionStorage` para `sessionId` activo
  - Ainda estou a pensar na possibilidade de tornar o estado obsfuscado com uma String codificada em Base64
- Cada página carrega os seus scripts de raiz - sem lazy loading (irrelevante neste momento inicial)

---

## Actualização - 2026-05-01

`select.html` foi absorvido em `index.html`. A selecção de tipo de exercício (INTERVAL/SCALE/CHORD)
e o arranque de sessao ficam no ecrã inicial, evitando uma página extra para 3 botões.
O ecrã de fim de sessao passa a ser `session-end.html` (autónomo) em vez de secção oculta em `exercise.html`.

Páginas actuais: `index.html`, `exercise.html`, `session-end.html`, `progress.html`.
