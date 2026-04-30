package pt.uab.musicaltrainer.dto;

/**
 * Schema JSON do campo question para exercícios de escala.
 * Formato: {"root":midiRaiz,"type":"MAJOR"}
 */
public record ScaleQuestion(int root, String type) {}
