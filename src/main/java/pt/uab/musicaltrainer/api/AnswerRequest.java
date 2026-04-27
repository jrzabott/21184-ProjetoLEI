package pt.uab.musicaltrainer.api;

/**
 * Pedido de avaliação de resposta.
 * O utilizador toca notas num teclado virtual ou controlador MIDI
 * e envia a sequência de números MIDI para avaliação (ADR-014).
 *
 * exerciseId no corpo — é um detalhe interno sem valor semântico para o utilizador.
 */
public record AnswerRequest(Long exerciseId, Long sessionId, int[] notes, long responseTimeMs) {}
