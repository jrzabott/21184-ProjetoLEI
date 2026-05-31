/* wrapper fino sobre sessionStorage.
 * as páginas usam estas funções - nenhuma conhece as chaves directamente.
 * sessionStorage sobrevive a refresh mas nao a fecho de tab - comportamento
 * intencional: o modo pratica é efémero, e a deteccao de sessoes orfas
 * funciona enquanto o tab estiver aberto. */

const K = {
    mode:          'mt_mode',
    type:          'mt_type',
    sessionId:     'mt_session_id',
    exercise:      'mt_exercise',
    difficulty:    'mt_difficulty',
    counters:      'mt_counters',
    sessionTs:     'mt_session_ts',
    sessionResult: 'mt_session_result',
    timbre:        'mt_timbre',
};

const get = k => { try { return JSON.parse(sessionStorage.getItem(k)); } catch { return null; } };
const set = (k, v) => sessionStorage.setItem(k, JSON.stringify(v));
const del = k => sessionStorage.removeItem(k);

export const getMode       = () => get(K.mode)      ?? 'practice';
export const setMode       = v  => set(K.mode, v);

export const getType       = () => get(K.type)      ?? 'INTERVAL';
export const setType       = v  => set(K.type, v);

export const getSessionId  = () => get(K.sessionId) ?? 0;
export const setSessionId  = v  => set(K.sessionId, v);

export const getExercise   = () => get(K.exercise);
export const setExercise   = v  => set(K.exercise, v);

export const getDifficulty = () => get(K.difficulty) ?? 1;
export const setDifficulty = v  => set(K.difficulty, v);

export const getCounters   = () => get(K.counters)  ?? { correct: 0, incorrect: 0, total: 0 };
export const setCounters   = v  => set(K.counters, v);

export const getSessionTs  = () => get(K.sessionTs);
export const setSessionTs  = v  => set(K.sessionTs, v);

export const getSessionResult = () => get(K.sessionResult);
export const setSessionResult = v  => set(K.sessionResult, v);

/** Limpa tudo excepto o tipo seleccionado (mantido entre sessoes para conveniencia). */
export function clearSession() {
    [K.mode, K.sessionId, K.exercise, K.counters, K.sessionTs, K.sessionResult]
        .forEach(del);
}

/** Limpa apenas o exercicio activo - mantém sessao, tipo e dificuldade. */
export function clearExercise() { del(K.exercise); }

/** True se ha uma sessao pontuada activa em memoria. */
export function hasActiveSession() {
    return getMode() === 'session' && getSessionId() > 0;
}

export const getTimbre     = () => get(K.timbre)     ?? 'sine';
export const setTimbre     = v  => set(K.timbre, v);

/**
 * Incrementa os contadores locais de correcto/incorrecto.
 * Usado tanto em modo pratica (unico registo) como em sessao pontuada (espelho local).
 * @param {boolean} correct
 */
export function incrementCounters(correct) {
    const c = getCounters();
    c.total++;
    if (correct) c.correct++; else c.incorrect++;
    setCounters(c);
}
