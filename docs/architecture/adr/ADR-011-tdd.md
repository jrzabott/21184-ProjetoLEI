# ADR-011 - TDD como metodologia de desenvolvimento

**Data:** 2026-04-12  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O projecto necessita de uma abordagem de desenvolvimento que garanta código correto, previna regressões, e produza um conjunto de testes que sirva como documentação viva do comportamento esperado do sistema. 

Dada a natureza do domínio musical - onde um erro num cálculo de intervalo propaga silenciosamente para todos os exercícios gerados - é crítico ter verificação automática em cada camada antes de qualquer código de produção existir.

---

## Decisão

O projecto segue, ao melhor do conhecimento, **TDD (Test-Driven Development)** em (*wishful thinking*) 85%+ o código de produção:

Seguindo os famosos passos:
1. **Red** - escrever um teste que falha, descrevendo o comportamento esperado
2. **Green** - escrever o código mínimo que faz o teste passar
3. **Refactor** - melhorar o código sem quebrar o teste

**Regras operacionais (não negociáveis):**
- Nenhuma feature é implementada sem um teste failing primeiro
- Nenhum bug é corrigido sem primeiro escrever um teste que reproduz o bug imediatamente e previne que este volte a aparecer sob outros nomes e formas no futuro.
- Um teste que passa antes de qualquer código ser escrito não é TDD - é inútil
- Regressões são prevenidas por definição: se o teste existia antes da feature, quebrar a feature quebra o teste
- Os testes serão focados em contratos funcionais, mais do que a explorar edge cases para Unit Tests - o objectivo é que cada critério funcional da aplicação possua um teste correspondente, ao invés de explorar edge cases raros ou limites da própria linguagem que causa comportamentos inesperados.
  - Pretendo escrever código seguro, mas sem exagerar na paranoia de edge cases que não são relevantes para o domínio musical.
  - O foco do projeto não é segurança, mitigação e prevenção de exploits. 

**Stack de testes - Backend (Java):**
- **JUnit 5** - framework de testes unitários
- **AssertJ** - assertions expressivas e legíveis
- **Spring Boot Test** + **MockMvc** - testes de integração para REST controllers
- **H2 in-memory** - base de dados isolada para testes de DAOs

**Stack de testes - Frontend (JavaScript):**
- Lógica isolada testada com asserções simples
  - Este é o maior desafio do projeto: a interacção com o DOM e a UI é difícil de testar com TDD puro em vanilla JS, especialmente sem ferramentas de build ou frameworks de teste.
- Interacções de DOM e UI testadas manualmente e documentadas no Cap. 4 do relatório
- TDD puro em JS vanilla sem framework é possível mas verboso - lógica pura terá prioridade

---

## Alternativas consideradas

| Alternativa | Razão de rejeição                                                                                                                        |
|------------|------------------------------------------------------------------------------------------------------------------------------------------|
| Test-after (testes escritos após implementação) | Não previne regressões com a mesma eficácia; tendência para testar apenas o caminho feliz; o teste é enviesado pelo código que já existe |
| Sem testes formais | Inaceitável: um júri que pergunte "como verificou que os intervalos estão correctos?" precisa de uma resposta concreta e demonstrável    |
| Cobertura mínima por percentagem (ex: 80%) | Percentagem é fácil de enganar com testes triviais; o critério real é: contratos funcionais têm um teste                                 |

---

## Consequências

**Positivas:**
- Cada teste é uma especificação executável - serve como documentação no Cap. 4 do relatório
- Regressões detectadas imediatamente ("ligo a luz e a batedeira não liga")
- Confiança para refactorizar: se os testes passam, o comportamento está preservado
- Bugs são reproduzidos antes de serem corrigidos - o fix é verificável e não introduz novos problemas

**Negativas / trade-offs:**
- Mais tempo por feature no curto prazo - compensado pela ausência de debugging tardio e regressões
- Frontend vanilla JS dificulta TDD de interacções de DOM - mitigado por separar lógica pura (testável) de manipulação de DOM (manual)
