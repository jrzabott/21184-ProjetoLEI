package pt.uab.musicaltrainer.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Pedido de avaliação de resposta.
 * O utilizador toca notas num teclado virtual ou controlador MIDI
 * e envia a sequência de números MIDI para avaliação (ADR-014).
 *
 * exerciseId no corpo — é um detalhe interno sem valor semântico para o utilizador.
 */
public record AnswerRequest(
    Long exerciseId,

    @Schema(description = "ID da sessão activa. Omitir ou usar 0 para sandbox — resposta avaliada sem persistir resultado.",
            example = "1",
            defaultValue = "0")
    long sessionId,

    int[] notes,
    long responseTimeMs
) {}
