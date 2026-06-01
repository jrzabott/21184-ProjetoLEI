# Testes para progress.html: dashboard de progresso
# Requisitos cobertos: F07 (dashboard com taxa de acerto por tipo e sessões recentes)
Feature: Dashboard de progresso

  Scenario: Estado vazio sem sessões
    Given que não existem sessões registadas
    When o utilizador abre a página de progresso
    Then o estado vazio está visível
    And a mensagem de estado vazio contém "Ainda sem histórico"
    And o botão Iniciar sessão está visível no estado vazio

  Scenario: Dashboard visível quando há sessões registadas
    Given que existem sessões registadas
    When o utilizador abre a página de progresso
    Then o dashboard de progresso está visível
    And a precisão global está preenchida
    And as barras por tipo estão visíveis

  Scenario: Sessões recentes estão listadas
    Given que existem sessões registadas
    When o utilizador abre a página de progresso
    Then a secção de sessões recentes está visível

  Scenario: Link de início navega para index
    Given que o utilizador está na página de progresso
    When o utilizador clica no link de início
    Then o botão Iniciar sessão da página inicial está visível

  Scenario: Iniciar sessão a partir do estado vazio
    Given que não existem sessões registadas
    And o utilizador abre a página de progresso
    When o utilizador clica em Iniciar sessão no estado vazio
    Then a página de exercício está visível
    And o banner de prática não está visível no exercício

  Scenario: LED MIDI presente e inicializado na página de progresso
    Given que não existem sessões registadas
    When o utilizador abre a página de progresso
    Then o LED MIDI está presente na página de progresso
