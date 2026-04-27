package pt.uab.musicaltrainer.dto;

/**
 * Schema JSON do campo question para exercícios de acorde.
 * Formato: {"root":midiRaiz,"type":"MAJOR"}
 */
public record ChordQuestion(int root, String type) {}
