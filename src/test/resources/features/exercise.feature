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
    Given o utilizador clicou na tecla MIDI 60 no exercício
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

  Scenario: Botão Enviar desativado quando não há notas tocadas
    Then o botão Enviar resposta está desativado
    When o utilizador clica na tecla MIDI 60 no exercício
    Then o botão Enviar resposta está activo
    When o utilizador clica em Limpar
    Then o botão Enviar resposta está desativado

  Scenario: Ouvir destaca apenas a nota raiz e nao todas as notas
    When o utilizador clica no botão Ouvir
    Then apenas uma tecla está destacada no teclado de exercício

  Scenario: Feedback de resposta incorrecta mostra nomes de nota nao numeros MIDI
    When o utilizador clica na tecla MIDI 60 no exercício
    And o utilizador clica em Enviar resposta
    Then o painel de feedback nao contém numeros MIDI em bruto

  Scenario: Intervalo aceita resposta com apenas a nota alvo
    When o utilizador toca apenas a nota alvo do intervalo
    And o utilizador clica em Enviar resposta
    Then o painel de feedback está visível
    And o painel tem classe correct ou incorrect

  Scenario: Selector de timbre existe abaixo do teclado
    Then deve existir um selector de timbre com as opcoes sine triangle sawtooth e piano

  Scenario: Botao Ouvir permanece activo apos reproducao da sequencia
    When o utilizador clica no botão Ouvir
    Then o botão Ouvir deve estar disponivel apos a reproducao

  Scenario: Intervalo com 2 notas nao activa prepend
    When o utilizador toca as notas correctas do exercício
    And o utilizador clica em Enviar resposta
    Then o painel de feedback tem classe correct

  Scenario: Modo escala nao faz prepend ao tocar so a nota alvo
    Given que o utilizador configurou o modo prática com tipo "SCALE"
    And está na página de exercício
    When o utilizador clica na tecla MIDI que nao e raiz
    And o utilizador clica em Enviar resposta
    Then o painel de feedback tem classe incorrect

  Scenario: Timbre sine esta seleccionado por defeito
    Then o radio button sine deve estar seleccionado

  Scenario: Notas tocadas em rapida sucessao registam todas no painel
    Given que o utilizador configurou o modo prática com tipo "CHORD"
    And está na página de exercício
    When o utilizador toca 3 notas em rapida sucessao
    Then o painel de notas do exercício contém 3 notas registadas

  Scenario: Submissao sem notas conta como incorreta sem erro de API
    When o utilizador clica em Enviar resposta
    Then o painel de feedback tem classe incorrect
    And o painel de feedback nao mostra erro de API
