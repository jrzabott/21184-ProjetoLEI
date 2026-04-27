package pt.uab.musicaltrainer.api;

import java.util.List;

public record NoteInfoResponse(List<NoteInfo> notes, IntervalInfo interval) {
    public record NoteInfo(int midiNumber, String name) {}
    public record IntervalInfo(int semitones, String name) {}
}
