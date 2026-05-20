# ADR-020 - Testes de integração frontend: Cucumber + Selenium

**Data:** 2026-05-20
**Estado:** Aceite
**Decisores:** Daniel Junior

---

## Contexto

O backend tem 230 testes unitários e de integração. O frontend foi implementado em HTML + JS vanilla
sem testes automáticos. Para validar os fluxos de utilizador de ponta a ponta e para documentar
a cobertura de requisitos no relatório final (Cap. 4), optei por introduzir testes de browser
automatizados.

A questão tem duas dimensões:
- Que framework de automação usar (Selenium, Playwright, Cypress, etc.)
- Que abordagem de especificação usar (JUnit puro, BDD/Gherkin, etc.)

---

## Decisão

Optei por **Cucumber + Gherkin + Selenide** integrados com `@SpringBootTest(RANDOM_PORT)`.

### Framework de browser: Selenide (sobre Selenium)

Selenide e um wrapper sobre Selenium WebDriver que elimina a maioria do código de
espera explícita (`WebDriverWait`, `ExpectedConditions`) e simplifica os seletores.
Enquanto Selenium puro requer algo como:

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("feedback-panel")));
```

Selenide escreve-se:

```java
$("#feedback-panel").shouldBe(visible);
```

O resultado e código de teste mais legivel, com menos linhas, e sem gestao manual de timeouts.
O Selenide 7.x usa o Selenium Manager incluido no Selenium 4.x para descarregar automaticamente
o ChromeDriver compativel com o Chrome instalado - sem configuracao manual.

### Abordagem de especificação: Cucumber + Gherkin

Os ficheiros `.feature` em Gherkin têm duas funções em simultâneo: são o contrato de teste
E a documentação dos casos de uso. Cada `Scenario` corresponde a um fluxo identificado nos
requisitos (F03-F09). Isto permite:

- Incluir os ficheiros `.feature` directamente no Cap. 4 do relatório como especificação de testes
- Gerar relatórios HTML automáticos (`target/cucumber-reports/`) com resultados por cenário
- Rastrear cada requisito funcional até ao cenário que o valida

### Integração com Spring Boot

O `cucumber-spring` (cucumber-junit-platform-engine) arranca o contexto Spring via
`@SpringBootTest(RANDOM_PORT)` antes dos testes. O Selenide e configurado com
`Configuration.baseUrl = "http://localhost:" + port` no hook `@Before` de cada cenário.
Isto garante:

- Stack completa: backend real + H2 in-memory + frontend servido via `file:frontend/`
- Sem mocks: os testes exercitam o mesmo código que corre em produção
- Isolamento: cada execução começa com base de dados vazia (H2 in-memory fresh por contexto)

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|-------------|-------------------|
| Playwright (Java) | API mais moderna mas menos documentação em contexto académico Java; Selenium e mais reconhecível para o júri |
| Cypress | JavaScript; requer runtime Node.js separado; mistura de linguagens no mesmo repositório Java |
| JUnit + Selenium puro (sem Cucumber) | Sem ficheiros `.feature` = sem documentação legível por não-programadores; menos valor para o relatório |
| Testes manuais apenas | Não reproduzíveis; não documentáveis de forma estruturada; violam o espírito de RNF09 (TDD/automação) |

---

## Consequências

**Positivas:**
- Ficheiros `.feature` como documentação viva dos casos de uso - citable no Cap. 4
- Relatório HTML automático com resultados por requisito funcional
- Testes executáveis com um único comando: `./mvnw test -Dtest=CucumberRunnerTest`
- Page Object Model isola os seletores CSS - se um ID do HTML mudar, uma linha de Java muda
- Cobertura verificável de F03, F05, F07, F08, F09

**Negativas / trade-offs:**
- F04 (MIDI físico) nao e testavel em browser headless - marcado `@manual` nos cenários
- F06 (sons distintos) nao e testavel via DOM em Chrome headless - verificado por classe CSS
- Adiciona ~8 dependências Maven e ~16 ficheiros de teste
- Testes de browser sao intrinsecamente mais lentos que testes unitários (segundos por cenário)

---

## Referências

- Cucumber docs: https://cucumber.io/docs/cucumber/
- Selenide: https://selenide.org/
- ADR-011 - TDD como metodologia de desenvolvimento
- RNF09 - todo o código de produção precedido por teste
