package pt.uab.musicaltrainer.api;

public record GenerateRequest(String type, int difficulty) {
    public GenerateRequest {
        if (difficulty < 1 || difficulty > 10) {
            throw new IllegalArgumentException("difficulty deve ser entre 1 e 10");
        }
    }
}
