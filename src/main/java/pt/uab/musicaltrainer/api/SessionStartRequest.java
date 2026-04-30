package pt.uab.musicaltrainer.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Pedido de início de sessão de treino.
 */
public record SessionStartRequest(
    @Schema(description = "Tipo de exercício da sessão (informativo — não altera o comportamento actual).",
            example = "INTERVAL")
    String exerciseType,

    @Schema(description = "Nível de dificuldade inicial (informativo — a dificuldade é adaptada automaticamente).",
            example = "3")
    int difficulty
) {}
