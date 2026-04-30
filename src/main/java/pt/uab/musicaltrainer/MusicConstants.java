package pt.uab.musicaltrainer;

/**
 * Constantes partilhadas por toda a aplicação.
 * Centraliza valores sentinel e contratos de API.
 */
public final class MusicConstants {
    private MusicConstants() {}

    /**
     * Valor sentinel para sessionId quando o exercício é realizado em modo sandbox.
     * Um sessionId omitido no JSON é desserializado como 0L (tipo primitivo long).
     * Quando sessionId == SESSION_NONE, a resposta é avaliada mas não persistida.
     */
    public static final long SESSION_NONE = 0L;

    // Intervalos MIDI para geração de exercícios por banda de dificuldade
    /** Nota mais baixa para exercícios fáceis (C3). */
    public static final int MIDI_EASY_LOW  = 48;
    /** Nota mais alta para exercícios fáceis (C5). */
    public static final int MIDI_EASY_HIGH = 72;
    /** Nota mais baixa para exercícios médios (C2). */
    public static final int MIDI_MEDIUM_LOW  = 36;
    /** Nota mais alta para exercícios médios (C6). */
    public static final int MIDI_MEDIUM_HIGH = 84;
}
