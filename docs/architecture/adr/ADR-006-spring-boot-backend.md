# ADR-006 - Java 21 + Spring Boot para o backend

**Data:** 2026-03-25  
**Estado:** Aceite  
**Decisores:** Daniel Junior

---

## Contexto

O backend necessita de expor uma API REST, gerir persistência em base de dados relacional, e conter a lógica de negócio do domínio musical. A escolha de stack do backend é o ponto onde tenho alguma experiência.

---

## Decisão

O backend é implementado em **Java 21** com **Spring Boot** (versão estável mais recente). Dependências: Spring (persistência via DAOs - ver ADR-010), H2 (base de dados em desenvolvimento). Package raiz: `pt.uab.musicaltrainer`.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição                                                                                                       |
|------------|-------------------------------------------------------------------------------------------------------------------------|
| Node.js + Express | Pouca experiência em Node.js. Introduziria curva de aprendizagem onde já há domínio em Java.                            |
| Python + FastAPI | Idem - familiar para scripting mas sem experiência em backends Python.                                                  |
| Quarkus (Java nativo) | Curva de configuração mais elevada que Spring Boot; menor ecossistema; familiaridade com Spring.                        |
| Jakarta EE puro (sem Spring) | Mais verboso, menor produtividade, sem vantagem imediata sobre Spring Boot. (excepto por estar em um ambiente full Java |

---

## Consequências

**Positivas:**
- Qualidade do código backend e velocidade no desenvolvimento
- Spring Boot minimiza configuração (auto-configuration, embedded Tomcat)
- Spring JDBC + H2 permite arrancar sem instalar base de dados; schema criado via `schema.sql`
- Ecossistema extenso - qualquer decisão tem documentação e exemplos disponíveis

**Negativas / trade-offs:**
- JVM startup time (~2-3s) - irrelevante para o âmbito de uma utilização em caráter académico.
  - Uma das formas de mitigação seria recorrer ao uso de `native-image`s (https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-native-image.html)
- Maior consumo de memória do que alternativas como Quarkus native ou Node.js - irrelevante para ambiente de desenvolvimento e demo
- Anotações mágicas do Spring. Apesar de intentar utilizar o Spring somente para fins de *Dependency Injection*. 
