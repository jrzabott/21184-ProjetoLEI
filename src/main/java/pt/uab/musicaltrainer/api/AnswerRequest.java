package pt.uab.musicaltrainer.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Pedido de avaliação de resposta.
 * O utilizador toca notas num teclado virtual ou controlador MIDI
 * e envia a sequência de números MIDI para avaliação (ADR-014).
 */
public record AnswerRequest(

    @NotNull(message = "é obrigatório")
    Long exerciseId,

    @Schema(description = "ID da sessão activa. Omitir ou usar 0 para sandbox — resposta avaliada sem persistir resultado.",
            example = "1", defaultValue = "0")
    long sessionId,

    @NotNull(message = "é obrigatório")
    int[] notes,

    @Schema(description = "Tempo de resposta em milissegundos.",
            example = "3200")
    long responseTimeMs
) {}
