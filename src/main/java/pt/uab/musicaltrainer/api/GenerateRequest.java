package pt.uab.musicaltrainer.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Pedido de geração de exercício.
 * sessionId opcional - quando fornecido, evita repetir o último exercício da sessão.
 */
public record GenerateRequest(
    String type,
    int difficulty,
    @Schema(description = "ID da sessão activa. Se fornecido, evita repetir o último exercício. Omitir para sandbox.",
            example = "1")
    Long sessionId
) {
    public GenerateRequest {
        if (difficulty < 1 || difficulty > 10) {
            throw new IllegalArgumentException("difficulty deve ser entre 1 e 10");
        }
    }
}
