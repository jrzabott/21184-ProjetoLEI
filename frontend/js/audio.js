/* sintese de audio via Web Audio API (ADR-003, ADR-023).
 * 4 presets de timbre: sine (referencia), triangle (mais quente),
 * sawtooth (brilhante), piano (sawtooth + decay rapido).
 *
 * AudioContext criado em lazy init porque browsers modernos suspendem contextos
 * criados antes de uma interaccao do utilizador (politica autoplay Chrome/Firefox). */

let ctx = null;
let currentTimbre = 'sine';

export function setCurrentTimbre(timbre) { currentTimbre = timbre; }

const PRESETS = {
    sine:     { type: 'sine',     attack: 0.005, sustain: 0.25, release: 0.4  },
    triangle: { type: 'triangle', attack: 0.005, sustain: 0.25, release: 0.4  },
    sawtooth: { type: 'sawtooth', attack: 0.005, sustain: 0.18, release: 0.3  },
    piano:    { type: 'sawtooth', attack: 0.001, sustain: 0.05, release: 0.8  },
};

function getCtx() {
    if (!ctx) ctx = new (window.AudioContext || window.webkitAudioContext)();
    // resume se o browser suspendeu o contexto apos inactividade
    if (ctx.state === 'suspended') ctx.resume();
    return ctx;
}

/**
 * Toca uma nota MIDI durante durationMs milissegundos.
 * @param {number} midiNumber 0-127
 * @param {number} [durationMs=500]
 */
export function playNote(midiNumber, durationMs = 500) {
    const ac     = getCtx();
    const t      = ac.currentTime;
    const dur    = durationMs / 1000;
    const freq   = 440 * Math.pow(2, (midiNumber - 69) / 12);
    const preset = PRESETS[currentTimbre] ?? PRESETS.sine;
    const osc    = ac.createOscillator();
    const gain   = ac.createGain();
    osc.connect(gain);
    gain.connect(ac.destination);
    osc.type = preset.type;
    osc.frequency.setValueAtTime(freq, t);
    const peak = preset.type === 'sawtooth' ? 0.22 : 0.35;
    gain.gain.setValueAtTime(0, t);
    gain.gain.linearRampToValueAtTime(peak, t + preset.attack);
    if (currentTimbre === 'piano') {
        gain.gain.exponentialRampToValueAtTime(0.001, t + dur * 0.6);
    } else {
        gain.gain.setValueAtTime(preset.sustain * peak, t + dur - preset.release);
        gain.gain.exponentialRampToValueAtTime(0.001, t + dur);
    }
    osc.start(t);
    osc.stop(t + dur + 0.05);
}

/**
 * Toca uma sequencia de notas em serie, com 300ms entre cada uma.
 * @param {number[]} midiNumbers
 */
export function playNotes(midiNumbers) {
    midiNumbers.forEach((midi, i) => setTimeout(() => playNote(midi, 400), i * 300));
}

/**
 * Som de acerto: acorde maior ascendente curto (C5-E5-G5).
 */
export function playCorrect() {
    [0, 4, 7].forEach((st, i) => setTimeout(() => playNote(72 + st, 250), i * 70));
}

/**
 * Som de erro: descida de semitom (C5 -> B4).
 */
export function playIncorrect() {
    playNote(72, 180);
    setTimeout(() => playNote(71, 280), 140);
}
