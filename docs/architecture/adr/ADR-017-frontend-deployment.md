# ADR-017 - Frontend: localização no repositório e integração com Spring Boot

**Data:** 2026-05-01
**Estado:** Aceite
**Decisores:** Daniel Junior

---

## Contexto

O projeto tem frontend (HTML/JS/CSS) e backend (Spring Boot) no mesmo repositório.
É preciso decidir onde os ficheiros frontend ficam no repositório e como o Spring Boot
os serve em tempo de execução.

A questão tem duas dimensões:
- Onde ficam os ficheiros no repo (organização e separação lógica)
- Como o servidor os serve (CORS, configuração, deployment)

A separação em repositórios distintos foi considerada mas rejeitada dado o scope académico
e o requisito CB07 (sem build tools frontend - npm, Webpack, etc).

---

## Decisão

Os ficheiros frontend ficam em `/frontend/` na raiz do repositório.
O Spring Boot serve essa directoria como recursos estáticos via configuração explícita
em `application.properties`:

```properties
spring.web.resources.static-locations=file:frontend/
```

A configuração do URL base do backend é centralizada em `js/api.js` - ficheiro único
que todas as páginas importam. Nenhuma outra página ou script conhece o URL do servidor.

```javascript
// frontend/js/api.js
const BASE = 'http://localhost:8080';

export const generateExercise = (type, difficulty, sessionId) =>
    fetch(`${BASE}/api/exercises/generate`, { ... });
```

Para mudar de servidor em produção: uma linha em `api.js`. Sem alterações nas páginas HTML.

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|---|---|
| `src/main/resources/static/` (convenção Spring Boot) | Acopla frontend ao layout Maven. Dificulta defesa da separação frontend/backend. Para mover a deployment separado seria necessário reorganizar ficheiros, nao apenas configuração. |
| Repositório separado para o frontend | Overhead de CI/CD e sincronização injustificado para MVP académico com um único developer. Viola o princípio de simplicidade para o scope actual. |
| `config.js` partilhado pelas 4 páginas | Cada página precisaria de importar `config.js` antes de qualquer lógica. Encapsular em `api.js` elimina esse acoplamento - as páginas nao conhecem detalhes de configuração. |

---

## Consequências

**Positivas:**
- Frontend e backend separados logicamente no repositório (directoria `/frontend` vs `src/`)
- Para separar deployments: mudar `application.properties` e apontar CDN para `/frontend` - é uma mudança de configuração, nao de arquitectura
- URL do backend em lugar único (`api.js`) - trivial de actualizar
- Sem CORS em desenvolvimento: frontend servido pelo mesmo processo Spring Boot
- Defensável em relatório e em defesa de júri: a separação existe ao nível do código, a unificação do deployment é uma decisao consciente de MVP

**Negativas / trade-offs:**
- Frontend e backend partilham o mesmo processo em runtime - escala horizontal requer decisao de separação futura
- `file:frontend/` funciona ao correr com `mvn spring-boot:run` e ao executar o JAR a partir da raiz do projecto; em deployment tipo WAR ou container precisaria de revisao
- Sem build pipeline frontend: qualquer mudança ao URL do backend é manual (aceite para MVP)

---

## Referências

- CB07 - sem frameworks ou build tools JavaScript
- ADR-008 - JavaScript vanilla
- ADR-009 - estrutura de ecrãs (páginas HTML separadas)
