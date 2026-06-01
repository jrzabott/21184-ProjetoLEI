/* módulo central de comunicação com o backend.
 * todas as páginas importam daqui - nenhuma usa fetch directamente.
 * mudar BASE aqui reflecte-se em toda a aplicação (ADR-017). */

/* vazio = mesma origem que serviu a pagina (ADR-017).
 * o frontend e sempre servido pelo Spring Boot, entao URLs relativas funcionam
 * em qualquer porta - dev (8080), testes (porta aleatoria), producao. */
const BASE = '';

/**
 * Faz um pedido HTTP e devolve o JSON de resposta.
 * Lança Error com mensagem legível em caso de falha - as páginas
 * nao lidam com códigos HTTP directamente.
 * @param {string} url path relativo ao BASE
 * @param {RequestInit} [opts]
 */
async function request(url, opts = {}) {
    const res = await fetch(BASE + url, {
        headers: { 'Content-Type': 'application/json' },
        ...opts,
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.detail ?? `Erro ${res.status} em ${url}`);
    }
    return res.json();
}

/**
 * Inicia uma sessão pontuada.
 * @returns {Promise<{id, startTime, totalExercises, correctAnswers, incorrectAnswers}>}
 */
export async function startSession() {
    return request('/api/sessions/start', { method: 'POST', body: '{}' });
}

/**
 * Termina a sessão indicada e devolve os resultados finais.
 * @param {number} id sessionId
 * @returns {Promise<SessionResponse>}
 */
export async function endSession(id) {
    return request(`/api/sessions/${id}/end`, { method: 'POST', body: '{}' });
}

/**
 * Gera um exercicio. sessionId=0 activa o modo pratica (sem persistencia).
 * @param {string} type 'INTERVAL' | 'SCALE' | 'CHORD'
 * @param {number} difficulty 1-10
 * @param {number} sessionId 0 para modo pratica
 * @returns {Promise<{exerciseId, type, difficulty, suggestedDifficulty, notes, description, hint}>}
 */
export async function generateExercise(type, difficulty, sessionId) {
    return request('/api/exercises/generate', {
        method: 'POST',
        body: JSON.stringify({ type, difficulty, sessionId }),
    });
}

/**
 * Avalia a resposta do utilizador.
 * O backend e agnóstico à origem das notas - teclado virtual ou MIDI fisico (ADR-002).
 * @param {number} exerciseId
 * @param {number} sessionId 0 para pratica
 * @param {number[]} notes numeros MIDI tocados
 * @param {number} responseTimeMs tempo em ms desde a geracao do exercicio
 * @returns {Promise<{correct, correctAnswer, userAnswer, explanation}>}
 */
export async function submitAnswer(exerciseId, sessionId, notes, responseTimeMs) {
    return request('/api/exercises/answer', {
        method: 'POST',
        body: JSON.stringify({ exerciseId, sessionId, notes, responseTimeMs }),
    });
}

/**
 * Devolve metricas globais de progresso.
 * @returns {Promise<ProgressResponse>}
 */
export async function getProgress() {
    return request('/api/progress');
}

/**
 * Devolve nome e intervalo para os numeros MIDI indicados (modo sandbox, F08).
 * Nao requer sessao activa.
 * @param {number[]} notes ex: [60, 67]
 * @returns {Promise<{notes: NoteInfo[], interval: IntervalInfo|null}>}
 */
export async function getNoteInfo(notes) {
    return request(`/api/sandbox/note-info?notes=${notes.join(',')}`);
}
