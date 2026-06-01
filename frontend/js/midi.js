/* integracao com Web MIDI API para controladores fisicos (ADR-005, F04).
 * degradacao graciosa se o browser nao suportar (Firefox, Safari) ou o utilizador
 * recusar a permissao - a aplicacao funciona normalmente so com o teclado virtual.
 * sem aviso agressivo: o LED fica cinzento e pronto. */

/**
 * Inicializa a integracao MIDI e liga os listeners.
 * @param {function(number): void} onNoteOn chamado com midiNumber em cada note-on
 * @param {function(string): void} onLedChange chamado com 'off' | 'connected' | 'active'
 */
export async function initMidi(onNoteOn, onLedChange) {
    if (!navigator.requestMIDIAccess) {
        // situacao normal em Firefox e Safari - sem erro visivel
        onLedChange('off');
        return;
    }

    let access;
    try {
        access = await navigator.requestMIDIAccess();
    } catch {
        // utilizador recusou a permissao MIDI
        onLedChange('off');
        return;
    }

    function attachInputs() {
        access.inputs.forEach(input => {
            input.onmidimessage = e => handleMessage(e, onNoteOn, onLedChange);
        });
        onLedChange(access.inputs.size > 0 ? 'connected' : 'off');
    }

    attachInputs();
    // reage a ligacao e desligacao de dispositivos em tempo real
    access.onstatechange = () => attachInputs();
}

function handleMessage(event, onNoteOn, onLedChange) {
    const [status, note, velocity] = event.data;
    // note-on = byte de status 0x9x com velocity > 0
    // velocity=0 num evento 0x9x e tratado como note-off por alguns controladores
    const isNoteOn = (status & 0xf0) === 0x90 && velocity > 0;
    if (!isNoteOn) return;

    onNoteOn(note);

    // flash do LED por 100ms, depois regressa ao estado 'connected'
    onLedChange('active');
    setTimeout(() => onLedChange('connected'), 100);
}
