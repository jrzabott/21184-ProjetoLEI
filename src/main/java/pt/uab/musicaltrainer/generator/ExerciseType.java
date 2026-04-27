package pt.uab.musicaltrainer.generator;

/**
 * Tipos de exercícios suportados pelo sistema.
 * Usado para type-safe dispatch em ExerciseService e GeneratorFactory.
 * Fronteira com BD/API: usar ExerciseType.name() ao escrever, valueOf() ao ler.
 */
public enum ExerciseType {
    INTERVAL, SCALE, CHORD
}
