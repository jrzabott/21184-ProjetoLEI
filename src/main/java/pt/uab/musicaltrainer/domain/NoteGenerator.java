package pt.uab.musicaltrainer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Comportamento comum para gerar notas a partir de uma nota raiz e padrão de intervalos.
 * <p>
 * Scale e Chord compartilham a mesma lógica de geração: aplicar intervalos (em semítons)
 * a uma nota raiz para obter uma sequência de notas. Esta interface extrai esse comportamento
 * duplicado, eliminando DRY violation.
 * <p>
 * Implementadores (Scale, Chord) usam a nota gerada para construir seus value objects.
 *
 * @author Daniel Junior
 */
public interface NoteGenerator {

    /**
     * Gera uma lista de notas aplicando um padrão de intervalos a uma nota raiz.
     * <p>
     * Exemplo: root=C4 (60), intervals=[0, 4, 7] produz [C4, E4, G4].
     *
     * @param root nota raiz
     * @param intervals distâncias em semítons a partir da raiz (primeiro sempre 0)
     * @return lista imutável de notas geradas
     */
    default List<Note> generateNotes(Note root, int[] intervals) {
        List<Note> result = new ArrayList<>();
        int rootMidi = root.getMidiNumber();

        for (int interval : intervals) {
            int noteMidi = rootMidi + interval;
            result.add(Note.fromMidi(noteMidi));
        }

        return result;
    }
}
