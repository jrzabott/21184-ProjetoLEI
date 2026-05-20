/* sintese de audio via Web Audio API (ADR-004).
 * onda sine porque e a mais proxima do timbre de piano simples -
 * square e sawtooth soam mais a sintetizador, menos util para treino auditivo.
 *
 * AudioContext criado em lazy init porque browsers modernos suspendem contextos
 * criados antes de uma interaccao do utilizador (politica autoplay Chrome/Firefox). */

let ctx = null;

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
    const ac   = getCtx();
    const freq = 440 * Math.pow(2, (midiNumber - 69) / 12);
    const osc  = ac.createOscillator();
    const gain = ac.createGain();

    osc.connect(gain);
    gain.connect(ac.destination);
    osc.type = 'sine';
    osc.frequency.setValueAtTime(freq, ac.currentTime);

    // envelope: attack de 10ms + decay exponencial para evitar clicks de audio
    gain.gain.setValueAtTime(0, ac.currentTime);
    gain.gain.linearRampToValueAtTime(0.35, ac.currentTime + 0.01);
    gain.gain.exponentialRampToValueAtTime(0.001, ac.currentTime + durationMs / 1000);

    osc.start(ac.currentTime);
    osc.stop(ac.currentTime + durationMs / 1000);
}

/**
 * Toca uma sequencia de notas em serie, com 300ms entre cada uma.
 * Optei por 300ms porque e o intervalo minimo para distinguir notas consecutivas
 * sem perder o senso de melodia - abaixo soa a acorde, acima a pausa abrupta.
 * @param {number[]} midiNumbers
 */
export function playNotes(midiNumbers) {
    midiNumbers.forEach((midi, i) => setTimeout(() => playNote(midi, 400), i * 300));
}

/**
 * Som de acerto: acorde maior ascendente curto (C5-E5-G5).
 * Consonante e "completo" - associacao clara com resposta correcta.
 */
export function playCorrect() {
    [0, 4, 7].forEach((semitones, i) => {
        setTimeout(() => playNote(72 + semitones, 250), i * 70);
    });
}

/**
 * Som de erro: descida de semitom (C5 -> B4).
 * Dissonante mas suave - inconfundivel com o som de acerto.
 */
export function playIncorrect() {
    playNote(72, 180);
    setTimeout(() => playNote(71, 280), 140);
}
