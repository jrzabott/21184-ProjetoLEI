package pt.uab.musicaltrainer.dto;

/**
 * Schema JSON do campo question para exercícios de intervalo.
 * Formato: {"notes":[midiA,midiB]}
 */
public record IntervalQuestion(int[] notes) {}
