package pt.uab.musicaltrainer.api;

import java.util.List;

/**
 * Pedido para geração em massa no endpoint de debug.
 * types nulo ou vazio = gerar todos os tipos disponíveis.
 * minDifficulty/maxDifficulty: 0 quando omitidos (normalizado para 1/10 no controller).
 */
public record GenerateAllRequest(
    List<String> types,
    int minDifficulty,
    int maxDifficulty
) {}
