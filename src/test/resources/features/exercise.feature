# Testes para exercise.html: geração, recolha de notas, avaliação
# Requisitos cobertos: F03 (teclado virtual), F05 (feedback imediato), F09 (dificuldade adaptativa)
Feature: Ecrã de exercício activo

  Background:
    Given que o utilizador configurou o modo prática com tipo "INTERVAL"
    And está na página de exercício

  Scenario: Exercício carrega com descrição e botão Ouvir
    Then um exercício está visível com descrição não vazia
    And o botão Ouvir está disponível
    And o painel de notas mostra o traço inicial

  Scenario: Banner de prática está visível em modo prática
    Then o banner de prática está visível no ecrã de exercício

  Scenario: Header mostra label Prática em modo prática
    Then o header de exercício mostra Prática

  Scenario: Clicar tecla actualiza painel de notas
    When o utilizador clica na tecla MIDI 60 no exercício
    Then o painel de notas do exercício contém "C4"

  Scenario: Botão Limpar reseta painel de notas
    Given o utilizador clicou na tecla MIDI 60 no exercício
    When o utilizador clica em Limpar
    Then o painel de notas mostra o traço inicial

  Scenario: Enviar resposta mostra painel de feedback
    When o utilizador clica em Enviar resposta
    Then o painel de feedback está visível
    And o painel tem classe correct ou incorrect

  Scenario: Enviar notas correctas produz feedback correct
    When o utilizador toca as notas correctas do exercício
    And o utilizador clica em Enviar resposta
    Then o painel de feedback tem classe correct

  Scenario: Próximo exercício limpa feedback e painel de notas
    Given o utilizador enviou uma resposta qualquer
    When o utilizador clica em Próximo exercício
    Then o painel de feedback não está visível
    And o painel de notas mostra o traço inicial

  Scenario: Terminar navega para o resumo de sessão
    When o utilizador clica em Terminar no exercício
    Then a página de resumo está visível
