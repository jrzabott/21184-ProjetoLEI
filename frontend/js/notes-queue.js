/* fila FIFO de notas MIDI com tamanho maximo configurável.
 * usada em dois contextos com maxSize diferente:
 *   index.html (sandbox): max=8, exploracão livre
 *   exercise.html: max=exercise.notes.length, derivado do backend apos gerar exercicio
 *
 * nomes com enarmonicos onde aplicavel - ex: "D#4/Eb4" - porque o contexto
 * tonal nao e conhecido no frontend e ambas as designacoes sao validas. */

const NOTE_NAMES = [
    'C', 'C#/Db', 'D', 'D#/Eb', 'E', 'F',
    'F#/Gb', 'G', 'G#/Ab', 'A', 'A#/Bb', 'B',
];

/**
 * Converte numero MIDI para nome legivel com oitava.
 * Formula: oitava = floor(midi/12) - 1, classe = midi % 12.
 * @param {number} midi 0-127
 * @returns {string} ex: "C4", "D#4/Eb4"
 */
export function midiToName(midi) {
    const octave = Math.floor(midi / 12) - 1;
    return NOTE_NAMES[midi % 12] + octave;
}

/**
 * Cria uma fila FIFO com tamanho maximo.
 * Nova nota ao atingir o limite: a mais antiga e descartada automaticamente.
 * @param {number} maxSize
 */
export function createNotesQueue(maxSize) {
    let queue = [];

    return {
        /**
         * Adiciona uma nota. Se a fila estiver cheia, descarta a mais antiga (FIFO).
         * @param {number} midiNumber
         */
        push(midiNumber) {
            if (queue.length >= maxSize) queue.shift();
            queue.push(midiNumber);
        },

        clear()   { queue = []; },

        /** @returns {number[]} copia do array actual */
        toArray() { return [...queue]; },

        size()    { return queue.length; },
        isFull()  { return queue.length >= maxSize; },

        /** @returns {string} notas formatadas, ex: "C4 - E4 - G4" */
        toDisplayString() {
            return queue.map(midiToName).join(' - ');
        },
    };
}
