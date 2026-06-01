/* construtor do teclado virtual.
 * expande a logica do wireframes.html para uso em multiplas paginas.
 * eventos de nota emitidos via callbacks registados com onNoteOn/onNoteOff -
 * qualquer modulo pode reagir sem depender directamente deste ficheiro.
 *
 * WK_W = 38px = 36px (largura tecla branca) + 2px (gap entre teclas).
 * nao alterar sem ajustar o CSS correspondente em main.css - sao dependentes. */

const WHITE_SEMITONES = [0, 2, 4, 5, 7, 9, 11]; // C D E F G A B
const NOTE_NAMES      = ['C','C#','D','D#','E','F','F#','G','G#','A','A#','B'];
const HAS_BLACK_RIGHT = { 0:1, 2:1, 5:1, 7:1, 9:1 }; // brancas com preta a direita
const WK_W            = 38; // largura total de tecla branca + gap (deve corresponder ao CSS)

const noteOnListeners  = [];
const noteOffListeners = [];

/**
 * Regista callback chamado com midiNumber quando uma tecla e pressionada.
 * @param {function(number): void} cb
 */
export function onNoteOn(cb)  { noteOnListeners.push(cb); }

/**
 * Regista callback chamado com midiNumber quando uma tecla e solta.
 * @param {function(number): void} cb
 */
export function onNoteOff(cb) { noteOffListeners.push(cb); }

/**
 * Remove todos os listeners de note-on registados.
 * Util quando uma pagina re-inicializa o teclado ou em teardown de testes,
 * para evitar acumulacao de callbacks que disparariam varias vezes por nota.
 */
export function clearNoteOnListeners()  { noteOnListeners.length  = 0; }

/**
 * Remove todos os listeners de note-off registados.
 */
export function clearNoteOffListeners() { noteOffListeners.length = 0; }

function fireNoteOn(midi)  { noteOnListeners.forEach(cb => cb(midi)); }
function fireNoteOff(midi) { noteOffListeners.forEach(cb => cb(midi)); }

/**
 * Constrói o teclado no elemento com o id indicado.
 * A nota final e sempre o C da oitava acima de endOctave.
 * @param {string} containerId id do elemento .keyboard no DOM
 * @param {number} startOctave oitava inicial (ex: 3 para C3)
 * @param {number} endOctave oitava final (ex: 4 para C5 como ultima nota)
 */
export function buildKeyboard(containerId, startOctave, endOctave) {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = '';

    let whiteIdx = 0; // indice de tecla branca para calcular posicao das pretas

    for (let oct = startOctave; oct <= endOctave; oct++) {
        for (const semitone of WHITE_SEMITONES) {
            const midi = (oct + 1) * 12 + semitone;
            const name = NOTE_NAMES[semitone] + oct;
            el.appendChild(makeWhiteKey(midi, name));

            if (HAS_BLACK_RIGHT[semitone]) {
                const bkMidi = midi + 1;
                const bkName = NOTE_NAMES[semitone + 1] + oct;
                el.appendChild(makeBlackKey(bkMidi, bkName, whiteIdx));
            }
            whiteIdx++;
        }
    }

    // ultima nota: C da oitava acima de endOctave
    const lastMidi = (endOctave + 2) * 12;
    el.appendChild(makeWhiteKey(lastMidi, 'C' + (endOctave + 1)));
}

function makeWhiteKey(midi, name) {
    const el        = document.createElement('div');
    el.className    = 'wk';
    el.dataset.midi = String(midi);
    el.innerHTML    = `<span class="lbl">${name}</span>`;

    el.addEventListener('mousedown',  () => { el.classList.add('active');    fireNoteOn(midi);  });
    el.addEventListener('mouseup',    () => { el.classList.remove('active'); fireNoteOff(midi); });
    el.addEventListener('mouseleave', () =>   el.classList.remove('active'));
    return el;
}

function makeBlackKey(midi, name, whiteIdx) {
    const el        = document.createElement('div');
    el.className    = 'bk';
    el.dataset.midi = String(midi);
    el.style.left   = (whiteIdx * WK_W + WK_W - 13) + 'px';
    el.innerHTML    = `<span class="lbl">${name}</span>`;

    el.addEventListener('mousedown', e => {
        e.stopPropagation(); // impede que o clique na preta dispare o mousedown da branca por baixo
        el.classList.add('active');
        fireNoteOn(midi);
    });
    el.addEventListener('mouseup',    e => { e.stopPropagation(); el.classList.remove('active'); fireNoteOff(midi); });
    el.addEventListener('mouseleave', ()  => el.classList.remove('active'));
    return el;
}

/**
 * Ilumina as teclas indicadas (ex: ao carregar no botao "Ouvir").
 * @param {number[]} midiNumbers
 */
export function highlightNotes(midiNumbers) {
    midiNumbers.forEach(midi => {
        const el = document.querySelector(`[data-midi="${midi}"]`);
        if (el) el.classList.add('highlighted');
    });
}

/** Remove todos os highlights do teclado. */
export function clearHighlights() {
    document.querySelectorAll('.highlighted')
        .forEach(el => el.classList.remove('highlighted'));
}

/**
 * Devolve o range de oitavas recomendado com base na largura do viewport (ADR-018).
 * Mobile (<768px): 25 teclas C3-C5 | Tablet: 37 teclas C2-C5 | Desktop: 49 teclas C2-C6.
 * @returns {[number, number]} [startOctave, endOctave]
 */
export function getKeyboardRange() {
    if (window.innerWidth < 768)  return [3, 4];
    if (window.innerWidth < 1024) return [2, 4];
    return [2, 5];
}
