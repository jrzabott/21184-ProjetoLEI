# ADR-003 — Geração procedural de exercícios sem datasets externos

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

Os exercícios de teoria musical (identificação de intervalos, escalas, acordes) precisam de ser gerados dinamicamente. A alternativa natural seria uma base de dados de exercícios pré-definidos. A questão é: exercícios pré-definidos vs geração algorítmica a partir do modelo de domínio.

---

## Decisão

Os exercícios são gerados **algoritmicamente** a partir do modelo de domínio musical (classes `Note`, `Interval`, `Scale`, `Chord`). Não existem ficheiros de dados externos, bases de dados de perguntas, nem conteúdo estático. Cada exercício é construído em runtime: selecciona uma raiz aleatória, aplica as regras musicais do tipo de exercício, e gera as opções de resposta.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|------------|------------------|
| Base de dados de exercícios pré-definidos (CSV, JSON, tabela SQL) | Requer criação e manutenção de conteúdo; não demonstra que o modelo musical está correctamente formalizado; variedade limitada ao que foi pré-escrito. |
| API de teoria musical externa (ex: teoria.ly, APIs de musicologia) | Introduz dependência externa; viola CB03 (sem IA) se a API usar ML; adiciona latência de rede; ponto de falha fora do controlo do sistema. |

---

## Consequências

**Positivas:**
- Variedade virtualmente infinita — nunca esgota os exercícios
- Deployment simples: sem ficheiros de dados, sem seed de base de dados de conteúdo
- Demonstra que o modelo musical está correctamente formalizado no código — valor académico directo
- Dificuldade adaptativa implementável de forma natural (parâmetros do gerador)

**Negativas / trade-offs:**
- A qualidade dos exercícios depende da correcta implementação do modelo de domínio — erros no modelo propagam para todos os exercícios
- Requer testes unitários rigorosos do modelo de domínio antes de usar os geradores
