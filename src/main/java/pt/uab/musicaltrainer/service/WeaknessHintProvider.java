package pt.uab.musicaltrainer.service;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fornece dicas pedagógicas para padrões de exercício problemáticos.
 * Chave: "EXERCISETYPE:pattern" para SCALE e CHORD (evitar ambiguidade com "MAJOR"),
 * ou "pattern" para INTERVAL (internalName único).
 */
@Component
public class WeaknessHintProvider {

    private static final Map<String, String> HINTS = Map.ofEntries(
        // Intervalos (internalName como chave — sem ambiguidade)
        Map.entry("Unissono",                   "Uníssono é a mesma nota repetida — distância zero semítons."),
        Map.entry("2a Menor",                   "2ª Menor: 1 semítom. O intervalo mais tenso e dissonante — resolve para cima ou baixo."),
        Map.entry("2a Maior",                   "2ª Maior: 2 semítons. Passos de tom inteiro, como Dó→Ré."),
        Map.entry("3a Menor",                   "3ª Menor: 3 semítons. Base dos acordes menores — som mais melancólico."),
        Map.entry("3a Maior",                   "3ª Maior: 4 semítons. Base dos acordes maiores — som mais alegre e estável."),
        Map.entry("4a Perfeita",                "4ª Perfeita: 5 semítons. Intervalo estável, muito usado em melodias e baixos."),
        Map.entry("4a Aumentada / 5a Diminuta", "Trítono: 6 semítons. O intervalo mais instável — chamado \"diabolus in musica\"."),
        Map.entry("5a Perfeita",                "5ª Perfeita: 7 semítons. Intervalo fundamental — a base da harmonia tonal."),
        Map.entry("5a Aumentada / 6a Menor",    "5ª Aug / 6ª Men: 8 semítons. Contexto harmónico define qual é. Som suspenso."),
        Map.entry("6a Maior",                   "6ª Maior: 9 semítons. Intervalo lírico e cantável. Frequente em melodias."),
        Map.entry("6a Aumentada / 7a Menor",    "7ª Menor: 10 semítons. Essencial nos acordes dominantes de sétima."),
        Map.entry("7a Maior",                   "7ª Maior: 11 semítons. Tensão máxima antes da oitava — quer resolver para a tónica."),
        Map.entry("Oitava Perfeita",            "Oitava: 12 semítons. A mesma nota na oitava seguinte — som mais grave ou agudo."),
        // Escalas — prefixo "SCALE:" para evitar conflito com acordes
        Map.entry("SCALE:MAJOR",                "Escala Maior: fórmula T-T-S-T-T-T-S. Som brilhante e estável. A mais importante para começar."),
        Map.entry("SCALE:MINOR_NATURAL",        "Escala Menor Natural: fórmula T-S-T-T-S-T-T. Som mais melancólico — base do som menor."),
        Map.entry("SCALE:HARMONIC_MINOR",       "Escala Menor Harmónica: como a menor natural mas com 7º grau elevado. Cria tensão dominante característica."),
        // Acordes — prefixo "CHORD:" para evitar conflito com escalas
        Map.entry("CHORD:MAJOR",                "Acorde Maior: raiz + 3ª maior + 5ª perfeita. Som estável e brilhante. O mais comum."),
        Map.entry("CHORD:MINOR",                "Acorde Menor: raiz + 3ª menor + 5ª perfeita. Som mais sombrio e introspectivo."),
        Map.entry("CHORD:DIMINISHED",           "Acorde Diminuto: raiz + 3ª menor + 5ª diminuta. Tensão extrema — quer resolver."),
        Map.entry("CHORD:AUGMENTED",            "Acorde Aumentado: raiz + 3ª maior + 5ª aumentada. Som suspenso e ambíguo.")
    );

    /**
     * Devolve a dica para um padrão de exercício.
     * Para INTERVAL: usar internalName (ex: "5a Perfeita").
     * Para SCALE/CHORD: usar nome do tipo (ex: "MAJOR", "DIMINISHED").
     */
    public String getHint(String exerciseType, String pattern) {
        // Tentar chave composta primeiro (SCALE:MAJOR, CHORD:MAJOR)
        String compositeKey = exerciseType + ":" + pattern;
        if (HINTS.containsKey(compositeKey)) return HINTS.get(compositeKey);
        // Fallback para chave simples (intervalos)
        return HINTS.getOrDefault(pattern,
            "Continua a praticar " + pattern + " — a repetição é a chave do progresso.");
    }
}
