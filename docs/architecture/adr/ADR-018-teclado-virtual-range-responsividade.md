# ADR-018 - Teclado virtual: range MIDI e design responsivo

**Data:** 2026-05-01
**Estado:** Aceite
**Decisores:** Daniel Junior

---

## Contexto

O teclado virtual é o componente central da interface. Duas questões interdependentes:

**Range de teclas:** Um piano completo tem 88 teclas (A0-C8). Exibir 88 teclas
num écran mobile é inviável. É preciso determinar o range mínimo que cobre todos
os exercicios gerados sem forçar scroll horizontal constante.

**Responsividade:** A largura disponível varia muito entre telemóvel (360px), tablet
(768px) e desktop (1280px+). O número de teclas visíveis e o tamanho de cada tecla
têm de adaptar-se sem comprometer a usabilidade.

A questão foi investigada com base em:
1. Leitura directa dos geradores Java (IntervalExerciseGenerator, ScaleExerciseGenerator,
   ChordExerciseGenerator) para determinar o range MIDI real produzido por dificuldade
2. Análise das regras de validação em ExerciseService (ADR-014): intervalos requerem
   match MIDI exacto; escalas e acordes são independentes de oitava
3. Pesquisa de boas práticas: Bootstrap 5, Tailwind CSS, W3C WCAG 2.5.8, Apple HIG,
   Google Material Design 3, e plataformas de piano web (virtualpiano.net, onlinepianist.com)

---

## Análise dos geradores

| Tipo | Range raiz (BEGINNER/ELEMENTARY) | Nota mais alta possivel | Validação |
|---|---|---|---|
| INTERVAL | C3-B4 (MIDI 48-71) | B5 (83) - raiz + oitava | Exacta (MIDI exacto) |
| SCALE | C3-B4 brancas (48-71) | B5 (83) - raiz + 12 semitoms | Independente de oitava |
| CHORD | C2-B4 (36-71) | ~B5 (83) | Independente de oitava |

Para ADVANCED/EXPERT: raiz até C6 (84), notas até B6+ (95+).

**Conclusão crítica:** Escalas e acordes aceitam qualquer oitava - o teclado pode ter
qualquer range. Intervalos exigem as notas exactas geradas. O teclado tem de cobrir
pelo menos C2-B5 (36-83) para que exercícios BEGINNER/ELEMENTARY de intervalos sejam
alcançáveis sem scroll.

---

## Decisão

**Implementação CSS pura** (sem imagem mapeada): teclas brancas em flexbox, teclas pretas
em position:absolute. Escala com o container. Hover e press states via classes CSS.
Labels de nota em cada tecla (C3, D#4, etc).

**Range e breakpoints:**

| Écran | Breakpoint | Teclas | Range | Largura tecla |
|---|---|---|---|---|
| Mobile | < 768px | 25 (C3-C5) | MIDI 48-72 | 28px |
| Tablet | 768-1024px | 37 (C2-C5) | MIDI 36-72 | 26px |
| Desktop | > 1024px | 49 (C2-C6) | MIDI 36-84 | 32px |

Breakpoints alinhados com Bootstrap 5 (md=768px, lg=992px) e Tailwind CSS (md=768px, lg=1024px)
- os standards mais adoptados da industria em 2025/26.

Em mobile, notas de intervalos geradas acima de C5 (MIDI 72) são indicadas visualmente
com seta de scroll - o teclado faz scroll horizontal para mostrar a tecla em causa.

**Indicador MIDI físico:** circulo LED CSS no canto superior de cada página.
3 estados: inactivo (cinzento), ligado (verde + box-shadow), activo/nota-recebida
(flash branco 100ms via keyframe animation).

---

## Alternativas consideradas

| Alternativa | Razão de rejeição |
|---|---|
| 88 teclas em todos os écrans | Inviável: 88 * 28px = 2464px, quase 7x a largura de um telemóvel |
| Imagem PNG/SVG com image map | Dificulta highlight dinâmico de teclas, feedback visual de acerto/erro, e resize. CSS permite adicionar classes sem coordenadas |
| Teclado fixo de 25 teclas em todos os écrans | Intervalos BEGINNER podem gerar notas até B5 (83) que ficam fora de C3-C5 (72). Causaria falhas de validação em mobile sem aviso |
| Scroll horizontal sempre | As principais plataformas de piano web (virtualpiano.net) usam scroll como fallback, nao como experiência principal. Para mobile, 25 teclas sem scroll é a experiência preferida segundo análise de plataformas similares |

---

## Consequências

**Positivas:**
- Desktop cobre todos os exercícios BEGINNER/ELEMENTARY sem scroll (C2-C6 = MIDI 36-84, B5=83 dentro)
- CSS puro: highlight, press state, e animações controlados por classes - sem coordenadas manuais
- Responsivo por CSS media queries - zero JavaScript para adaptar o teclado
- Labels em todas as teclas reduzem curva de aprendizagem para utilizadores sem treino formal
- Indicador MIDI informa o utilizador sem texto adicional nos ecrãs de exercício

**Negativas / trade-offs:**
- Mobile (C3-C5) pode exigir scroll horizontal para intervalos avançados (ADVANCED/EXPERT)
  com raiz próxima de B4 - aceite porque esses niveis não são o foco do MVP
- 49 teclas em desktop: a quantidade de CSS para posicionar correctamente as teclas pretas
  é verbosa mas é código estático e testável visualmente
- Dimensões de tecla (28px em mobile) ficam abaixo do recomendado W3C (44px) e Apple HIG (44pt)
  mas são consistentes com plataformas de piano web especializadas e com a natureza de instrumento
  que o utilizador conhece previamente

---

## Referências

- ADR-014 - protocolo de resposta baseado em notas MIDI (validação exacta vs por padrão)
- ADR-005 - Web MIDI API
- Bootstrap 5 Breakpoints: https://getbootstrap.com/docs/5.3/layout/breakpoints/
- Tailwind CSS Responsive: https://tailwindcss.com/docs/responsive-design
- W3C WCAG 2.5.8 Target Size: https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum.html
- Apple HIG: https://developer.apple.com/design/human-interface-guidelines/
- Google Material Design 3: https://m3.material.io/foundations/layout/understanding-layout/overview
