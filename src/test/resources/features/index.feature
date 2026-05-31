# Testes para index.html: landing page e modo sandbox
# Requisitos cobertos: F08 (modo sandbox - nome de nota e intervalo em tempo real)
Feature: Página principal e modo sandbox

  Scenario: Página carrega com elementos essenciais
    Given que o utilizador abre a aplicação
    Then o título da página é "Musical Trainer"
    And o botão Praticar está visível
    And o botão Iniciar sessão está visível
    And o teclado sandbox está visível

  Scenario: Tipo padrão é Intervalos
    Given que o utilizador abre a aplicação
    Then o tipo "INTERVAL" está activo

  Scenario: Utilizador selecciona tipo Escalas
    Given que o utilizador abre a aplicação
    When o utilizador selecciona o tipo "SCALE"
    Then o tipo "SCALE" está activo
    And o tipo "INTERVAL" não está activo

  Scenario: Utilizador selecciona tipo Acordes
    Given que o utilizador abre a aplicação
    When o utilizador selecciona o tipo "CHORD"
    Then o tipo "CHORD" está activo

  Scenario: Clicar tecla mostra nome da nota no painel
    Given que o utilizador abre a aplicação
    When o utilizador clica na tecla MIDI 60
    Then o painel de notas sandbox contém "C4"

  Scenario: Clicar duas teclas mostra intervalo no painel
    Given que o utilizador abre a aplicação
    When o utilizador clica na tecla MIDI 60
    And o utilizador clica na tecla MIDI 67
    Then o painel de intervalo sandbox não está vazio

  Scenario: Modal de ajuda abre ao clicar no botão de ajuda
    Given que o utilizador abre a aplicação
    When o utilizador clica no botão de ajuda
    Then o modal de ajuda está visível

  Scenario: Modal de ajuda fecha ao clicar Fechar
    Given que o utilizador abre a aplicação
    And o utilizador clicou no botão de ajuda
    When o utilizador fecha o modal de ajuda
    Then o modal de ajuda não está visível

  Scenario: Praticar navega para exercício em modo prática
    Given que o utilizador abre a aplicação
    And o utilizador selecciona o tipo "INTERVAL"
    When o utilizador clica em Praticar
    Then a página de exercício está visível
    And o banner de prática está visível no exercício

  Scenario: Iniciar sessão navega para exercício sem banner de prática
    Given que o utilizador abre a aplicação
    When o utilizador clica em Iniciar sessão
    Then a página de exercício está visível
    And o banner de prática não está visível no exercício

  Scenario: Banner de sessão órfã aparece quando há sessão activa
    Given que existe uma sessão activa no storage
    Then o banner de sessão em curso está visível

  Scenario: Terminar sessão órfã limpa o estado e oculta banner
    Given que existe uma sessão activa no storage
    When o utilizador termina a sessão em curso
    Then o banner de sessão em curso não está visível

  Scenario: Mudar tipo de exercício limpa o painel de notas sandbox
    Given que o utilizador abre a aplicação
    When o utilizador clica na tecla MIDI 60
    And o utilizador selecciona o tipo "SCALE"
    Then o painel de notas sandbox contém "Toca uma tecla"

  Scenario: Escolha de timbre persiste ao navegar para exercicio
    Given que o utilizador abre a aplicação
    When o utilizador selecciona o timbre "piano"
    And o utilizador clica em Praticar
    Then o timbre "piano" deve estar seleccionado na pagina de exercicio

  Scenario: Selector de timbre existe na pagina inicial
    Given que o utilizador abre a aplicação
    Then deve existir um selector de timbre na pagina inicial com as opcoes sine triangle sawtooth e piano
