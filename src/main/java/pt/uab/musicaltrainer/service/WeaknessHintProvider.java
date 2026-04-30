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
        // Intervalos (internalName como chave - sem ambiguidade)
        Map.entry("Unissono",                   "Uníssono é a mesma nota repetida - distância zero semítons."),
        Map.entry("2a Menor",                   "2ª Menor: 1 semítom. O intervalo mais tenso e dissonante - resolve para cima ou baixo."),
        Map.entry("2a Maior",                   "2ª Maior: 2 semítons. Passos de tom inteiro, como Dó→Ré."),
        Map.entry("3a Menor",                   "3ª Menor: 3 semítons. Base dos acordes menores - som mais melancólico."),
        Map.entry("3a Maior",                   "3ª Maior: 4 semítons. Base dos acordes maiores - som mais alegre e estável."),
        Map.entry("4a Perfeita",                "4ª Perfeita: 5 semítons. Intervalo estável, muito usado em melodias e baixos."),
        Map.entry("4a Aumentada / 5a Diminuta", "Trítono: 6 semítons. O intervalo mais instável - chamado \"diabolus in musica\"."),
        Map.entry("5a Perfeita",                "5ª Perfeita: 7 semítons. Intervalo fundamental - a base da harmonia tonal."),
        Map.entry("5a Aumentada / 6a Menor",    "5ª Aug / 6ª Men: 8 semítons. Contexto harmónico define qual é. Som suspenso."),
        Map.entry("6a Maior",                   "6ª Maior: 9 semítons. Intervalo lírico e cantável. Frequente em melodias."),
        Map.entry("6a Aumentada / 7a Menor",    "7ª Menor: 10 semítons. Essencial nos acordes dominantes de sétima."),
        Map.entry("7a Maior",                   "7ª Maior: 11 semítons. Tensão máxima antes da oitava - quer resolver para a tónica."),
        Map.entry("Oitava Perfeita",            "Oitava: 12 semítons. A mesma nota na oitava seguinte - som mais grave ou agudo."),
        // Escalas - prefixo "SCALE:" para evitar conflito com acordes
        // Família maior
        Map.entry("SCALE:MAJOR",                   "Escala Maior: T-T-S-T-T-T-S. Som brilhante e estável. A mais fundamental da música ocidental."),
        Map.entry("SCALE:IONIAN",                  "Escala Jónica (alias de Maior): idêntica à escala maior. T-T-S-T-T-T-S."),
        // Família menor
        Map.entry("SCALE:MINOR_NATURAL",           "Escala Menor Natural: T-S-T-T-S-T-T. Som melancólico - 6º e 7º graus rebaixados em relação à maior."),
        Map.entry("SCALE:AEOLIAN",                 "Escala Eólia (alias de Menor Natural): idêntica à menor natural. Base do som menor moderno."),
        Map.entry("SCALE:HARMONIC_MINOR",          "Escala Menor Harmónica: como a menor natural mas com 7º grau natural. Cria tensão dominante - salto de 3 semítons entre 6º e 7º."),
        Map.entry("SCALE:MELODIC_MINOR",           "Escala Menor Melódica: 6º e 7º graus naturais a subir, rebaixados a descer. Muito usada em jazz."),
        // Modos diatónicos
        Map.entry("SCALE:DORIAN",                  "Modo Dórico: menor com 6º grau natural. T-S-T-T-T-S-T. Som típico de jazz e música celta."),
        Map.entry("SCALE:PHRYGIAN",                "Modo Frígio: menor com 2º grau rebaixado. S-T-T-T-S-T-T. Característico da música espanhola e flamenca."),
        Map.entry("SCALE:LYDIAN",                  "Modo Lídio: maior com 4º grau aumentado. T-T-T-S-T-T-S. Som etéreo e brilhante."),
        Map.entry("SCALE:MIXOLYDIAN",              "Modo Mixolídio: maior com 7º grau rebaixado. T-T-S-T-T-S-T. Muito usado em rock e blues."),
        Map.entry("SCALE:LOCRIAN",                 "Modo Lócrio: o mais dissonante - com 2º e 5º rebaixados. S-T-T-S-T-T-T. Raro em uso prático."),
        // Pentatónicas
        Map.entry("SCALE:PENTATONIC_MAJOR",        "Pentatónica Maior: 5 notas (1-2-3-5-6). Sem meios-tons - som aberto e vocal. Base do folk e pop."),
        Map.entry("SCALE:PENTATONIC_MINOR",        "Pentatónica Menor: 5 notas (1-b3-4-5-b7). Sem meios-tons - base do blues, rock e muita música popular."),
        // Blues e intermédias
        Map.entry("SCALE:BLUES",                   "Escala de Blues: pentatónica menor com nota blues (b5). 6 notas com som caracteristicamente tenso."),
        Map.entry("SCALE:MINOR_BLUES",             "Escala de Blues Menor (alias de Blues): pentatónica menor com nota blue (b5). Mesmo som de blues."),
        Map.entry("SCALE:HARMONIC_MAJOR",          "Escala Maior Harmónica: maior com 6º grau rebaixado. Cria tensão dominante - usada em jazz e música clássica."),
        // Modos alterados e exóticos
        Map.entry("SCALE:PHRYGIAN_DOMINANT",       "Frígio Dominante: modo frígio com 3º grau natural. Som árabe e flamenco muito característico."),
        Map.entry("SCALE:LYDIAN_DOMINANT",         "Lídio Dominante: mixolídio com 4º aumentado. Som jazz moderno - dominante com trítono."),
        Map.entry("SCALE:DORIAN_FLAT_2",           "Dórico b2: dórico com 2º rebaixado. Som oriental e jazz moderno."),
        Map.entry("SCALE:LOCRIAN_NATURAL_2",       "Lócrio Natural 2: lócrio com 2º grau natural. Menos dissonante que o lócrio regular."),
        Map.entry("SCALE:ALTERED",                 "Escala Alterada: 7 notas com múltiplas tensões (#9, b9, #11, b13). Máxima tensão dominante em jazz."),
        Map.entry("SCALE:SUPER_LOCRIAN",           "Super-Lócrio (alias de Alterada): escala dominante com todas as tensões alteradas."),
        // Simétricas
        Map.entry("SCALE:WHOLE_TONE",              "Escala de Tons Inteiros: 6 notas, cada uma a 2 semítons. Som ambíguo sem tónica clara - usada por Debussy."),
        Map.entry("SCALE:CHROMATIC",               "Escala Cromática: todas as 12 notas. Usa todos os semítons - sem tonalidade definida."),
        Map.entry("SCALE:HALF_WHOLE_OCTATONIC",    "Octatónica S-T: alterna semítons e tons. 8 notas simétricas - muito usada em jazz sobre acordes diminutos."),
        Map.entry("SCALE:WHOLE_HALF_OCTATONIC",    "Octatónica T-S: alterna tons e semítons. 8 notas simétricas - variante da octatónica."),
        // Harmónicas exóticas
        Map.entry("SCALE:DOUBLE_HARMONIC_MAJOR",   "Maior Dupla Harmónica: som árabe/cigano com dois saltos de 3 semítons. Muito característica."),
        Map.entry("SCALE:BYZANTINE",               "Escala Bizantina (alias de Maior Dupla Harmónica): mesmo som árabe característico."),
        // Acordes - prefixo "CHORD:" para evitar conflito com escalas
        Map.entry("CHORD:MAJOR",                "Acorde Maior: raiz + 3ª maior + 5ª perfeita. Som estável e brilhante. O mais comum."),
        Map.entry("CHORD:MINOR",                "Acorde Menor: raiz + 3ª menor + 5ª perfeita. Som mais sombrio e introspectivo."),
        Map.entry("CHORD:DIMINISHED",           "Acorde Diminuto: raiz + 3ª menor + 5ª diminuta. Tensão extrema - quer resolver."),
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
            "Continua a praticar " + pattern + " - a repetição é a chave do progresso.");
    }
}
