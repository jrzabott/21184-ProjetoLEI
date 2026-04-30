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
}
