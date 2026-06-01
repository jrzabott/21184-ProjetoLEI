# Testes para session-end.html: resumo após sessão
Feature: Resumo de sessão

  Scenario: Título correcto em modo prática
    Given que o utilizador concluiu uma sessão de prática
    Then o título do resumo é "Prática terminada"
    And o banner de prática no resumo está visível

  Scenario: Contadores preenchidos após prática com exercícios
    Given que o utilizador concluiu uma sessão de prática com exercícios
    Then os contadores de exercícios estão preenchidos

  Scenario: Barra de precisão tem largura maior que zero após exercícios
    Given que o utilizador concluiu uma sessão de prática com exercícios
    Then a barra de precisão tem largura maior que zero

  Scenario: Título correcto em sessão pontuada
    Given que o utilizador concluiu uma sessão pontuada
    Then o título do resumo é "Sessão terminada"
    And o banner de prática no resumo não está visível

  Scenario: Botão Voltar regressa à página inicial
    Given que a página de resumo está carregada
    When o utilizador clica em Voltar
    Then o botão Iniciar sessão da página inicial está visível

  Scenario: Iniciar nova sessão a partir do resumo
    Given que a página de resumo está carregada
    When o utilizador clica em Iniciar sessão no resumo
    Then a página de exercício está visível
    And o banner de prática não está visível no exercício

  Scenario: Sessão pontuada iniciada pela UI guarda exercícios correctamente
    Given que o utilizador abre a aplicação
    And o utilizador selecciona o tipo "INTERVAL"
    When o utilizador clica em Iniciar sessão
    Then a página de exercício está visível
    And o banner de prática não está visível no exercício
    When o utilizador toca as notas correctas do exercício
    And o utilizador clica em Enviar resposta
    And o utilizador clica em Terminar no exercício
    Then o título do resumo é "Sessão terminada"
    And os totais da sessão mostram pelo menos um exercício
